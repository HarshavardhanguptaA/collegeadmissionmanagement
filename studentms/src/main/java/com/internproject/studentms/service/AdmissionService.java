package com.internproject.studentms.service;

import com.internproject.studentms.dto.*;
import com.internproject.studentms.entity.*;
import com.internproject.studentms.repository.*;
import com.internproject.studentms.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class AdmissionService {
    private final AdmissionApplicationRepository applications;
    private final AdmissionRecordRepository records;
    private final StudentRepository students;
    private final CourseRepository courses;
    private final UserAccountRepository users;

    public AdmissionService(AdmissionApplicationRepository applications, AdmissionRecordRepository records,
            StudentRepository students, CourseRepository courses, UserAccountRepository users) {
        this.applications = applications; this.records = records; this.students = students; this.courses = courses; this.users = users;
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> activeCourses() {
        return courses.findPublicCourses().stream().map(this::courseDto).toList();
    }

    @Transactional
    public ApplicationView apply(UserPrincipal principal, ApplicationRequest request) {
        Student student = studentFor(principal);
        Course course = courses.findById(request.courseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course was not found"));
        if (Boolean.FALSE.equals(course.getActive())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This course is not accepting applications");
        if (applications.existsByStudent_IdAndCourse_Id(student.getId(), course.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied to this course");
        AdmissionApplication app = new AdmissionApplication();
        app.setStudent(student); app.setCourse(course); app.setQualification(request.qualification().trim());
        app.setMarksPercentage(request.marksPercentage()); app.setStatus(AdmissionStatus.SUBMITTED);
        student.setQualification(request.qualification().trim()); student.setMarksPercentage(request.marksPercentage());
        students.save(student);
        return view(applications.save(app));
    }

    @Transactional(readOnly = true)
    public List<ApplicationView> myApplications(UserPrincipal principal) {
        Student student = studentFor(principal);
        return applications.findByStudent_IdOrderBySubmittedAtDesc(student.getId()).stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public StudentDashboardResponse studentDashboard(UserPrincipal principal) {
        Student student = studentFor(principal);
        List<AdmissionApplication> all = applications.findByStudent_IdOrderBySubmittedAtDesc(student.getId());
        return new StudentDashboardResponse(principal.getFullName(), applications.countByStudent_Id(student.getId()),
            applications.countByStudent_IdAndStatus(student.getId(), AdmissionStatus.SUBMITTED) + applications.countByStudent_IdAndStatus(student.getId(), AdmissionStatus.UNDER_REVIEW),
            applications.countByStudent_IdAndStatus(student.getId(), AdmissionStatus.APPROVED),
            applications.countByStudent_IdAndStatus(student.getId(), AdmissionStatus.REJECTED),
            all.stream().limit(5).map(this::view).toList());
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(UserPrincipal principal) {
        Student student = studentFor(principal);
        return profileDto(student);
    }

    @Transactional
    public StudentProfileResponse updateProfile(UserPrincipal principal, StudentProfileRequest request) {
        Student student = studentFor(principal);
        if (request.firstName() != null && !request.firstName().isBlank()) student.setFirstName(request.firstName().trim());
        if (request.lastName() != null && !request.lastName().isBlank()) student.setLastName(request.lastName().trim());
        if (request.dob() != null) student.setDob(request.dob());
        if (request.gender() != null && !request.gender().isBlank()) student.setGender(request.gender().trim());
        student.setPhone(blankToNull(request.phone()));
        student.setAddress(blankToNull(request.address()));
        if (request.qualification() != null) student.setQualification(blankToNull(request.qualification()));
        if (request.marksPercentage() != null) student.setMarksPercentage(request.marksPercentage());
        student = students.save(student);
        UserAccount user = users.findById(principal.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        user.setFullName(student.getFirstName() + " " + student.getLastName());
        users.save(user);
        return profileDto(student);
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse adminDashboard() {
        return new AdminDashboardResponse(users.countByRole(AccountRole.STUDENT), applications.count(),
            applications.countByStatus(AdmissionStatus.SUBMITTED), applications.countByStatus(AdmissionStatus.UNDER_REVIEW),
            applications.countByStatus(AdmissionStatus.APPROVED), applications.countByStatus(AdmissionStatus.REJECTED),
            applications.countByStatus(AdmissionStatus.WAITLISTED), courses.countPublicCourses());
    }

    @Transactional(readOnly = true)
    public List<ApplicationView> allApplications(String status, String query, Long courseId) {
        List<AdmissionApplication> list = applications.findAllByOrderBySubmittedAtDesc();
        if (status != null && !status.isBlank()) {
            AdmissionStatus parsed;
            try { parsed = AdmissionStatus.valueOf(status.toUpperCase(Locale.ROOT)); }
            catch (IllegalArgumentException ex) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown application status"); }
            list = list.stream().filter(a -> a.getStatus() == parsed).toList();
        }
        if (courseId != null) {
            list = list.stream().filter(a -> a.getCourse().getId().equals(courseId)).toList();
        }
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase(Locale.ROOT);
            list = list.stream().filter(a -> a.getApplicationNumber().toLowerCase(Locale.ROOT).contains(q)
                || (a.getStudent().getFirstName() + " " + a.getStudent().getLastName()).toLowerCase(Locale.ROOT).contains(q)
                || a.getStudent().getEmail().toLowerCase(Locale.ROOT).contains(q)).toList();
        }
        return list.stream().map(this::view).toList();
    }

    @Transactional
    public ApplicationView decide(Long applicationId, ApplicationDecisionRequest request, UserPrincipal admin) {
        AdmissionApplication app = applications.findById(applicationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (app.getStatus() == AdmissionStatus.APPROVED || app.getStatus() == AdmissionStatus.REJECTED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A finalised application cannot be changed");
        AdmissionStatus newStatus = request.status();
        if (newStatus == AdmissionStatus.SUBMITTED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An application cannot be moved back to SUBMITTED");
        if (newStatus == AdmissionStatus.APPROVED) {
            Course lockedCourse = courses.findByIdForUpdate(app.getCourse().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
            long admitted = records.countByApplication_Course_Id(lockedCourse.getId());
            int seats = lockedCourse.getTotalSeats() == null ? 60 : lockedCourse.getTotalSeats();
            if (admitted >= seats) throw new ResponseStatusException(HttpStatus.CONFLICT, "No seats remain for this course. You can waitlist or reject this application.");
            if (records.existsByApplication_Id(app.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "An admission record already exists");
            AdmissionRecord record = new AdmissionRecord(); record.setApplication(app); records.save(record);
        }
        app.setStatus(newStatus);
        app.setDecisionNotes(blankToNull(request.notes()));
        app.setDecisionAt(Instant.now());
        UserAccount decisionMaker = users.findById(admin.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        app.setDecidedBy(decisionMaker);
        return view(applications.save(app));
    }

    @Transactional(readOnly = true)
    public List<AdmissionRecordView> admissionRecords() {
        return records.findAllByOrderByAdmittedAtDesc().stream().map(record -> {
            AdmissionApplication app = record.getApplication();
            Student student = app.getStudent(); Course course = app.getCourse();
            return new AdmissionRecordView(record.getId(), record.getAdmissionNumber(), app.getApplicationNumber(),
                student.getFirstName() + " " + student.getLastName(), student.getEmail(), course.getCourseName(), record.getAdmittedAt());
        }).toList();
    }

    private Student studentFor(UserPrincipal principal) {
        if (principal.getStudentId() == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student profile is not linked to this account");
        return students.findById(principal.getStudentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }
    private ApplicationView view(AdmissionApplication app) {
        Student student = app.getStudent(); Course course = app.getCourse();
        return new ApplicationView(app.getId(), app.getApplicationNumber(), student.getId(), student.getFirstName() + " " + student.getLastName(),
            student.getEmail(), course.getId(), course.getCourseName(), course.getCourseCode(), app.getQualification(), app.getMarksPercentage(),
            app.getStatus(), app.getSubmittedAt(), app.getDecisionNotes(), app.getDecisionAt(), app.getDecidedBy() == null ? null : app.getDecidedBy().getFullName());
    }
    private CourseDTO courseDto(Course course) {
        CourseDTO dto = new CourseDTO(); dto.setId(course.getId()); dto.setCourseName(course.getCourseName()); dto.setCourseCode(course.getCourseCode());
        dto.setCredits(course.getCredits()); dto.setDepartment(course.getDepartment() == null ? "General" : course.getDepartment());
        dto.setDurationYears(course.getDurationYears() == null ? 4 : course.getDurationYears());
        dto.setTotalSeats(course.getTotalSeats() == null ? 60 : course.getTotalSeats()); dto.setActive(course.getActive() == null || course.getActive());
        return dto;
    }
    private StudentProfileResponse profileDto(Student s) {
        return new StudentProfileResponse(s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(), s.getDob(), s.getGender(), s.getPhone(), s.getAddress(), s.getQualification(), s.getMarksPercentage());
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
