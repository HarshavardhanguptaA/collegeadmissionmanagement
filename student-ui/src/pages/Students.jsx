import { useEffect, useState } from "react";
import {
  getStudents,
  createStudent,
  deleteStudent,
  updateStudent,
} from "../services/studentService";
import { getDepartments } from "../services/departmentService";

function Students() {
  const [students, setStudents] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [departments, setDepartments] = useState([]);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    gender: "",
    departmentId: "",
  });

  useEffect(() => {
    fetchStudents();
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    const response = await getDepartments();
    setDepartments(response.data);
  };

  const fetchStudents = async () => {
    const response = await getStudents();
    setStudents(response.data);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleEdit = (student) => {
    setEditingId(student.id);
    setFormData({
      firstName: student.firstName,
      lastName: student.lastName,
      email: student.email,
      gender: student.gender,
      departmentId: student.department?.id || "",
    });
    // Optional: Scroll to top of form when editing
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (editingId) {
      await updateStudent(editingId, formData);
    } else {
      await createStudent(formData);
    }
    fetchStudents();
    setFormData({
      firstName: "",
      lastName: "",
      email: "",
      gender: "",
      departmentId: "",
    });
    setEditingId(null);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this student?")) {
      await deleteStudent(id);
      fetchStudents();
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-8">
      {/* Sidebar: Form Section */}
      <aside className="w-full lg:w-1/3">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 sticky top-24">
          <h2 className="text-xl font-bold text-slate-800 mb-6">
            {editingId ? "Edit Student" : "Register Student"}
          </h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                First Name
              </label>
              <input
                type="text"
                name="firstName"
                placeholder="John"
                value={formData.firstName}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Last Name
              </label>
              <input
                type="text"
                name="lastName"
                placeholder="Doe"
                value={formData.lastName}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Email Address
              </label>
              <input
                type="email"
                name="email"
                placeholder="john.doe@example.com"
                value={formData.email}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Gender
                </label>
                <input
                  type="text"
                  name="gender"
                  placeholder="Male/Female"
                  value={formData.gender}
                  onChange={handleChange}
                  className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">
                  Department
                </label>
                <select
                  name="departmentId"
                  value={formData.departmentId}
                  onChange={handleChange}
                  className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                  required
                >
                  <option value="">Select</option>
                  {departments.map((dept) => (
                    <option key={dept.id} value={dept.id}>
                      {dept.departmentName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="pt-2">
              <button
                type="submit"
                className="w-full bg-blue-600 text-white font-bold py-2.5 rounded-lg hover:bg-blue-700 transition-colors shadow-lg shadow-blue-100"
              >
                {editingId ? "Update Student Profile" : "Add Student"}
              </button>
              {editingId && (
                <button
                  type="button"
                  onClick={() => {
                    setEditingId(null);
                    setFormData({
                      firstName: "",
                      lastName: "",
                      email: "",
                      gender: "",
                      departmentId: "",
                    });
                  }}
                  className="w-full mt-2 text-slate-500 text-sm hover:underline"
                >
                  Cancel Edit
                </button>
              )}
            </div>
          </form>
        </div>
      </aside>

      {/* Main Content: Student List Section */}
      <section className="flex-1">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Student Records
          </h1>
          <div className="bg-blue-100 text-blue-700 px-4 py-1 rounded-full text-sm font-bold">
            {students.length} Total
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {students.map((student) => (
            <div
              key={student.id}
              className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm hover:shadow-md transition-shadow relative group"
            >
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-xl font-bold text-slate-800">
                    {student.firstName} {student.lastName}
                  </h3>
                  <p className="text-blue-600 font-medium text-sm mb-2">
                    {student.email}
                  </p>
                  <div className="flex gap-2">
                    <span className="text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded">
                      {student.gender || "N/A"}
                    </span>
                    <span className="text-xs bg-indigo-50 text-indigo-600 px-2 py-1 rounded font-semibold">
                      {student.departmentName || "No Department"}
                    </span>
                  </div>
                </div>
              </div>

              <div className="mt-6 flex gap-3 border-t border-slate-100 pt-4">
                <button
                  onClick={() => handleEdit(student)}
                  className="flex-1 text-sm font-bold text-blue-600 hover:bg-blue-50 py-2 rounded-lg transition-colors"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(student.id)}
                  className="flex-1 text-sm font-bold text-red-500 hover:bg-red-50 py-2 rounded-lg transition-colors"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        {students.length === 0 && (
          <div className="text-center py-20 bg-white rounded-2xl border border-dashed border-slate-300">
            <p className="text-slate-400">
              No student records found. Add one on the left!
            </p>
          </div>
        )}
      </section>
    </div>
  );
}

export default Students;
