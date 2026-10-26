import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("admissionToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export function saveSession(authResponse) {
  if (authResponse.accessToken) localStorage.setItem("admissionToken", authResponse.accessToken);
  const user = {
    userId: authResponse.userId,
    fullName: authResponse.fullName,
    email: authResponse.email,
    role: authResponse.role,
    studentId: authResponse.studentId,
  };
  localStorage.setItem("admissionUser", JSON.stringify(user));
  return user;
}

export function clearSession() {
  localStorage.removeItem("admissionToken");
  localStorage.removeItem("admissionUser");
}

export default api;
