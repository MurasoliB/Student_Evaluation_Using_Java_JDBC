import java.sql.*;
import java.util.*;

/**
 * AcademicDAO - Data Access Object and Evaluation Engine
 * Kongu Engineering College (Autonomous) - Regulation 2024
 */
public class AcademicDAO {

    // =========================================================================
    // Data Transfer Objects (DTOs)
    // =========================================================================

    public static class Student {
        public String rollNo;
        public String name;
        public String department;
        public int currentSemester;

        public Student(String rollNo, String name, String department, int currentSemester) {
            this.rollNo = rollNo;
            this.name = name;
            this.department = department;
            this.currentSemester = currentSemester;
        }
    }

    public static class Course {
        public String courseCode;
        public String title;
        public int credits;
        public String courseType; // THEORY, PRACTICAL, EMBEDDED
        public int semester;

        public Course(String courseCode, String title, int credits, String courseType, int semester) {
            this.courseCode = courseCode;
            this.title = title;
            this.credits = credits;
            this.courseType = courseType;
            this.semester = semester;
        }
    }

    public static class EnrollmentRecord {
        public int enrollmentId;
        public String rollNo;
        public String studentName;
        public String department;
        public String courseCode;
        public String courseTitle;
        public String courseType;
        public int credits;
        public int semester;
        public int totalClasses;
        public int attendedClasses;
        public boolean isCondoned;
        public Double cia1;
        public Double cia2;
        public Double assignment;
        public Double eseMarks;
        public boolean isEseAbsent;
    }

    public static class CourseEvaluationResult {
        public String courseCode;
        public String courseTitle;
        public String courseType;
        public int credits;
        public int totalClasses;
        public int attendedClasses;
        public double attendancePct;
        public String attendanceStatus; // ELIGIBLE, CONDONED, DEFICIENT / SA

        public Double cia1;
        public Double cia2;
        public Double assignment;
        public double rawCia100;
        public double scaledCia;
        public double ciaWeightage;

        public Double eseMarks;
        public double scaledEse;
        public double eseWeightage;
        public boolean isEseAbsent;
        public boolean eseThresholdMet;

        public double totalMarks;
        public String letterGrade;
        public int gradePoint;
        public String resultStatus;
        public boolean isPass;
    }

    public static class SemesterReport {
        public Student student;
        public int semester;
        public List<CourseEvaluationResult> courses = new ArrayList<>();
        public int totalEnrolledCredits;
        public int totalEarnedCredits;
        public double totalGradePointsEarned;
        public double sgpa;
        public double percentage;
        public boolean allCoursesPassed;
    }

    // =========================================================================
    // 1. Student & Course Management (CRUD)
    // =========================================================================

    public boolean addStudent(String rollNo, String name, String department, int currentSemester) {
        String sql = "INSERT INTO students (roll_no, name, department, current_semester) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = VALUES(name), department = VALUES(department), current_semester = VALUES(current_semester)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setString(2, name.trim());
            ps.setString(3, department.trim().toUpperCase());
            ps.setInt(4, currentSemester);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add student: " + e.getMessage());
            return false;
        }
    }

    public Student getStudent(String rollNo) {
        String sql = "SELECT roll_no, name, department, current_semester FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getInt("current_semester")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch student: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT roll_no, name, department, current_semester FROM students ORDER BY roll_no ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getInt("current_semester")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch students: " + e.getMessage());
        }
        return list;
    }

    public boolean addCourse(String courseCode, String title, int credits, String courseType, int semester) {
        String sql = "INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE title = VALUES(title), credits = VALUES(credits), course_type = VALUES(course_type), semester = VALUES(semester)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode.trim().toUpperCase());
            ps.setString(2, title.trim());
            ps.setInt(3, credits);
            ps.setString(4, courseType.trim().toUpperCase());
            ps.setInt(5, semester);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add course: " + e.getMessage());
            return false;
        }
    }

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_code, title, credits, course_type, semester FROM courses ORDER BY course_code ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Course(
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getInt("credits"),
                    rs.getString("course_type"),
                    rs.getInt("semester")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch courses: " + e.getMessage());
        }
        return list;
    }

    // =========================================================================
    // Course Enrollment & Attendance Tracking
    // =========================================================================

    public int enrollStudent(String rollNo, String courseCode, int semester, int totalClasses, int attendedClasses, boolean isCondoned) {
        String sql = "INSERT INTO enrollments (roll_no, course_code, semester, total_classes, attended_classes, is_condoned) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE total_classes = VALUES(total_classes), attended_classes = VALUES(attended_classes), is_condoned = VALUES(is_condoned)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            ps.setInt(3, semester);
            ps.setInt(4, totalClasses);
            ps.setInt(5, attendedClasses);
            ps.setBoolean(6, isCondoned);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int enrollId = rs.getInt(1);
                    initAssessmentRow(conn, enrollId);
                    return enrollId;
                }
            }

            // If updated existing row, query the ID
            return getEnrollmentId(rollNo, courseCode, semester);
        } catch (SQLException e) {
            System.err.println("[ERROR] Enrollment failed: " + e.getMessage());
            return -1;
        }
    }

    public boolean updateAttendance(int enrollmentId, int totalClasses, int attendedClasses, boolean isCondoned) {
        String sql = "UPDATE enrollments SET total_classes = ?, attended_classes = ?, is_condoned = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, totalClasses);
            ps.setInt(2, attendedClasses);
            ps.setBoolean(3, isCondoned);
            ps.setInt(4, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update attendance: " + e.getMessage());
            return false;
        }
    }

    public boolean setCondonation(int enrollmentId, boolean isCondoned) {
        String sql = "UPDATE enrollments SET is_condoned = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isCondoned);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update condonation: " + e.getMessage());
            return false;
        }
    }

    public int getEnrollmentId(String rollNo, String courseCode, int semester) {
        String sql = "SELECT enrollment_id FROM enrollments WHERE roll_no = ? AND course_code = ? AND semester = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setString(2, courseCode.trim().toUpperCase());
            ps.setInt(3, semester);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("enrollment_id");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get enrollment ID: " + e.getMessage());
        }
        return -1;
    }

    private void initAssessmentRow(Connection conn, int enrollmentId) {
        String sql = "INSERT IGNORE INTO assessment_marks (enrollment_id) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Ignore if already exists
        }
    }

    // =========================================================================
    // 2. Incremental Marks Entry ("Then and Now")
    // =========================================================================

    /**
     * Phase 1 (Mid-Semester): Record CIA Test 1 (0–100) and CIA Test 2 (0–100)
     */
    public boolean recordPhase1Marks(int enrollmentId, Double cia1, Double cia2) {
        String sql = "INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE cia1_marks = VALUES(cia1_marks), cia2_marks = VALUES(cia2_marks)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            if (cia1 != null) ps.setDouble(2, cia1); else ps.setNull(2, Types.DECIMAL);
            if (cia2 != null) ps.setDouble(3, cia2); else ps.setNull(3, Types.DECIMAL);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Phase 1 marks entry failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Phase 2 (End of Semester - CIA): Record Assignments / Quizzes / Lab Evaluations (0–100)
     */
    public boolean recordPhase2Marks(int enrollmentId, Double assignmentMarks) {
        String sql = "INSERT INTO assessment_marks (enrollment_id, assignment_marks) " +
                     "VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE assignment_marks = VALUES(assignment_marks)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            if (assignmentMarks != null) ps.setDouble(2, assignmentMarks); else ps.setNull(2, Types.DECIMAL);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Phase 2 marks entry failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Phase 3 (Post Semester): Record ESE Marks (0–100) and Mark Absence (AB)
     */
    public boolean recordPhase3Marks(int enrollmentId, Double eseMarks, boolean isAbsent) {
        String sql = "INSERT INTO assessment_marks (enrollment_id, ese_marks, is_ese_absent) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE ese_marks = VALUES(ese_marks), is_ese_absent = VALUES(is_ese_absent)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            if (isAbsent || eseMarks == null) {
                ps.setNull(2, Types.DECIMAL);
                ps.setBoolean(3, isAbsent);
            } else {
                ps.setDouble(2, eseMarks);
                ps.setBoolean(3, false);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Phase 3 marks entry failed: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // 3. Evaluation Rules & Weightage Engine (KEC R2024)
    // =========================================================================

    public CourseEvaluationResult evaluateCourse(EnrollmentRecord rec) {
        CourseEvaluationResult res = new CourseEvaluationResult();
        res.courseCode = rec.courseCode;
        res.courseTitle = rec.courseTitle;
        res.courseType = rec.courseType != null ? rec.courseType.toUpperCase() : "THEORY";
        res.credits = rec.credits;
        res.totalClasses = rec.totalClasses;
        res.attendedClasses = rec.attendedClasses;
        res.cia1 = rec.cia1;
        res.cia2 = rec.cia2;
        res.assignment = rec.assignment;
        res.eseMarks = rec.eseMarks;
        res.isEseAbsent = rec.isEseAbsent;

        // Attendance Percentage Calculation
        res.attendancePct = (rec.totalClasses > 0) ? (rec.attendedClasses * 100.0 / rec.totalClasses) : 100.0;

        // Weightage Settings per KEC R2024
        double ciaWeight;
        double eseWeight;
        double eseMinPct;

        switch (res.courseType) {
            case "PRACTICAL":
                ciaWeight = 60.0;
                eseWeight = 40.0;
                eseMinPct = 50.0; // 50% of 40 = 20 marks
                break;
            case "EMBEDDED":
                ciaWeight = 50.0;
                eseWeight = 50.0;
                eseMinPct = 45.0; // 45% of 50 = 22.5 marks
                break;
            case "THEORY":
            default:
                ciaWeight = 40.0;
                eseWeight = 60.0;
                eseMinPct = 45.0; // 45% of 60 = 27 marks
                break;
        }

        res.ciaWeightage = ciaWeight;
        res.eseWeightage = eseWeight;

        // Attendance Eligibility Filter:
        // >= 75%: Eligible
        // 65% - 74.9%: Conditionally eligible (Requires medical condonation)
        // < 65%: Shortage of Attendance (SA) - Barred from ESE
        boolean isShortage = false;
        if (res.attendancePct < 65.0) {
            res.attendanceStatus = "SHORTAGE (<65%)";
            isShortage = true;
        } else if (res.attendancePct < 75.0) {
            if (rec.isCondoned) {
                res.attendanceStatus = "CONDONED (65-74.9%)";
            } else {
                res.attendanceStatus = "PENDING CONDONATION (<75%)";
                isShortage = true; // Barred until condonation approved
            }
        } else {
            res.attendanceStatus = "ELIGIBLE (>=75%)";
        }

        // CIA Calculation:
        // Phase 1 (CIA1 35% + CIA2 35%) + Phase 2 (Assignments/Quizzes/Lab 30%) = 100% CIA
        double c1 = (res.cia1 != null) ? res.cia1 : 0.0;
        double c2 = (res.cia2 != null) ? res.cia2 : 0.0;
        double asg = (res.assignment != null) ? res.assignment : 0.0;

        res.rawCia100 = (c1 * 0.35) + (c2 * 0.35) + (asg * 0.30);
        res.scaledCia = (res.rawCia100 * ciaWeight) / 100.0;

        // Case 1: Shortage of Attendance (SA)
        if (isShortage) {
            res.scaledEse = 0.0;
            res.totalMarks = res.scaledCia;
            res.letterGrade = "SA";
            res.gradePoint = 0;
            res.resultStatus = "FAIL (Shortage of Attendance)";
            res.isPass = false;
            res.eseThresholdMet = false;
            return res;
        }

        // Case 2: Absent in End Semester Examination (AB)
        if (res.isEseAbsent) {
            res.scaledEse = 0.0;
            res.totalMarks = res.scaledCia;
            res.letterGrade = "AB";
            res.gradePoint = 0;
            res.resultStatus = "FAIL (Absent in ESE)";
            res.isPass = false;
            res.eseThresholdMet = false;
            return res;
        }

        // Case 3: ESE Marks Recorded
        if (res.eseMarks != null) {
            res.scaledEse = (res.eseMarks * eseWeight) / 100.0;
            res.totalMarks = res.scaledCia + res.scaledEse;

            // ESE Minimum Threshold Check (raw >= eseMinPct)
            res.eseThresholdMet = (res.eseMarks >= eseMinPct);

            // Pass Criteria: ESE threshold met AND Overall Aggregate >= 50.0
            if (!res.eseThresholdMet) {
                res.letterGrade = "RA";
                res.gradePoint = 0;
                res.resultStatus = "FAIL (Failed ESE Min Threshold " + eseMinPct + "%)";
                res.isPass = false;
            } else if (res.totalMarks < 50.0) {
                res.letterGrade = "RA";
                res.gradePoint = 0;
                res.resultStatus = "FAIL (Aggregate < 50)";
                res.isPass = false;
            } else {
                // Pass - Map 10-Point KEC Grading Scale
                int roundedTotal = (int) Math.round(res.totalMarks);
                if (roundedTotal >= 91) {
                    res.letterGrade = "O";
                    res.gradePoint = 10;
                } else if (roundedTotal >= 81) {
                    res.letterGrade = "A+";
                    res.gradePoint = 9;
                } else if (roundedTotal >= 71) {
                    res.letterGrade = "A";
                    res.gradePoint = 8;
                } else if (roundedTotal >= 61) {
                    res.letterGrade = "B+";
                    res.gradePoint = 7;
                } else {
                    res.letterGrade = "B";
                    res.gradePoint = 6;
                }
                res.resultStatus = "PASS";
                res.isPass = true;
            }
        } else {
            // ESE Not Yet Conducted/Recorded
            res.scaledEse = 0.0;
            res.totalMarks = res.scaledCia;
            res.letterGrade = "--";
            res.gradePoint = 0;
            res.resultStatus = "PENDING ESE";
            res.isPass = false;
            res.eseThresholdMet = false;
        }

        return res;
    }

    // =========================================================================
    // 4. Reports Generation
    // =========================================================================

    /**
     * Fetch all enrollment and marks data for a student in a specific semester
     */
    public SemesterReport generateStudentSemesterReport(String rollNo, int semester) {
        Student student = getStudent(rollNo);
        if (student == null) return null;

        SemesterReport rep = new SemesterReport();
        rep.student = student;
        rep.semester = semester;

        String sql = "SELECT e.enrollment_id, e.roll_no, s.name, s.department, " +
                     "       e.course_code, c.title, c.credits, c.course_type, e.semester, " +
                     "       e.total_classes, e.attended_classes, e.is_condoned, " +
                     "       m.cia1_marks, m.cia2_marks, m.assignment_marks, m.ese_marks, m.is_ese_absent " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_no = s.roll_no " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "LEFT JOIN assessment_marks m ON e.enrollment_id = m.enrollment_id " +
                     "WHERE e.roll_no = ? AND e.semester = ? " +
                     "ORDER BY e.course_code ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim().toUpperCase());
            ps.setInt(2, semester);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnrollmentRecord rec = new EnrollmentRecord();
                    rec.enrollmentId = rs.getInt("enrollment_id");
                    rec.rollNo = rs.getString("roll_no");
                    rec.studentName = rs.getString("name");
                    rec.department = rs.getString("department");
                    rec.courseCode = rs.getString("course_code");
                    rec.courseTitle = rs.getString("title");
                    rec.credits = rs.getInt("credits");
                    rec.courseType = rs.getString("course_type");
                    rec.semester = rs.getInt("semester");
                    rec.totalClasses = rs.getInt("total_classes");
                    rec.attendedClasses = rs.getInt("attended_classes");
                    rec.isCondoned = rs.getBoolean("is_condoned");

                    double c1 = rs.getDouble("cia1_marks");
                    rec.cia1 = rs.wasNull() ? null : c1;

                    double c2 = rs.getDouble("cia2_marks");
                    rec.cia2 = rs.wasNull() ? null : c2;

                    double asg = rs.getDouble("assignment_marks");
                    rec.assignment = rs.wasNull() ? null : asg;

                    double ese = rs.getDouble("ese_marks");
                    rec.eseMarks = rs.wasNull() ? null : ese;

                    rec.isEseAbsent = rs.getBoolean("is_ese_absent");

                    CourseEvaluationResult eval = evaluateCourse(rec);
                    rep.courses.add(eval);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch semester data: " + e.getMessage());
        }

        // Aggregate SGPA & Percentage
        int totalCredits = 0;
        int earnedCredits = 0;
        double weightedPoints = 0.0;
        boolean allPassed = !rep.courses.isEmpty();

        for (CourseEvaluationResult cr : rep.courses) {
            totalCredits += cr.credits;
            if (cr.isPass) {
                earnedCredits += cr.credits;
            } else {
                allPassed = false;
            }
            weightedPoints += (cr.credits * cr.gradePoint);
        }

        rep.totalEnrolledCredits = totalCredits;
        rep.totalEarnedCredits = earnedCredits;
        rep.totalGradePointsEarned = weightedPoints;
        rep.sgpa = totalCredits > 0 ? (weightedPoints / totalCredits) : 0.0;
        rep.percentage = rep.sgpa * 10.0;
        rep.allCoursesPassed = allPassed;

        return rep;
    }

    /**
     * Attendance Defaulter Report: List students with attendance < 65% (SA) and 65%-74.9% condonation
     */
    public List<EnrollmentRecord> getAttendanceDefaulters(int semester) {
        List<EnrollmentRecord> list = new ArrayList<>();
        boolean allSem = (semester <= 0);
        String sql = "SELECT e.enrollment_id, e.roll_no, s.name, s.department, " +
                     "       e.course_code, c.title, c.credits, c.course_type, e.semester, " +
                     "       e.total_classes, e.attended_classes, e.is_condoned " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.roll_no = s.roll_no " +
                     "JOIN courses c ON e.course_code = c.course_code " +
                     "WHERE " + (allSem ? "" : "e.semester = ? AND ") + "(e.attended_classes * 100.0 / e.total_classes) < 75.0 " +
                     "ORDER BY (e.attended_classes * 100.0 / e.total_classes) ASC, e.roll_no ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!allSem) {
                ps.setInt(1, semester);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnrollmentRecord rec = new EnrollmentRecord();
                    rec.enrollmentId = rs.getInt("enrollment_id");
                    rec.rollNo = rs.getString("roll_no");
                    rec.studentName = rs.getString("name");
                    rec.department = rs.getString("department");
                    rec.courseCode = rs.getString("course_code");
                    rec.courseTitle = rs.getString("title");
                    rec.credits = rs.getInt("credits");
                    rec.courseType = rs.getString("course_type");
                    rec.semester = rs.getInt("semester");
                    rec.totalClasses = rs.getInt("total_classes");
                    rec.attendedClasses = rs.getInt("attended_classes");
                    rec.isCondoned = rs.getBoolean("is_condoned");
                    list.add(rec);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to query attendance defaulters: " + e.getMessage());
        }
        return list;
    }

    /**
     * Department-wise Class Performance & Result Summary
     */
    public static class DepartmentSummary {
        public String department;
        public int totalStudents;
        public int passedStudents;
        public int failedStudents;
        public double totalSgpaSum;
        public double averageSgpa;
        public double passPercentage;
    }

    public Map<String, DepartmentSummary> getClassPerformanceSummary(int semester) {
        Map<String, DepartmentSummary> map = new TreeMap<>();
        List<Student> students = getAllStudents();

        for (Student st : students) {
            SemesterReport rep = generateStudentSemesterReport(st.rollNo, semester);
            if (rep == null || rep.courses.isEmpty()) continue;

            DepartmentSummary ds = map.computeIfAbsent(st.department, k -> {
                DepartmentSummary d = new DepartmentSummary();
                d.department = k;
                return d;
            });

            ds.totalStudents++;
            ds.totalSgpaSum += rep.sgpa;
            if (rep.allCoursesPassed) {
                ds.passedStudents++;
            } else {
                ds.failedStudents++;
            }
        }

        for (DepartmentSummary ds : map.values()) {
            ds.averageSgpa = ds.totalStudents > 0 ? (ds.totalSgpaSum / ds.totalStudents) : 0.0;
            ds.passPercentage = ds.totalStudents > 0 ? (ds.passedStudents * 100.0 / ds.totalStudents) : 0.0;
        }

        return map;
    }

    /**
     * Course-wise Performance & Result Summary (for IT Sem 3 Subject Analytics)
     */
    public static class CourseSummary {
        public String courseCode;
        public String courseTitle;
        public String courseType;
        public int credits;
        public int totalStudents;
        public int passedStudents;
        public double totalScoreSum;
        public double averageScore;
        public double passPercentage;
    }

    public List<CourseSummary> getCoursePerformanceSummary(int semester) {
        Map<String, CourseSummary> summaryMap = new LinkedHashMap<>();
        List<Course> courses = getAllCourses();
        for (Course c : courses) {
            if (c.semester == semester) {
                CourseSummary cs = new CourseSummary();
                cs.courseCode = c.courseCode;
                cs.courseTitle = c.title;
                cs.courseType = c.courseType;
                cs.credits = c.credits;
                summaryMap.put(c.courseCode, cs);
            }
        }

        List<Student> students = getAllStudents();
        for (Student st : students) {
            SemesterReport rep = generateStudentSemesterReport(st.rollNo, semester);
            if (rep == null) continue;
            for (CourseEvaluationResult cr : rep.courses) {
                CourseSummary cs = summaryMap.get(cr.courseCode);
                if (cs != null) {
                    cs.totalStudents++;
                    cs.totalScoreSum += cr.totalMarks;
                    if (cr.isPass) {
                        cs.passedStudents++;
                    }
                }
            }
        }

        List<CourseSummary> list = new ArrayList<>();
        for (CourseSummary cs : summaryMap.values()) {
            cs.averageScore = cs.totalStudents > 0 ? (cs.totalScoreSum / cs.totalStudents) : 0.0;
            cs.passPercentage = cs.totalStudents > 0 ? (cs.passedStudents * 100.0 / cs.totalStudents) : 0.0;
            list.add(cs);
        }
        return list;
    }
}
