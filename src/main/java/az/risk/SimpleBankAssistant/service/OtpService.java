package az.risk.SimpleBankAssistant.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.OtpCode;
import az.risk.SimpleBankAssistant.repository.OtpCodeRepository;

@Service
public class OtpService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private OtpCodeRepository otpRepo;

	public String generateOtp() {
		return String.valueOf((int) ((Math.random() * 900000) + 100000));
	}

	public void sendOtpToEmail(String email) {
		String otp = generateOtp();

		OtpCode otpEntity = new OtpCode();
		otpEntity.setEmail(email);
		otpEntity.setCode(otp);
		otpEntity.setExpiryDate(new Date(System.currentTimeMillis() + 5 * 60 * 1000)); // 5 dəqiqəlik
		otpEntity.setUsed(false);
		otpRepo.save(otpEntity);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("SimpleBank OTP");
		message.setText("OTP kodunuz: " + otp);
		mailSender.send(message);
	}

	public boolean verifyOtp(String email, String code) {
		OtpCode otp = otpRepo.findTopByEmailAndCodeAndUsedFalseOrderByExpiryDateDesc(email, code);
		if (otp != null && otp.getExpiryDate().after(new Date())) {
			otp.setUsed(true);
			otpRepo.save(otp);
			return true;
		}
		return false;
	}
}
