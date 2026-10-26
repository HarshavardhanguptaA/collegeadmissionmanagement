// src/services/courseService.js
import axios from "axios";

const API_URL = "http://localhost:8080/api/courses";

export const getCourses = () => axios.get(API_URL);

export const createCourse = (courseData) => axios.post(API_URL, courseData);

export const deleteCourse = (id) => axios.delete(`${API_URL}/${id}`);