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
  --schema '[{"Name":"email","Required":true,"Mutable":true},{"Name":"custom:role","AttributeDataType":"String","Mutable":true}]' \
  --query 'UserPool.Id' --output text)
echo "Created User Pool: $POOL_ID"

# Cognito App Client (SPA - no secret)
CLIENT_ID=$($AWS cognito-idp create-user-pool-client \
  --user-pool-id $POOL_ID \
  --client-name freehold-spa \
  --no-generate-secret \
  --explicit-auth-flows ALLOW_USER_SRP_AUTH ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_PASSWORD_AUTH \
  --query 'UserPoolClient.ClientId' --output text)
echo "Created App Client: $CLIENT_ID"

# Persist Cognito config for other services
cat > /init/cognito-config.env <<EOF
COGNITO_USER_POOL_ID=$POOL_ID
COGNITO_CLIENT_ID=$CLIENT_ID
COGNITO_ENDPOINT=http://ministack:4566
COGNITO_ISSUER_URI=https://cognito-idp.us-east-1.amazonaws.com/$POOL_ID
COGNITO_JWK_SET_URI=http://ministack:4566/$POOL_ID/.well-known/jwks.json
EOF
echo "Wrote cognito-config.env"

# Cognito Groups (RBAC roles)
$AWS cognito-idp create-group --user-pool-id $POOL_ID --group-name LANDOWNER
$AWS cognito-idp create-group --user-pool-id $POOL_ID --group-name CUSTOMER
$AWS cognito-idp create-group --user-pool-id $POOL_ID --group-name ADMIN
echo "Created Cognito Groups: LANDOWNER, CUSTOMER, ADMIN"

# Post-confirmation Lambda trigger (assigns group based on custom:role)
python3 -c "import zipfile; z=zipfile.ZipFile('/tmp/post-confirmation.zip','w'); z.write('/lambda/post-confirmation/index.py','index.py'); z.close()"
POST_CONFIRM_ARN=$($AWS lambda create-function \
  --function-name freehold-post-confirmation \
  --runtime python3.11 \
  --handler index.handler \
  --zip-file fileb:///tmp/post-confirmation.zip \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --environment "Variables={AWS_ENDPOINT=http://ministack:4566,AWS_REGION=us-east-1}" \
  --query 'FunctionArn' --output text)
echo "Deployed post-confirmation Lambda: $POST_CONFIRM_ARN"

# Attach Lambda trigger to Cognito User Pool
$AWS cognito-idp update-user-pool \
  --user-pool-id $POOL_ID \
  --lambda-config "PostConfirmation=$POST_CONFIRM_ARN"
echo "Attached post-confirmation trigger to User Pool"

# Test users (one per role)
for USER_EMAIL in landowner@freehold.com customer@freehold.com admin@freehold.com; do
  $AWS cognito-idp admin-create-user \
    --user-pool-id $POOL_ID \
    --username "$USER_EMAIL" \
    --temporary-password 'Test1234!' \
    --user-attributes Name=email,Value="$USER_EMAIL" Name=email_verified,Value=true \
    --message-action SUPPRESS

  $AWS cognito-idp admin-set-user-password \
    --user-pool-id $POOL_ID \
    --username "$USER_EMAIL" \
    --password 'Test1234!' \
    --permanent
done

# Assign users to groups
$AWS cognito-idp admin-add-user-to-group --user-pool-id $POOL_ID --username landowner@freehold.com --group-name LANDOWNER
$AWS cognito-idp admin-add-user-to-group --user-pool-id $POOL_ID --username customer@freehold.com --group-name CUSTOMER
$AWS cognito-idp admin-add-user-to-group --user-pool-id $POOL_ID --username admin@freehold.com --group-name ADMIN
echo "Created test users: landowner/customer/admin @freehold.com (Test1234!)"

# SQS Queues
$AWS sqs create-queue --queue-name production-orders
$AWS sqs create-queue --queue-name production-results
echo "Created SQS queues"

# DynamoDB Tables (idempotent)
$AWS dynamodb create-table --table-name audit-log \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST 2>/dev/null || echo "audit-log table already exists"

$AWS dynamodb create-table --table-name rbac-permissions \
  --attribute-definitions AttributeName=group_name,AttributeType=S \
  --key-schema AttributeName=group_name,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST 2>/dev/null || echo "rbac-permissions table already exists"

# Seed RBAC permission rows
$AWS dynamodb put-item --table-name rbac-permissions --item '{
  "group_name": {"S": "LANDOWNER"},
  "routes": {"L": [
    {"S": "GET /production/*"},
    {"S": "PUT /production/*"},
    {"S": "POST /production/*"},
    {"S": "DELETE /production/*"},
    {"S": "GET /marketplace/*"}
  ]}
}'
$AWS dynamodb put-item --table-name rbac-permissions --item '{
  "group_name": {"S": "CUSTOMER"},
  "routes": {"L": [
    {"S": "GET /production/*"},
    {"S": "GET /marketplace/*"},
    {"S": "POST /order/*"},
    {"S": "GET /order/*"}
  ]}
}'
$AWS dynamodb put-item --table-name rbac-permissions --item '{
  "group_name": {"S": "ADMIN"},
  "routes": {"L": [
    {"S": "*"}
  ]}
}'
echo "DynamoDB ready (audit-log + rbac-permissions seeded)"

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

# Integration: Order service
ORDER_INT=$($AWS apigatewayv2 create-integration \
  --api-id $API_ID \
  --integration-type HTTP_PROXY \
  --integration-method ANY \
  --integration-uri http://order:8093/{proxy} \
  --payload-format-version 1.0 \
  --query 'IntegrationId' --output text)
echo "Created Order integration: $ORDER_INT"

# Lambda Authorizer function
python3 -c "import zipfile; z=zipfile.ZipFile('/tmp/authorizer.zip','w'); z.write('/lambda/authorizer/index.py','index.py'); z.close()"
LAMBDA_ARN=$($AWS lambda create-function \
  --function-name freehold-authorizer \
  --runtime python3.11 \
  --handler index.handler \
  --zip-file fileb:///tmp/authorizer.zip \
  --role arn:aws:iam::000000000000:role/lambda-role \
  --environment "Variables={AWS_ENDPOINT=http://ministack:4566,AWS_REGION=us-east-1,DYNAMODB_TABLE=rbac-permissions}" \
  --query 'FunctionArn' --output text)
echo "Deployed Lambda Authorizer: $LAMBDA_ARN"

# Lambda Authorizer on API Gateway (replaces JWT Authorizer)
AUTHORIZER_ID=$($AWS apigatewayv2 create-authorizer \
  --api-id $API_ID \
  --authorizer-type REQUEST \
  --authorizer-uri "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/$LAMBDA_ARN/invocations" \
  --authorizer-payload-format-version 2.0 \
  --enable-simple-responses \
  --identity-source '$request.header.Authorization' \
  --name rbac-authorizer \
  --query 'AuthorizerId' --output text)
echo "Created Lambda Authorizer: $AUTHORIZER_ID"

# Routes (all protected by Lambda Authorizer)
$AWS apigatewayv2 create-route \
  --api-id $API_ID \
  --route-key 'ANY /production/{proxy+}' \
  --target integrations/$PROD_INT \
  --authorization-type CUSTOM \
  --authorizer-id $AUTHORIZER_ID
echo "Created route /production/{proxy+} (Lambda authorizer)"

$AWS apigatewayv2 create-route \
  --api-id $API_ID \
  --route-key 'ANY /marketplace/{proxy+}' \
  --target integrations/$MARKET_INT \
  --authorization-type CUSTOM \
  --authorizer-id $AUTHORIZER_ID
echo "Created route /marketplace/{proxy+} (Lambda authorizer)"

$AWS apigatewayv2 create-route \
  --api-id $API_ID \
  --route-key 'ANY /order/{proxy+}' \
  --target integrations/$ORDER_INT \
  --authorization-type CUSTOM \
  --authorizer-id $AUTHORIZER_ID
echo "Created route /order/{proxy+} (Lambda authorizer)"

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
