package com.fixit.notificacao_ms.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixit.notificacao_ms.application.port.in.EnviarNotificacaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OsEventoListener {

    private final EnviarNotificacaoUseCase enviarNotificacaoUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "fixit.eventos.os", groupId = "notificacao-ms-group")
    public void handleOsEvento(String messageStr) {
        try {
            OsEventoMessage evento = objectMapper.readValue(messageStr, OsEventoMessage.class);
            log.info("Evento OS recebido: osId={}, situacao={}", evento.getOsId(), evento.getSituacao());
            enviarNotificacaoUseCase.executar(evento);
        } catch (Exception e) {
            log.error("Erro ao processar evento OS: {}", e.getMessage(), e);
        }
    }
}
