import { useEffect, useState } from "react";
import {
  getDepartments,
  createDepartment,
  deleteDepartment,
} from "../services/departmentService";

function Departments() {
  const [departments, setDepartments] = useState([]);
  const [editingId, setEditingId] = useState(null);

  const [formData, setFormData] = useState({
    departmentName: "",
    hodName: "",
  });

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    const response = await getDepartments();
    setDepartments(response.data);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (editingId) {
      await updateDepartment(editingId, formData);
    } else {
      await createDepartment(formData);
    }
    fetchDepartments();
    setFormData({ departmentName: "", hodName: "" }); // Reset both fields
    setEditingId(null);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this department?")) {
      await deleteDepartment(id);
      fetchDepartments();
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-8">
      {/* Sidebar: Department Form */}
      <aside className="w-full lg:w-1/3">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 sticky top-24">
          <h2 className="text-xl font-bold text-slate-800 mb-6">
            {editingId ? "Update Department" : "New Department"}
          </h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                Department Name
              </label>
              <input
                type="text"
                name="departmentName"
                placeholder="e.g. Faculty of Computing"
                value={formData.departmentName}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                HOD Name
              </label>
              <input
                type="text"
                name="hodName"
                placeholder="e.g. Dr. Smith"
                value={formData.hodName}
                onChange={handleChange}
                className="w-full border border-slate-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                required
              />
            </div>

            <div className="pt-2">
              <button
                type="submit"
                className="w-full bg-emerald-600 text-white font-bold py-2.5 rounded-lg hover:bg-emerald-700 transition-all shadow-lg shadow-emerald-100"
              >
                {editingId ? "Save Changes" : "Create Department"}
              </button>
              {editingId && (
                <button
                  type="button"
                  onClick={() => {
                    setEditingId(null);
                    setFormData({ departmentName: "" });
                  }}
                  className="w-full mt-2 text-slate-500 text-sm hover:underline"
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>
      </aside>

      {/* Main Content: Department List */}
      <section className="flex-1">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">
            Departments
          </h1>
          <div className="bg-emerald-100 text-emerald-700 px-4 py-1 rounded-full text-sm font-bold">
            {departments.length} Active
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {departments.map((dept) => (
            <div
              key={dept.id}
              className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm hover:border-emerald-200 transition-colors"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="w-10 h-10 bg-emerald-50 rounded-lg flex items-center justify-center text-emerald-600 font-bold">
                  {dept.departmentName.charAt(0)}
                </div>
                {/* Displaying ID */}
                <span className="text-xs font-mono text-slate-400 bg-slate-50 px-2 py-1 rounded">
                  ID: {dept.id}
                </span>
              </div>

              <div>
                <h3 className="text-lg font-bold text-slate-800 leading-tight">
                  {dept.departmentName}
                </h3>
                {/* Displaying HOD Name */}
                <p className="text-sm text-slate-500 mt-1 flex items-center gap-1">
                  <span className="font-semibold text-slate-700">HOD:</span>{" "}
                  {dept.hodName}
                </p>
              </div>

              <div className="mt-6 flex gap-2 border-t border-slate-50 pt-4">
                <button
                  onClick={() => handleEdit(dept)}
                  className="flex-1 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleDelete(dept.id)}
                  className="flex-1 py-2 text-sm font-semibold text-red-500 hover:bg-red-50 rounded-lg"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        {departments.length === 0 && (
          <div className="text-center py-16 bg-white rounded-xl border border-dashed border-slate-300">
            <p className="text-slate-400 font-medium">
              No departments registered yet.
            </p>
          </div>
        )}
      </section>
    </div>
  );
}

export default Departments;
