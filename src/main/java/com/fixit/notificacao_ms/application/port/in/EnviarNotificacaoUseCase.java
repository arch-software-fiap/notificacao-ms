package com.fixit.notificacao_ms.application.port.in;

import com.fixit.notificacao_ms.infrastructure.messaging.OsEventoMessage;

public interface EnviarNotificacaoUseCase {
    void executar(OsEventoMessage evento);
}
