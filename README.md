1. docker-compose up -d
2. in grafana-terraform directory: 
    2.1) terraform init 
    2.2) terraform plan 
    3.3) terraform apply -var="telegram_bot_token=TOKEN" -var="telegram_chat_id=CHAT_ID"