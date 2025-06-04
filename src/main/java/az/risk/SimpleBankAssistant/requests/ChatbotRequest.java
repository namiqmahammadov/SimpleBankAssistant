package az.risk.SimpleBankAssistant.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatbotRequest {

    @Schema(description = "İstifadəçi tərəfindən verilən sual", example = "balansim ne qederdir?")
    private String question;
}
