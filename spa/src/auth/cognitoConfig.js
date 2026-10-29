let config = null;

export async function loadCognitoConfig() {
    const res = await fetch('/config.json');
    config = await res.json();
    return config;
}

export function getCognitoConfig() {
    if (!config) throw new Error('Cognito config not loaded — call loadCognitoConfig() first');
    return config;
}
