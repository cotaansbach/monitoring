resource "grafana_data_source" "prometheus" {
  name = "Prometheus"
  type = "prometheus"
  url  = "http://prometheus:9090"
  access_mode = "proxy"
  is_default = true

  lifecycle {
    create_before_destroy = true
  }
}

resource "grafana_dashboard" "order_service" {
  config_json = file("${path.module}/dashboard.json")
  message = "Развернуто с помощью Terraform"
  overwrite = true
  depends_on = [grafana_data_source.prometheus]
}