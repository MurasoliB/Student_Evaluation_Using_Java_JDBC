import java.io.*;
import java.sql.*;

public class ExportSchemaData {
    public static void main(String[] args) {
        System.out.println("[INFO] Exporting complete database state to sql/kec_schema.sql...");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             PrintWriter writer = new PrintWriter(new FileWriter("sql/kec_schema.sql"))) {

            writer.println("-- ============================================================================");
            writer.println("-- Kongu Engineering College (Autonomous) - Regulation 2024 (R2024)");
            writer.println("-- Student Academic & Continuous Evaluation System");
            writer.println("-- Comprehensive Database Schema & Official Course Catalog");
            writer.println("-- Covering All 8 Engineering Departments across All 8 Semesters");
            writer.println("-- Includes 10 Dummy Students per Semester across all 8 Semesters (80+ Students)");
            writer.println("-- ============================================================================\n");

            writer.println("DROP DATABASE IF EXISTS kec_eval;");
            writer.println("CREATE DATABASE kec_eval CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            writer.println("USE kec_eval;\n");

            writer.println("-- 1. Students Table");
            writer.println("CREATE TABLE students (");
            writer.println("    roll_no VARCHAR(20) PRIMARY KEY,");
            writer.println("    name VARCHAR(100) NOT NULL,");
            writer.println("    department VARCHAR(20) NOT NULL,");
            writer.println("    current_semester INT NOT NULL DEFAULT 3 CHECK (current_semester BETWEEN 1 AND 8),");
            writer.println("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            writer.println(");\n");

            writer.println("-- 2. Course Catalog Table");
            writer.println("CREATE TABLE courses (");
            writer.println("    course_code VARCHAR(20) PRIMARY KEY,");
            writer.println("    title VARCHAR(150) NOT NULL,");
            writer.println("    credits INT NOT NULL CHECK (credits >= 0),");
            writer.println("    course_type ENUM('THEORY', 'PRACTICAL', 'EMBEDDED') NOT NULL,");
            writer.println("    semester INT NOT NULL DEFAULT 3 CHECK (semester BETWEEN 1 AND 8),");
            writer.println("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            writer.println(");\n");

            writer.println("-- 3. Enrollments & Attendance Tracking Table");
            writer.println("CREATE TABLE enrollments (");
            writer.println("    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,");
            writer.println("    roll_no VARCHAR(20) NOT NULL,");
            writer.println("    course_code VARCHAR(20) NOT NULL,");
            writer.println("    semester INT NOT NULL DEFAULT 3 CHECK (semester BETWEEN 1 AND 8),");
            writer.println("    total_classes INT NOT NULL DEFAULT 0 CHECK (total_classes >= 0),");
            writer.println("    attended_classes INT NOT NULL DEFAULT 0 CHECK (attended_classes >= 0),");
            writer.println("    is_condoned BOOLEAN NOT NULL DEFAULT FALSE,");
            writer.println("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,");
            writer.println("    CONSTRAINT fk_enrollment_student FOREIGN KEY (roll_no) REFERENCES students(roll_no) ON DELETE CASCADE ON UPDATE CASCADE,");
            writer.println("    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE ON UPDATE CASCADE,");
            writer.println("    CONSTRAINT uk_student_course_sem UNIQUE (roll_no, course_code, semester),");
            writer.println("    CONSTRAINT chk_attendance_valid CHECK (attended_classes <= total_classes)");
            writer.println(");\n");

            writer.println("-- 4. Phased Assessment Marks Table");
            writer.println("CREATE TABLE assessment_marks (");
            writer.println("    mark_id INT AUTO_INCREMENT PRIMARY KEY,");
            writer.println("    enrollment_id INT NOT NULL UNIQUE,");
            writer.println("    cia1_marks DECIMAL(5,2) NULL CHECK (cia1_marks IS NULL OR (cia1_marks >= 0 AND cia1_marks <= 100)),");
            writer.println("    cia2_marks DECIMAL(5,2) NULL CHECK (cia2_marks IS NULL OR (cia2_marks >= 0 AND cia2_marks <= 100)),");
            writer.println("    assignment_marks DECIMAL(5,2) NULL CHECK (assignment_marks IS NULL OR (assignment_marks >= 0 AND assignment_marks <= 100)),");
            writer.println("    ese_marks DECIMAL(5,2) NULL CHECK (ese_marks IS NULL OR (ese_marks >= 0 AND ese_marks <= 100)),");
            writer.println("    is_ese_absent BOOLEAN NOT NULL DEFAULT FALSE,");
            writer.println("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,");
            writer.println("    CONSTRAINT fk_assessment_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE ON UPDATE CASCADE");
            writer.println(");\n");

            // Dump courses
            writer.println("-- ============================================================================");
            writer.println("-- OFFICIAL COURSE CATALOG DATA (SEMESTERS 1 TO 8)");
            writer.println("-- ============================================================================");
            writer.println("INSERT INTO courses (course_code, title, credits, course_type, semester) VALUES");
            try (ResultSet rs = stmt.executeQuery("SELECT course_code, title, credits, course_type, semester FROM courses ORDER BY semester ASC, course_code ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) writer.println(",");
                    writer.print(String.format("('%s', '%s', %d, '%s', %d)",
                            rs.getString(1),
                            rs.getString(2).replace("'", "''"),
                            rs.getInt(3),
                            rs.getString(4),
                            rs.getInt(5)));
                    first = false;
                }
                writer.println(";\n");
            }

            // Dump students
            writer.println("-- ============================================================================");
            writer.println("-- STUDENT REGISTRATION (10+ STUDENTS PER SEMESTER ACROSS SEMESTERS 1 TO 8)");
            writer.println("-- ============================================================================");
            writer.println("INSERT INTO students (roll_no, name, department, current_semester) VALUES");
            try (ResultSet rs = stmt.executeQuery("SELECT roll_no, name, department, current_semester FROM students ORDER BY current_semester ASC, roll_no ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) writer.println(",");
                    writer.print(String.format("('%s', '%s', '%s', %d)",
                            rs.getString(1),
                            rs.getString(2).replace("'", "''"),
                            rs.getString(3),
                            rs.getInt(4)));
                    first = false;
                }
                writer.println(";\n");
            }

            // Dump enrollments
            writer.println("-- ============================================================================");
            writer.println("-- ENROLLMENTS & ATTENDANCE RECORDS (SEMESTERS 1 TO 8)");
            writer.println("-- ============================================================================");
            writer.println("INSERT INTO enrollments (enrollment_id, roll_no, course_code, semester, total_classes, attended_classes, is_condoned) VALUES");
            try (ResultSet rs = stmt.executeQuery("SELECT enrollment_id, roll_no, course_code, semester, total_classes, attended_classes, is_condoned FROM enrollments ORDER BY enrollment_id ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) writer.println(",");
                    writer.print(String.format("(%d, '%s', '%s', %d, %d, %d, %s)",
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getInt(4),
                            rs.getInt(5),
                            rs.getInt(6),
                            rs.getBoolean(7) ? "TRUE" : "FALSE"));
                    first = false;
                }
                writer.println(";\n");
            }

            // Dump assessment marks
            writer.println("-- ============================================================================");
            writer.println("-- ASSESSMENT MARKS (PHASED EVALUATION DATA)");
            writer.println("-- ============================================================================");
            writer.println("INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) VALUES");
            try (ResultSet rs = stmt.executeQuery("SELECT enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent FROM assessment_marks ORDER BY enrollment_id ASC")) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) writer.println(",");
                    String c1 = rs.getObject("cia1_marks") != null ? String.valueOf(rs.getDouble("cia1_marks")) : "NULL";
                    String c2 = rs.getObject("cia2_marks") != null ? String.valueOf(rs.getDouble("cia2_marks")) : "NULL";
                    String asg = rs.getObject("assignment_marks") != null ? String.valueOf(rs.getDouble("assignment_marks")) : "NULL";
                    String ese = rs.getObject("ese_marks") != null ? String.valueOf(rs.getDouble("ese_marks")) : "NULL";
                    boolean ab = rs.getBoolean("is_ese_absent");
                    writer.print(String.format("(%d, %s, %s, %s, %s, %s)",
                            rs.getInt(1), c1, c2, asg, ese, ab ? "TRUE" : "FALSE"));
                    first = false;
                }
                writer.println(";\n");
            }

            System.out.println("[SUCCESS] sql/kec_schema.sql updated with complete 8-semester student dataset!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

