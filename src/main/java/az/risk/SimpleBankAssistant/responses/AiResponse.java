package az.risk.SimpleBankAssistant.responses;

import java.util.Map;

import lombok.Data;
@Data
public class AiResponse {
    private String intent;
    private Map<String, String> parameters;}

