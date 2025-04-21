package az.risk.SimpleBankAssistant.util;

import java.util.Random;

public class IbanGenerator {

    // 16 rəqəmli təsadüfi IBAN yaradılması
    public static String generateRandomIban() {
        Random random = new Random();
        StringBuilder iban = new StringBuilder();

        // Azərbaycan üçün prefiks: "AZ" (Bu istəyə görə dəyişdirilə bilər)
        iban.append("AZ");

        // 14 təsadüfi rəqəm əlavə edirik
        for (int i = 0; i < 14; i++) {
            iban.append(random.nextInt(10)); // 0-9 arasında təsadüfi rəqəm
        }

        return iban.toString();
    }

    public static void main(String[] args) {
        // IBAN generatorunun sınaqdan keçirilməsi
        String randomIban = generateRandomIban();
        System.out.println("Generated IBAN: " + randomIban);
    }
}
