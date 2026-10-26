import { useEffect, useState } from "react";
import {
  getEnrollments,
  createEnrollment,
  deleteEnrollment,
} from "../services/enrollmentService";
import { getStudents } from "../services/studentService";
import { getCourses } from "../services/courseService";

function Enrollments() {
  const [enrollments, setEnrollments] = useState([]);
  const [students, setStudents] = useState([]);
  const [courses, setCourses] = useState([]);

  // State aligned with backend EnrollmentDTO
  const [formData, setFormData] = useState({
    studentId: "",
    courseId: "",
    semester: "",
    grade: "",
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [enrolRes, studRes, courRes] = await Promise.all([
        getEnrollments(),
        getStudents(),
        getCourses(),
      ]);
      setEnrollments(enrolRes.data);
      setStudents(studRes.data);
      setCourses(courRes.data);
    } catch (error) {
      console.error("Error fetching enrollment data:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Convert IDs to numbers for the backend
    const payload = {
      studentId: Number(formData.studentId),
      courseId: Number(formData.courseId),
      semester: formData.semester.trim(),
      grade: formData.grade.trim(),
    };

    try {
      await createEnrollment(payload);
      fetchData(); // Refresh the active enrollments table
      setFormData({
        studentId: "",
        courseId: "",
        semester: "",
        grade: "",
      });
    } catch (error) {
      console.error("Enrollment submission failed:", error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to remove this enrollment?")) {
      try {
        await deleteEnrollment(id);
        fetchData();
      } catch (error) {
        console.error("Error deleting enrollment:", error);
      }
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-8">
      {/* Sidebar: Enrollment Form */}
      <aside className="w-full lg:w-1/3">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 sticky top-24">
          <h2 className="text-xl font-bold text-slate-800 mb-6 tracking-tight">
            New Enrollment
          </h2>

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Select Student
              </label>
              <select
                name="studentId"
                value={formData.studentId}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none bg-white transition-all"
                required
              >
                <option value="">Choose a student...</option>
                {students.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.firstName} {s.lastName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Select Course
              </label>
              <select
                name="courseId"
                value={formData.courseId}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 outline-none bg-white transition-all"
                required
              >
                <option value="">Choose a course...</option>
                {courses.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.courseName} ({c.courseCode})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Semester
              </label>
              <input
                type="text"
                name="semester"
                placeholder="e.g. Spring 2026"
                value={formData.semester}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-500 outline-none"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Grade (Optional)
              </label>
              <input
                type="text"
                name="grade"
                placeholder="e.g. A+"
                value={formData.grade}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-indigo-500 outline-none"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-indigo-600 text-white font-bold py-3 rounded-lg hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-100 flex items-center justify-center gap-2"
            >
              Confirm Enrollment
            </button>
          </form>
        </div>
      </aside>

      {/* Main Content: Enrollment List Table */}
      <section className="flex-1">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Active Enrollments
          </h1>
          <div className="bg-indigo-100 text-indigo-700 px-4 py-1 rounded-full text-sm font-bold">
            {enrollments.length} Records
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <table className="w-full text-left border-collapse">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                  Student
                </th>
                <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                  Course
                </th>
                <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                  Semester
                </th>
                <th className="px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                  Grade
                </th>
                <th className="px-6 py-4 text-right"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {enrollments.map((enrol) => (
                <tr
                  key={enrol.id}
                  className="hover:bg-slate-50 transition-colors"
                >
                  <td className="px-6 py-4">
                    <div className="font-bold text-slate-800">
                      {/* Use studentName directly from the DTO */}
                      {enrol.studentName}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {/* Use courseName directly from the DTO */}
                      {enrol.courseName}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-600 font-medium">
                    {enrol.semester}
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm font-bold text-slate-900">
                      {enrol.grade || "N/A"}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button
                      onClick={() => handleDelete(enrol.id)}
                      className="text-slate-400 hover:text-red-500 p-2 rounded-lg hover:bg-red-50 transition-all font-medium text-sm"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {enrollments.length === 0 && (
            <div className="text-center py-16 text-slate-400 border-t border-slate-100 italic">
              No active enrollments found.
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

export default Enrollments;
