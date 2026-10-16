#!/bin/bash
echo "Creating SQS queues..."

awslocal sqs create-queue --queue-name production-orders
awslocal sqs create-queue --queue-name production-results

echo "SQS queues created:"
awslocal sqs list-queues