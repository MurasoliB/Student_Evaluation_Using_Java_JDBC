import java.sql.*;
import java.util.*;

public class SeedAllSemesters {

    static class StudentData {
        String rollNo;
        String name;
        String dept;
        int semester;

        StudentData(String rollNo, String name, String dept, int semester) {
            this.rollNo = rollNo;
            this.name = name;
            this.dept = dept;
            this.semester = semester;
        }
    }

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println(" >> Seeding 10 Dummy Students in Each of All 8 Semesters (80 Students)");
        System.out.println("====================================================================");

        List<StudentData> students = new ArrayList<>();

        // Semester 1 (10 Students)
        students.add(new StudentData("26ITR001", "Aarav Sharma", "IT", 1));
        students.add(new StudentData("26ITR002", "Ananya Ramesh", "IT", 1));
        students.add(new StudentData("26CSR001", "Bhuvanesh K", "CSE", 1));
        students.add(new StudentData("26CSR002", "Charulatha M", "CSE", 1));
        students.add(new StudentData("26ECR001", "Dhanush S", "ECE", 1));
        students.add(new StudentData("26EER001", "Gokul V", "EEE", 1));
        students.add(new StudentData("26MER001", "Harish Kumar R", "MECH", 1));
        students.add(new StudentData("26CER001", "Ishwarya P", "CIVIL", 1));
        students.add(new StudentData("26ADR001", "Jeeva S", "AIDS", 1));
        students.add(new StudentData("26CHR001", "Keerthana N", "CHEM", 1));

        // Semester 2 (10 Students)
        students.add(new StudentData("26ITR011", "Lokesh M", "IT", 2));
        students.add(new StudentData("26ITR012", "Madhumitha S", "IT", 2));
        students.add(new StudentData("26CSR011", "Naveen Kumar B", "CSE", 2));
        students.add(new StudentData("26CSR012", "Pavithra R", "CSE", 2));
        students.add(new StudentData("26ECR011", "Ragul K", "ECE", 2));
        students.add(new StudentData("26EER011", "Sandhiya T", "EEE", 2));
        students.add(new StudentData("26MER011", "Tharun Prasath V", "MECH", 2));
        students.add(new StudentData("26CER011", "Umadevi G", "CIVIL", 2));
        students.add(new StudentData("26ADR011", "Varun S", "AIDS", 2));
        students.add(new StudentData("26CHR011", "Yamuna M", "CHEM", 2));

        // Semester 3 (10 Students)
        students.add(new StudentData("24ITR001", "Arun Kumar S", "IT", 3));
        students.add(new StudentData("24ITR002", "Bavithra M", "IT", 3));
        students.add(new StudentData("24ITR003", "Chandru V", "IT", 3));
        students.add(new StudentData("24CSR001", "Abhinav R", "CSE", 3));
        students.add(new StudentData("24CSR015", "Deepak Raja K", "CSE", 3));
        students.add(new StudentData("24ECR008", "Elango P", "ECE", 3));
        students.add(new StudentData("24EER005", "Ashwin Kumar T", "EEE", 3));
        students.add(new StudentData("24MER011", "Balaji S", "MECH", 3));
        students.add(new StudentData("24CER003", "Aravind K", "CIVIL", 3));
        students.add(new StudentData("24ADR010", "Ananya S", "AIDS", 3));

        // Semester 4 (10 Students)
        students.add(new StudentData("24ITR051", "Gowtham R", "IT", 4));
        students.add(new StudentData("24ITR052", "Hemalatha K", "IT", 4));
        students.add(new StudentData("24CSR051", "Jagadeesh M", "CSE", 4));
        students.add(new StudentData("24CSR052", "Kabilan S", "CSE", 4));
        students.add(new StudentData("24ECR051", "Lavanya M", "ECE", 4));
        students.add(new StudentData("24EER051", "Mohanraj P", "EEE", 4));
        students.add(new StudentData("24MER051", "Nithish Kumar V", "MECH", 4));
        students.add(new StudentData("24CER051", "Oviya S", "CIVIL", 4));
        students.add(new StudentData("24ADR051", "Pranesh B", "AIDS", 4));
        students.add(new StudentData("24CHR051", "Ramya T", "CHEM", 4));

        // Semester 5 (10 Students)
        students.add(new StudentData("23ITR051", "Sanjay Kumar M", "IT", 5));
        students.add(new StudentData("23ITR052", "Sathish V", "IT", 5));
        students.add(new StudentData("23CSR051", "Shalini P", "CSE", 5));
        students.add(new StudentData("23CSR052", "Siva Prakash R", "CSE", 5));
        students.add(new StudentData("23ECR051", "Soundarya K", "ECE", 5));
        students.add(new StudentData("23EER051", "Subash Chandran M", "EEE", 5));
        students.add(new StudentData("23MER051", "Vignesh S", "MECH", 5));
        students.add(new StudentData("23CER051", "Vinothini A", "CIVIL", 5));
        students.add(new StudentData("23ADR051", "Yuvaraj K", "AIDS", 5));
        students.add(new StudentData("23CHR051", "Abitha R", "CHEM", 5));

        // Semester 6 (10 Students)
        students.add(new StudentData("23ITR061", "Bharath Kumar S", "IT", 6));
        students.add(new StudentData("23ITR062", "Chandralekha M", "IT", 6));
        students.add(new StudentData("23CSR061", "Divakar V", "CSE", 6));
        students.add(new StudentData("23CSR062", "Gayathri R", "CSE", 6));
        students.add(new StudentData("23ECR061", "Gokulakrishnan P", "ECE", 6));
        students.add(new StudentData("23EER061", "Hemanth S", "EEE", 6));
        students.add(new StudentData("23MER061", "Ilayaraja M", "MECH", 6));
        students.add(new StudentData("23CER061", "Janani K", "CIVIL", 6));
        students.add(new StudentData("23ADR061", "Karthikeyan T", "AIDS", 6));
        students.add(new StudentData("23CHR061", "Lavanya D", "CHEM", 6));

        // Semester 7 (10 Students)
        students.add(new StudentData("22ITR071", "Manikandan P", "IT", 7));
        students.add(new StudentData("22ITR072", "Nandhini S", "IT", 7));
        students.add(new StudentData("22CSR071", "Praveen Kumar R", "CSE", 7));
        students.add(new StudentData("22CSR072", "Priyanga M", "CSE", 7));
        students.add(new StudentData("22ECR071", "Rajesh K", "ECE", 7));
        students.add(new StudentData("22EER071", "Rithika S", "EEE", 7));
        students.add(new StudentData("22MER071", "Sakthi Vel M", "MECH", 7));
        students.add(new StudentData("22CER071", "Saranya V", "CIVIL", 7));
        students.add(new StudentData("22ADR071", "Sudharsan G", "AIDS", 7));
        students.add(new StudentData("22CHR071", "Swetha T", "CHEM", 7));

        // Semester 8 (10 Students)
        students.add(new StudentData("22ITR081", "Tamilselvan K", "IT", 8));
        students.add(new StudentData("22ITR082", "Vaishnavi M", "IT", 8));
        students.add(new StudentData("22CSR081", "Venkatesh S", "CSE", 8));
        students.add(new StudentData("22CSR082", "Vidhya R", "CSE", 8));
        students.add(new StudentData("22ECR081", "Vigneshwar B", "ECE", 8));
        students.add(new StudentData("22EER081", "Vishnu Vardhan M", "EEE", 8));
        students.add(new StudentData("22MER081", "Yalini P", "MECH", 8));
        students.add(new StudentData("22CER081", "Yogeshwaran T", "CIVIL", 8));
        students.add(new StudentData("22ADR081", "Akilesh R", "AIDS", 8));
        students.add(new StudentData("22CHR081", "Balamurugan S", "CHEM", 8));

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert or update all 80 students
            String sqlStudent = "INSERT INTO students (roll_no, name, department, current_semester) " +
                                "VALUES (?, ?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE name = VALUES(name), department = VALUES(department), current_semester = VALUES(current_semester)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStudent)) {
                for (StudentData s : students) {
                    ps.setString(1, s.rollNo);
                    ps.setString(2, s.name);
                    ps.setString(3, s.dept);
                    ps.setInt(4, s.semester);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            System.out.println("[SUCCESS] Inserted/Updated 80 Students across Semesters 1 to 8.");

            // 2. Fetch all courses grouped by semester
            Map<Integer, List<String>> coursesBySem = new HashMap<>();
            String sqlCourses = "SELECT course_code, semester FROM courses";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sqlCourses)) {
                while (rs.next()) {
                    int sem = rs.getInt("semester");
                    String code = rs.getString("course_code");
                    coursesBySem.computeIfAbsent(sem, k -> new ArrayList<>()).add(code);
                }
            }

            // 3. Enroll students and assign realistic attendance & marks
            String sqlEnroll = "INSERT INTO enrollments (roll_no, course_code, semester, total_classes, attended_classes, is_condoned) " +
                               "VALUES (?, ?, ?, ?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE total_classes = VALUES(total_classes), attended_classes = VALUES(attended_classes), is_condoned = VALUES(is_condoned)";

            String sqlMarks = "INSERT INTO assessment_marks (enrollment_id, cia1_marks, cia2_marks, assignment_marks, ese_marks, is_ese_absent) " +
                              "VALUES (?, ?, ?, ?, ?, ?) " +
                              "ON DUPLICATE KEY UPDATE cia1_marks = VALUES(cia1_marks), cia2_marks = VALUES(cia2_marks), " +
                              "assignment_marks = VALUES(assignment_marks), ese_marks = VALUES(ese_marks), is_ese_absent = VALUES(is_ese_absent)";

            int totalEnrolled = 0;
            int totalMarksAssigned = 0;

            for (int i = 0; i < students.size(); i++) {
                StudentData s = students.get(i);
                int sem = s.semester;
                int semIndex = i % 10; // 0 to 9 within the semester

                List<String> semCourses = coursesBySem.getOrDefault(sem, Collections.emptyList());
                List<String> targetCourses = new ArrayList<>();

                if (sem <= 2) {
                    // Sem 1 and 2: Common courses
                    targetCourses.addAll(semCourses);
                } else {
                    // Sem 3 to 8: Department specific courses
                    String d = s.dept.toUpperCase();
                    for (String c : semCourses) {
                        String up = c.toUpperCase();
                        boolean match = false;
                        if (d.equals("IT")) match = up.contains("IT") || up.contains("MN") || up.contains("MA");
                        else if (d.equals("CSE")) match = up.contains("CS") || up.contains("MA");
                        else if (d.equals("ECE")) match = up.contains("EC") || up.contains("MA");
                        else if (d.equals("EEE")) match = up.contains("EE") || up.contains("MA");
                        else if (d.equals("MECH")) match = up.contains("ME") || up.contains("MA");
                        else if (d.equals("CIVIL")) match = up.contains("CE") || up.contains("MA");
                        else if (d.equals("AIDS")) match = up.contains("AD") || up.contains("MA");
                        else if (d.equals("CHEM")) match = up.contains("CH") || up.contains("MA");

                        if (match) targetCourses.add(c);
                    }
                    if (targetCourses.isEmpty()) {
                        // Fallback to first 3 courses of that semester
                        for (int k = 0; k < Math.min(3, semCourses.size()); k++) {
                            targetCourses.add(semCourses.get(k));
                        }
                    }
                }

                for (int cIdx = 0; cIdx < targetCourses.size(); cIdx++) {
                    String courseCode = targetCourses.get(cIdx);
                    int totalClasses = courseCode.contains("L") ? 30 : 60;
                    int attended;
                    boolean isCondoned = false;

                    // Attendance Profiles:
                    // semIndex 0..7: Normal high attendance (80% - 98%)
                    // semIndex 8: Condonation range (68% - 73%)
                    // semIndex 9: Defaulter SA (<65%, e.g. 52%)
                    if (semIndex <= 7) {
                        double pct = 0.82 + (0.15 * ((semIndex + cIdx) % 5) / 5.0);
                        attended = (int) Math.round(totalClasses * pct);
                        if (attended > totalClasses) attended = totalClasses;
                    } else if (semIndex == 8) {
                        // 68% - 73%
                        attended = (int) Math.round(totalClasses * 0.70);
                        isCondoned = (cIdx % 2 == 0); // half condoned, half requires condonation
                    } else {
                        // Defaulter: <65% (award SA)
                        attended = (int) Math.round(totalClasses * 0.54);
                    }

                    // Insert enrollment
                    int enrollId = -1;
                    try (PreparedStatement psE = conn.prepareStatement(sqlEnroll, Statement.RETURN_GENERATED_KEYS)) {
                        psE.setString(1, s.rollNo);
                        psE.setString(2, courseCode);
                        psE.setInt(3, sem);
                        psE.setInt(4, totalClasses);
                        psE.setInt(5, attended);
                        psE.setBoolean(6, isCondoned);
                        psE.executeUpdate();

                        try (ResultSet rs = psE.getGeneratedKeys()) {
                            if (rs.next()) {
                                enrollId = rs.getInt(1);
                            }
                        }
                    }

                    if (enrollId <= 0) {
                        // Query existing ID
                        String queryId = "SELECT enrollment_id FROM enrollments WHERE roll_no = ? AND course_code = ? AND semester = ?";
                        try (PreparedStatement psQ = conn.prepareStatement(queryId)) {
                            psQ.setString(1, s.rollNo);
                            psQ.setString(2, courseCode);
                            psQ.setInt(3, sem);
                            try (ResultSet rsQ = psQ.executeQuery()) {
                                if (rsQ.next()) enrollId = rsQ.getInt(1);
                            }
                        }
                    }

                    if (enrollId > 0) {
                        totalEnrolled++;

                        // Marks generation
                        Double cia1, cia2, asg, ese;
                        boolean isAbsent = false;

                        if (semIndex == 9 && attended < (totalClasses * 0.65)) {
                            // Barred due to SA
                            cia1 = 65.0;
                            cia2 = 60.0;
                            asg = 70.0;
                            ese = null;
                        } else if (semIndex == 7 && cIdx == 0) {
                            // 1 student absent in 1 exam to test AB
                            cia1 = 78.0;
                            cia2 = 82.0;
                            asg = 85.0;
                            ese = null;
                            isAbsent = true;
                        } else {
                            // Realistic performance
                            double base = 60.0 + (semIndex * 3.5) + (cIdx * 2.0);
                            if (base > 95) base = 95;
                            cia1 = Math.min(100.0, base + ((cIdx % 3) * 2.5));
                            cia2 = Math.min(100.0, base + 2.0 + ((cIdx % 2) * 3.0));
                            asg = Math.min(100.0, base + 5.0);
                            ese = Math.min(100.0, base - 1.0 + ((cIdx % 4) * 2.0));
                        }

                        try (PreparedStatement psM = conn.prepareStatement(sqlMarks)) {
                            psM.setInt(1, enrollId);
                            if (cia1 != null) psM.setDouble(2, cia1); else psM.setNull(2, Types.DECIMAL);
                            if (cia2 != null) psM.setDouble(3, cia2); else psM.setNull(3, Types.DECIMAL);
                            if (asg != null) psM.setDouble(4, asg); else psM.setNull(4, Types.DECIMAL);
                            if (ese != null) psM.setDouble(5, ese); else psM.setNull(5, Types.DECIMAL);
                            psM.setBoolean(6, isAbsent);
                            psM.executeUpdate();
                            totalMarksAssigned++;
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("[SUCCESS] Created " + totalEnrolled + " Course Enrollments.");
            System.out.println("[SUCCESS] Created " + totalMarksAssigned + " Phased Assessment Records.");
            System.out.println("====================================================================");
            System.out.println(" >> 10 Dummy Students in Each of All 8 Semesters are LIVE in Database!");
            System.out.println("====================================================================");

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to seed database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

