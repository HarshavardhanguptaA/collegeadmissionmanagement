// src/pages/Courses.jsx
import { useEffect, useState } from "react";
import {
  getCourses,
  createCourse,
  deleteCourse,
} from "../services/courseService";

function Courses() {
  const [courses, setCourses] = useState([]);
  const [formData, setFormData] = useState({
    courseName: "",
    courseCode: "",
    credits: "",
  });

  useEffect(() => {
    fetchCourses();
  }, []);

  const fetchCourses = async () => {
    try {
      const res = await getCourses();
      setCourses(res.data);
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Destructure for cleaner code
    const { courseName, courseCode, credits } = formData;

    // Improved validation logic
    if (
      !courseName.trim() ||
      !courseCode.trim() ||
      credits === "" ||
      credits === null
    ) {
      alert("Please fill all fields");
      return;
    }

    try {
      const payload = {
        courseName: courseName.trim(),
        courseCode: courseCode.trim(),
        credits: Number(credits),
      };

      await createCourse(payload);
      fetchCourses();
      setFormData({ courseName: "", courseCode: "", credits: "" });
    } catch (error) {
      console.error("Error submitting course:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteCourse(id);
      fetchCourses();
    } catch (error) {
      console.error("Error deleting course:", error);
    }
  };

  return (
    <div className="flex flex-col lg:flex-row gap-8">
      {/* Sidebar: Entry Form */}
      <aside className="w-full lg:w-1/3">
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 sticky top-24">
          <h2 className="text-lg font-bold text-gray-800 mb-4">
            Add New Course
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-500 uppercase mb-1">
                Course Name
              </label>
              <input
                type="text"
                name="courseName"
                value={formData.courseName}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                placeholder="e.g. Computer Science"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-500 uppercase mb-1">
                Course Code
              </label>
              <input
                type="text"
                name="courseCode"
                value={formData.courseCode}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                placeholder="CS101"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-500 uppercase mb-1">
                Credits
              </label>
              <input
                type="number"
                name="credits"
                value={formData.credits}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                placeholder="e.g. 3"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white font-bold py-2 rounded-lg hover:bg-blue-700 transition-colors shadow-md shadow-blue-100"
            >
              Create Course
            </button>
          </form>
        </div>
      </aside>

      {/* Main Content: Course List */}
      <section className="flex-1">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold text-gray-900">
            Available Courses
          </h1>
          <span className="bg-blue-50 text-blue-700 px-3 py-1 rounded-full text-sm font-medium">
            {courses.length} Total
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {courses.map((course) => (
            <div
              key={course.id}
              className="bg-white p-5 rounded-xl border border-gray-200 hover:border-blue-300 transition-colors shadow-sm"
            >
              <h3 className="font-bold text-gray-800">{course.courseName}</h3>
              <p className="text-sm text-gray-500">
                {course.courseCode} • {course.credits} Credits
              </p>
              <button
                onClick={() => handleDelete(course.id)}
                className="mt-4 text-xs font-bold text-red-500 hover:text-red-700 transition-colors uppercase tracking-wider"
              >
                Delete Course
              </button>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Courses;
