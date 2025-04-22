package az.risk.SimpleBankAssistant.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyConverterUtil {

    // Statik kurslar
    private static final BigDecimal USD_TO_AZN = BigDecimal.valueOf(1.7);   // 1 USD = 1.7 AZN
    private static final BigDecimal EUR_TO_AZN = BigDecimal.valueOf(1.85);  // 1 EUR = 1.85 AZN

    // Əlavə kurslar
    private static final BigDecimal EUR_TO_USD = EUR_TO_AZN.divide(USD_TO_AZN, 6, RoundingMode.HALF_UP); // ~1.088
    private static final BigDecimal USD_TO_EUR = USD_TO_AZN.divide(EUR_TO_AZN, 6, RoundingMode.HALF_UP); // ~0.919

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        switch (fromCurrency + "_" + toCurrency) {
            case "AZN_USD":
                return amount.divide(USD_TO_AZN, 4, RoundingMode.HALF_UP);
            case "AZN_EUR":
                return amount.divide(EUR_TO_AZN, 4, RoundingMode.HALF_UP);
            case "USD_AZN":
                return amount.multiply(USD_TO_AZN);
            case "EUR_AZN":
                return amount.multiply(EUR_TO_AZN);
            case "USD_EUR":
                return amount.multiply(USD_TO_EUR).setScale(4, RoundingMode.HALF_UP);
            case "EUR_USD":
                return amount.multiply(EUR_TO_USD).setScale(4, RoundingMode.HALF_UP);
            default:
                throw new IllegalArgumentException("Unsupported currency conversion: " + fromCurrency + " to " + toCurrency);
        }
    }
}
