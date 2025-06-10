package az.risk.SimpleBankAssistant.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import az.risk.SimpleBankAssistant.exception.CheckTransferException;
import az.risk.SimpleBankAssistant.responses.GlobalErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	GlobalErrorResponse res = new GlobalErrorResponse();

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public GlobalErrorResponse handelCheckTransferException(CheckTransferException exc) {
		res.setInternalMessage("Transfer information has wrong");
		res.setMessage("Transfer melumatlarinda sehv var");
		res.setCode(404);
		return res;
	}

}
