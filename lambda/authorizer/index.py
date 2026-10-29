import json
import base64
import os
import time
import boto3


def handler(event, context):
    """API Gateway v2 Lambda Authorizer (simple response format)."""
    headers = event.get('headers', {})
    request_context = event.get('requestContext', {})
    http_info = request_context.get('http', {})
    method = http_info.get('method', 'GET')
    path = event.get('rawPath', http_info.get('path', '/'))

    # Strip stage prefix (e.g. /prod/production/goods → /production/goods)
    stage = request_context.get('stage', '')
    if stage and stage != '$default' and path.startswith(f'/{stage}/'):
        path = path[len(f'/{stage}'):]

    # Extract Bearer token
    auth_header = headers.get('authorization', '') or headers.get('Authorization', '')
    token = auth_header[7:] if auth_header.startswith('Bearer ') else ''

    # No token → allow public GET, deny writes
    if not token:
        if method == 'GET':
            return _allow('anonymous', '')
        return _deny()

    # Decode JWT payload (base64, no signature check — local dev only)
    try:
        payload = _decode_jwt_payload(token)
    except Exception:
        return _deny()

    if payload.get('exp', 0) < time.time():
        return _deny()

    # One group per user (mutually exclusive roles)
    groups = payload.get('cognito:groups', [])
    if not groups:
        return _deny()

    user_group = groups[0]
    sub = payload.get('sub', '')

    # DynamoDB permission lookup
    allowed_routes = _get_allowed_routes(user_group)
    if allowed_routes is None:
        return _deny()

    if _is_route_allowed(method, path, allowed_routes):
        return _allow(sub, user_group)

    return _deny()


def _allow(sub, group):
    return {
        'isAuthorized': True,
        'context': {'sub': sub, 'groups': group}
    }


def _deny():
    return {'isAuthorized': False}


def _decode_jwt_payload(token):
    parts = token.split('.')
    if len(parts) != 3:
        raise ValueError('Invalid JWT format')
    payload_b64 = parts[1]
    padding = 4 - len(payload_b64) % 4
    if padding != 4:
        payload_b64 += '=' * padding
    return json.loads(base64.urlsafe_b64decode(payload_b64))


def _get_allowed_routes(group_name):
    endpoint_url = os.environ.get('AWS_ENDPOINT')
    kwargs = {'region_name': os.environ.get('AWS_REGION', 'us-east-1')}
    if endpoint_url:
        kwargs['endpoint_url'] = endpoint_url

    dynamodb = boto3.resource('dynamodb', **kwargs)
    table = dynamodb.Table(os.environ.get('DYNAMODB_TABLE', 'rbac-permissions'))

    try:
        response = table.get_item(Key={'group_name': group_name})
        item = response.get('Item')
        if not item:
            return None
        return item.get('routes', [])
    except Exception:
        return None


def _is_route_allowed(method, path, allowed_routes):
    for route in allowed_routes:
        if route == '*':
            return True
        parts = route.split(' ', 1)
        if len(parts) != 2:
            continue
        allowed_method, allowed_pattern = parts
        if allowed_method != method:
            continue
        if allowed_pattern.endswith('/*'):
            base = allowed_pattern[:-2]
            if path == base or path.startswith(base + '/'):
                return True
        elif path == allowed_pattern:
            return True
    return False
