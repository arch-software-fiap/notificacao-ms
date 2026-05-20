package com.fixit.notificacao_ms.application.usecase;

import com.fixit.notificacao_ms.application.port.in.EnviarNotificacaoUseCase;
import com.fixit.notificacao_ms.infrastructure.client.ClienteVeiculoClient;
import com.fixit.notificacao_ms.infrastructure.client.CobrancaClient;
import com.fixit.notificacao_ms.infrastructure.mail.EmailSenderAdapter;
import com.fixit.notificacao_ms.infrastructure.messaging.OsEventoMessage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnviarNotificacaoUseCaseImpl implements EnviarNotificacaoUseCase {

    private final ClienteVeiculoClient clienteVeiculoClient;
    private final CobrancaClient cobrancaClient;
    private final EmailSenderAdapter emailSender;

    @Override
    public void executar(OsEventoMessage evento) {
        String email;
        String nomeCliente;

        try {
            var cliente = clienteVeiculoClient.buscarCliente(evento.getClienteId());
            email = cliente.email();
            nomeCliente = cliente.nome();
        } catch (Exception e) {
            log.error("Não foi possível buscar cliente {}: {}", evento.getClienteId(), e.getMessage());
            return;
        }

        switch (evento.getSituacao()) {
            case "AGUARDANDO_APROVACAO" ->
                    emailSender.enviar(email,
                            "FixIt — Orçamento disponível para aprovação",
                            templateOrcamentoCriado(nomeCliente, evento.getOsId().toString()));

            case "AGUARDANDO_PAGAMENTO" -> {
                String qrCodeData = "(indisponível)";
                BigDecimal valor = BigDecimal.ZERO;
                try {
                    var orcamento = cobrancaClient.buscarPorOs(evento.getOsId());
                    qrCodeData = orcamento.qrCodeData() != null ? orcamento.qrCodeData() : "(indisponível)";
                    valor = orcamento.valorTotal() != null ? orcamento.valorTotal() : BigDecimal.ZERO;
                } catch (Exception e) {
                    log.warn("Não foi possível buscar orçamento para OS {}: {}", evento.getOsId(), e.getMessage());
                }
                emailSender.enviar(email,
                        "FixIt — Seu QR Code PIX está pronto!",
                        templateQrCode(nomeCliente, evento.getOsId().toString(), qrCodeData, valor));
            }

            case "CANCELADA" ->
                    emailSender.enviar(email,
                            "FixIt — Ordem de Serviço cancelada",
                            templateCancelada(nomeCliente, evento.getOsId().toString()));

            case "FINALIZADA" ->
                    emailSender.enviar(email,
                            "FixIt — Seu veículo está pronto para retirada!",
                            templateFinalizada(nomeCliente, evento.getOsId().toString()));

            default -> log.debug("Situação sem notificação configurada: {}", evento.getSituacao());
        }
    }

    private String templateOrcamentoCriado(String nome, String osId) {
        return """
                <html><body style="font-family:Arial,sans-serif;color:#333">
                <h2 style="color:#1a73e8">Olá, %s!</h2>
                <p>O orçamento da sua Ordem de Serviço <strong>#%s</strong> foi criado e está
                <strong>aguardando sua aprovação</strong>.</p>
                <p>Acesse o sistema FixIt para visualizar os detalhes e aprovar o orçamento.</p>
                <hr/><p style="font-size:12px;color:#888">FixIt — Oficina Inteligente</p>
                </body></html>
                """.formatted(nome, osId);
    }

    private String templateQrCode(String nome, String osId, String qrCodeData, BigDecimal valor) {
        String qrBase64 = gerarQrCodeBase64(qrCodeData);
        String imgHtml = qrBase64 != null
                ? "<div style=\"text-align:center;margin:16px 0\">" +
                  "<img src=\"data:image/png;base64," + qrBase64 + "\" alt=\"QR Code PIX\" width=\"250\" height=\"250\"/>" +
                  "</div>"
                : "";
        return """
                <html><body style="font-family:Arial,sans-serif;color:#333">
                <h2 style="color:#1a73e8">Olá, %s!</h2>
                <p>Seu pagamento PIX para a OS <strong>#%s</strong> está pronto.</p>
                <p><strong>Valor:</strong> R$ %s</p>
                <p>Copie o código abaixo e cole no aplicativo do seu banco:</p>
                <div style="background:#f4f4f4;padding:12px;border-radius:6px;word-break:break-all;font-family:monospace;font-size:12px">
                %s
                </div>
                %s
                <hr/><p style="font-size:12px;color:#888">FixIt — Oficina Inteligente</p>
                </body></html>
                """.formatted(nome, osId, valor.toPlainString(), qrCodeData, imgHtml);
    }

    private String gerarQrCodeBase64(String conteudo) {
        try {
            var bitMatrix = new MultiFormatWriter().encode(conteudo, BarcodeFormat.QR_CODE, 300, 300);
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.warn("Não foi possível gerar QR Code: {}", e.getMessage());
            return null;
        }
    }

    private String templateCancelada(String nome, String osId) {
        return """
                <html><body style="font-family:Arial,sans-serif;color:#333">
                <h2 style="color:#d32f2f">Olá, %s</h2>
                <p>Infelizmente sua Ordem de Serviço <strong>#%s</strong> foi <strong>cancelada</strong>.</p>
                <p>Entre em contato com nossa equipe para mais informações.</p>
                <hr/><p style="font-size:12px;color:#888">FixIt — Oficina Inteligente</p>
                </body></html>
                """.formatted(nome, osId);
    }

    private String templateFinalizada(String nome, String osId) {
        return """
                <html><body style="font-family:Arial,sans-serif;color:#333">
                <h2 style="color:#2e7d32">Olá, %s!</h2>
                <p>Seu veículo está pronto! A Ordem de Serviço <strong>#%s</strong> foi
                <strong>finalizada</strong> com sucesso.</p>
                <p>Passe na nossa oficina para retirar o veículo. Obrigado pela confiança!</p>
                <hr/><p style="font-size:12px;color:#888">FixIt — Oficina Inteligente</p>
                </body></html>
                """.formatted(nome, osId);
    }
}
