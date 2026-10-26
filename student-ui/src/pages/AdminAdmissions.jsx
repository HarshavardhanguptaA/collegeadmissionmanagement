import { useEffect, useState } from "react";
import api from "../api/client";

export default function AdminAdmissions() {
  const [records, setRecords] = useState([]); const [error, setError] = useState("");
  useEffect(() => { api.get("/admin/admissions").then((r) => setRecords(r.data)).catch(() => setError("Could not load admission records.")); }, []);
  return <div className="page-stack"><header className="page-header"><div><p className="eyebrow">CONFIRMED PLACES</p><h1>Admission records</h1><p className="muted">Students with approved applications and unique admission numbers.</p></div><span className="count-chip">{records.length} admitted</span></header>{error && <div className="alert alert-error">{error}</div>}<section className="panel"><div className="table-wrap"><table><thead><tr><th>Admission number</th><th>Applicant</th><th>Course</th><th>Application</th><th>Admitted on</th></tr></thead><tbody>{records.map((record) => <tr key={record.id}><td><strong>{record.admissionNumber}</strong></td><td>{record.studentName}<small>{record.studentEmail}</small></td><td>{record.courseName}</td><td>{record.applicationNumber}</td><td>{new Date(record.admittedAt).toLocaleDateString()}</td></tr>)}</tbody></table></div>{!records.length && !error && <div className="empty-state"><h3>No admission records yet</h3><p>Records are created when an administrator approves an application.</p></div>}</section></div>;
}
