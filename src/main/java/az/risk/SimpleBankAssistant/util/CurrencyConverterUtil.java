package az.risk.SimpleBankAssistant.util;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CurrencyConverterUtil {

	private final String API_KEY = "eccdf95feb5d9531d6338a98"; 
	private final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

	private RestTemplate restTemplate = new RestTemplate();

	public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
		
		String url = API_URL + fromCurrency;
		ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

		if (response != null && response.getConversionRates() != null) {
			BigDecimal conversionRate = response.getConversionRates().get(toCurrency);
			if (conversionRate != null) {
				return amount.multiply(conversionRate);
			}
		}
		return amount; 
	}
    // Valyuta çevirmə üçün response obyekti
    public static class ExchangeRateResponse {
        private String result;
        private String baseCode;
        private java.util.Map<String, BigDecimal> conversionRates;

        public java.util.Map<String, BigDecimal> getConversionRates() {
            return conversionRates;
        }

        public void setConversionRates(java.util.Map<String, BigDecimal> conversionRates) {
            this.conversionRates = conversionRates;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getBaseCode() {
            return baseCode;
        }

        public void setBaseCode(String baseCode) {
            this.baseCode = baseCode;
        }
    }

}
