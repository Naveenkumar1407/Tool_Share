import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getMyTools, deleteTool, getIncomingRequests, getMyBorrowRequests, approveBorrow, rejectBorrow, markReturned } from '../services/api';
import { useAuth } from '../context/AuthContext';
import './DashboardPage.css';

function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [myTools, setMyTools] = useState([]);
  const [incoming, setIncoming] = useState([]);
  const [outgoing, setOutgoing] = useState([]);
  const [activeTab, setActiveTab] = useState('tools');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    loadData();
  }, [user]);

  async function loadData() {
    setLoading(true);
    try {
      const [tools, inc, out] = await Promise.all([
        getMyTools(),
        getIncomingRequests(),
        getMyBorrowRequests()
      ]);
      setMyTools(tools);
      setIncoming(inc);
      setOutgoing(out);
    } catch (err) {
      console.error('Error loading dashboard:', err);
    }
    setLoading(false);
  }

  async function handleDelete(toolId) {
    if (!window.confirm('Are you sure you want to remove this tool?')) return;
    try {
      await deleteTool(toolId);
      setMyTools(myTools.filter(t => t.id !== toolId));
    } catch (err) {
      alert(err.response?.data || 'Delete failed');
    }
  }

  async function handleApprove(requestId) {
    try {
      await approveBorrow(requestId);
      loadData();
    } catch (err) {
      alert(err.response?.data || 'Approve failed');
    }
  }

  async function handleReject(requestId) {
    try {
      await rejectBorrow(requestId);
      loadData();
    } catch (err) {
      alert(err.response?.data || 'Reject failed');
    }
  }

  async function handleReturn(requestId) {
    try {
      await markReturned(requestId);
      loadData();
    } catch (err) {
      alert(err.response?.data || 'Return failed');
    }
  }

  function statusBadge(status) {
    const colors = {
      PENDING: '#f39c12', APPROVED: '#27ae60',
      REJECTED: '#e74c3c', RETURNED: '#95a5a6'
    };
    return <span className="req-badge" style={{ background: colors[status] }}>{status}</span>;
  }

  if (loading) return <div className="dashboard"><p>Loading dashboard...</p></div>;

  return (
    <div className="dashboard">
      <h1>My Dashboard</h1>
      <p className="welcome-text">Welcome back, {user?.username}!</p>

      <div className="stats-row">
        <div className="stat-card">
          <div className="stat-num">{myTools.length}</div>
          <div className="stat-label">My Tools</div>
        </div>
        <div className="stat-card">
          <div className="stat-num">{incoming.filter(r => r.status === 'PENDING').length}</div>
          <div className="stat-label">Pending Requests</div>
        </div>
        <div className="stat-card">
          <div className="stat-num">{outgoing.filter(r => r.status === 'APPROVED').length}</div>
          <div className="stat-label">Active Borrows</div>
        </div>
      </div>

      <div className="tab-bar">
        <button className={activeTab === 'tools' ? 'tab active' : 'tab'} onClick={() => setActiveTab('tools')}>
          My Tools ({myTools.length})
        </button>
        <button className={activeTab === 'incoming' ? 'tab active' : 'tab'} onClick={() => setActiveTab('incoming')}>
          Incoming Requests ({incoming.length})
        </button>
        <button className={activeTab === 'outgoing' ? 'tab active' : 'tab'} onClick={() => setActiveTab('outgoing')}>
          My Borrows ({outgoing.length})
        </button>
      </div>

      {/* My Tools Tab */}
      {activeTab === 'tools' && (
        <div className="tab-content">
          {myTools.length === 0 ? (
            <p className="empty-msg">You haven't listed any tools yet. <Link to="/tools/add">Add one now!</Link></p>
          ) : (
            <div className="list">
              {myTools.map(tool => (
                <div key={tool.id} className="list-item">
                  <div className="item-info">
                    <strong>{tool.name}</strong>
                    <span className="item-cat">{tool.category}</span>
                    <span className={tool.available ? 'avail-yes' : 'avail-no'}>
                      {tool.available ? 'Available' : 'Borrowed'}
                    </span>
                  </div>
                  <div className="item-actions">
                    <Link to={`/tools/${tool.id}`} className="btn-sm btn-info">View</Link>
                    <button onClick={() => handleDelete(tool.id)} className="btn-sm btn-danger">Remove</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Incoming Requests Tab */}
      {activeTab === 'incoming' && (
        <div className="tab-content">
          {incoming.length === 0 ? (
            <p className="empty-msg">No borrow requests for your tools yet.</p>
          ) : (
            <div className="list">
              {incoming.map(req => (
                <div key={req.id} className="list-item">
                  <div className="item-info">
                    <strong>{req.tool?.name}</strong>
                    <span>from: {req.borrowerUsername}</span>
                    <span>{req.startDate} to {req.endDate}</span>
                    {statusBadge(req.status)}
                  </div>
                  <div className="item-actions">
                    {req.status === 'PENDING' && (
                      <>
                        <button onClick={() => handleApprove(req.id)} className="btn-sm btn-success">Approve</button>
                        <button onClick={() => handleReject(req.id)} className="btn-sm btn-danger">Reject</button>
                      </>
                    )}
                    {req.status === 'APPROVED' && (
                      <button onClick={() => handleReturn(req.id)} className="btn-sm btn-info">Mark Returned</button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* My Borrows Tab */}
      {activeTab === 'outgoing' && (
        <div className="tab-content">
          {outgoing.length === 0 ? (
            <p className="empty-msg">You haven't requested any tools. <Link to="/">Browse tools</Link></p>
          ) : (
            <div className="list">
              {outgoing.map(req => (
                <div key={req.id} className="list-item">
                  <div className="item-info">
                    <strong>{req.tool?.name}</strong>
                    <span>{req.startDate} to {req.endDate}</span>
                    {statusBadge(req.status)}
                  </div>
                  <div className="item-actions">
                    {req.status === 'APPROVED' && (
                      <button onClick={() => handleReturn(req.id)} className="btn-sm btn-info">Return Tool</button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default DashboardPage;
