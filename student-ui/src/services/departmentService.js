// src/services/departmentService.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/departments";

export const getDepartments = () => axios.get(API_URL);

export const createDepartment = (deptData) => axios.post(API_URL, deptData);

export const deleteDepartment = (id) => axios.delete(`${API_URL}/${id}`);