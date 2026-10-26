import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import StatCard from "../components/StatCard";
import StatusPill from "../components/StatusPill";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null); const [apps, setApps] = useState([]); const [error, setError] = useState("");
  useEffect(() => { Promise.all([api.get("/admin/dashboard"), api.get("/admin/applications")]).then(([a, b]) => { setStats(a.data); setApps(b.data.slice(0, 5)); }).catch(() => setError("Could not load administrator data.")); }, []);
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!stats) return <div className="page-loading">Loading overview…</div>;
  return <div className="page-stack"><header className="page-header"><div><p className="eyebrow">ADMISSION OFFICE</p><h1>Application overview</h1><p className="muted">A live view of admissions activity across the institution.</p></div><Link to="/admin-applications" className="button button-primary">Review applications <span aria-hidden="true">→</span></Link></header>
    <section className="stats-grid"><StatCard label="Registered students" value={stats.totalStudents}/><StatCard label="Total applications" value={stats.totalApplications}/><StatCard label="Awaiting review" value={stats.submitted}/><StatCard label="Under review" value={stats.underReview}/><StatCard label="Approved" value={stats.approved}/><StatCard label="Rejected" value={stats.rejected}/><StatCard label="Waitlisted" value={stats.waitlisted}/><StatCard label="Active courses" value={stats.activeCourses}/></section>
    <section className="panel"><div className="panel-heading"><div><h2>Latest applications</h2><p className="muted">Recent submissions that may need attention.</p></div><Link className="text-link" to="/admin-applications">All applications →</Link></div>
      {apps.length ? <div className="table-wrap"><table><thead><tr><th>Application</th><th>Applicant</th><th>Course</th><th>Submitted</th><th>Status</th></tr></thead><tbody>{apps.map((a) => <tr key={a.id}><td><strong>{a.applicationNumber}</strong></td><td>{a.studentName}<small>{a.studentEmail}</small></td><td>{a.courseName}</td><td>{new Date(a.submittedAt).toLocaleDateString()}</td><td><StatusPill status={a.status}/></td></tr>)}</tbody></table></div> : <div className="empty-state"><h3>No applications have been submitted yet</h3><p>New student submissions will appear here.</p></div>}
    </section>
  </div>;
}
