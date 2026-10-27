#!/bin/bash
set -euo pipefail
echo "=== Initializing AWS resources on MiniStack ==="

AWS="aws --endpoint-url=http://localhost:4566 --region us-east-1"

# --- SQS Queues ---
echo "Creating SQS queues..."
$AWS sqs create-queue --queue-name production-orders
$AWS sqs create-queue --queue-name production-results

echo "SQS queues created:"
$AWS sqs list-queues

# --- DynamoDB Tables ---
echo "Creating DynamoDB tables..."
$AWS dynamodb create-table \
  --table-name audit-log \
  --attribute-definitions \
    AttributeName=pk,AttributeType=S \
    AttributeName=sk,AttributeType=S \
  --key-schema \
    AttributeName=pk,KeyType=HASH \
    AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST

echo "DynamoDB tables created:"
$AWS dynamodb list-tables

# --- S3 Buckets ---
echo "Creating S3 buckets..."
$AWS s3 mb s3://freehold-invoices

# --- SNS Topics ---
echo "Creating SNS topics..."
$AWS sns create-topic --name order-notifications

echo "=== MiniStack initialization complete ==="