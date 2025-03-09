package com.bolane.backend.Services;

import com.bolane.backend.Entities.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.sender-name}")
    private String senderName;

    /**
     * Send a booking confirmation email to the user
     *
     * @param booking the booking entity
     */
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            // Create a new MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties using InternetAddress for proper sender name
            InternetAddress from = new InternetAddress(fromEmail);
            from.setPersonal(senderName);
            helper.setFrom(from);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Confirmation - Bolane Beauty Services");

            // Format dates for display
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedDate = booking.getDate().format(dateFormatter);
            String formattedTime = booking.getTime().format(timeFormatter);

            // Create the Thymeleaf context with variables
            Context context = new Context();
            context.setVariables(Map.of(
                    "customerName", booking.getUser().getFirstName() + " " + booking.getUser().getLastName(),
                    "bookingId", booking.getId(),
                    "serviceName", booking.getServiceRequired(),
                    "location", booking.getLocation(),
                    "date", formattedDate,
                    "time", formattedTime,
                    "primaryColor", "#673ab7"  // Using your theme color
            ));

            // Process the HTML template with Thymeleaf
            String emailContent = templateEngine.process("emails/booking-confirmation", context);
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(message);
            log.info("Booking confirmation email sent successfully to {}", booking.getUser().getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send booking confirmation email", e);
        }
    }

    /**
     * Send a booking update email to the user
     *
     * @param newBooking the updated booking entity
     * @param oldBooking the original booking entity
     */
    public void sendBookingUpdateEmail(Booking newBooking, Booking oldBooking) {
        try {
            // Create a new MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties using InternetAddress for proper sender name
            InternetAddress from = new InternetAddress(fromEmail);
            from.setPersonal(senderName);
            helper.setFrom(from);
            helper.setTo(newBooking.getUser().getEmail());
            helper.setSubject("Booking Update - Bolane Beauty Services");

            // Format dates for display
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

            // Format new booking details
            String newFormattedDate = newBooking.getDate().format(dateFormatter);
            String newFormattedTime = newBooking.getTime().format(timeFormatter);

            // Format old booking details
            String oldFormattedDate = oldBooking.getDate().format(dateFormatter);
            String oldFormattedTime = oldBooking.getTime().format(timeFormatter);

            // Check what changed
            boolean serviceChanged = !oldBooking.getServiceRequired().equals(newBooking.getServiceRequired());
            boolean locationChanged = !oldBooking.getLocation().equals(newBooking.getLocation());
            boolean dateChanged = !oldBooking.getDate().equals(newBooking.getDate());
            boolean timeChanged = !oldBooking.getTime().equals(newBooking.getTime());
            boolean hasChanges = serviceChanged || locationChanged || dateChanged || timeChanged;

            // Create the Thymeleaf context with variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", newBooking.getUser().getFirstName() + " " + newBooking.getUser().getLastName());
            variables.put("bookingId", newBooking.getId());
            variables.put("serviceName", newBooking.getServiceRequired());
            variables.put("location", newBooking.getLocation());
            variables.put("date", newFormattedDate);
            variables.put("time", newFormattedTime);
            variables.put("primaryColor", "#673ab7");

            // Add change tracking variables
            variables.put("hasChanges", hasChanges);
            variables.put("serviceChanged", serviceChanged);
            variables.put("locationChanged", locationChanged);
            variables.put("dateChanged", dateChanged);
            variables.put("timeChanged", timeChanged);

            // Add old values for comparison
            variables.put("oldService", oldBooking.getServiceRequired());
            variables.put("oldLocation", oldBooking.getLocation());
            variables.put("oldDate", oldFormattedDate);
            variables.put("oldTime", oldFormattedTime);

            Context context = new Context();
            context.setVariables(variables);

            // Process the HTML template with Thymeleaf
            String emailContent = templateEngine.process("emails/booking-update", context);
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(message);
            log.info("Booking update email sent successfully to {}", newBooking.getUser().getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send booking update email", e);
        }
    }

    /**
     * Send a booking cancellation email to the user
     *
     * @param booking the booking entity
     */
    public void sendBookingCancellationEmail(Booking booking) {
        try {
            // Create a new MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties using InternetAddress for proper sender name
            InternetAddress from = new InternetAddress(fromEmail);
            from.setPersonal(senderName);
            helper.setFrom(from);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Cancellation - Bolane Beauty Services");

            // Format dates for display
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedDate = booking.getDate().format(dateFormatter);
            String formattedTime = booking.getTime().format(timeFormatter);

            // Create the Thymeleaf context with variables
            Context context = new Context();
            context.setVariables(Map.of(
                    "customerName", booking.getUser().getFirstName() + " " + booking.getUser().getLastName(),
                    "bookingId", booking.getId(),
                    "serviceName", booking.getServiceRequired(),
                    "location", booking.getLocation(),
                    "date", formattedDate,
                    "time", formattedTime,
                    "primaryColor", "#673ab7"  // Using your theme color
            ));

            // Process the HTML template with Thymeleaf
            String emailContent = templateEngine.process("emails/booking-cancellation", context);
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(message);
            log.info("Booking cancellation email sent successfully to {}", booking.getUser().getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send booking cancellation email", e);
        }
    }
}