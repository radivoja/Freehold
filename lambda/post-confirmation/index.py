import os
import boto3


def handler(event, context):
    """Cognito post-confirmation trigger — assigns user to group based on custom:role."""
    user_attrs = event['request']['userAttributes']
    role = user_attrs.get('custom:role', '')

    if role not in ('LANDOWNER', 'CUSTOMER'):
        return event

    endpoint_url = os.environ.get('AWS_ENDPOINT')
    kwargs = {'region_name': os.environ.get('AWS_REGION', 'us-east-1')}
    if endpoint_url:
        kwargs['endpoint_url'] = endpoint_url

    client = boto3.client('cognito-idp', **kwargs)
    client.admin_add_user_to_group(
        UserPoolId=event['userPoolId'],
        Username=event['userName'],
        GroupName=role,
    )

    return event
