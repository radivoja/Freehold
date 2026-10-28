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

# DynamoDB Tables (idempotent)
$AWS dynamodb create-table --table-name audit-log \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST 2>/dev/null || echo "audit-log table already exists"
echo "DynamoDB ready"

# S3 Buckets (idempotent)
$AWS s3 mb s3://freehold-invoices 2>/dev/null || echo "freehold-invoices bucket already exists"
$AWS s3 mb s3://freehold-spa 2>/dev/null || echo "freehold-spa bucket already exists"

# SPA bucket: website hosting + public read
$AWS s3 website s3://freehold-spa --index-document index.html --error-document index.html
$AWS s3api put-bucket-policy --bucket freehold-spa --policy '{
  "Version": "2012-10-17",
  "Statement": [{
    "Sid": "PublicRead",
    "Effect": "Allow",
    "Principal": "*",
    "Action": "s3:GetObject",
    "Resource": "arn:aws:s3:::freehold-spa/*"
  }]
}'
echo "S3 ready"

# SNS Topics (idempotent)
TOPIC_ARN=$($AWS sns create-topic --name order-notifications --query 'TopicArn' --output text)
echo "Created SNS topic: $TOPIC_ARN"

# Notification queue + subscribe to SNS
$AWS sqs create-queue --queue-name notification-queue
QUEUE_ARN=$($AWS sqs get-queue-attributes \
  --queue-url http://localhost:4566/000000000000/notification-queue \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' --output text)
$AWS sns subscribe \
  --topic-arn $TOPIC_ARN \
  --protocol sqs \
  --notification-endpoint $QUEUE_ARN
echo "Subscribed notification-queue to SNS topic"

# API Gateway v2 (HTTP API)
API_ID=$($AWS apigatewayv2 create-api \
  --name freehold-api \
  --protocol-type HTTP \
  --query 'ApiId' --output text)
echo "Created API Gateway: $API_ID"

# Integration: Production service
PROD_INT=$($AWS apigatewayv2 create-integration \
  --api-id $API_ID \
  --integration-type HTTP_PROXY \
  --integration-method ANY \
  --integration-uri http://production:8090/{proxy} \
  --payload-format-version 1.0 \
  --query 'IntegrationId' --output text)
echo "Created Production integration: $PROD_INT"

# Integration: Marketplace service
MARKET_INT=$($AWS apigatewayv2 create-integration \
  --api-id $API_ID \
  --integration-type HTTP_PROXY \
  --integration-method ANY \
  --integration-uri http://marketplace:8091/{proxy} \
  --payload-format-version 1.0 \
  --query 'IntegrationId' --output text)
echo "Created Market integration: $MARKET_INT"

# Routes
$AWS apigatewayv2 create-route \
  --api-id $API_ID \
  --route-key 'ANY /production/{proxy+}' \
  --target integrations/$PROD_INT
echo "Created route /production/{proxy+}"

$AWS apigatewayv2 create-route \
  --api-id $API_ID \
  --route-key 'ANY /marketplace/{proxy+}' \
  --target integrations/$MARKET_INT
echo "Created route /marketplace/{proxy+}"

# CloudFront distribution for SPA
CF_ID=$($AWS cloudfront create-distribution \
  --origin-domain-name freehold-spa.s3.amazonaws.com \
  --default-root-object index.html \
  --query 'Distribution.Id' --output text)
echo "Created CloudFront distribution: $CF_ID"

# Deploy
$AWS apigatewayv2 create-stage \
  --api-id $API_ID \
  --stage-name prod \
  --auto-deploy
echo "Deployed API Gateway to prod stage"

echo "=== MiniStack initialization complete ==="
