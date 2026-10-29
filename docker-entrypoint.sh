#!/bin/sh
if [ -f /cognito/cognito-config.env ]; then
    export $(grep -v '^#' /cognito/cognito-config.env | grep -v '^$' | xargs)
fi
exec java -jar app.jar
