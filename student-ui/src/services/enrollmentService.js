// src/services/enrollmentService.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/enrollments";

export const getEnrollments = () => axios.get(API_URL);

export const createEnrollment = (enrollmentData) => axios.post(API_URL, enrollmentData);

export const deleteEnrollment = (id) => axios.delete(`${API_URL}/${id}`);