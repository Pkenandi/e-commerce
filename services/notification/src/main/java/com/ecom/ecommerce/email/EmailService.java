package com.ecom.ecommerce.email;

import com.ecom.ecommerce.kafka.order.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private static final String FROM_EMAIL = "kenandiprince@yahoo.com";

    @Async
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(mimeMailMessage, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());
            messageHelper.setFrom(FROM_EMAIL);
            messageHelper.setSubject(subject);

            Context context = new Context();
            context.setVariables(variables);
            String htmlTemplate = templateEngine.process(templateName, context);

            messageHelper.setText(htmlTemplate, true);
            messageHelper.setTo(to);
            mailSender.send(mimeMailMessage);

            log.info("Email successfully sent to {} with template {}", to, templateName);
        } catch (MessagingException e) {
            log.warn("WARNING - Cannot send email to {} due to error: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendPaymentSuccessEmail(String to, String customerName, BigDecimal amount, String orderReference) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("amount", amount);
        variables.put("orderReference", orderReference);

        sendEmail(to, EmailTemplates.PAYMENT_CONFIRMATION.getSubject(),
                EmailTemplates.PAYMENT_CONFIRMATION.getTemplate(), variables);
    }

    @Async
    public void sendOrderConfirmationEmail(
            String to, String customerName, BigDecimal amount, String orderReference, List<Product> products
    ) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("totalAmount", amount);
        variables.put("orderReference", orderReference);
        variables.put("products", products);

        sendEmail(to, EmailTemplates.ORDER_CONFIRMATION.getSubject(),
                EmailTemplates.ORDER_CONFIRMATION.getTemplate(), variables);
    }
}
