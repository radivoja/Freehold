import json
import logging
import os
import threading
import time

import boto3
from fastapi import FastAPI
from pydantic import BaseModel

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
log = logging.getLogger("notification")

app = FastAPI(title="Freehold Notification Service")

AWS_ENDPOINT = os.getenv("AWS_ENDPOINT", "http://localhost:4566")
AWS_REGION = os.getenv("AWS_REGION", "us-east-1")
QUEUE_NAME = os.getenv("SQS_NOTIFICATION_QUEUE", "notification-queue")
POLL_INTERVAL = int(os.getenv("POLL_INTERVAL", "5"))

session = boto3.Session(
    aws_access_key_id="test",
    aws_secret_access_key="test",
    region_name=AWS_REGION,
)
sqs = session.client("sqs", endpoint_url=AWS_ENDPOINT)


class HealthResponse(BaseModel):
    status: str
    service: str


class Notification(BaseModel):
    orderId: str
    goodsType: str
    quantity: int
    status: str


notifications: list[dict] = []


def get_queue_url() -> str:
    return sqs.get_queue_url(QueueName=QUEUE_NAME)["QueueUrl"]


def process_message(body: str):
    try:
        envelope = json.loads(body)
        message = envelope.get("Message", body)
        if isinstance(message, str):
            message = json.loads(message)

        notification = Notification(**message)
        log.info(
            "Notification: order %s — %s %d x %s",
            notification.orderId,
            notification.status,
            notification.quantity,
            notification.goodsType,
        )
        notifications.append(notification.model_dump())

    except Exception as e:
        log.error("Failed to process message: %s — raw: %s", e, body[:200])


def poll_loop():
    time.sleep(5)
    queue_url = get_queue_url()
    log.info("Polling %s", queue_url)

    while True:
        try:
            resp = sqs.receive_message(
                QueueUrl=queue_url,
                MaxNumberOfMessages=10,
                WaitTimeSeconds=10,
            )
            for msg in resp.get("Messages", []):
                process_message(msg["Body"])
                sqs.delete_message(QueueUrl=queue_url, ReceiptHandle=msg["ReceiptHandle"])
        except Exception as e:
            log.error("Poll error: %s", e)
            time.sleep(POLL_INTERVAL)


@app.on_event("startup")
def start_poller():
    thread = threading.Thread(target=poll_loop, daemon=True)
    thread.start()
    log.info("Notification poller started")


@app.get("/health", response_model=HealthResponse)
def health():
    return HealthResponse(status="UP", service="notification")


@app.get("/notifications")
def get_notifications():
    return notifications


@app.get("/notifications/count")
def get_count():
    return {"count": len(notifications)}
