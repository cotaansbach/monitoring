terraform {
  required_providers {
    grafana = {
      source  = "grafana/grafana"
      version = "~> 2.9"
    }
  }
}

provider "grafana" {
  url  = "http://localhost:3000"
  auth = "glsa_S3pfUSCQAMf9GyK10KpIl3MR3cuMTYkC_24321094"
}