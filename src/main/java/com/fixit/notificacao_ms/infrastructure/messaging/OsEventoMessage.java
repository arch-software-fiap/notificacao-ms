package com.fixit.notificacao_ms.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsEventoMessage {
    private UUID osId;
    private UUID clienteId;
    private String situacao;
}
