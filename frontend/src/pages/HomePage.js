import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getTools } from '../services/api';
import './HomePage.css';

function HomePage() {
  const [tools, setTools] = useState([]);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [loading, setLoading] = useState(true);

  const categories = ['Power Tools', 'Garden', 'Kitchen', 'Camping', 'Other'];

  useEffect(() => {
    loadTools();
  }, []);

  async function loadTools() {
    setLoading(true);
    try {
      const data = await getTools(search, category);
      setTools(data);
    } catch (err) {
      console.error('Failed to load tools:', err);
    }
    setLoading(false);
  }

  function handleSearch(e) {
    e.preventDefault();
    loadTools();
  }

  function handleCategoryClick(cat) {
    if (category === cat) {
      setCategory('');
    } else {
      setCategory(cat);
    }
    // trigger search after state updates
    setTimeout(() => loadTools(), 50);
  }

  function conditionBadge(condition) {
    const colors = { NEW: '#27ae60', GOOD: '#2980b9', FAIR: '#f39c12', WORN: '#e74c3c' };
    return (
      <span className="badge" style={{ background: colors[condition] || '#95a5a6' }}>
        {condition}
      </span>
    );
  }

  return (
    <div className="home-page">
      <div className="hero-section">
        <h1>Share Tools, Save Money</h1>
        <p>Borrow tools from your neighbors instead of buying them</p>
      </div>

      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <input
            type="text"
            placeholder="Search tools (e.g. drill, ladder, tent...)"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <button type="submit">Search</button>
        </form>

        <div className="category-filters">
          {categories.map(cat => (
            <button
              key={cat}
              className={`cat-btn ${category === cat ? 'active' : ''}`}
              onClick={() => handleCategoryClick(cat)}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <p className="loading-text">Loading tools...</p>
      ) : tools.length === 0 ? (
        <p className="no-results">No tools found. Try a different search.</p>
      ) : (
        <div className="tools-grid">
          {tools.map(tool => (
            <div key={tool.id} className="tool-card">
              <div className="tool-card-header">
                <h3>{tool.name}</h3>
                {conditionBadge(tool.toolCondition)}
              </div>
              <p className="tool-desc">{tool.description}</p>
              <div className="tool-meta">
                <span className="tool-category">{tool.category}</span>
                <span className={`tool-status ${tool.available ? 'available' : 'unavailable'}`}>
                  {tool.available ? 'Available' : 'Borrowed'}
                </span>
              </div>
              <p className="tool-owner">Listed by: {tool.ownerUsername}</p>
              <Link to={`/tools/${tool.id}`} className="btn-view">View Details</Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default HomePage;
