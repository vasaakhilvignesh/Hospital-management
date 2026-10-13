import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/modules';
import { Notification } from '../types';
import { Bell, Check, CheckCheck } from 'lucide-react';

export const NotificationsPage: React.FC = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  const loadNotifications = async () => {
    try {
      setLoading(true);
      const data = await notificationService.getNotifications();
      setNotifications(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, [user]);

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      loadNotifications();
    } catch (err: any) {
      alert(err.message || 'Failed to update notification');
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      loadNotifications();
    } catch (err: any) {
      alert(err.message || 'Failed to update notifications');
    }
  };

  const unreadCount = notifications.filter(n => n.status === 'UNREAD').length;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Notifications</h1>
          <p className="page-subtitle">Hospital updates, appointment reminders, and billing receipts</p>
        </div>
        {unreadCount > 0 && (
          <button onClick={handleMarkAllRead} className="btn btn-secondary">
            <CheckCheck size={16} />
            <span>Mark All as Read</span>
          </button>
        )}
      </div>

      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {loading ? (
          <div className="state-box">Loading notifications...</div>
        ) : notifications.length === 0 ? (
          <div className="state-box">
            <Bell size={36} color="#94a3b8" />
            <p>You have no notifications at this time.</p>
          </div>
        ) : (
          notifications.map(n => (
            <div
              key={n.id}
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'space-between',
                padding: '16px',
                borderRadius: 'var(--radius-sm)',
                border: '1px solid var(--border)',
                backgroundColor: n.status === 'UNREAD' ? 'var(--primary-light)' : '#ffffff',
                gap: '16px'
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
                  <h4 style={{ fontSize: '0.95rem', fontWeight: 600 }}>{n.title}</h4>
                  <span className="badge badge-info">{n.type}</span>
                  {n.status === 'UNREAD' && <span className="badge badge-danger">New</span>}
                </div>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '8px' }}>
                  {n.message}
                </p>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-light)' }}>
                  Received: {new Date(n.createdAt).toLocaleString()}
                </div>
              </div>

              {n.status === 'UNREAD' && (
                <button
                  onClick={() => handleMarkAsRead(n.id)}
                  className="btn btn-secondary btn-sm"
                  title="Mark as Read"
                >
                  <Check size={14} />
                  <span>Mark Read</span>
                </button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};
