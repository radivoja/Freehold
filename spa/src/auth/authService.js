import { CognitoUserPool, CognitoUser, AuthenticationDetails } from 'amazon-cognito-identity-js';
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

            user.getUserAttributes((err, attrs) => {
                if (err) return resolve(null);
                const email = attrs.find(a => a.Name === 'email')?.Value;
                resolve({ email, username: user.getUsername() });
            });
        });
    });
}
