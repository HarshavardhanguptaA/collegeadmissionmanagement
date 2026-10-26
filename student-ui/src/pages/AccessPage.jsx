import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { saveSession } from "../api/client";

export default function AccessPage({ mode, onAuthenticated }) {
  const register = mode === "register";
  const [form, setForm] = useState({ firstName: "", lastName: "", email: "", password: "", phone: "", address: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const submit = async (event) => {
    event.preventDefault();
    setError(""); setBusy(true);
    try {
      const endpoint = register ? "/auth/register" : "/auth/login";
      const payload = register ? form : { email: form.email, password: form.password };
      const response = await api.post(endpoint, payload);
      const user = saveSession(response.data);
      onAuthenticated(user);
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.detail || err.response?.data?.message || "Unable to sign in. Check your details and try again.");
    } finally { setBusy(false); }
  };

  return (
    <main className="auth-page">
      <section className="auth-story">
        <div className="brand-mark">CA</div>
        <p className="eyebrow">COLLEGE ADMISSIONS</p>
        <h1>Your next chapter starts here.</h1>
        <p className="auth-description">One clear place to explore programmes, submit your application, and follow every decision.</p>
        <div className="story-foot"><span className="story-line" /> A simpler path to your future</div>
      </section>
      <section className="auth-panel">
        <div className="auth-form-wrap">
          <p className="eyebrow">WELCOME {register ? "ABOARD" : "BACK"}</p>
          <h2>{register ? "Create your account" : "Sign in to your portal"}</h2>
          <p className="muted">{register ? "Create a student account to begin an application." : "Use your student or administrator credentials."}</p>
          <form onSubmit={submit} className="form-stack">
            {register && <div className="field-grid"><label>First name<input name="firstName" value={form.firstName} onChange={update} required autoComplete="given-name" /></label><label>Last name<input name="lastName" value={form.lastName} onChange={update} required autoComplete="family-name" /></label></div>}
            <label>Email address<input type="email" name="email" value={form.email} onChange={update} required autoComplete="email" /></label>
            <label>Password<input type="password" name="password" value={form.password} onChange={update} required minLength={register ? 8 : 1} autoComplete={register ? "new-password" : "current-password"} />{register && <small>Use at least 8 characters.</small>}</label>
            {register && <><label>Phone number <span className="optional">Optional</span><input name="phone" value={form.phone} onChange={update} inputMode="numeric" maxLength={10} placeholder="10-digit phone number" /></label><label>Address <span className="optional">Optional</span><textarea name="address" value={form.address} onChange={update} rows="2" /></label></>}
            {error && <div className="alert alert-error" role="alert">{error}</div>}
            <button className="button button-primary button-full" disabled={busy}>{busy ? "Please wait…" : register ? "Create student account" : "Sign in"}</button>
          </form>
          <p className="auth-switch">{register ? "Already registered? " : "New applicant? "}<Link to={register ? "/login" : "/register"}>{register ? "Sign in" : "Create an account"}</Link></p>
          <p className="security-note">Access to admission records is protected by your account role.</p>
        </div>
      </section>
    </main>
  );
}
