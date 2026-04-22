import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addTool } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './AuthPages.css';

function AddToolPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '', description: '', category: 'Power Tools', toolCondition: 'GOOD'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!user) {
    navigate('/login');
    return null;
  }

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await addTool(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data || 'Failed to add tool');
    }
    setLoading(false);
  }

  return (
    <div className="auth-page">
      <div className="auth-card" style={{ maxWidth: '500px' }}>
        <h2>List a Tool</h2>
        <p style={{ textAlign: 'center', color: '#7f8c8d', marginBottom: '1rem' }}>
          Share a tool with your community
        </p>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Tool Name *</label>
            <input name="name" value={form.name} onChange={handleChange} required
                   placeholder="e.g. Bosch Power Drill" />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea name="description" value={form.description} onChange={handleChange}
                      placeholder="Describe the tool, what it includes..." rows={3} />
          </div>
          <div className="form-group">
            <label>Category *</label>
            <select name="category" value={form.category} onChange={handleChange}>
              <option value="Power Tools">Power Tools</option>
              <option value="Garden">Garden</option>
              <option value="Kitchen">Kitchen</option>
              <option value="Camping">Camping</option>
              <option value="Other">Other</option>
            </select>
          </div>
          <div className="form-group">
            <label>Condition</label>
            <select name="toolCondition" value={form.toolCondition} onChange={handleChange}>
              <option value="NEW">New</option>
              <option value="GOOD">Good</option>
              <option value="FAIR">Fair</option>
              <option value="WORN">Worn</option>
            </select>
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Adding...' : 'Add Tool'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default AddToolPage;
