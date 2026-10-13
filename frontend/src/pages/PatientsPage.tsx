import React, { useState, useEffect } from 'react';
import { patientService } from '../services/modules';
import { Patient, MedicalHistory, Immunization } from '../types';
import { Search, Plus, Trash2, Edit2, FileText, ShieldAlert, X } from 'lucide-react';

export const PatientsPage: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [histories, setHistories] = useState<MedicalHistory[]>([]);
  const [immunizations, setImmunizations] = useState<Immunization[]>([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [formData, setFormData] = useState({
    username: '', password: '',
    firstName: '', lastName: '', dateOfBirth: '1995-01-01',
    gender: 'Male', phone: '555-1234', email: 'user@example.com',
    address: '123 Main St', bloodGroup: 'O+', emergencyContact: 'Contact (555-9999)'
  });

  const loadPatients = async () => {
    try {
      setLoading(true);
      const data = search.trim() ? await patientService.search(search) : await patientService.getAll();
      setPatients(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPatients();
  }, [search]);

  const handleSelectPatient = async (patient: Patient) => {
    setSelectedPatient(patient);
    try {
      const [h, imm] = await Promise.all([
        patientService.getHistory(patient.id),
        patientService.getImmunizations(patient.id)
      ]);
      setHistories(h);
      setImmunizations(imm);
    } catch (err) {
      console.error('Failed to load patient records', err);
    }
  };

  const handleCreatePatient = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await patientService.registerPatient(formData);
      setShowCreateModal(false);
      setFormData({
        username: '', password: '',
        firstName: '', lastName: '', dateOfBirth: '1995-01-01',
        gender: 'Male', phone: '555-1234', email: 'user@example.com',
        address: '123 Main St', bloodGroup: 'O+', emergencyContact: 'Contact (555-9999)'
      });
      loadPatients();
    } catch (err: any) {
      alert(err.message || 'Failed to register patient');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this patient record?')) {
      try {
        await patientService.delete(id);
        if (selectedPatient?.id === id) setSelectedPatient(null);
        loadPatients();
      } catch (err: any) {
        alert(err.message || 'Failed to delete patient');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Patient Management</h1>
          <p className="page-subtitle">Directory, medical histories, and immunization tracking</p>
        </div>
        <button onClick={() => setShowCreateModal(true)} className="btn btn-primary">
          <Plus size={16} />
          <span>Register Patient</span>
        </button>
      </div>

      <div className="card" style={{ marginBottom: '20px', padding: '16px' }}>
        <div style={{ position: 'relative', maxWidth: '380px' }}>
          <input
            type="text"
            className="form-control"
            placeholder="Search patients by name or code..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ paddingLeft: '34px' }}
          />
          <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '11px' }} />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: selectedPatient ? '1fr 1fr' : '1fr', gap: '20px' }}>
        {/* Table View */}
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th>Gender / Blood</th>
                <th>Phone</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={5} style={{ textAlign: 'center', padding: '30px' }}>Loading patients...</td></tr>
              ) : patients.length === 0 ? (
                <tr><td colSpan={5} style={{ textAlign: 'center', padding: '30px' }}>No patients found.</td></tr>
              ) : (
                patients.map(p => (
                  <tr key={p.id} style={{ cursor: 'pointer', backgroundColor: selectedPatient?.id === p.id ? 'var(--primary-light)' : 'transparent' }} onClick={() => handleSelectPatient(p)}>
                    <td style={{ fontWeight: 600 }}>{p.patientCode}</td>
                    <td>{p.firstName} {p.lastName}</td>
                    <td>{p.gender} / <span className="badge badge-info">{p.bloodGroup}</span></td>
                    <td>{p.phone}</td>
                    <td>
                      <button onClick={(e) => { e.stopPropagation(); handleDelete(p.id); }} className="btn btn-danger btn-sm" title="Delete">
                        <Trash2 size={14} />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Selected Patient Details Drawer */}
        {selectedPatient && (
          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border)', paddingBottom: '12px' }}>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>{selectedPatient.firstName} {selectedPatient.lastName}</h3>
                <span className="badge badge-neutral">{selectedPatient.patientCode}</span>
              </div>
              <button onClick={() => setSelectedPatient(null)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>

            <div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '0.875rem' }}>
                <div><strong>DOB:</strong> {selectedPatient.dateOfBirth}</div>
                <div><strong>Gender:</strong> {selectedPatient.gender}</div>
                <div><strong>Email:</strong> {selectedPatient.email}</div>
                <div><strong>Phone:</strong> {selectedPatient.phone}</div>
                <div><strong>Blood:</strong> {selectedPatient.bloodGroup}</div>
                <div><strong>Address:</strong> {selectedPatient.address}</div>
              </div>
            </div>

            {/* Medical History */}
            <div style={{ borderTop: '1px solid var(--border)', paddingTop: '12px' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '8px' }}>Medical History ({histories.length})</h4>
              {histories.length === 0 ? (
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No medical history records available.</p>
              ) : (
                histories.map(h => (
                  <div key={h.id} style={{ background: '#f8fafc', padding: '10px', borderRadius: '6px', marginBottom: '6px', fontSize: '0.85rem' }}>
                    <div><strong>Diagnosis:</strong> {h.diagnosis}</div>
                    <div><strong>Treatment:</strong> {h.treatment}</div>
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.78rem' }}>Date: {h.recordDate}</div>
                  </div>
                ))
              )}
            </div>

            {/* Immunizations */}
            <div style={{ borderTop: '1px solid var(--border)', paddingTop: '12px' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '8px' }}>Immunizations ({immunizations.length})</h4>
              {immunizations.length === 0 ? (
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No immunization records available.</p>
              ) : (
                immunizations.map(imm => (
                  <div key={imm.id} style={{ background: '#f8fafc', padding: '10px', borderRadius: '6px', marginBottom: '6px', fontSize: '0.85rem' }}>
                    <div><strong>{imm.vaccineName}</strong> (Dose {imm.doseNumber})</div>
                    <div style={{ color: 'var(--text-muted)', fontSize: '0.78rem' }}>Administered: {imm.dateAdministered}</div>
                  </div>
                ))
              )}
            </div>
          </div>
        )}
      </div>

      {/* Register Patient Modal */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Register New Patient</h3>
              <button onClick={() => setShowCreateModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleCreatePatient}>
              <div className="modal-body" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Username *</label>
                  <input type="text" className="form-control" required placeholder="Login username" value={formData.username} onChange={e => setFormData({ ...formData, username: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Password *</label>
                  <input type="password" className="form-control" required placeholder="Initial password" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">First Name *</label>
                  <input type="text" className="form-control" required value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Last Name *</label>
                  <input type="text" className="form-control" required value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Date of Birth *</label>
                  <input type="date" className="form-control" required value={formData.dateOfBirth} onChange={e => setFormData({ ...formData, dateOfBirth: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Gender *</label>
                  <select className="form-control" value={formData.gender} onChange={e => setFormData({ ...formData, gender: e.target.value })}>
                    <option value="Male">Male</option>
                    <option value="Female">Female</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Blood Group</label>
                  <input type="text" className="form-control" value={formData.bloodGroup} onChange={e => setFormData({ ...formData, bloodGroup: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone *</label>
                  <input type="text" className="form-control" required value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} />
                </div>
                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Email *</label>
                  <input type="email" className="form-control" required value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} />
                </div>
                <div className="form-group" style={{ gridColumn: 'span 2' }}>
                  <label className="form-label">Address</label>
                  <input type="text" className="form-control" value={formData.address} onChange={e => setFormData({ ...formData, address: e.target.value })} />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowCreateModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Save Patient</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
