import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerUser } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './AuthPages.css';

function RegisterPage() {
  const [form, setForm] = useState({
    username: '', email: '', password: '', fullName: '', phone: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await registerUser(form);
      login(data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data || 'Registration failed. Try again.');
    }
    setLoading(false);
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2>Create Account</h2>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username *</label>
            <input name="username" value={form.username} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Email *</label>
            <input name="email" type="email" value={form.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Password *</label>
            <input name="password" type="password" value={form.password} onChange={handleChange} required minLength={6} />
          </div>
          <div className="form-group">
            <label>Full Name</label>
            <input name="fullName" value={form.fullName} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label>Phone</label>
            <input name="phone" value={form.phone} onChange={handleChange} />
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Creating...' : 'Sign Up'}
          </button>
        </form>
        <p className="auth-switch">
          Already have an account? <Link to="/login">Log In</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
