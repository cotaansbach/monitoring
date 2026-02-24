1. docker-compose up -d
2. in grafana-terraform directory: \
terraform init \
terraform plan \
With your bot token and chat id: terraform apply -var="telegram_bot_token=TOKEN" -var="telegram_chat_id=CHAT_ID"
