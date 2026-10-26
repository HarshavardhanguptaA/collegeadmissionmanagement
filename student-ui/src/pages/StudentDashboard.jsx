import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/client";
import StatusPill from "../components/StatusPill";
import StatCard from "../components/StatCard";

export default function StudentDashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  useEffect(() => { api.get("/student/dashboard").then((r) => setData(r.data)).catch(() => setError("Could not load your dashboard. Please sign in again.")); }, []);
  if (error) return <div className="alert alert-error">{error}</div>;
  if (!data) return <div className="page-loading">Loading your dashboard…</div>;
  return <div className="page-stack">
    <header className="page-header"><div><p className="eyebrow">STUDENT PORTAL</p><h1>Welcome, {data.fullName?.split(" ")[0]}</h1><p className="muted">Keep your admission journey moving. Your latest updates are below.</p></div><Link to="/courses" className="button button-primary">Explore courses <span aria-hidden="true">→</span></Link></header>
    <section className="stats-grid"><StatCard label="Applications" value={data.totalApplications} note="All submitted applications"/><StatCard label="Awaiting review" value={data.awaitingReview} note="Submitted or under review"/><StatCard label="Approved" value={data.approved} note="Admission decisions"/><StatCard label="Rejected" value={data.rejected} note="View application notes"/></section>
    <section className="panel"><div className="panel-heading"><div><h2>Recent applications</h2><p className="muted">Track the status of your recent submissions.</p></div><Link className="text-link" to="/applications">View all <span aria-hidden="true">→</span></Link></div>
      {data.recentApplications?.length ? <div className="table-wrap"><table><thead><tr><th>Application</th><th>Programme</th><th>Submitted</th><th>Status</th></tr></thead><tbody>{data.recentApplications.map((item) => <tr key={item.id}><td><strong>{item.applicationNumber}</strong></td><td>{item.courseName}</td><td>{new Date(item.submittedAt).toLocaleDateString()}</td><td><StatusPill status={item.status}/></td></tr>)}</tbody></table></div> : <div className="empty-state"><div className="empty-icon">01</div><h3>Your application journey starts here</h3><p>You haven't submitted an application yet. Browse active programmes to get started.</p><Link to="/courses" className="button button-secondary">Browse courses</Link></div>}
    </section>
  </div>;
}
