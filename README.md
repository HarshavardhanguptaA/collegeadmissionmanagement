# College Admission Management System

A basic full-stack college admission application built by extending the existing Student Management System repository. The application supports student accounts, a public course catalogue, admission applications, administrator review, and admission-number generation.

## Technology stack

- **Frontend:** React, Vite, React Router, Axios
- **Backend:** Java 17+, Spring Boot, Spring WebMVC, Spring Data JPA, Spring Security, Bean Validation
- **Authentication:** BCrypt password hashing and signed bearer tokens (HS256)
- **Database:** PostgreSQL
- **Build:** Maven Wrapper (`mvnw.cmd` on Windows)

## Features

### Student portal

- Student registration and login
- Student dashboard with database-backed application counts
- View and update profile and academic details
- Browse active courses
- Submit one application per course
- Track application statuses and administrator notes

### Administrator portal

- Role-protected administrator account provisioned from environment variables
- Dashboard with live student, application, status, and course counts
- Create and edit courses, set duration and seat capacity, activate/deactivate courses
- Search and filter admission applications
- Move applications into review, approve, reject, or waitlist applicants
- Unique admission number creation on approval
- Admission records page
- Course capacity checks performed inside a database transaction with a pessimistic course-row lock

The previous student, department, course, and enrollment APIs remain in the codebase. Legacy `/api/**` endpoints are protected as administrator endpoints. Course deletion via the legacy API deactivates the course rather than deleting its history.

## Prerequisites

- Java 17 or newer
- PostgreSQL installed and running
- Node.js compatible with the Vite version in `student-ui/package.json` (Node 20.19+ or 22.12+ is recommended)
- Internet access for the first Maven Wrapper / npm dependency download

You do **not** need a global Maven installation; use `studentms/mvnw.cmd`.

## 1. Create the PostgreSQL database

In pgAdmin's Query Tool connected to your PostgreSQL server, execute:

```sql
CREATE DATABASE college_admission;
```

If the database already exists, do not create it a second time. The backend uses Hibernate `ddl-auto=update` to add the new entity tables/columns without intentionally dropping existing tables. Back up any database containing important information before running schema updates. The additive SQL migration is in `database/V2__admission_management.sql`; run it against an already initialized copy of the original Student Management database if you prefer an explicit SQL schema step. It assumes the original `students` and `courses` tables exist. For a new empty database, start the app once first so Hibernate creates those original tables; do not drop or recreate tables.

## 2. Configure backend variables (Windows PowerShell)

From the project root, set the following in the same PowerShell window you will use to run the backend. Change the password and secret values; do not commit them to Git.

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/college_admission"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "YOUR_POSTGRES_PASSWORD"
$env:APP_JWT_SECRET = "PUT_A_RANDOM_SECRET_OF_AT_LEAST_32_CHARACTERS_HERE"
$env:APP_ADMIN_EMAIL = "admin@college.local"
$env:APP_ADMIN_PASSWORD = "CHOOSE_A_STRONG_ADMIN_PASSWORD"
$env:APP_ADMIN_NAME = "College Administrator"
```

`backend.env.example` contains the same PowerShell variable template. An administrator account is created when the app first starts with both `APP_ADMIN_EMAIL` and `APP_ADMIN_PASSWORD` set. The app will not create a default universal admin account if those values are absent.

## 3. Start the backend

```powershell
cd studentms
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`. The Maven Wrapper downloads its Maven distribution on the first run, so internet access is needed if it is not already cached.

## 4. Add optional starter courses

After the backend has started once and Hibernate has created/updated the tables, run `database/seed_courses.sql` in pgAdmin connected to `college_admission`. These are optional demonstration courses. You can also create courses from the administrator portal.

## 5. Start the React frontend

Open another PowerShell window:

```powershell
cd student-ui
npm ci
npm run dev
```

Open the Vite URL shown in the terminal, normally `http://localhost:5173`. The default API base URL is `http://localhost:8080/api`. To override it, copy `student-ui/.env.example` to `student-ui/.env.local` and set `VITE_API_URL`.

## Main API routes

### Public and authentication

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Create a student account |
| POST | `/api/auth/login` | Authenticate and obtain a bearer token |
| GET | `/api/auth/me` | Return the current account |
| GET | `/api/public/courses` | List active courses |

### Student role

| Method | Route | Purpose |
|---|---|---|
| GET | `/api/student/dashboard` | Student application summary |
| GET / PUT | `/api/student/profile` | Read or update the authenticated student's profile |
| GET | `/api/student/applications` | List only the current student's applications |
| POST | `/api/student/applications` | Submit an application |

Application submission body example:

```json
{
  "courseId": 1,
  "qualification": "Higher Secondary / 12th",
  "marksPercentage": 87.5
}
```

### Administrator role

| Method | Route | Purpose |
|---|---|---|
| GET | `/api/admin/dashboard` | Live dashboard counts |
| GET | `/api/admin/applications?status=SUBMITTED&courseId=1&q=APP-` | Search and filter applications by status, course, student, email, or application number |
| PATCH | `/api/admin/applications/{id}/decision` | Record a review decision |
| GET / POST | `/api/admin/courses` | List or create courses |
| PUT | `/api/admin/courses/{id}` | Update a course or its availability |
| GET | `/api/admin/admissions` | List generated admission records |

Example decision body:

```json
{
  "status": "APPROVED",
  "notes": "Eligibility verified."
}
```

Valid statuses are `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, and `WAITLISTED`. Students cannot change application status. Finalised approved/rejected applications cannot be changed through the decision API.

## Security and data notes

- Passwords are stored as BCrypt hashes, never in plaintext.
- The administrator role is provisioned from environment settings.
- All student and administrator API endpoints require a valid signed bearer token, except registration, login, and the public catalogue.
- Student ownership is determined from the authenticated account, not from a client-supplied student ID.
- Database credentials and JWT secret are read from environment variables.
- Approval runs transactionally and locks the course row while checking seat capacity. Admission numbers and approved application records have database uniqueness constraints.
- Tokens are stored in browser local storage in this basic college-project implementation. For higher-security production use, review token storage, refresh/revocation strategy, rate limiting, audit requirements, and deployment secrets.

## Validation checklist

Run these from the project folders after dependencies are available:

```powershell
# Backend
cd studentms
.\mvnw.cmd clean test
.\mvnw.cmd package

# Frontend (in a separate PowerShell window or after the backend commands finish)
cd student-ui
npm ci
npm run build
npm run lint
```

Test manually with both roles:

1. Register a student account and sign in.
2. Browse active courses and submit an application.
3. Confirm that the application appears on the student dashboard and in My Applications.
4. Sign in using the provisioned administrator account.
5. Review an application and approve it; verify an admission number is created.
6. Attempt duplicate applications and approval when a course is full; verify the API returns a meaningful error.
7. Confirm that a student cannot access administrator routes or another student's applications.

## Project structure

```text
StudentManagementSystem-master/
├── database/V2__admission_management.sql
├── database/seed_courses.sql
├── backend.env.example
├── studentms/       # Spring Boot backend and Maven Wrapper
└── student-ui/      # React + Vite frontend
```
