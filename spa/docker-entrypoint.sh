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

export API_GATEWAY_URL="http://ministack:4566/restapis/${API_GATEWAY_ID}/prod/_user_request_"
envsubst '${API_GATEWAY_URL}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
