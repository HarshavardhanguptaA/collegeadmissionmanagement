-- Optional starter data for a new college_admission database.
-- Run this after the Spring Boot application has created/updated the courses table.
INSERT INTO courses (course_name, course_code, credits, department, duration_years, total_seats, active)
VALUES
  ('B.E. Computer Science and Engineering', 'BE-CSE', 160, 'Computer Science', 4, 120, TRUE),
  ('B.E. Electronics and Communication Engineering', 'BE-ECE', 160, 'Electronics and Communication', 4, 90, TRUE),
  ('B.Tech Information Technology', 'BTECH-IT', 160, 'Information Technology', 4, 90, TRUE),
  ('B.E. Mechanical Engineering', 'BE-MECH', 160, 'Mechanical Engineering', 4, 60, TRUE)
ON CONFLICT (course_code) DO NOTHING;
