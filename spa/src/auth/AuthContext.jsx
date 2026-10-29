import React, { createContext, useContext, useState, useEffect } from 'react';
import { signIn as cognitoSignIn, signOut as cognitoSignOut, getCurrentUser } from './authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getCurrentUser()
            .then(setUser)
            .finally(() => setLoading(false));
    }, []);

    async function login(email, password) {
        await cognitoSignIn(email, password);
        const u = await getCurrentUser();
        setUser(u);
    }

    function logout() {
        cognitoSignOut();
        setUser(null);
    }

    const role = user?.role || null;

    return (
        <AuthContext.Provider value={{ user, role, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be inside AuthProvider');
    return ctx;
}
