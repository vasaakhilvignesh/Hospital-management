import React, { useState, useEffect } from 'react';
import { staffService } from '../services/modules';
import { Staff } from '../types';
import { Search, UserCheck } from 'lucide-react';

export const StaffPage: React.FC = () => {
  const [staffList, setStaffList] = useState<Staff[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadStaff = async () => {
      try {
        setLoading(true);
        const data = search.trim() ? await staffService.search(search) : await staffService.getAll();
        setStaffList(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    loadStaff();
  }, [search]);

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Hospital Staff Directory</h1>
          <p className="page-subtitle">Administrative and clinical support staff records</p>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '20px', padding: '16px' }}>
        <div style={{ position: 'relative', maxWidth: '380px' }}>
          <input
            type="text"
            className="form-control"
            placeholder="Search staff by name or department..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ paddingLeft: '34px' }}
          />
          <Search size={16} color="#94a3b8" style={{ position: 'absolute', left: '10px', top: '11px' }} />
        </div>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Code</th>
              <th>Full Name</th>
              <th>Designation</th>
              <th>Department</th>
              <th>Shift Schedule</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '30px' }}>Loading staff...</td></tr>
            ) : staffList.length === 0 ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '30px' }}>No staff records found.</td></tr>
            ) : (
              staffList.map(s => (
                <tr key={s.id}>
                  <td style={{ fontWeight: 600 }}>{s.staffCode}</td>
                  <td>{s.fullName}</td>
                  <td>{s.designation}</td>
                  <td>{s.department}</td>
                  <td>{s.shiftSchedule}</td>
                  <td>
                    <span className={`badge ${s.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}`}>
                      {s.status}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
