import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { pharmacyService, patientService } from '../services/modules';
import { Prescription, Medicine, Patient } from '../types';
import { CheckCircle, Plus, X, AlertCircle } from 'lucide-react';

export const PrescriptionsPage: React.FC = () => {
  const { user } = useAuth();
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [medicines, setMedicines] = useState<Medicine[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [showCreateModal, setShowCreateModal] = useState(false);

  // New prescription form state
  const [newRx, setNewRx] = useState({
    patientId: 1,
    doctorId: user?.doctorId || 1,
    diagnosis: '',
    notes: '',
    items: [
      {
        medicineId: 1,
        dosage: '500mg',
        frequency: 'Twice daily',
        durationDays: 5,
        quantity: 10,
        instructions: 'Take after meals'
      }
    ]
  });

  const loadPrescriptions = async () => {
    try {
      setLoading(true);
      setError('');
      if (user?.role === 'PATIENT') {
        const data = user.patientId
          ? await pharmacyService.getPrescriptionsByPatient(user.patientId)
          : await pharmacyService.getPrescriptions();
        setPrescriptions(data || []);
      } else {
        const data = await pharmacyService.getPrescriptions();
        setPrescriptions(data || []);
      }
    } catch (err: any) {
      console.error('Error loading prescriptions:', err);
      setError(err?.message || 'Failed to load prescriptions. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const loadDropdownData = async () => {
    try {
      if (user?.role === 'ADMIN' || user?.role === 'DOCTOR') {
        const [medsData, patientsData] = await Promise.all([
          pharmacyService.getMedicines(),
          patientService.getAll()
        ]);
        setMedicines(medsData || []);
        setPatients(patientsData || []);
        if (patientsData && patientsData.length > 0) {
          setNewRx(prev => ({
            ...prev,
            patientId: patientsData[0].id,
            doctorId: user?.doctorId || prev.doctorId
          }));
        }
        if (medsData && medsData.length > 0) {
          setNewRx(prev => ({
            ...prev,
            items: [{ ...prev.items[0], medicineId: medsData[0].id }]
          }));
        }
      }
    } catch (err) {
      console.error('Failed to load auxiliary data', err);
    }
  };

  useEffect(() => {
    loadPrescriptions();
    loadDropdownData();
  }, [user]);

  const handleDispense = async (id: number) => {
    if (window.confirm('Confirm dispensing medications for this prescription?')) {
      try {
        await pharmacyService.dispensePrescription(id);
        loadPrescriptions();
      } catch (err: any) {
        alert(err.message || 'Dispensing failed. Check pharmacy stock.');
      }
    }
  };

  const handleCreatePrescription = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await pharmacyService.createPrescription({
        patientId: Number(newRx.patientId),
        doctorId: Number(user?.doctorId || newRx.doctorId),
        diagnosis: newRx.diagnosis,
        notes: newRx.notes,
        items: newRx.items.map(item => ({
          medicineId: Number(item.medicineId),
          dosage: item.dosage,
          frequency: item.frequency,
          durationDays: Number(item.durationDays),
          quantity: Number(item.quantity),
          instructions: item.instructions
        }))
      });
      setShowCreateModal(false);
      loadPrescriptions();
    } catch (err: any) {
      alert(err.message || 'Failed to create prescription');
    }
  };

  const canDispense = user?.role === 'ADMIN' || user?.role === 'STAFF';
  const canWriteRx = user?.role === 'ADMIN' || user?.role === 'DOCTOR';

  // Helper to resolve patient name
  const getPatientLabel = (pId: number) => {
    const pat = patients.find(p => p.id === pId);
    if (pat) {
      return `${pat.firstName} ${pat.lastName} (${pat.patientCode})`;
    }
    return `Patient #${pId}`;
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Prescriptions</h1>
          <p className="page-subtitle">
            {user?.role === 'PATIENT'
              ? 'Your medical prescriptions and medication instructions'
              : 'Physician prescriptions and dispensing workflows'}
          </p>
        </div>
        {canWriteRx && (
          <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
            <Plus size={16} />
            <span>Write Prescription</span>
          </button>
        )}
      </div>

      {error && (
        <div style={{
          backgroundColor: '#fee2e2',
          border: '1px solid #f87171',
          color: '#991b1b',
          padding: '12px 16px',
          borderRadius: '8px',
          marginBottom: '16px',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>RX Code</th>
              <th>Date</th>
              {user?.role !== 'PATIENT' && <th>Patient</th>}
              <th>Doctor</th>
              <th>Diagnosis</th>
              <th>Medications</th>
              <th>Status</th>
              {canDispense && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={user?.role !== 'PATIENT' ? 8 : 7} style={{ textAlign: 'center', padding: '30px' }}>
                  Loading prescriptions...
                </td>
              </tr>
            ) : prescriptions.length === 0 ? (
              <tr>
                <td colSpan={user?.role !== 'PATIENT' ? 8 : 7} style={{ textAlign: 'center', padding: '30px' }}>
                  No prescriptions found.
                </td>
              </tr>
            ) : (
              prescriptions.map(p => (
                <tr key={p.id}>
                  <td style={{ fontWeight: 600 }}>{p.prescriptionNumber}</td>
                  <td>
                    {p.createdAt
                      ? new Date(p.createdAt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })
                      : '-'}
                  </td>
                  {user?.role !== 'PATIENT' && (
                    <td>{getPatientLabel(p.patientId)}</td>
                  )}
                  <td>Doctor #{p.doctorId}</td>
                  <td>
                    <div style={{ fontWeight: 500 }}>{p.diagnosis || 'General'}</div>
                    {p.notes && (
                      <div style={{ fontSize: '0.75rem', color: '#6b7280', marginTop: '2px' }}>
                        Note: {p.notes}
                      </div>
                    )}
                  </td>
                  <td>
                    {p.items?.map((item, idx) => (
                      <div key={idx} style={{ marginBottom: idx < p.items.length - 1 ? '6px' : 0, fontSize: '0.825rem' }}>
                        <strong style={{ color: '#111827' }}>
                          {item.medicineName || `Medicine #${item.medicineId}`}
                        </strong>
                        {' '}- {item.dosage} ({item.frequency}, {item.durationDays}d) &bull; Qty: {item.quantity}
                        {item.instructions && (
                          <div style={{ color: '#6b7280', fontSize: '0.75rem' }}>
                            Instructions: {item.instructions}
                          </div>
                        )}
                      </div>
                    ))}
                  </td>
                  <td>
                    <span className={`badge ${p.status === 'DISPENSED' ? 'badge-success' : p.status === 'PENDING' ? 'badge-warning' : 'badge-danger'}`}>
                      {p.status}
                    </span>
                    {p.dispensedAt && (
                      <div style={{ fontSize: '0.7rem', color: '#6b7280', marginTop: '2px' }}>
                        Dispensed: {new Date(p.dispensedAt).toLocaleDateString()}
                      </div>
                    )}
                  </td>
                  {canDispense && (
                    <td>
                      {p.status === 'PENDING' && (
                        <button
                          onClick={() => handleDispense(p.id)}
                          className="btn btn-primary btn-sm"
                          title="Dispense"
                        >
                          <CheckCircle size={14} />
                          <span>Dispense</span>
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Write Prescription Modal */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Write Prescription</h3>
              <button onClick={() => setShowCreateModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleCreatePrescription}>
              <div className="modal-body" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Patient *</label>
                  <select
                    className="form-control"
                    required
                    value={newRx.patientId}
                    onChange={e => setNewRx({ ...newRx, patientId: Number(e.target.value) })}
                  >
                    {patients.map(p => (
                      <option key={p.id} value={p.id}>{p.firstName} {p.lastName} ({p.patientCode})</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Doctor ID</label>
                  <input
                    type="number"
                    className="form-control"
                    disabled={user?.role === 'DOCTOR'}
                    value={user?.doctorId || newRx.doctorId}
                    onChange={e => setNewRx({ ...newRx, doctorId: Number(e.target.value) })}
                  />
                </div>

                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Diagnosis *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    placeholder="e.g. Acute Bronchitis"
                    value={newRx.diagnosis}
                    onChange={e => setNewRx({ ...newRx, diagnosis: e.target.value })}
                  />
                </div>

                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Medicine *</label>
                  <select
                    className="form-control"
                    required
                    value={newRx.items[0]?.medicineId}
                    onChange={e => {
                      const updated = [...newRx.items];
                      updated[0].medicineId = Number(e.target.value);
                      setNewRx({ ...newRx, items: updated });
                    }}
                  >
                    {medicines.map(m => (
                      <option key={m.id} value={m.id}>{m.name} ({m.strength}) - Stock: {m.stockQuantity}</option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Dosage *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={newRx.items[0]?.dosage}
                    onChange={e => {
                      const updated = [...newRx.items];
                      updated[0].dosage = e.target.value;
                      setNewRx({ ...newRx, items: updated });
                    }}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Frequency *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={newRx.items[0]?.frequency}
                    onChange={e => {
                      const updated = [...newRx.items];
                      updated[0].frequency = e.target.value;
                      setNewRx({ ...newRx, items: updated });
                    }}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Duration (Days) *</label>
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    required
                    value={newRx.items[0]?.durationDays}
                    onChange={e => {
                      const updated = [...newRx.items];
                      updated[0].durationDays = Number(e.target.value);
                      setNewRx({ ...newRx, items: updated });
                    }}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Dispense Quantity *</label>
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    required
                    value={newRx.items[0]?.quantity}
                    onChange={e => {
                      const updated = [...newRx.items];
                      updated[0].quantity = Number(e.target.value);
                      setNewRx({ ...newRx, items: updated });
                    }}
                  />
                </div>

                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Instructions / Notes</label>
                  <input
                    type="text"
                    className="form-control"
                    value={newRx.notes}
                    onChange={e => setNewRx({ ...newRx, notes: e.target.value })}
                    placeholder="Special instructions or clinical observations"
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Save & Issue Prescription</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
