package com.diginamic.wemouv.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service utilitaire dédié à l'envoi de courriers électroniques.
 * <p>
 * Ce service utilise {@link JavaMailSender} de Spring Boot pour expédier
 * des notifications par e-mail de manière centralisée (par exemple lors
 * de la modification ou de la suppression d'un covoiturage).
 * </p>
 */
@Service
public class EmailService {

    /** Composant Spring chargé de l'envoi effectif des e-mails. */
    private final JavaMailSender mailSender;

    /**
     * Constructeur avec injection du composant d'envoi d'e-mail.
     *
     * @param mailSender le composant configuré par Spring Boot (via les propriétés SMTP)
     */
    public EmailService(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    /**
     * Prépare et expédie un e-mail au format texte simple.
     *
     * @param to l'adresse e-mail du destinataire
     * @param subject l'objet (titre) de l'e-mail
     * @param body le contenu (corps) du message
     * @throws org.springframework.mail.MailException si l'envoi échoue (problème réseau, configuration SMTP invalide, etc.)
     */
    public void sendMail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}