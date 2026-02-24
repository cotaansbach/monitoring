import time
import http.client
import json
import random
import os

# -----------------------------
# CONFIG FROM ENV
# -----------------------------
TARGET_HOSTS = os.getenv("TARGET_HOSTS", "localhost:8080,localhost:8081")
RPS = int(os.getenv("RPS", "100"))
GET_WEIGHT = int(os.getenv("GET_WEIGHT", "99"))
POST_WEIGHT = int(os.getenv("POST_WEIGHT", "1"))

hosts = [h.strip() for h in TARGET_HOSTS.split(",")]
conn_index = 0

# -----------------------------
# HELPER FUNCTIONS
# -----------------------------
def get_next_host():
    global conn_index
    host = hosts[conn_index]
    conn_index = (conn_index + 1) % len(hosts)
    return host

def safe_request(host, method, url, body=None, headers=None):
    """Send a request, auto-reconnect on error."""
    if headers is None:
        headers = {}

    try:
        conn = http.client.HTTPConnection(host, timeout=5)
        conn.request(method, url, body=body, headers=headers)
        response = conn.getresponse()
        response.read()
        conn.close()
        return response.status
    except Exception as e:
        print(f"{method} error on {host}: {e}")
        return None

# -----------------------------
# REQUESTS
# -----------------------------
def make_get_orders_request(host):
    status = safe_request(host, "GET", "/api/v1/orders")
    print("GET", status, host)

def make_post_order_request(host):
    payload = json.dumps({
        "id": None,
        "amount": round(random.uniform(10, 20000), 2),
        "createDateTimeMsk": None
    })
    status = safe_request(host, "POST", "/api/v1/orders", body=payload, headers={"Content-Type": "application/json"})
    print("POST", status, host)

# -----------------------------
# MAIN LOOP
# -----------------------------
while True:
    start = time.time()
    host = get_next_host()

    # Weighted choice for GET vs POST
    if random.randint(1, GET_WEIGHT + POST_WEIGHT) <= GET_WEIGHT:
        make_get_orders_request(host)
    else:
        make_post_order_request(host)

    # maintain target RPS
    elapsed = time.time() - start
    sleep_time = max(0, 1 / RPS - elapsed)
    time.sleep(sleep_time)