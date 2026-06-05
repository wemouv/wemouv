package com.diginamic.wemouv.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service utilitaire dédié à l'envoi de courriers électroniques.
 */
@Service
public class EmailService {

    /** Composant Spring chargé de l'envoi effectif des e-mails. */
    private final JavaMailSender mailSender;

    /** Récupère automatiquement l'adresse e-mail configurée dans application.properties */
    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * Constructeur avec injection du composant d'envoi d'e-mail.
     *
     * @param mailSender le composant configuré par Spring Boot (via les propriétés SMTP)
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Prépare et expédie un e-mail au format texte simple.
     */
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Prépare et expédie un e-mail formaté en HTML à plusieurs destinataires en copie cachée (BCC).
     * * @param bcc Tableau contenant les adresses e-mails des destinataires
     * @param subject L'objet (titre) de l'e-mail
     * @param htmlBody Le contenu de l'e-mail (qui peut contenir des balises HTML)
     */
    public void sendMailGroup(String[] bcc, String subject, String htmlBody) {
        try {
            // On crée un message complexe (MIME) au lieu d'un message simple
            MimeMessage message = mailSender.createMimeMessage();

            // Le "true" indique qu'on veut pouvoir utiliser du HTML/Multipart, et "UTF-8" gère les accents
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setBcc(bcc); // Copie cachée pour protéger la vie privée des passagers
            helper.setSubject(subject);

            // Le deuxième paramètre "true" dit à Spring Boot : "Attention, ce texte est du HTML !"
            helper.setText(htmlBody, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Erreur lors de la création de l'e-mail HTML : " + e.getMessage());
        }
    }
}