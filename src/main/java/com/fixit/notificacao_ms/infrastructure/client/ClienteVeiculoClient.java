package com.fixit.notificacao_ms.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "cliente-veiculo-ms", url = "${fixit.services.cliente-veiculo}")
public interface ClienteVeiculoClient {

    @GetMapping("/internal/clientes/{id}")
    ClienteResponse buscarCliente(@PathVariable UUID id);

    record ClienteResponse(UUID id, String nome, String email, String telefone) {}
}
