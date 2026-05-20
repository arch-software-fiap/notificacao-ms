terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.35"
    }
  }
}

variable "cluster_state_path" {
  default = "../../fixit-k8s/terraform/terraform.tfstate"
}

data "terraform_remote_state" "cluster" {
  backend = "local"
  config = {
    path = var.cluster_state_path
  }
}

locals {
  kubeconfig = yamldecode(data.terraform_remote_state.cluster.outputs.kubeconfig)
}

provider "kubernetes" {
  host                   = local.kubeconfig.clusters[0].cluster.server
  cluster_ca_certificate = base64decode(local.kubeconfig.clusters[0].cluster["certificate-authority-data"])
  client_certificate     = base64decode(local.kubeconfig.users[0].user["client-certificate-data"])
  client_key             = base64decode(local.kubeconfig.users[0].user["client-key-data"])
}

resource "kubernetes_manifest" "deployment" {
  manifest = yamldecode(file("${path.module}/../.k8s/deployment.yml"))
}

resource "kubernetes_manifest" "service" {
  manifest = yamldecode(file("${path.module}/../.k8s/service.yml"))
}
