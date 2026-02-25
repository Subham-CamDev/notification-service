package com.subham.microservices.service;

import com.subham.microservices.events.OrderPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

  private final JavaMailSender javaMailSender;

  @KafkaListener(topics = "order-placed")
  public void listen(OrderPlacedEvent orderPlacedEvent) throws MessagingException {
    log.info("Received order placed event for order number: {}", orderPlacedEvent.getOrderNumber());
    // Simulate sending email notification
    log.info("Sending email notification to: {}", orderPlacedEvent.getEmail());
    // Here you would implement the actual email sending logic using javaMailSender
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

    helper.setTo(orderPlacedEvent.getEmail());
    helper.setSubject(String.format("Order Confirmation - %s", orderPlacedEvent.getOrderNumber()));
    helper.setText(String.format("""
                    Hi,
                    
                    Your Order with order number %s has been placed successfully.
                    We will notify you once it is shipped.
                    
                    Thank you for shopping with us!
                    Best regards,
                    Subham Microservices Stores
                    """,
            orderPlacedEvent.getOrderNumber()));
    helper.setFrom("subham@demomailtrap.co");

    try {
      javaMailSender.send(mimeMessage);
      log.info("Email notification sent successfully to: {}", orderPlacedEvent.getEmail());
    } catch (MailException e) {
      log.error("Failed to send email notification to: {}", orderPlacedEvent.getEmail(), e);
      throw new RuntimeException("Exception Occurred while sending Email Notification", e);
    }
  }
}
