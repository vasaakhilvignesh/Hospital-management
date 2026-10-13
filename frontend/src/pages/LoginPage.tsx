import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService, patientService } from '../services/modules';
import {
  Activity,
  Lock,
  User as UserIcon,
  UserPlus,
  LogIn,
  CheckCircle,
  Eye,
  EyeOff,
  Stethoscope,
  Heart,
  ShieldCheck,
  Mail,
  Phone,
  Calendar,
  MapPin
} from 'lucide-react';

export const LoginPage: React.FC = () => {
  // Primary portal selector: Staff/Doctor vs Patient
  const [portal, setPortal] = useState<'STAFF' | 'PATIENT'>('STAFF');

  // Patient portal sub-tab: Sign In vs Register
  const [patientTab, setPatientTab] = useState<'LOGIN' | 'REGISTER'>('LOGIN');

  // Staff login state
  const [staffUsername, setStaffUsername] = useState('admin');
  const [staffPassword, setStaffPassword] = useState('admin123');
  const [showStaffPassword, setShowStaffPassword] = useState(false);

  // Patient login state
  const [patientUsername, setPatientUsername] = useState('patient');
  const [patientPassword, setPatientPassword] = useState('patient123');
  const [showPatientPassword, setShowPatientPassword] = useState(false);

  // Patient registration state
  const [regData, setRegData] = useState({
    username: '',
    password: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    dateOfBirth: '1995-05-15',
    gender: 'Male',
    bloodGroup: 'O+',
    address: '123 Health Ave',
    emergencyContact: 'Family Contact (555-9000)'
  });
  const [showRegPassword, setShowRegPassword] = useState(false);

  // Shared feedback state
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleStaffLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const res = await authService.login({ username: staffUsername, password: staffPassword });
      localStorage.setItem('token', res.token);
      const userProfile = await authService.getMe();
      await login(res.token, userProfile);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Staff authentication failed. Please check your username and password.');
    } finally {
      setLoading(false);
    }
  };

  const handlePatientLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const res = await authService.login({ username: patientUsername, password: patientPassword });
      localStorage.setItem('token', res.token);
      const userProfile = await authService.getMe();
      await login(res.token, userProfile);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Patient sign in failed. Please check credentials or register an account.');
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const createdPatient = await patientService.registerPatient(regData);
      setSuccess(`Registration successful for ${createdPatient.firstName} ${createdPatient.lastName}! Patient Code: ${createdPatient.patientCode}. Sign in with your new credentials below.`);
      setPatientUsername(regData.username);
      setPatientPassword(regData.password);
      setPatientTab('LOGIN');
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please check your details and try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickFillStaff = (u: string, p: string) => {
    setStaffUsername(u);
    setStaffPassword(p);
    setError('');
  };

  const handleQuickFillPatient = (u: string, p: string) => {
    setPatientUsername(u);
    setPatientPassword(p);
    setError('');
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: 'var(--bg-main)',
      padding: '24px 16px'
    }}>
      {/* Brand Header */}
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div style={{
          display: 'inline-flex',
          padding: '12px',
          borderRadius: '16px',
          backgroundColor: 'var(--primary-light)',
          color: 'var(--primary)',
          marginBottom: '12px',
          boxShadow: 'var(--shadow-sm)'
        }}>
          <Activity size={36} />
        </div>
        <h1 style={{ fontSize: '1.65rem', fontWeight: 700, color: 'var(--secondary)', letterSpacing: '-0.02em' }}>
          Hospital Management System
        </h1>
        <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginTop: '4px' }}>
          CarePulse Unified Healthcare & Clinical Operations Platform
        </p>
      </div>

      {/* Main Card Container */}
      <div className="card" style={{
        width: '100%',
        maxWidth: (portal === 'PATIENT' && patientTab === 'REGISTER') ? '660px' : '480px',
        boxShadow: 'var(--shadow-lg)',
        transition: 'max-width 0.25s ease'
      }}>
        {/* Dual Portal Switcher Tabs */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: '8px',
          backgroundColor: 'var(--bg-card)',
          padding: '6px',
          borderRadius: '10px',
          border: '1px solid var(--border)',
          marginBottom: '20px'
        }}>
          <button
            type="button"
            onClick={() => { setPortal('STAFF'); setError(''); setSuccess(''); }}
            className={`btn ${portal === 'STAFF' ? 'btn-primary' : 'btn-ghost'}`}
            style={{
              justifyContent: 'center',
              padding: '10px 12px',
              fontWeight: 600,
              fontSize: '0.875rem'
            }}
          >
            <Stethoscope size={16} />
            <span>Staff & Doctor</span>
          </button>

          <button
            type="button"
            onClick={() => { setPortal('PATIENT'); setError(''); setSuccess(''); }}
            className={`btn ${portal === 'PATIENT' ? 'btn-primary' : 'btn-ghost'}`}
            style={{
              justifyContent: 'center',
              padding: '10px 12px',
              fontWeight: 600,
              fontSize: '0.875rem'
            }}
          >
            <Heart size={16} />
            <span>Patient Portal</span>
          </button>
        </div>

        {/* Global Feedback Banners */}
        {error && <div className="error-banner" style={{ marginBottom: '16px' }}>{error}</div>}
        {success && (
          <div style={{
            backgroundColor: '#f0fdf4',
            color: '#166534',
            padding: '12px 14px',
            borderRadius: '8px',
            marginBottom: '16px',
            fontSize: '0.85rem',
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            border: '1px solid #bbf7d0'
          }}>
            <CheckCircle size={18} />
            <span>{success}</span>
          </div>
        )}

        {/* ================= PANEL A: STAFF & DOCTOR LOGIN ================= */}
        {portal === 'STAFF' && (
          <div>
            <div style={{ marginBottom: '16px' }}>
              <h2 style={{ fontSize: '1.15rem', fontWeight: 600, color: 'var(--secondary)' }}>
                Staff & Clinician Sign In
              </h2>
              <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                Access clinical charts, schedules, billing & administration
              </p>
            </div>

            <form onSubmit={handleStaffLogin}>
              <div className="form-group" style={{ marginBottom: '16px' }}>
                <label className="form-label">Username</label>
                <div style={{ position: 'relative' }}>
                  <input
                    type="text"
                    className="form-control"
                    value={staffUsername}
                    onChange={e => setStaffUsername(e.target.value)}
                    required
                    style={{ paddingLeft: '36px' }}
                    placeholder="Enter staff username"
                    autoComplete="username"
                  />
                  <UserIcon size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '12px' }} />
                </div>
              </div>

              <div className="form-group" style={{ marginBottom: '22px' }}>
                <label className="form-label">Password</label>
                <div style={{ position: 'relative' }}>
                  <input
                    type={showStaffPassword ? 'text' : 'password'}
                    className="form-control"
                    value={staffPassword}
                    onChange={e => setStaffPassword(e.target.value)}
                    required
                    style={{ paddingLeft: '36px', paddingRight: '36px' }}
                    placeholder="Enter password"
                    autoComplete="current-password"
                  />
                  <Lock size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '12px' }} />
                  <button
                    type="button"
                    onClick={() => setShowStaffPassword(!showStaffPassword)}
                    style={{
                      position: 'absolute',
                      right: '10px',
                      top: '10px',
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer',
                      padding: 0,
                      color: '#94a3b8'
                    }}
                    title={showStaffPassword ? 'Hide password' : 'Show password'}
                  >
                    {showStaffPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-primary"
                style={{ width: '100%', justifyContent: 'center', padding: '11px', fontWeight: 600 }}
                disabled={loading}
              >
                <LogIn size={16} />
                <span>{loading ? 'Authenticating...' : 'Sign In to Staff Portal'}</span>
              </button>
            </form>

            {/* Quick Demo Switcher for Staff */}
            <div style={{ marginTop: '24px', paddingTop: '16px', borderTop: '1px solid var(--border)' }}>
              <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '8px' }}>
                Quick Demo Accounts (Staff & Doctors):
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('admin', 'admin123')}
                  className="btn btn-secondary btn-sm"
                  title="Full Hospital Administrator"
                >
                  Admin
                </button>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('doctor', 'doctor123')}
                  className="btn btn-secondary btn-sm"
                  title="Dr. John Smith (Cardiology)"
                >
                  Dr. Smith (Cardio)
                </button>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('doctor2', 'doctor123')}
                  className="btn btn-secondary btn-sm"
                  title="Dr. Emily Clark (General Medicine)"
                >
                  Dr. Clark (Gen Med)
                </button>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('doctor3', 'doctor123')}
                  className="btn btn-secondary btn-sm"
                  title="Dr. Robert Patel (Neurology)"
                >
                  Dr. Patel (Neuro)
                </button>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('receptionist', 'receptionist123')}
                  className="btn btn-secondary btn-sm"
                  title="Front Desk & Patient Registrar"
                >
                  Receptionist
                </button>
                <button
                  type="button"
                  onClick={() => handleQuickFillStaff('staff', 'staff123')}
                  className="btn btn-secondary btn-sm"
                  title="Head Nurse & Pharmacy Staff"
                >
                  Pharmacy / Staff
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ================= PANEL B: PATIENT PORTAL ================= */}
        {portal === 'PATIENT' && (
          <div>
            {/* Sub-tab selection between Patient Sign In and New Registration */}
            <div style={{ display: 'flex', gap: '8px', marginBottom: '18px', borderBottom: '1px solid var(--border)', paddingBottom: '10px' }}>
              <button
                type="button"
                onClick={() => { setPatientTab('LOGIN'); setError(''); }}
                className={`btn ${patientTab === 'LOGIN' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ flex: 1, justifyContent: 'center' }}
              >
                <LogIn size={15} />
                <span>Patient Sign In</span>
              </button>
              <button
                type="button"
                onClick={() => { setPatientTab('REGISTER'); setError(''); }}
                className={`btn ${patientTab === 'REGISTER' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ flex: 1, justifyContent: 'center' }}
              >
                <UserPlus size={15} />
                <span>Register as Patient</span>
              </button>
            </div>

            {patientTab === 'LOGIN' ? (
              <div>
                <div style={{ marginBottom: '16px' }}>
                  <h2 style={{ fontSize: '1.15rem', fontWeight: 600, color: 'var(--secondary)' }}>
                    Patient Account Access
                  </h2>
                  <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                    View appointment schedules, medical prescriptions & hospital bills
                  </p>
                </div>

                <form onSubmit={handlePatientLogin}>
                  <div className="form-group" style={{ marginBottom: '16px' }}>
                    <label className="form-label">Patient Username</label>
                    <div style={{ position: 'relative' }}>
                      <input
                        type="text"
                        className="form-control"
                        value={patientUsername}
                        onChange={e => setPatientUsername(e.target.value)}
                        required
                        style={{ paddingLeft: '36px' }}
                        placeholder="Enter your registered username"
                        autoComplete="username"
                      />
                      <UserIcon size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '12px' }} />
                    </div>
                  </div>

                  <div className="form-group" style={{ marginBottom: '22px' }}>
                    <label className="form-label">Password</label>
                    <div style={{ position: 'relative' }}>
                      <input
                        type={showPatientPassword ? 'text' : 'password'}
                        className="form-control"
                        value={patientPassword}
                        onChange={e => setPatientPassword(e.target.value)}
                        required
                        style={{ paddingLeft: '36px', paddingRight: '36px' }}
                        placeholder="Enter password"
                        autoComplete="current-password"
                      />
                      <Lock size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '12px' }} />
                      <button
                        type="button"
                        onClick={() => setShowPatientPassword(!showPatientPassword)}
                        style={{
                          position: 'absolute',
                          right: '10px',
                          top: '10px',
                          background: 'none',
                          border: 'none',
                          cursor: 'pointer',
                          padding: 0,
                          color: '#94a3b8'
                        }}
                        title={showPatientPassword ? 'Hide password' : 'Show password'}
                      >
                        {showPatientPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary"
                    style={{ width: '100%', justifyContent: 'center', padding: '11px', fontWeight: 600 }}
                    disabled={loading}
                  >
                    <LogIn size={16} />
                    <span>{loading ? 'Authenticating...' : 'Sign In as Patient'}</span>
                  </button>
                </form>

                <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  Don't have a patient profile yet?{' '}
                  <button
                    type="button"
                    onClick={() => { setPatientTab('REGISTER'); setError(''); }}
                    style={{ background: 'none', border: 'none', color: 'var(--primary)', fontWeight: 600, cursor: 'pointer', padding: 0 }}
                  >
                    Sign up here
                  </button>
                </div>

                <div style={{ marginTop: '24px', paddingTop: '16px', borderTop: '1px solid var(--border)' }}>
                  <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '8px' }}>
                    Demo Patient Account:
                  </div>
                  <button
                    type="button"
                    onClick={() => handleQuickFillPatient('patient', 'patient123')}
                    className="btn btn-secondary btn-sm"
                  >
                    Peter Patient (Demo)
                  </button>
                </div>
              </div>
            ) : (
              <div>
                <div style={{ marginBottom: '16px' }}>
                  <h2 style={{ fontSize: '1.15rem', fontWeight: 600, color: 'var(--secondary)' }}>
                    New Patient Registration
                  </h2>
                  <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                    Creates your secure hospital profile and patient portal account
                  </p>
                </div>

                <form onSubmit={handleRegisterSubmit}>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '14px' }}>
                    <div className="form-group">
                      <label className="form-label">Username *</label>
                      <input
                        type="text"
                        className="form-control"
                        required
                        value={regData.username}
                        onChange={e => setRegData({ ...regData, username: e.target.value })}
                        placeholder="e.g. jdoe_patient"
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Password (min 6 chars) *</label>
                      <div style={{ position: 'relative' }}>
                        <input
                          type={showRegPassword ? 'text' : 'password'}
                          className="form-control"
                          required
                          minLength={6}
                          value={regData.password}
                          onChange={e => setRegData({ ...regData, password: e.target.value })}
                          placeholder="Choose password"
                          style={{ paddingRight: '36px' }}
                        />
                        <button
                          type="button"
                          onClick={() => setShowRegPassword(!showRegPassword)}
                          style={{
                            position: 'absolute',
                            right: '10px',
                            top: '10px',
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            padding: 0,
                            color: '#94a3b8'
                          }}
                        >
                          {showRegPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                        </button>
                      </div>
                    </div>

                    <div className="form-group">
                      <label className="form-label">First Name *</label>
                      <input
                        type="text"
                        className="form-control"
                        required
                        value={regData.firstName}
                        onChange={e => setRegData({ ...regData, firstName: e.target.value })}
                        placeholder="First name"
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Last Name *</label>
                      <input
                        type="text"
                        className="form-control"
                        required
                        value={regData.lastName}
                        onChange={e => setRegData({ ...regData, lastName: e.target.value })}
                        placeholder="Last name"
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Email Address *</label>
                      <input
                        type="email"
                        className="form-control"
                        required
                        value={regData.email}
                        onChange={e => setRegData({ ...regData, email: e.target.value })}
                        placeholder="patient@example.com"
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Phone Number *</label>
                      <input
                        type="text"
                        className="form-control"
                        required
                        value={regData.phone}
                        onChange={e => setRegData({ ...regData, phone: e.target.value })}
                        placeholder="555-0100"
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Date of Birth *</label>
                      <input
                        type="date"
                        className="form-control"
                        required
                        value={regData.dateOfBirth}
                        onChange={e => setRegData({ ...regData, dateOfBirth: e.target.value })}
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Gender *</label>
                      <select
                        className="form-control"
                        value={regData.gender}
                        onChange={e => setRegData({ ...regData, gender: e.target.value })}
                      >
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Blood Group</label>
                      <select
                        className="form-control"
                        value={regData.bloodGroup}
                        onChange={e => setRegData({ ...regData, bloodGroup: e.target.value })}
                      >
                        <option value="A+">A+</option>
                        <option value="A-">A-</option>
                        <option value="B+">B+</option>
                        <option value="B-">B-</option>
                        <option value="AB+">AB+</option>
                        <option value="AB-">AB-</option>
                        <option value="O+">O+</option>
                        <option value="O-">O-</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label className="form-label">Address</label>
                      <input
                        type="text"
                        className="form-control"
                        value={regData.address}
                        onChange={e => setRegData({ ...regData, address: e.target.value })}
                        placeholder="Street address, city"
                      />
                    </div>

                    <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                      <label className="form-label">Emergency Contact</label>
                      <input
                        type="text"
                        className="form-control"
                        value={regData.emergencyContact}
                        onChange={e => setRegData({ ...regData, emergencyContact: e.target.value })}
                        placeholder="Contact name and phone number"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary"
                    style={{ width: '100%', justifyContent: 'center', padding: '12px', marginTop: '20px', fontWeight: 600 }}
                    disabled={loading}
                  >
                    <UserPlus size={16} />
                    <span>{loading ? 'Creating Profile & Account...' : 'Complete Patient Registration'}</span>
                  </button>
                </form>

                <div style={{ marginTop: '16px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                  Already registered?{' '}
                  <button
                    type="button"
                    onClick={() => { setPatientTab('LOGIN'); setError(''); }}
                    style={{ background: 'none', border: 'none', color: 'var(--primary)', fontWeight: 600, cursor: 'pointer', padding: 0 }}
                  >
                    Sign in to your patient account
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
