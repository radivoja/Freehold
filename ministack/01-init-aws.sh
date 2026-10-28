#!/bin/bash
set -euo pipefail
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
echo "=== Initializing AWS resources on MiniStack ==="

AWS="aws --endpoint-url=http://ministack:4566 --region us-east-1"

# Cognito User Pool
POOL_ID=$($AWS cognito-idp create-user-pool \
  --pool-name freehold-users \
  --auto-verified-attributes email \
  --schema '[{"Name":"email","Required":true,"Mutable":true}]' \
  --query 'UserPool.Id' --output text)
echo "Created User Pool: $POOL_ID"

# Cognito App Client (SPA - no secret)
$AWS cognito-idp create-user-pool-client \
  --user-pool-id $POOL_ID \
  --client-name freehold-spa \
  --no-generate-secret \
  --explicit-auth-flows ALLOW_USER_SRP_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_PASSWORD_AUTH
echo "Created App Client for pool $POOL_ID"

# SQS Queues
$AWS sqs create-queue --queue-name production-orders
$AWS sqs create-queue --queue-name production-results
echo "Created SQS queues"

# DynamoDB Tables
$AWS dynamodb create-table --table-name audit-log \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST
echo "Created DynamoDB audit-log table"

# S3 Buckets
$AWS s3 mb s3://freehold-invoices
echo "Created S3 bucket"

# SNS Topics
$AWS sns create-topic --name order-notifications
echo "Created SNS topic"

echo "=== MiniStack initialization complete ==="