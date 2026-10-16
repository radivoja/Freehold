#!/bin/bash
echo "=== Initializing AWS resources on LocalStack ==="

# --- SQS Queues ---
echo "Creating SQS queues..."
awslocal sqs create-queue --queue-name production-orders
awslocal sqs create-queue --queue-name production-results

echo "SQS queues created:"
awslocal sqs list-queues

# --- DynamoDB Tables ---
echo "Creating DynamoDB tables..."
awslocal dynamodb create-table \
  --table-name audit-log \
  --attribute-definitions \
    AttributeName=pk,AttributeType=S \
    AttributeName=sk,AttributeType=S \
  --key-schema \
    AttributeName=pk,KeyType=HASH \
    AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST

echo "DynamoDB tables created:"
awslocal dynamodb list-tables

echo "=== LocalStack initialization complete ==="