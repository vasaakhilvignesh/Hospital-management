import React, { useState, useEffect, useMemo } from 'react';
import { useAuth } from '../context/AuthContext';
import { appointmentService, patientService, doctorService } from '../services/modules';
import { Appointment, Patient, Doctor } from '../types';
import { Calendar, Plus, CheckCircle, XCircle, Clock, X } from 'lucide-react';

export const AppointmentsPage: React.FC = () => {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);

  const [formData, setFormData] = useState({
    patientId: 1,
    doctorId: 1,
    appointmentDate: new Date().toISOString().split('T')[0],
    startTime: '09:00',
    endTime: '09:30',
    reason: 'General Consultation'
  });

  const [availableSlots, setAvailableSlots] = useState<{ startTime: string; endTime: string; available: boolean }[]>([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [bookingError, setBookingError] = useState('');
  const [submittingBooking, setSubmittingBooking] = useState(false);

  const patientMap = useMemo(() => {
    const map: Record<number, string> = {};
    patients.forEach(p => { map[p.id] = `${p.firstName} ${p.lastName}`; });
    return map;
  }, [patients]);

  const doctorMap = useMemo(() => {
    const map: Record<number, string> = {};
    doctors.forEach(d => { map[d.id] = `Dr. ${d.fullName} (${d.specialization})`; });
    return map;
  }, [doctors]);

  const loadAppointments = async () => {
    try {
      setLoading(true);
      if (user?.role === 'PATIENT') {
        const pid = user.patientId || 1;
        const data = await appointmentService.getByPatient(pid);
        setAppointments(data);
      } else if (user?.role === 'DOCTOR') {
        const did = user.doctorId || 1;
        const data = await appointmentService.getByDoctor(did);
        setAppointments(data);
      } else {
        const data = await appointmentService.getAll();
        setAppointments(data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAppointments();
    // Always fetch doctors list for booking select
    doctorService.getAll().then(docs => {
      setDoctors(docs);
      if (docs.length > 0 && !formData.doctorId) {
        setFormData(prev => ({ ...prev, doctorId: docs[0].id }));
      }
    }).catch(() => {});

    // Fetch patients for front desk / doctors / admins
    if (user?.role !== 'PATIENT') {
      patientService.getAll().then(pats => {
        setPatients(pats);
        if (pats.length > 0) {
          setFormData(prev => ({ ...prev, patientId: pats[0].id }));
        }
      }).catch(() => {});
    }
  }, [user]);

  useEffect(() => {
    if (!showModal || !formData.doctorId || !formData.appointmentDate) return;
    let isCurrent = true;
    setLoadingSlots(true);
    setBookingError('');
    appointmentService.getAvailableSlots(Number(formData.doctorId), formData.appointmentDate)
      .then(slots => {
        if (!isCurrent) return;
        setAvailableSlots(slots);
        const firstAvail = slots.find(s => s.available);
        if (firstAvail) {
          setFormData(prev => ({
            ...prev,
            startTime: firstAvail.startTime,
            endTime: firstAvail.endTime
          }));
        }
      })
      .catch(() => {
        if (!isCurrent) return;
        setAvailableSlots([]);
      })
      .finally(() => {
        if (isCurrent) setLoadingSlots(false);
      });

    return () => { isCurrent = false; };
  }, [showModal, formData.doctorId, formData.appointmentDate]);

  const handleCreateAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    setBookingError('');
    setSubmittingBooking(true);
    try {
      const payload = {
        appointmentDate: formData.appointmentDate,
        startTime: formData.startTime,
        endTime: formData.endTime,
        reason: formData.reason,
        patientId: user?.role === 'PATIENT' ? (user.patientId || 1) : Number(formData.patientId),
        doctorId: Number(formData.doctorId)
      };

      await appointmentService.create(payload);
      setShowModal(false);
      loadAppointments();
    } catch (err: any) {
      setBookingError(err.message || 'Failed to book appointment');
    } finally {
      setSubmittingBooking(false);
    }
  };

  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      await appointmentService.updateStatus(id, status);
      loadAppointments();
    } catch (err: any) {
      alert(err.message || 'Failed to update status');
    }
  };

  const handleCancel = async (id: number) => {
    if (window.confirm('Are you sure you want to cancel this appointment?')) {
      try {
        await appointmentService.cancel(id);
        loadAppointments();
      } catch (err: any) {
        alert(err.message || 'Failed to cancel appointment');
      }
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'badge-success';
      case 'COMPLETED': return 'badge-info';
      case 'SCHEDULED': return 'badge-warning';
      case 'CANCELLED': return 'badge-danger';
      default: return 'badge-neutral';
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Appointments</h1>
          <p className="page-subtitle">Consultation bookings, time slots, and doctor schedules</p>
        </div>
        <button onClick={() => setShowModal(true)} className="btn btn-primary">
          <Plus size={16} />
          <span>Book Appointment</span>
        </button>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Patient</th>
              <th>Doctor</th>
              <th>Date</th>
              <th>Time</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={8} style={{ textAlign: 'center', padding: '30px' }}>Loading appointments...</td></tr>
            ) : appointments.length === 0 ? (
              <tr><td colSpan={8} style={{ textAlign: 'center', padding: '30px' }}>No appointments scheduled.</td></tr>
            ) : (
              appointments.map(a => (
                <tr key={a.id}>
                  <td style={{ fontWeight: 600 }}>{a.appointmentCode}</td>
                  <td>{patientMap[a.patientId] ? `${patientMap[a.patientId]} (#${a.patientId})` : `Patient #${a.patientId}`}</td>
                  <td>{doctorMap[a.doctorId] || `Doctor #${a.doctorId}`}</td>
                  <td>{a.appointmentDate}</td>
                  <td>{a.startTime} - {a.endTime}</td>
                  <td>{a.reason}</td>
                  <td><span className={`badge ${getStatusBadge(a.status)}`}>{a.status}</span></td>
                  <td>
                    <div style={{ display: 'flex', gap: '6px' }}>
                      {user?.role !== 'PATIENT' && a.status === 'SCHEDULED' && (
                        <button onClick={() => handleUpdateStatus(a.id, 'CONFIRMED')} className="btn btn-secondary btn-sm" title="Confirm">
                          <CheckCircle size={14} color="#15803d" />
                        </button>
                      )}
                      {user?.role !== 'PATIENT' && a.status === 'CONFIRMED' && (
                        <button onClick={() => handleUpdateStatus(a.id, 'COMPLETED')} className="btn btn-secondary btn-sm" title="Mark Completed">
                          <CheckCircle size={14} color="#0369a1" />
                        </button>
                      )}
                      {a.status !== 'CANCELLED' && a.status !== 'COMPLETED' && (
                        <button onClick={() => handleCancel(a.id)} className="btn btn-danger btn-sm" title="Cancel">
                          <XCircle size={14} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Book Appointment</h3>
              <button onClick={() => setShowModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleCreateAppointment}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                {bookingError && (
                  <div className="error-banner" style={{ fontSize: '0.85rem', marginBottom: '4px' }}>
                    {bookingError}
                  </div>
                )}

                {user?.role !== 'PATIENT' && (
                  <div className="form-group">
                    <label className="form-label">Patient *</label>
                    <select
                      className="form-control"
                      value={formData.patientId}
                      onChange={e => setFormData({ ...formData, patientId: Number(e.target.value) })}
                    >
                      {patients.map(p => (
                        <option key={p.id} value={p.id}>{p.firstName} {p.lastName} ({p.patientCode})</option>
                      ))}
                    </select>
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Doctor *</label>
                  <select
                    className="form-control"
                    value={formData.doctorId}
                    onChange={e => setFormData({ ...formData, doctorId: Number(e.target.value) })}
                  >
                    {doctors.length === 0 ? (
                      <option value={1}>Loading doctors...</option>
                    ) : (
                      doctors.map(d => (
                        <option key={d.id} value={d.id}>
                          Dr. {d.fullName} ({d.specialization}) - ${d.consultationFee}
                        </option>
                      ))
                    )}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Date *</label>
                  <input
                    type="date"
                    className="form-control"
                    required
                    min={new Date().toISOString().split('T')[0]}
                    value={formData.appointmentDate}
                    onChange={e => setFormData({ ...formData, appointmentDate: e.target.value })}
                  />
                </div>

                {/* Dynamic doctor slot availability pills */}
                <div className="form-group">
                  <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <Clock size={15} /> Select Consultation Slot *
                  </label>
                  {loadingSlots ? (
                    <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', padding: '6px 0' }}>
                      Checking doctor availability for selected date...
                    </div>
                  ) : availableSlots.length === 0 ? (
                    <div style={{ fontSize: '0.85rem', color: '#b45309', backgroundColor: '#fef3c7', padding: '10px 12px', borderRadius: '6px' }}>
                      No doctor availability registered for this day of the week, or all slots are unavailable.
                    </div>
                  ) : (
                    <div>
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', maxHeight: '160px', overflowY: 'auto', padding: '4px 0' }}>
                        {availableSlots.map(slot => {
                          const isSelected = formData.startTime === slot.startTime && formData.endTime === slot.endTime;
                          return (
                            <button
                              key={`${slot.startTime}-${slot.endTime}`}
                              type="button"
                              disabled={!slot.available}
                              onClick={() => {
                                setFormData(prev => ({ ...prev, startTime: slot.startTime, endTime: slot.endTime }));
                              }}
                              className={`btn btn-sm ${isSelected ? 'btn-primary' : slot.available ? 'btn-secondary' : 'btn-ghost'}`}
                              style={{
                                opacity: slot.available ? 1 : 0.45,
                                cursor: slot.available ? 'pointer' : 'not-allowed',
                                border: isSelected ? '2px solid var(--primary)' : '1px solid var(--border)',
                                textDecoration: slot.available ? 'none' : 'line-through'
                              }}
                              title={slot.available ? 'Slot Available' : 'Slot Already Booked'}
                            >
                              {slot.startTime} - {slot.endTime} {!slot.available && '(Booked)'}
                            </button>
                          );
                        })}
                      </div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                        Selected: <strong>{formData.startTime} - {formData.endTime}</strong>
                      </div>
                    </div>
                  )}
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                  <div className="form-group">
                    <label className="form-label">Start Time *</label>
                    <input
                      type="time"
                      className="form-control"
                      required
                      value={formData.startTime}
                      onChange={e => setFormData({ ...formData, startTime: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">End Time *</label>
                    <input
                      type="time"
                      className="form-control"
                      required
                      value={formData.endTime}
                      onChange={e => setFormData({ ...formData, endTime: e.target.value })}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Reason for Visit *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={formData.reason}
                    onChange={e => setFormData({ ...formData, reason: e.target.value })}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button type="button" onClick={() => setShowModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={submittingBooking}>
                  {submittingBooking ? 'Booking...' : 'Confirm Booking'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
