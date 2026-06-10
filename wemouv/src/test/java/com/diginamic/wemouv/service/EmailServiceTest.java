package com.diginamic.wemouv.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de tests unitaires pour {@link EmailService}.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", "test@exemple.com");
    }

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
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals("test@exemple.com", capturedMessage.getFrom());
        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertEquals(body, capturedMessage.getText());
    }

    @Test
    void sendMailGroup_DoitAppelerMailSender_AvecMimeMessage() {
        // ARRANGE
        String[] bcc = {"a@test.com", "b@test.com"};
        String subject = "Sujet HTML";
        String htmlBody = "<h1>Bonjour</h1>";

        jakarta.mail.internet.MimeMessage mimeMessage = mock(jakarta.mail.internet.MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // ACT
        emailService.sendMailGroup(bcc, subject, htmlBody);

        // ASSERT
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendMailGroup_QuandMessagingException_DoitGererException() throws Exception {
        // ARRANGE
        String[] bcc = {"a@test.com"};
        String subject = "Sujet";
        String htmlBody = "corps";

        jakarta.mail.internet.MimeMessage mimeMessage = mock(jakarta.mail.internet.MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new jakarta.mail.MessagingException("test exception"))
                .when(mimeMessage).setFrom(any(jakarta.mail.Address.class));

        // ACT & ASSERT
        assertDoesNotThrow(() -> emailService.sendMailGroup(bcc, subject, htmlBody));
    }
}