import { useEffect, useState } from "react";
import api from "../api/client";

export default function StudentCourses() {
  const [courses, setCourses] = useState([]);
  const [selected, setSelected] = useState(null);
  const [qualification, setQualification] = useState("");
  const [marks, setMarks] = useState("");
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  useEffect(() => { api.get("/public/courses").then((r) => setCourses(r.data)).catch(() => setError("Could not load courses from the server.")); }, []);
  const submit = async (event) => {
    event.preventDefault(); if (!selected) return;
    setBusy(true); setError(""); setMessage("");
    try {
      const response = await api.post("/student/applications", { courseId: selected.id, qualification, marksPercentage: Number(marks) });
      setMessage(`Application ${response.data.applicationNumber} submitted successfully.`); setSelected(null); setQualification(""); setMarks("");
    } catch (err) { setError(err.response?.data?.detail || err.response?.data?.message || "Application could not be submitted."); }
    finally { setBusy(false); }
  };
  return <div className="page-stack"><header className="page-header"><div><p className="eyebrow">FIND YOUR PROGRAMME</p><h1>Available courses</h1><p className="muted">Explore active programmes and submit your application in a few steps.</p></div><span className="count-chip">{courses.length} programmes</span></header>
    {message && <div className="alert alert-success">{message} <a href="/applications">View applications</a></div>}{error && <div className="alert alert-error">{error}</div>}
    {!courses.length && !error ? <div className="empty-state panel"><h3>No active courses are published yet</h3><p>Check back later or contact the admission office.</p></div> : <div className="course-grid">{courses.map((course) => <article className="course-card" key={course.id}><div className="course-card-top"><span className="course-initial">{course.courseCode?.slice(0, 2) || "CO"}</span><span className="status status-approved">Applications open</span></div><p className="eyebrow">{course.department || "General"}</p><h2>{course.courseName}</h2><p className="course-code">{course.courseCode}</p><div className="course-facts"><div><span>Duration</span><strong>{course.durationYears || 4} years</strong></div><div><span>Seat capacity</span><strong>{course.totalSeats ?? 60}</strong></div><div><span>Credits</span><strong>{course.credits}</strong></div></div><button className="button button-primary button-full" onClick={() => { setSelected(course); setMessage(""); setError(""); }}>Apply to this course <span aria-hidden="true">→</span></button></article>)}</div>}
    {selected && <div className="modal-backdrop" role="presentation" onClick={(e) => { if (e.target === e.currentTarget) setSelected(null); }}><section className="modal panel" role="dialog" aria-modal="true" aria-labelledby="apply-title"><div className="panel-heading"><div><p className="eyebrow">NEW APPLICATION</p><h2 id="apply-title">{selected.courseName}</h2><p className="muted">Enter the academic qualification you are applying with.</p></div><button className="icon-button" onClick={() => setSelected(null)} aria-label="Close">×</button></div><form className="form-stack" onSubmit={submit}><label>Highest qualification<input value={qualification} onChange={(e) => setQualification(e.target.value)} required maxLength={120} placeholder="e.g. Higher Secondary / 12th"/></label><label>Marks percentage<input type="number" min="0" max="100" step="0.01" value={marks} onChange={(e) => setMarks(e.target.value)} required placeholder="e.g. 87.50"/></label>{error && <div className="alert alert-error">{error}</div>}<div className="modal-actions"><button type="button" className="button button-secondary" onClick={() => setSelected(null)}>Cancel</button><button className="button button-primary" disabled={busy}>{busy ? "Submitting…" : "Submit application"}</button></div></form></section></div>}
  </div>;
}
