import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // check localStorage on mount
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const userId = localStorage.getItem('userId');
    if (token && username) {
      setUser({ token, username, role, userId: Number(userId) });
    }
    setLoading(false);
  }, []);

  function login(authData) {
    localStorage.setItem('token', authData.token);
    localStorage.setItem('username', authData.username);
    localStorage.setItem('role', authData.role);
    localStorage.setItem('userId', authData.userId);
    setUser({
      token: authData.token,
      username: authData.username,
      role: authData.role,
      userId: authData.userId
    });
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
