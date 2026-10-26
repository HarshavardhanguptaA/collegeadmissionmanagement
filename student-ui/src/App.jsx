import { useEffect, useState } from "react";
import { BrowserRouter, Navigate, NavLink, Route, Routes, useNavigate } from "react-router-dom";
import api, { clearSession } from "./api/client";
import AccessPage from "./pages/AccessPage";
import StudentDashboard from "./pages/StudentDashboard";
import StudentCourses from "./pages/StudentCourses";
import StudentApplications from "./pages/StudentApplications";
import StudentProfile from "./pages/StudentProfile";
import AdminDashboard from "./pages/AdminDashboard";
import AdminApplications from "./pages/AdminApplications";
import AdminCourses from "./pages/AdminCourses";
import AdminAdmissions from "./pages/AdminAdmissions";
import "./App.css";

function AccessRoutes({ onAuthenticated }) {
  return <Routes>
    <Route path="/login" element={<AccessPage mode="login" onAuthenticated={onAuthenticated} />} />
    <Route path="/register" element={<AccessPage mode="register" onAuthenticated={onAuthenticated} />} />
    <Route path="*" element={<Navigate to="/login" replace />} />
  </Routes>;
}

function PortalShell({ user, onLogout, onUserUpdated }) {
  const admin = user.role === "ADMIN";
  const links = admin
    ? [{ to: "/", label: "Overview", end: true }, { to: "/admin-applications", label: "Applications" }, { to: "/courses", label: "Course management" }, { to: "/admissions", label: "Admission records" }]
    : [{ to: "/", label: "Dashboard", end: true }, { to: "/courses", label: "Explore courses" }, { to: "/applications", label: "My applications" }, { to: "/profile", label: "My profile" }];
  const navigate = useNavigate();
  const logout = () => { clearSession(); onLogout(); navigate("/login"); };
  return <div className="app-layout">
    <aside className="sidebar">
      <div className="brand-lockup"><span className="brand-mark small-mark">CA</span><div><strong>CampusPath</strong><small>ADMISSION SYSTEM</small></div></div>
      <div className="sidebar-context"><span className="eyebrow">WORKSPACE</span><strong>{admin ? "Admission office" : "Student portal"}</strong></div>
      <nav className="sidebar-nav" aria-label="Main navigation">{links.map((link) => <NavLink key={link.to} to={link.to} end={link.end} className={({ isActive }) => `nav-link${isActive ? " nav-link-active" : ""}`}><span className="nav-dot" />{link.label}</NavLink>)}</nav>
      <div className="sidebar-bottom"><div className="help-card"><strong>Need assistance?</strong><p>Contact the admission office for help with your application.</p></div><button className="user-menu" onClick={logout}><span className="avatar">{user.fullName?.split(" ").map((part) => part[0]).slice(0, 2).join("").toUpperCase()}</span><span className="user-meta"><strong>{user.fullName}</strong><small>{user.role === "ADMIN" ? "Administrator" : "Student account"}</small></span><span className="logout-mark" aria-hidden="true">↗</span></button></div>
    </aside>
    <div className="main-column"><header className="topbar"><div><span className="topbar-label">ACADEMIC YEAR</span><strong>2026–27 Admissions</strong></div><div className="topbar-right"><span className="secure-indicator"><span className="secure-dot"/> Secure portal</span><span className="topbar-avatar">{user.fullName?.charAt(0)?.toUpperCase()}</span></div></header><main className="content-area"><Routes>
      <Route path="/" element={admin ? <AdminDashboard /> : <StudentDashboard />} />
      <Route path="/courses" element={admin ? <AdminCourses /> : <StudentCourses />} />
      <Route path="/applications" element={admin ? <Navigate to="/admin-applications" replace /> : <StudentApplications />} />
      <Route path="/admin-applications" element={admin ? <AdminApplications /> : <Navigate to="/" replace />} />
      <Route path="/admissions" element={admin ? <AdminAdmissions /> : <Navigate to="/" replace />} />
      <Route path="/profile" element={admin ? <Navigate to="/" replace /> : <StudentProfile onProfileUpdated={(profile) => onUserUpdated({ ...user, fullName: `${profile.firstName} ${profile.lastName}`.trim() })} />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes></main><footer className="site-footer"><span>CampusPath · College Admission Management System</span><span>Secure, clear, student-focused</span></footer></div>
  </div>;
}

function AppContent() {
  const [user, setUser] = useState(() => {
    if (!localStorage.getItem("admissionToken")) return null;
    try { return JSON.parse(localStorage.getItem("admissionUser")) || null; } catch { return null; }
  });
  const [checking, setChecking] = useState(Boolean(localStorage.getItem("admissionToken")));
  useEffect(() => {
    if (!localStorage.getItem("admissionToken")) return;
    let active = true;
    api.get("/auth/me").then((response) => {
      const nextUser = { userId: response.data.userId, fullName: response.data.fullName, email: response.data.email, role: response.data.role, studentId: response.data.studentId };
      if (!active) return;
      localStorage.setItem("admissionUser", JSON.stringify(nextUser)); setUser(nextUser);
    }).catch(() => { if (active) { clearSession(); setUser(null); } }).finally(() => { if (active) setChecking(false); });
    return () => { active = false; };
  }, []);
  const authenticated = (nextUser) => setUser(nextUser);
  const updatedUser = (nextUser) => { setUser(nextUser); localStorage.setItem("admissionUser", JSON.stringify(nextUser)); };
  if (checking) return <div className="full-loading"><div className="loading-spinner"/><p>Checking your session…</p></div>;
  return <BrowserRouter>
    {user ? <PortalShell user={user} onLogout={() => setUser(null)} onUserUpdated={updatedUser} /> : <AccessRoutes onAuthenticated={authenticated} />}
  </BrowserRouter>;
}

export default function App() { return <AppContent />; }
