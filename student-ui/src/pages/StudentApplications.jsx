import { useEffect, useState } from "react";
import api from "../api/client";
import StatusPill from "../components/StatusPill";

export default function StudentApplications() {
  const [applications, setApplications] = useState([]);
  const [error, setError] = useState("");
  useEffect(() => { api.get("/student/applications").then((r) => setApplications(r.data)).catch(() => setError("Could not load applications.")); }, []);
  return <div className="page-stack"><header className="page-header"><div><p className="eyebrow">YOUR SUBMISSIONS</p><h1>My applications</h1><p className="muted">Every application and its latest decision, in one place.</p></div></header>{error && <div className="alert alert-error">{error}</div>}<section className="panel"><div className="table-wrap"><table><thead><tr><th>Application ID</th><th>Programme</th><th>Qualification</th><th>Submitted</th><th>Status</th><th>Decision note</th></tr></thead><tbody>{applications.map((app) => <tr key={app.id}><td><strong>{app.applicationNumber}</strong></td><td>{app.courseName}<small>{app.courseCode}</small></td><td>{app.qualification}<small>{app.marksPercentage}%</small></td><td>{new Date(app.submittedAt).toLocaleDateString()}</td><td><StatusPill status={app.status}/></td><td>{app.decisionNotes || "—"}</td></tr>)}</tbody></table></div>{!applications.length && !error && <div className="empty-state"><h3>No applications yet</h3><p>Once you apply to a course, it will appear here.</p></div>}</section></div>;
}
