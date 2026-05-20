package com.fixit.notificacao_ms.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter {

    private final JavaMailSender mailSender;

    public void enviar(String para, String assunto, String corpoHtml) {
        try {
            var msg = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom("noreply@fixit.com.br");
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);
            mailSender.send(msg);
            log.info("E-mail enviado para {}: {}", para, assunto);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", para, e.getMessage(), e);
        }
    }
}
