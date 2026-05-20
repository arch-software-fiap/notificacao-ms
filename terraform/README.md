# notificacao-ms — Deploy Kubernetes

Provisiona o `notificacao-ms` no cluster Kind local via Terraform, lendo os manifests de `.k8s/deployment.yml` e `.k8s/service.yml`.

O serviço é stateless: consome eventos `fixit.eventos.os` do Kafka e envia e-mails via SMTP (MailHog em desenvolvimento).

## Pré-requisitos

- Cluster Kind rodando (`fixit-k8s/terraform`)
- MailHog implantado (`fixit-infra/mailhog/terraform`)
- Kafka implantado (`fixit-infra/kafka/terraform`)
- Secret `ghcr-secret` criado no namespace `fixit`

## Portas

| Porta interna | NodePort | Descrição |
|---|---|---|
| 8084 | **30084** | API / Actuator |

## Implantação

```bash
cd terraform

terraform init
terraform apply
terraform destroy   # para remover
```

## Verificação

```bash
kubectl logs -n fixit deployment/notificacao-ms --tail=50 | grep -E "Evento|email|ERROR"
```
