package com.example.Social.Media.Platform.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        String subject = "Verify your Fav-Z account";
        String content = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h1 style='color: #8b5cf6;'>Welcome to Fav-Z!</h1>"
                + "<p>You're almost there! Please click the button below to verify your account and start sharing your vibe:</p>"
                + "<div style='text-align: center; margin: 30px 0;'>"
                + "<a href=\"" + verificationUrl
                + "\" style='background: linear-gradient(135deg, #8b5cf6, #ec4899); color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Verify Account</a>"
                + "</div>"
                + "<p style='color: #666; font-size: 0.9em;'>This link will expire in 24 hours.</p>"
                + "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            System.out.println("Verification email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("CRITICAL: Failed to send email to " + to + ". Error: " + e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
