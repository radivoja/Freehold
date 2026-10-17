#!/bin/bash
set -euo pipefail
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

# --- S3 Buckets ---
echo "Creating S3 buckets..."
awslocal s3 mb s3://freehold-invoices

# --- SNS Topics ---
echo "Creating SNS topics..."
awslocal sns create-topic --name order-notifications

echo "=== LocalStack initialization complete ==="