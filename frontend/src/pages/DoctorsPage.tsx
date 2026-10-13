import React, { useState, useEffect } from 'react';
import { doctorService } from '../services/modules';
import { Doctor } from '../types';
import { Stethoscope, Search, Plus, Calendar, Clock, X } from 'lucide-react';

export const DoctorsPage: React.FC = () => {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor | null>(null);
  const [schedules, setSchedules] = useState<any[]>([]);

  const loadDoctors = async () => {
    try {
      setLoading(true);
      const data = search.trim() ? await doctorService.search(search) : await doctorService.getAll();
      setDoctors(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDoctors();
  }, [search]);

  const handleSelectDoctor = async (doc: Doctor) => {
    setSelectedDoctor(doc);
    try {
      const avail = await doctorService.getAvailability(doc.id);
      setSchedules(avail);
    } catch (err) {
      console.error('Failed to load schedules', err);
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Doctors Directory</h1>
          <p className="page-subtitle">Medical specialists, departments, and consultation schedules</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '20px', padding: '16px' }}>
        <div style={{ position: 'relative', maxWidth: '380px' }}>
          <input
            type="text"
            className="form-control"
            placeholder="Search doctors by name or department..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ paddingLeft: '34px' }}
          />
          <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '11px' }} />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: selectedDoctor ? '1fr 1fr' : '1fr', gap: '20px' }}>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Code</th>
                <th>Doctor</th>
                <th>Specialization / Dept</th>
                <th>Fee</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr><td colSpan={5} style={{ textAlign: 'center', padding: '30px' }}>Loading doctors...</td></tr>
              ) : doctors.length === 0 ? (
                <tr><td colSpan={5} style={{ textAlign: 'center', padding: '30px' }}>No doctors found.</td></tr>
              ) : (
                doctors.map(d => (
                  <tr key={d.id} style={{ cursor: 'pointer', backgroundColor: selectedDoctor?.id === d.id ? 'var(--primary-light)' : 'transparent' }} onClick={() => handleSelectDoctor(d)}>
                    <td style={{ fontWeight: 600 }}>{d.doctorCode}</td>
                    <td>{d.fullName}</td>
                    <td>{d.specialization} ({d.department})</td>
                    <td>${d.consultationFee.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${d.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}`}>
                        {d.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {selectedDoctor && (
          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border)', paddingBottom: '12px' }}>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>{selectedDoctor.fullName}</h3>
                <span className="badge badge-info">{selectedDoctor.specialization}</span>
              </div>
              <button onClick={() => setSelectedDoctor(null)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '0.875rem' }}>
              <div><strong>Department:</strong> {selectedDoctor.department}</div>
              <div><strong>Qualification:</strong> {selectedDoctor.qualification}</div>
              <div><strong>Experience:</strong> {selectedDoctor.experienceYears} Years</div>
              <div><strong>Consultation Fee:</strong> ${selectedDoctor.consultationFee.toFixed(2)}</div>
              <div><strong>Email:</strong> {selectedDoctor.email}</div>
              <div><strong>Phone:</strong> {selectedDoctor.phone}</div>
            </div>

            <div style={{ borderTop: '1px solid var(--border)', paddingTop: '12px' }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '8px' }}>Available Schedule Slots ({schedules.length})</h4>
              {schedules.length === 0 ? (
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No regular schedule configured.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {schedules.map((s, idx) => (
                    <div key={idx} style={{ background: '#f8fafc', padding: '10px', borderRadius: '6px', fontSize: '0.85rem', display: 'flex', justifyContent: 'space-between' }}>
                      <span style={{ fontWeight: 600 }}>{s.dayOfWeek}</span>
                      <span>{s.startTime} - {s.endTime}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
