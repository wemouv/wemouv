package com.diginamic.wemouv.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Suite de tests unitaires pour {@link EmailService}.
 * <p>
 * Cette classe valide que le service d'e-mail interagit correctement
 * avec {@link JavaMailSender} et que les messages sont construits
 * avec les paramètres attendus.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    /**
     * Vérifie que la méthode {@code sendMail} prépare un {@link SimpleMailMessage}
     * avec les bons destinataires, objet et contenu, avant de demander au
     * {@code mailSender} de l'expédier.
     */
    @Test
    void sendMail_DoitAppelerMailSender_AvecLesBonsParametres() {
        // ARRANGE
        String to = "test@exemple.com";
        String subject = "Test Objet";
        String body = "Contenu du message de test";

        // ACT
        emailService.sendMail(to, subject, body);

        // ASSERT
        // Utilisation d'un ArgumentCaptor pour inspecter le message envoyé au mock
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(body, capturedMessage.getText());
    }
}