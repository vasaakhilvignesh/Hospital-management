import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Role } from '../types';

interface ProtectedRouteProps {
  allowedRoles?: Role[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="state-box">Loading session...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return (
      <div className="page-body">
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <h2 style={{ color: 'var(--danger)', marginBottom: '10px' }}>Access Denied (403)</h2>
          <p style={{ color: 'var(--text-muted)' }}>You do not have permission to access this module.</p>
        </div>
      </div>
    );
  }

  return <Outlet />;
};
