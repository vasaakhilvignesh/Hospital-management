import React, { useState, useEffect } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/modules';
import {
  LayoutDashboard, Users, Stethoscope, UserCheck, Calendar,
  Pill, FileText, Receipt, BarChart3, Bell, LogOut, Activity
} from 'lucide-react';

export const AppLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState<number>(0);

  useEffect(() => {
    if (user) {
      notificationService.getUnreadCount()
        .then(res => setUnreadCount(res.unreadCount))
        .catch(() => {});
    }
  }, [user]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const role = user?.role;

  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <Activity size={26} color="#3b82f6" />
          <span className="sidebar-logo">CarePulse HMS</span>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/" end className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} />
            <span>Dashboard</span>
          </NavLink>

          {role !== 'PATIENT' && (
            <NavLink to="/patients" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Users size={18} />
              <span>Patients</span>
            </NavLink>
          )}

          {role !== 'PATIENT' && (
            <NavLink to="/doctors" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Stethoscope size={18} />
              <span>Doctors</span>
            </NavLink>
          )}

          {(role === 'ADMIN' || role === 'RECEPTIONIST') && (
            <NavLink to="/staff" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <UserCheck size={18} />
              <span>Staff</span>
            </NavLink>
          )}

          <NavLink to="/appointments" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Calendar size={18} />
            <span>Appointments</span>
          </NavLink>

          {role !== 'PATIENT' && (
            <NavLink to="/pharmacy" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
              <Pill size={18} />
              <span>Pharmacy</span>
            </NavLink>
          )}

          <NavLink to="/prescriptions" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <FileText size={18} />
            <span>Prescriptions</span>
          </NavLink>

          <NavLink to="/billing" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Receipt size={18} />
            <span>Billing</span>
          </NavLink>

          <NavLink to="/reports" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <BarChart3 size={18} />
            <span>Reports</span>
          </NavLink>

          <NavLink to="/notifications" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Bell size={18} />
            <span>Notifications</span>
            {unreadCount > 0 && (
              <span className="badge badge-danger" style={{ marginLeft: 'auto', padding: '1px 6px', fontSize: '0.7rem' }}>
                {unreadCount}
              </span>
            )}
          </NavLink>
        </nav>
      </aside>

      {/* Main Content Area */}
      <div className="main-content">
        <header className="top-bar">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.95rem' }}>
              Hospital Care System
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.875rem', fontWeight: 600 }}>{user?.fullName}</div>
              <span className="badge badge-info" style={{ textTransform: 'capitalize' }}>
                {user?.role.toLowerCase()}
              </span>
            </div>

            <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Log out">
              <LogOut size={16} />
              <span>Logout</span>
            </button>
          </div>
        </header>

        <main className="page-body">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
