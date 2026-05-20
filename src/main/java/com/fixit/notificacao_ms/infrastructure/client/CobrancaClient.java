package com.fixit.notificacao_ms.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "cobranca-ms", url = "${fixit.services.cobranca}")
public interface CobrancaClient {

    @GetMapping("/internal/orcamentos/os/{osId}")
    OrcamentoResponse buscarPorOs(@PathVariable UUID osId);

    record OrcamentoResponse(UUID id, UUID ordemServicoId, String situacao,
                             BigDecimal valorTotal, String qrCodeData) {}
}
