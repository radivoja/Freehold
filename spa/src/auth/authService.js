import { CognitoUserPool, CognitoUser, AuthenticationDetails, CognitoUserAttribute } from 'amazon-cognito-identity-js';
import { getCognitoConfig } from './cognitoConfig';

function getUserPool() {
    const { userPoolId, clientId, endpoint } = getCognitoConfig();
    return new CognitoUserPool({
        UserPoolId: userPoolId,
        ClientId: clientId,
        endpoint: endpoint,
    });
}

export function signIn(email, password) {
    return new Promise((resolve, reject) => {
        const user = new CognitoUser({
            Username: email,
            Pool: getUserPool(),
        });

        const authDetails = new AuthenticationDetails({
            Username: email,
            Password: password,
        });

        user.authenticateUser(authDetails, {
            onSuccess: (session) => resolve(session),
            onFailure: (err) => reject(err),
        });
    });
}

export function signUp(email, password, role) {
    return new Promise((resolve, reject) => {
        const pool = getUserPool();
        const attributes = [
            new CognitoUserAttribute({ Name: 'email', Value: email }),
            new CognitoUserAttribute({ Name: 'custom:role', Value: role }),
        ];

        pool.signUp(email, password, attributes, null, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

export function confirmAccount(email, code) {
    return new Promise((resolve, reject) => {
        const user = new CognitoUser({ Username: email, Pool: getUserPool() });
        user.confirmRegistration(code, true, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

export function signOut() {
    const user = getUserPool().getCurrentUser();
    if (user) user.signOut();
}

export function getIdToken() {
    return new Promise((resolve, reject) => {
        const user = getUserPool().getCurrentUser();
        if (!user) return resolve(null);

        user.getSession((err, session) => {
            if (err || !session?.isValid()) return resolve(null);
            resolve(session.getIdToken().getJwtToken());
        });
    });
}

export function getCurrentUser() {
    return new Promise((resolve) => {
        const user = getUserPool().getCurrentUser();
        if (!user) return resolve(null);

        user.getSession((err, session) => {
            if (err || !session?.isValid()) return resolve(null);

            const idToken = session.getIdToken().getJwtToken();
            const payload = JSON.parse(atob(idToken.split('.')[1]));
            const groups = payload['cognito:groups'] || [];
            const role = groups[0] || null;

            user.getUserAttributes((err, attrs) => {
                if (err) return resolve(null);
                const email = attrs.find(a => a.Name === 'email')?.Value;
                resolve({ email, username: user.getUsername(), role });
            });
        });
    });
}
