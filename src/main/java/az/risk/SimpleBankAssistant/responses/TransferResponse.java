package az.risk.SimpleBankAssistant.responses;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransferResponse {
	private String message;
	private Timestamp date;
	private boolean success;

}
