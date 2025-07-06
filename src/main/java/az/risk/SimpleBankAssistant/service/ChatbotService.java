package az.risk.SimpleBankAssistant.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;

@Service
public class ChatbotService {
	private final CustomerAccountService customerAccountService;
	private final LoanService loanService;
	private final MoneyTransferService moneyTransferService;
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final String CLASSIFIER_URL = "https://bankbot-2.onrender.com/classify";

	@Autowired
	public ChatbotService(CustomerAccountService customerAccountService, LoanService loanService,
			MoneyTransferService moneyTransferService) {
		this.customerAccountService = customerAccountService;
		this.loanService = loanService;
		this.moneyTransferService = moneyTransferService;
	}

	public String handleUserInput(String userInput, String language) {
		try {
			// AI cavabını al
			String aiRawResponse = getAiRawResponse(userInput, language);
			JsonNode aiJson = objectMapper.readTree(aiRawResponse);

			String category = aiJson.path("class name").asText("[other]");
			String staticText = aiJson.path("response").asText("");

			// Şəxsi sualdırsa və login olunmayıbsa
			if (isPersonalCategory(category) && !isUserAuthenticated()) {
				return "Bu sual üçün hesabınıza daxil olun.";
			}

			// Dinamik cavab yalnız şəxsi suallar üçün əlavə olunur
			String dynamicPart = isPersonalCategory(category) ? getDynamicPart(category) : "";

			return staticText + dynamicPart;
		} catch (Exception e) {
			e.printStackTrace();
			return "Cavab hazırlanarkən xəta baş verdi.";
		}
	}

	private String getAiRawResponse(String input, String language) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Question question = new Question(input, language);
			HttpEntity<Question> entity = new HttpEntity<>(question, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(CLASSIFIER_URL, entity, String.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			} else {
				return "{\"class name\":\"[other]\",\"response\":\"AI cavabı alınmadı. \"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"class name\":\"[other]\",\"response\":\"AI cavabı alınmadı. \"}";
		}
	}

	private String getDynamicPart(String category) {
		String username = getAuthenticatedUsername();

		return switch (category) {
			case "[account balans check]" -> {
				List<CustomerAccount> accounts = customerAccountService.getUserAccounts(username);
				yield accounts.isEmpty() ? "" :
					accounts.stream()
						.map(account -> account.getIban() + " - " + account.getAvailableBalance().toPlainString() + " " + account.getCurrency())
						.collect(Collectors.joining("\n"));
			}
			case "[iban code check]" -> {
				List<CustomerAccount> ibanAccounts = customerAccountService.getUserAccounts(username);
				yield ibanAccounts.isEmpty() ? "" :
					ibanAccounts.stream().map(CustomerAccount::getIban).collect(Collectors.joining(", "));
			}
			case "[number of accounts check]" -> {
				List<CustomerAccount> accs = customerAccountService.getUserAccounts(username);
				yield accs.isEmpty() ? "" : String.valueOf(accs.size());
			}
			case "[account currency check]" -> {
				List<CustomerAccount> currencyAccounts = customerAccountService.getUserAccounts(username);
				yield currencyAccounts.isEmpty() ? "" :
					currencyAccounts.stream().map(a -> a.getCurrency().name()).distinct().collect(Collectors.joining(" "));
			}
			case "[loan debt check]" -> {
				Object loanResponse = loanService.getLoanDebt().getBody();
				if (loanResponse instanceof String str) {
					yield str;
				} else if (loanResponse instanceof Map<?, ?> map) {
					Object totalDebt = map.get("totalDebt");
					yield totalDebt != null ? totalDebt.toString() + " AZN" : "";
				}
				yield "";
			}
			case "[account history]" -> {
				List<CustomerAccountHistory> historyList = customerAccountService.getAccountHistory();
				if (historyList.isEmpty()) yield "";
				CustomerAccountHistory lastOp = historyList.get(historyList.size() - 1);
				yield String.format("%s, %s %s", lastOp.getOperationType(), lastOp.getAmount(), lastOp.getCurrency());
			}
			case "[loan history]" -> {
				Object loanHistoryObj = loanService.getLoanHistory().getBody();
				if (!(loanHistoryObj instanceof List<?> list) || list.isEmpty()) yield "";
				Object lastLoanObj = list.get(list.size() - 1);
				if (lastLoanObj instanceof Map<?, ?> map) {
					yield String.format("%s AZN, %s", map.get("amount"), map.get("date"));
				}
				yield "";
			}
			case "[transfer history]" -> {
				List<MoneyTransfer> transfers = moneyTransferService.getTransferHistory();
				if (transfers.isEmpty()) yield "";
				MoneyTransfer lastTransfer = transfers.get(transfers.size() - 1);
				yield String.format("%s AZN -> %s", lastTransfer.getAmount().toPlainString(), lastTransfer.getReceiverIban());
			}
			default -> "";
		};
	}

	private boolean isUserAuthenticated() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
	}

	private String getAuthenticatedUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private boolean isPersonalCategory(String category) {
		return switch (category) {
			case "[account balans check]",
				 "[iban code check]",
				 "[number of accounts check]",
				 "[account currency check]",
				 "[loan debt check]",
				 "[account history]",
				 "[loan history]",
				 "[transfer history]" -> true;
			default -> false;
		};
	}

	static class Question {
		public String question;
		public String language;

		public Question(String question, String language) {
			this.question = question;
			this.language = language;
		}

		public Question() {
		}
	}
}
