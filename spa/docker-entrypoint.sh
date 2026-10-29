#!/bin/sh
if [ -f /cognito/cognito-config.env ]; then
    export $(grep -v '^#' /cognito/cognito-config.env | grep -v '^$' | xargs)
fi
BROWSER_ENDPOINT=$(echo "$COGNITO_ENDPOINT" | sed 's|ministack|localhost|')
cat > /usr/share/nginx/html/config.json <<EOF
{
  "userPoolId": "${COGNITO_USER_POOL_ID}",
  "clientId": "${COGNITO_CLIENT_ID}",
  "endpoint": "${BROWSER_ENDPOINT}"
}
EOF
exec nginx -g 'daemon off;'
