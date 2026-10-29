import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { signUp, confirmAccount } from '../auth/authService'

export default function Register() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPw, setConfirmPw] = useState('')
    const [role, setRole] = useState('CUSTOMER')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const [needsConfirm, setNeedsConfirm] = useState(false)
    const [code, setCode] = useState('')
    const navigate = useNavigate()

    async function handleSignUp(e) {
        e.preventDefault()
        setError('')
        if (password !== confirmPw) {
            setError('Passwords do not match')
            return
        }
        setLoading(true)
        try {
            const result = await signUp(email, password, role)
            if (result.userConfirmed) {
                navigate('/login')
            } else {
                setNeedsConfirm(true)
            }
        } catch (err) {
            setError(err.message || 'Registration failed')
        } finally {
            setLoading(false)
        }
    }

    async function handleConfirm(e) {
        e.preventDefault()
        setError('')
        setLoading(true)
        try {
            await confirmAccount(email, code)
            navigate('/login')
        } catch (err) {
            setError(err.message || 'Confirmation failed')
        } finally {
            setLoading(false)
        }
    }

    if (needsConfirm) {
        return (
            <div className="login-page">
                <div className="login-card">
                    <h1>Freehold</h1>
                    <p className="login-subtitle">Confirm your account</p>
                    <form onSubmit={handleConfirm}>
                        <label>
                            Confirmation Code
                            <input
                                type="text"
                                value={code}
                                onChange={e => setCode(e.target.value)}
                                placeholder="Enter code from email"
                                required
                            />
                        </label>
                        {error && <p className="error">{error}</p>}
                        <button type="submit" disabled={loading}>
                            {loading ? 'Confirming...' : 'Confirm'}
                        </button>
                    </form>
                    <p className="auth-link">
                        <Link to="/login">Back to Sign In</Link>
                    </p>
                </div>
            </div>
        )
    }

    return (
        <div className="login-page">
            <div className="login-card">
                <h1>Freehold</h1>
                <p className="login-subtitle">Create your account</p>
                <form onSubmit={handleSignUp}>
                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            required
                        />
                    </label>
                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            placeholder="Min 8 chars, uppercase, number"
                            required
                        />
                    </label>
                    <label>
                        Confirm Password
                        <input
                            type="password"
                            value={confirmPw}
                            onChange={e => setConfirmPw(e.target.value)}
                            required
                        />
                    </label>
                    <fieldset className="role-picker">
                        <legend>I am a</legend>
                        <label className="role-option">
                            <input
                                type="radio"
                                name="role"
                                value="CUSTOMER"
                                checked={role === 'CUSTOMER'}
                                onChange={e => setRole(e.target.value)}
                            />
                            <span className="role-label">Customer</span>
                            <span className="role-desc">Browse goods and place orders</span>
                        </label>
                        <label className="role-option">
                            <input
                                type="radio"
                                name="role"
                                value="LANDOWNER"
                                checked={role === 'LANDOWNER'}
                                onChange={e => setRole(e.target.value)}
                            />
                            <span className="role-label">Landowner</span>
                            <span className="role-desc">Manage and sell your own goods</span>
                        </label>
                    </fieldset>
                    {error && <p className="error">{error}</p>}
                    <button type="submit" disabled={loading}>
                        {loading ? 'Creating account...' : 'Create Account'}
                    </button>
                </form>
                <p className="auth-link">
                    Already have an account? <Link to="/login">Sign In</Link>
                </p>
            </div>
        </div>
    )
}
