package az.risk.SimpleBankAssistant.responses;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransferResponse {
    private String message;
    private LocalDateTime date;
}
