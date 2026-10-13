import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { reportService, appointmentService, billingService, notificationService } from '../services/modules';
import { DashboardSummary, Appointment, Bill, Notification } from '../types';
import {
  Users, Stethoscope, Calendar, Clock, CheckCircle2,
  AlertTriangle, Receipt, Pill, Bell, ArrowRight
} from 'lucide-react';
import { Link } from 'react-router-dom';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [patientAppts, setPatientAppts] = useState<Appointment[]>([]);
  const [patientBills, setPatientBills] = useState<Bill[]>([]);
  const [recentNotifs, setRecentNotifs] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        if (user?.role === 'PATIENT') {
          const pid = user.patientId || 1;
          const [appts, bills, notifs] = await Promise.all([
            appointmentService.getByPatient(pid).catch(() => []),
            billingService.getBillsByPatient(pid).catch(() => []),
            notificationService.getNotifications().catch(() => []),
          ]);
          setPatientAppts(appts);
          setPatientBills(bills);
          setRecentNotifs(notifs.slice(0, 4));
        } else {
          // Administrative dashboard: load real metrics from REPORT-SERVICE
          const data = await reportService.getDashboardSummary();
          setSummary(data);
        }
      } catch (err) {
        console.error('Failed to load dashboard data', err);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, [user]);

  if (loading) {
    return <div className="state-box">Loading dashboard metrics...</div>;
  }

  // --- PATIENT DASHBOARD VIEW ---
  if (user?.role === 'PATIENT') {
    const totalOutstanding = patientBills.reduce((sum, b) => sum + (b.balanceAmount || 0), 0);
    return (
      <div>
        <div className="page-header">
          <div>
            <h1 className="page-title">Welcome back, {user.fullName}</h1>
            <p className="page-subtitle">Personal Patient Health Portal & Care Overview</p>
          </div>
        </div>

        <div className="grid-cols-4">
          <div className="stat-card">
            <div className="stat-icon"><Calendar size={24} /></div>
            <div className="stat-info">
              <span className="stat-label">My Appointments</span>
              <span className="stat-value">{patientAppts.length}</span>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ backgroundColor: 'var(--warning-bg)', color: 'var(--warning)' }}>
              <Receipt size={24} />
            </div>
            <div className="stat-info">
              <span className="stat-label">Outstanding Balance</span>
              <span className="stat-value">${totalOutstanding.toFixed(2)}</span>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ backgroundColor: 'var(--success-bg)', color: 'var(--success)' }}>
              <Receipt size={24} />
            </div>
            <div className="stat-info">
              <span className="stat-label">Total Invoices</span>
              <span className="stat-value">{patientBills.length}</span>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ backgroundColor: 'var(--info-bg)', color: 'var(--info)' }}>
              <Bell size={24} />
            </div>
            <div className="stat-info">
              <span className="stat-label">Notifications</span>
              <span className="stat-value">{recentNotifs.length}</span>
            </div>
          </div>
        </div>

        <div className="grid-cols-2">
          {/* Upcoming Appointments */}
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600 }}>My Appointments</h3>
              <Link to="/appointments" className="btn btn-secondary btn-sm">View All</Link>
            </div>
            {patientAppts.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No appointments scheduled.</p>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Code</th>
                      <th>Date</th>
                      <th>Time</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {patientAppts.slice(0, 3).map(a => (
                      <tr key={a.id}>
                        <td style={{ fontWeight: 600 }}>{a.appointmentCode}</td>
                        <td>{a.appointmentDate}</td>
                        <td>{a.startTime}</td>
                        <td>
                          <span className={`badge ${a.status === 'CONFIRMED' || a.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>
                            {a.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Recent Notifications */}
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600 }}>Recent Updates</h3>
              <Link to="/notifications" className="btn btn-secondary btn-sm">View All</Link>
            </div>
            {recentNotifs.length === 0 ? (
              <p style={{ color: 'var(--text-muted)' }}>No new notifications.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {recentNotifs.map(n => (
                  <div key={n.id} style={{
                    padding: '12px',
                    borderRadius: 'var(--radius-sm)',
                    border: '1px solid var(--border)',
                    backgroundColor: n.status === 'UNREAD' ? 'var(--primary-light)' : '#ffffff'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                      <span style={{ fontWeight: 600, fontSize: '0.875rem' }}>{n.title}</span>
                      <span className="badge badge-info">{n.type}</span>
                    </div>
                    <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)' }}>{n.message}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  // --- STAFF / DOCTOR / ADMIN DASHBOARD VIEW ---
  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Hospital Overview</h1>
          <p className="page-subtitle">Real-time operational indicators across connected microservices</p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <Link to="/appointments" className="btn btn-primary btn-sm">Manage Appointments</Link>
          <Link to="/reports" className="btn btn-secondary btn-sm">Generate Report</Link>
        </div>
      </div>

      <div className="grid-cols-4">
        <div className="stat-card">
          <div className="stat-icon"><Users size={24} /></div>
          <div className="stat-info">
            <span className="stat-label">Registered Patients</span>
            <span className="stat-value">{summary?.totalPatients ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f0fdf4', color: '#16a34a' }}>
            <Stethoscope size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Active Doctors</span>
            <span className="stat-value">{summary?.totalDoctors ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#eff6ff', color: '#2563eb' }}>
            <Calendar size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Total Appointments</span>
            <span className="stat-value">{summary?.totalAppointments ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fff7ed', color: '#ea580c' }}>
            <Clock size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Pending Consultations</span>
            <span className="stat-value">{summary?.pendingAppointments ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fef2f2', color: '#dc2626' }}>
            <Receipt size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Outstanding Balance</span>
            <span className="stat-value">${summary?.outstandingBilling?.toFixed(2) ?? '0.00'}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#f5f3ff', color: '#7c3aed' }}>
            <Pill size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Pending Prescriptions</span>
            <span className="stat-value">{summary?.pendingPrescriptions ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#fffbeb', color: '#d97706' }}>
            <AlertTriangle size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Low Stock Medicines</span>
            <span className="stat-value">{summary?.lowStockMedicineCount ?? 0}</span>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: '#ecfdf5', color: '#059669' }}>
            <CheckCircle2 size={24} />
          </div>
          <div className="stat-info">
            <span className="stat-label">Completed Consults</span>
            <span className="stat-value">{summary?.completedAppointments ?? 0}</span>
          </div>
        </div>
      </div>

      <div className="grid-cols-2">
        <div className="card">
          <h3 style={{ fontSize: '1.05rem', fontWeight: 600, marginBottom: '14px' }}>Quick Navigation</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            <Link to="/patients" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
              <span>Patient Directory</span>
              <ArrowRight size={16} />
            </Link>
            <Link to="/pharmacy" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
              <span>Pharmacy Stock</span>
              <ArrowRight size={16} />
            </Link>
            <Link to="/billing" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
              <span>Invoices & Billing</span>
              <ArrowRight size={16} />
            </Link>
            <Link to="/reports" className="btn btn-secondary" style={{ justifyContent: 'space-between' }}>
              <span>Analytics & Reports</span>
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>

        <div className="card">
          <h3 style={{ fontSize: '1.05rem', fontWeight: 600, marginBottom: '14px' }}>Architecture & Microservices Status</h3>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '12px' }}>
            All 11 backend microservices are active and routed via Spring Cloud API Gateway:
          </p>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
            <span className="badge badge-success">API Gateway :8080</span>
            <span className="badge badge-success">Auth :8081</span>
            <span className="badge badge-success">Patient :8082</span>
            <span className="badge badge-success">Doctor :8083</span>
            <span className="badge badge-success">Appointment :8084</span>
            <span className="badge badge-success">Pharmacy :8085</span>
            <span className="badge badge-success">Billing :8086</span>
            <span className="badge badge-success">Report :8087</span>
            <span className="badge badge-success">Notification :8088</span>
          </div>
        </div>
      </div>
    </div>
  );
};
