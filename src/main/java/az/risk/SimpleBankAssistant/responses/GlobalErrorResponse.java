package az.risk.SimpleBankAssistant.responses;

import java.util.List;


import lombok.Data;

@Data
public class GlobalErrorResponse {
	private Integer code;
	private String message;
	private String internalMessage;



}
