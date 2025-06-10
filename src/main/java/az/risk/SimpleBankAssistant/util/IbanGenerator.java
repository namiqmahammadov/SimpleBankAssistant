package az.risk.SimpleBankAssistant.util;

import java.util.Random;

public class IbanGenerator {
	public static String generateRandomIban() {
		Random random = new Random();
		StringBuilder iban = new StringBuilder();

		iban.append("AZ");
		
		for (int i = 0; i < 14; i++) {
			iban.append(random.nextInt(10)); 
		}
		return iban.toString();
	}

	public static void main(String[] args) {
		String randomIban = generateRandomIban();
		System.out.println("Generated IBAN: " + randomIban);
	}
}
