import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getToolById, createBorrowRequest } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './ToolDetailPage.css';

function ToolDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tool, setTool] = useState(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTool();
  }, [id]);

  async function loadTool() {
    try {
      const data = await getToolById(id);
      setTool(data);
    } catch (err) {
      setError('Tool not found');
    }
    setLoading(false);
  }

  async function handleBorrow(e) {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!user) {
      navigate('/login');
      return;
    }

    try {
      await createBorrowRequest({
        toolId: tool.id,
        startDate,
        endDate,
        message
      });
      setSuccess('Borrow request sent! The owner will review it.');
      setStartDate('');
      setEndDate('');
      setMessage('');
    } catch (err) {
      setError(err.response?.data || 'Failed to send request');
    }
  }

  if (loading) return <div className="detail-page"><p>Loading...</p></div>;
  if (!tool) return <div className="detail-page"><p>Tool not found</p></div>;

  const isOwner = user && user.userId === tool.ownerId;

  return (
    <div className="detail-page">
      <div className="detail-card">
        <div className="detail-header">
          <h1>{tool.name}</h1>
          <span className={`status ${tool.available ? 'available' : 'unavailable'}`}>
            {tool.available ? 'Available' : 'Currently Borrowed'}
          </span>
        </div>

        <div className="detail-info">
          <div className="info-row">
            <strong>Category:</strong> <span>{tool.category}</span>
          </div>
          <div className="info-row">
            <strong>Condition:</strong> <span>{tool.toolCondition}</span>
          </div>
          <div className="info-row">
            <strong>Owner:</strong> <span>{tool.ownerUsername}</span>
          </div>
        </div>

        <div className="detail-desc">
          <h3>Description</h3>
          <p>{tool.description || 'No description provided.'}</p>
        </div>

        {/* Borrow form - only show if not owner and tool is available */}
        {!isOwner && tool.available && (
          <div className="borrow-section">
            <h3>Request to Borrow</h3>
            {error && <div className="error-msg">{error}</div>}
            {success && <div className="success-msg">{success}</div>}
            <form onSubmit={handleBorrow}>
              <div className="date-row">
                <div className="form-group">
                  <label>Start Date</label>
                  <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} required />
                </div>
                <div className="form-group">
                  <label>End Date</label>
                  <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} required />
                </div>
              </div>
              <div className="form-group">
                <label>Message (optional)</label>
                <textarea
                  value={message}
                  onChange={e => setMessage(e.target.value)}
                  placeholder="Let the owner know what you need it for..."
                  rows={3}
                />
              </div>
              <button type="submit" className="btn-borrow">Send Request</button>
            </form>
          </div>
        )}

        {isOwner && (
          <p className="owner-note">This is your tool. You can manage it from your dashboard.</p>
        )}

        {!tool.available && !isOwner && (
          <p className="owner-note">This tool is currently borrowed. Check back later!</p>
        )}
      </div>
    </div>
  );
}

export default ToolDetailPage;
