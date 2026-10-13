import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { reportService } from '../services/modules';
import { Report } from '../types';
import { BarChart3, Plus, Archive, Eye, X } from 'lucide-react';

export const ReportsPage: React.FC = () => {
  const { user } = useAuth();
  const [reports, setReports] = useState<Report[]>([]);
  const [loading, setLoading] = useState(true);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [selectedReport, setSelectedReport] = useState<Report | null>(null);

  const [formData, setFormData] = useState({
    patientId: 1,
    reportType: 'MEDICAL_SUMMARY',
    title: 'Hospital Patient Health Summary',
    description: 'Dynamic inter-service aggregated report generated via Eureka'
  });

  const loadReports = async () => {
    try {
      setLoading(true);
      if (user?.role === 'PATIENT') {
        const data = user.patientId
          ? await reportService.getReportsByPatient(user.patientId)
          : await reportService.getReports();
        setReports(data);
      } else {
        const data = await reportService.getReports();
        setReports(data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadReports();
  }, [user]);

  const handleGenerateReport = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await reportService.generateReport({
        ...formData,
        patientId: Number(formData.patientId)
      });
      setShowGenerateModal(false);
      loadReports();
    } catch (err: any) {
      alert(err.message || 'Failed to generate report');
    }
  };

  const handleArchive = async (id: number) => {
    if (window.confirm('Archive this clinical report?')) {
      try {
        await reportService.archiveReport(id);
        loadReports();
      } catch (err: any) {
        alert(err.message || 'Failed to archive report');
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Clinical & Operational Reports</h1>
          <p className="page-subtitle">Aggregated medical, appointment, and financial health intelligence</p>
        </div>
        {user?.role !== 'PATIENT' && (
          <button onClick={() => setShowGenerateModal(true)} className="btn btn-primary">
            <Plus size={16} />
            <span>Generate Report</span>
          </button>
        )}
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Report Code</th>
              <th>Type</th>
              <th>Title</th>
              <th>Patient</th>
              <th>Date</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '30px' }}>Loading reports...</td></tr>
            ) : reports.length === 0 ? (
              <tr><td colSpan={7} style={{ textAlign: 'center', padding: '30px' }}>No reports found.</td></tr>
            ) : (
              reports.map(r => (
                <tr key={r.id}>
                  <td style={{ fontWeight: 600 }}>{r.reportCode}</td>
                  <td><span className="badge badge-info">{r.reportType}</span></td>
                  <td>{r.title}</td>
                  <td>{r.patientId ? `Patient #${r.patientId}` : 'Hospital-wide'}</td>
                  <td>{r.reportDate}</td>
                  <td>
                    <span className={`badge ${r.status === 'AVAILABLE' ? 'badge-success' : 'badge-neutral'}`}>
                      {r.status}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '6px' }}>
                      <button onClick={() => setSelectedReport(r)} className="btn btn-secondary btn-sm" title="View Data">
                        <Eye size={14} />
                      </button>
                      {user?.role !== 'PATIENT' && r.status === 'AVAILABLE' && (
                        <button onClick={() => handleArchive(r.id)} className="btn btn-danger btn-sm" title="Archive">
                          <Archive size={14} />
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

      {/* Report Data Details View */}
      {selectedReport && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>{selectedReport.title} ({selectedReport.reportCode})</h3>
              <button onClick={() => setSelectedReport(null)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <div className="modal-body">
              <p style={{ marginBottom: '12px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>{selectedReport.description}</p>
              <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '6px', border: '1px solid var(--border)' }}>
                <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-all', fontSize: '0.825rem', fontFamily: 'monospace' }}>
                  {(() => {
                    try {
                      return JSON.stringify(JSON.parse(selectedReport.data || '{}'), null, 2);
                    } catch {
                      return selectedReport.data || 'No payload data attached.';
                    }
                  })()}
                </pre>
              </div>
            </div>
            <div className="modal-footer">
              <button onClick={() => setSelectedReport(null)} className="btn btn-secondary">Close</button>
            </div>
          </div>
        </div>
      )}

      {/* Generate Report Modal */}
      {showGenerateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 600 }}>Generate Microservice Report</h3>
              <button onClick={() => setShowGenerateModal(false)} className="btn btn-secondary btn-sm"><X size={16} /></button>
            </div>
            <form onSubmit={handleGenerateReport}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Report Type *</label>
                  <select
                    className="form-control"
                    value={formData.reportType}
                    onChange={e => setFormData({ ...formData, reportType: e.target.value })}
                  >
                    <option value="MEDICAL_SUMMARY">MEDICAL_SUMMARY (Patient History & Info)</option>
                    <option value="APPOINTMENT_SUMMARY">APPOINTMENT_SUMMARY (Patient Appointments)</option>
                    <option value="BILLING_SUMMARY">BILLING_SUMMARY (Patient Invoices & Balances)</option>
                    <option value="GENERAL">GENERAL (Hospital Analytics)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Target Patient ID</label>
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    value={formData.patientId}
                    onChange={e => setFormData({ ...formData, patientId: Number(e.target.value) })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Report Title *</label>
                  <input
                    type="text"
                    className="form-control"
                    required
                    value={formData.title}
                    onChange={e => setFormData({ ...formData, title: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Description</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.description}
                    onChange={e => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowGenerateModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary">Generate Live Report</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
