# notificacao-ms

Envia notificações por e-mail ao cliente em cada etapa da Saga: orçamento disponível, QR Code PIX e OS finalizada. Stateless — sem banco de dados.

- **Mensageria:** consome `fixit.eventos.os` via Kafka
- **SMTP:** MailHog em desenvolvimento (`fixit-infra/mailhog/terraform`)
- **Porta:** 8084 | NodePort **30084**

## Implantação no cluster local

```bash
cd terraform

terraform init
terraform apply
terraform destroy   # para remover
```

> Pré-requisitos: cluster Kind, namespace `fixit`, secret `ghcr-secret`, Kafka e MailHog em pé.
