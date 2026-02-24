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
  message = "Created by Terraform"
  overwrite = true
  depends_on = [grafana_data_source.prometheus]
}

resource "grafana_folder" "rule_folder" {
    title = "Rule Folder"
}

resource "grafana_rule_group" "rule_group" {
  name            = "Order Service Alerts"
  folder_uid      = grafana_folder.rule_folder.uid
  interval_seconds = 60
  org_id          = 1

  rule {
    name      = "Order Service Down Alert"
    condition = "A"
    for       = "0s"

    data {
      ref_id        = "A"
      datasource_uid = grafana_data_source.prometheus.uid
      model = jsonencode({
        expr           = "last_over_time(up{job=\"order-service\"}[2m]) == 0"
        intervalMs     = 60000
        maxDataPoints  = 43200
        refId          = "A"
      })
      relative_time_range {
        from = 60
        to   = 0
      }
    }
  }
  depends_on = [grafana_data_source.prometheus]
}

resource "grafana_contact_point" "telegram_contact" {
  name = "Telegram Notifications"

  telegram {
    token = var.telegram_bot_token
    chat_id   = var.telegram_chat_id
    message = "{{ (index .Alerts.Firing 0).Labels.alertname }}"
  }
}

resource "grafana_notification_policy" "order_service_policy" {
  contact_point = grafana_contact_point.telegram_contact.name
  org_id        = 1
  group_by      = ["alertname"]

    policy {
      matcher {
      label = "alertname"
      match = "="
      value = "Order Service Down Alert"
      }
  }
}