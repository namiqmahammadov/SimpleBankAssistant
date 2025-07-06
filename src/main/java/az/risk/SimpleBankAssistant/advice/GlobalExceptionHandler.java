package az.risk.SimpleBankAssistant.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import az.risk.SimpleBankAssistant.exception.CheckTransferException;
import az.risk.SimpleBankAssistant.exception.UnauthorizedAccessException;
import az.risk.SimpleBankAssistant.exception.UserNotFoundException;
import az.risk.SimpleBankAssistant.responses.GlobalErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	GlobalErrorResponse res = new GlobalErrorResponse();

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public GlobalErrorResponse handelCheckTransferException(CheckTransferException exc) {
		res.setInternalMessage("There is wrong in transfer informations ");
		res.setMessage("Transfer melumatlarinda sehv var");
		res.setCode(404);
		return res;
	}
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public GlobalErrorResponse handelUserNotFoundException(UserNotFoundException exc) {
		res.setInternalMessage("User not found");
		res.setMessage("Istifadəçi tapılmadı");
		res.setCode(404);
		return res;
	}
	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public GlobalErrorResponse handelUnauthorizedAccessException(UnauthorizedAccessException exc) {
		res.setInternalMessage("there is not authority of user ");
		res.setMessage("Istifadəçinin hüququ yoxdur");
		res.setCode(404);
		return res;
	}

}


