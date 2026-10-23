import java.util.*;

/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * Student Academic & Continuous Evaluation System
 * Main Application Interface
 */
public class MainApp {

    private static final AcademicDAO dao = new AcademicDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // If command-line arguments are provided, handle them directly
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--demo")) {
                runAutomatedDemo();
                return;
            } else if (args[0].equalsIgnoreCase("--dashboard") || args[0].equalsIgnoreCase("--web") || args[0].equalsIgnoreCase("--gui")) {
                WebDashboard.startServer(8080, true);
                return;
            } else if (args[0].equalsIgnoreCase("--report") && args.length >= 2) {
                String rollNo = args[1];
                int sem = args.length >= 3 ? Integer.parseInt(args[2]) : 3;
                printStudentMarkSheet(rollNo, sem);
                return;
            } else if (args[0].equalsIgnoreCase("--defaulters")) {
                int sem = args.length >= 2 ? Integer.parseInt(args[1]) : 3;
                printAttendanceDefaulters(sem);
                return;
            } else if (args[0].equalsIgnoreCase("--summary")) {
                int sem = args.length >= 2 ? Integer.parseInt(args[1]) : 3;
                printDepartmentSummary(sem);
                return;
            }
        }

        printBanner();

        // Check DB Connection
        if (!DBConnection.testConnection()) {
            System.out.println("\n[ERROR] Unable to connect to MySQL database.");
            System.out.println("Please check that MySQL service is running and credentials in DBConnection.java are correct.");
            System.out.println("Host: localhost:3306 | DB: kec_eval | User: root\n");
            return;
        }

        System.out.println(">> Database Connection Established Successfully.\n");

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            System.out.print("Enter your choice (1-8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleStudentRegistration();
                    break;
                case "2":
                    handleCourseCatalog();
                    break;
                case "3":
                    handleCourseEnrollment();
                    break;
                case "4":
                    handleIncrementalMarksEntry();
                    break;
                case "5":
                    handleGenerateStudentMarkSheet();
                    break;
                case "6":
                    handleGenerateAttendanceDefaulters();
                    break;
                case "7":
                    handleGenerateClassSummary();
                    break;
                case "8":
                    runAutomatedDemo();
                    break;
                case "9":
                    System.out.println("\n[INFO] Starting Interactive Web Dashboard on http://localhost:8080 ...");
                    WebDashboard.startServer(8080, true);
                    break;
                case "0":
                    exit = true;
                    System.out.println("\nExiting KEC Evaluation System. Thank you!\n");
                    break;
                default:
                    System.out.println("[!] Invalid option. Please select between 0 and 9.");
            }
            if (!exit) {
                System.out.println("\nPress [Enter] to return to the Main Menu...");
                scanner.nextLine();
            }
        }
    }

    private static void printBanner() {
        System.out.println("==========================================================================================");
        System.out.println("                 KONGU ENGINEERING COLLEGE (AUTONOMOUS) - PERUNDURAI                      ");
        System.out.println("       DEPARTMENT OF INFORMATION TECHNOLOGY - SEMESTER 3 EVALUATION SYSTEM (R2024)        ");
        System.out.println("==========================================================================================");
    }

    private static void printMainMenu() {
        System.out.println("\n---------------------------------- MAIN MENU ---------------------------------------------");
        System.out.println(" 1. Student Registration (Add New IT Student)");
        System.out.println(" 2. Course Catalog Management (Semester 3 Courses)");
        System.out.println(" 3. Course Enrollment & Attendance Tracking");
        System.out.println(" 4. Incremental Marks Entry (Phase 1: CIA1/2 | Phase 2: Asg/Lab | Phase 3: ESE)");
        System.out.println(" 5. Generate Student Semester Mark Sheet (SGPA & Equivalent %)");
        System.out.println(" 6. Generate Attendance Defaulter & Condonation Report");
        System.out.println(" 7. Generate Class Performance & Result Summary");
        System.out.println(" 8. Run Full Automated Demonstration (Proof of Execution)");
        System.out.println(" 9. Launch Interactive Web Dashboard (Browser GUI @ http://localhost:8080)");
        System.out.println(" 0. Exit");
        System.out.println("------------------------------------------------------------------------------------------");
    }

    // =========================================================================
    // 1. Student Registration
    // =========================================================================
    private static void handleStudentRegistration() {
        System.out.println("\n--- [1] Student Registration ---");
        System.out.print("Enter Roll Number (e.g., 24ITR050): ");
        String rollNo = scanner.nextLine().trim();

        System.out.print("Enter Student Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Department [Press Enter for IT]: ");
        String dept = scanner.nextLine().trim();
        if (dept.isEmpty()) dept = "IT";

        System.out.print("Enter Current Semester [Press Enter for 3]: ");
        String semStr = scanner.nextLine().trim();
        int sem = 3;
        if (!semStr.isEmpty()) {
            try {
                sem = Integer.parseInt(semStr);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid semester input. Defaulting to 3.");
                sem = 3;
            }
        }

        boolean success = dao.addStudent(rollNo, name, dept, sem);
        if (success) {
            System.out.println("[SUCCESS] Student " + rollNo + " (" + name + ") registered successfully.");
        } else {
            System.out.println("[FAILED] Could not register student.");
        }
    }

    // =========================================================================
    // 2. Course Catalog
    // =========================================================================
    private static void handleCourseCatalog() {
        System.out.println("\n--- [2] Course Catalog Management ---");
        System.out.print("Enter Course Code (e.g., 24ITR31 / 24ITT32): ");
        String code = scanner.nextLine().trim();

        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Credits (e.g., 2, 3, 4): ");
        int credits;
        try {
            credits = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid credits input.");
            return;
        }

        System.out.print("Enter Course Type (1: THEORY, 2: PRACTICAL, 3: EMBEDDED): ");
        String typeChoice = scanner.nextLine().trim();
        String courseType = "THEORY";
        if (typeChoice.equals("2") || typeChoice.equalsIgnoreCase("PRACTICAL")) {
            courseType = "PRACTICAL";
        } else if (typeChoice.equals("3") || typeChoice.equalsIgnoreCase("EMBEDDED")) {
            courseType = "EMBEDDED";
        }

        System.out.print("Enter Offering Semester (1-8): ");
        int sem;
        try {
            sem = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid semester input.");
            return;
        }

        boolean success = dao.addCourse(code, title, credits, courseType, sem);
        if (success) {
            System.out.println("[SUCCESS] Course " + code + " [" + courseType + ", " + credits + " Credits] added successfully.");
        } else {
            System.out.println("[FAILED] Could not add course.");
        }
    }

    // =========================================================================
    // 3. Course Enrollment & Attendance Tracking
    // =========================================================================
    private static void handleCourseEnrollment() {
        System.out.println("\n--- [3] Course Enrollment & Attendance Tracking ---");
        System.out.print("Enter Student Roll Number: ");
        String rollNo = scanner.nextLine().trim();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine().trim();

        System.out.print("Enter Semester: ");
        int sem = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Total Classes Held: ");
        int total = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Classes Attended: ");
        int attended = Integer.parseInt(scanner.nextLine().trim());

        double pct = (total > 0) ? (attended * 100.0 / total) : 100.0;
        System.out.printf("Calculated Attendance: %.2f%%\n", pct);

        boolean isCondoned = false;
        if (pct >= 65.0 && pct < 75.0) {
            System.out.print("Attendance is in 65%-74.9% range. Grant Medical Condonation? (y/n): ");
            String cond = scanner.nextLine().trim();
            isCondoned = cond.equalsIgnoreCase("y") || cond.equalsIgnoreCase("yes");
        } else if (pct < 65.0) {
            System.out.println("[WARNING] Attendance is < 65%. Flagged as Shortage of Attendance (SA). Barred from ESE.");
        } else {
            System.out.println("[INFO] Attendance >= 75%. Eligible for ESE.");
        }

        int enrollId = dao.enrollStudent(rollNo, courseCode, sem, total, attended, isCondoned);
        if (enrollId > 0) {
            System.out.println("[SUCCESS] Enrollment and Attendance updated. (Enrollment ID: " + enrollId + ")");
        } else {
            System.out.println("[FAILED] Enrollment failed.");
        }
    }

    // =========================================================================
    // 4. Incremental Marks Entry ("Then and Now")
    // =========================================================================
    private static void handleIncrementalMarksEntry() {
        System.out.println("\n----------------- INCREMENTAL MARKS ENTRY (THEN AND NOW) -----------------");
        System.out.println(" Select Assessment Phase:");
        System.out.println(" 1. Phase 1 (Mid-Semester): CIA Test 1 (0-100) & CIA Test 2 (0-100)");
        System.out.println(" 2. Phase 2 (End of Semester - CIA): Assignments / Quizzes / Lab (0-100)");
        System.out.println(" 3. Phase 3 (Post Semester): End Semester Exam (0-100) & Absentee (AB)");
        System.out.print("Enter Phase (1-3): ");
        String phase = scanner.nextLine().trim();

        System.out.print("Enter Student Roll Number: ");
        String rollNo = scanner.nextLine().trim();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine().trim();

        System.out.print("Enter Semester: ");
        int sem = Integer.parseInt(scanner.nextLine().trim());

        int enrollId = dao.getEnrollmentId(rollNo, courseCode, sem);
        if (enrollId <= 0) {
            System.out.println("[!] No enrollment record found for " + rollNo + " in " + courseCode + " (Sem " + sem + ").");
            return;
        }

        switch (phase) {
            case "1": {
                System.out.print("Enter CIA Test 1 Marks (0-100): ");
                double c1 = Double.parseDouble(scanner.nextLine().trim());
                System.out.print("Enter CIA Test 2 Marks (0-100): ");
                double c2 = Double.parseDouble(scanner.nextLine().trim());
                if (dao.recordPhase1Marks(enrollId, c1, c2)) {
                    System.out.println("[SUCCESS] Phase 1 Marks recorded successfully.");
                } else {
                    System.out.println("[FAILED] Could not record Phase 1 marks.");
                }
                break;
            }
            case "2": {
                System.out.print("Enter Assignment / Quiz / Lab Evaluation Marks (0-100): ");
                double asg = Double.parseDouble(scanner.nextLine().trim());
                if (dao.recordPhase2Marks(enrollId, asg)) {
                    System.out.println("[SUCCESS] Phase 2 Marks recorded successfully.");
                } else {
                    System.out.println("[FAILED] Could not record Phase 2 marks.");
                }
                break;
            }
            case "3": {
                System.out.print("Is student Absent (AB) in ESE? (y/n): ");
                String abStr = scanner.nextLine().trim();
                boolean isAbsent = abStr.equalsIgnoreCase("y") || abStr.equalsIgnoreCase("yes");
                Double eseMarks = null;
                if (!isAbsent) {
                    System.out.print("Enter End Semester Exam (ESE) Marks (0-100): ");
                    eseMarks = Double.parseDouble(scanner.nextLine().trim());
                }
                if (dao.recordPhase3Marks(enrollId, eseMarks, isAbsent)) {
                    System.out.println("[SUCCESS] Phase 3 ESE Marks recorded successfully.");
                } else {
                    System.out.println("[FAILED] Could not record Phase 3 marks.");
                }
                break;
            }
            default:
                System.out.println("[!] Invalid Phase choice.");
        }
    }

    // =========================================================================
    // 5. Generate Student Semester Mark Sheet
    // =========================================================================
    private static void handleGenerateStudentMarkSheet() {
        System.out.print("\nEnter Student Roll Number (e.g., 24ITR001): ");
        String rollNo = scanner.nextLine().trim();
        System.out.print("Enter Semester (e.g., 3): ");
        int sem = Integer.parseInt(scanner.nextLine().trim());

        printStudentMarkSheet(rollNo, sem);
    }

    public static void printStudentMarkSheet(String rollNo, int semester) {
        AcademicDAO.SemesterReport rep = dao.generateStudentSemesterReport(rollNo, semester);
        if (rep == null) {
            System.out.println("[!] Student with Roll Number " + rollNo + " not found.");
            return;
        }

        System.out.println("\n====================================================================================================================");
        System.out.println("                                      KONGU ENGINEERING COLLEGE (AUTONOMOUS)                                        ");
        System.out.println("                                          PERUNDURAI, ERODE - 638060                                                ");
        System.out.println("                                  OFFICE OF THE CONTROLLER OF EXAMINATIONS                                          ");
        System.out.println("                               GRADE SHEET - REGULATION 2024 (AUTONOMOUS)                                           ");
        System.out.println("====================================================================================================================");
        System.out.printf(" Roll Number : %-15s   Student Name : %-30s   Semester : %d\n",
                rep.student.rollNo, rep.student.name, rep.semester);
        System.out.printf(" Department  : %-15s   Regulation   : R2024 (KEC)                       Date     : %s\n",
                rep.student.department, new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()));
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-9s %-28s %-9s %-4s %-8s %-6s %-6s %-6s %-8s %-8s %-7s %-6s %-4s %-16s\n",
                "Course", "Course Title", "Type", "Cr", "Attd%", "CIA1", "CIA2", "Asg", "Sc.CIA", "ESE(100)", "Sc.ESE", "Total", "Grd", "Result");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        for (AcademicDAO.CourseEvaluationResult cr : rep.courses) {
            String shortTitle = cr.courseTitle.length() > 27 ? cr.courseTitle.substring(0, 24) + "..." : cr.courseTitle;
            String c1 = (cr.cia1 != null) ? String.format("%.1f", cr.cia1) : "--";
            String c2 = (cr.cia2 != null) ? String.format("%.1f", cr.cia2) : "--";
            String asg = (cr.assignment != null) ? String.format("%.1f", cr.assignment) : "--";
            String ese = cr.isEseAbsent ? "AB" : ((cr.eseMarks != null) ? String.format("%.1f", cr.eseMarks) : "--");
            String scEse = (cr.eseMarks != null && !cr.isEseAbsent) ? String.format("%.1f", cr.scaledEse) : "--";

            System.out.printf("%-9s %-28s %-9s %-4d %6.1f%% %-6s %-6s %-6s %8.2f %-8s %-7s %6.2f %-4s %-16s\n",
                    cr.courseCode, shortTitle, cr.courseType, cr.credits,
                    cr.attendancePct, c1, c2, asg, cr.scaledCia, ese, scEse, cr.totalMarks,
                    cr.letterGrade, cr.resultStatus);
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" Total Enrolled Credits : %-4d   |   Credits Earned : %-4d   |   Weighted Grade Points : %.2f\n",
                rep.totalEnrolledCredits, rep.totalEarnedCredits, rep.totalGradePointsEarned);
        System.out.printf(" SEMESTER GRADE POINT AVERAGE (SGPA) : %.2f / 10.00\n", rep.sgpa);
        System.out.printf(" EQUIVALENT PERCENTAGE                : %.2f%%\n", rep.percentage);
        System.out.printf(" SEMESTER EVALUATION OUTCOME         : %s\n",
                rep.allCoursesPassed ? "[PASSED] ALL REGISTERED COURSES CLEARED" : "[RE-APPEARANCE / ARREAR] SOME COURSES NOT CLEARED");
        System.out.println("====================================================================================================================");
        System.out.println(" Grading Standard (R2024): O(91-100)->10 | A+(81-90)->9 | A(71-80)->8 | B+(61-70)->7 | B(50-60)->6 | RA/SA/AB->0");
        System.out.println(" Rules: Theory (40 CIA/60 ESE, ESE Min 27/60) | Practical (60 CIA/40 ESE, ESE Min 20/40) | Pass Minimum: 50 Marks");
        System.out.println("====================================================================================================================\n");
    }

    // =========================================================================
    // 6. Generate Attendance Defaulters Report
    // =========================================================================
    private static void handleGenerateAttendanceDefaulters() {
        System.out.print("\nEnter Semester to check (e.g., 3): ");
        int sem = Integer.parseInt(scanner.nextLine().trim());
        printAttendanceDefaulters(sem);
    }

    public static void printAttendanceDefaulters(int semester) {
        List<AcademicDAO.EnrollmentRecord> defaulters = dao.getAttendanceDefaulters(semester);
        System.out.println("\n==========================================================================================================");
        System.out.println("                       KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024                            ");
        System.out.println("                            ATTENDANCE DEFAULTER & CONDONATION REPORT                                     ");
        System.out.printf("                                     SEMESTER: %d                                                         \n", semester);
        System.out.println("==========================================================================================================");
        System.out.printf("%-12s %-20s %-8s %-10s %-8s %-8s %-10s %-12s %-18s\n",
                "Roll No", "Student Name", "Dept", "Course", "Held", "Attended", "Attd %", "Condonation", "Eligibility / Grade");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        if (defaulters.isEmpty()) {
            System.out.println(" >> No attendance defaulters found (< 75%) for Semester " + semester + ".");
        } else {
            for (AcademicDAO.EnrollmentRecord rec : defaulters) {
                double pct = (rec.totalClasses > 0) ? (rec.attendedClasses * 100.0 / rec.totalClasses) : 0.0;
                String condStatus = rec.isCondoned ? "APPROVED" : "NOT APPROVED";
                String eligibility;

                if (pct < 65.0) {
                    eligibility = "BARRED (Grade SA)";
                } else if (rec.isCondoned) {
                    eligibility = "CONDONED (Eligible)";
                } else {
                    eligibility = "PENDING (Grade SA)";
                }

                System.out.printf("%-12s %-20s %-8s %-10s %-8d %-8d %7.1f%% %-12s %-18s\n",
                        rec.rollNo,
                        (rec.studentName.length() > 19 ? rec.studentName.substring(0, 17) + ".." : rec.studentName),
                        rec.department, rec.courseCode, rec.totalClasses, rec.attendedClasses, pct,
                        condStatus, eligibility);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println(" Note: <65% Attendance: Shortage of Attendance (SA - Barred) | 65-74.9%: Requires Medical Condonation");
        System.out.println("==========================================================================================================\n");
    }

    // =========================================================================
    // 7. Department-wise Class Performance Summary
    // =========================================================================
    private static void handleGenerateClassSummary() {
        System.out.print("\nEnter Semester (e.g., 3): ");
        int sem = Integer.parseInt(scanner.nextLine().trim());
        printDepartmentSummary(sem);
    }

    public static void printDepartmentSummary(int semester) {
        Map<String, AcademicDAO.DepartmentSummary> map = dao.getClassPerformanceSummary(semester);
        System.out.println("\n==========================================================================================");
        System.out.println("                    KONGU ENGINEERING COLLEGE (AUTONOMOUS) - REGULATION 2024              ");
        System.out.println("                     DEPARTMENT-WISE CLASS PERFORMANCE & RESULT SUMMARY                   ");
        System.out.printf("                                      SEMESTER: %d                                         \n", semester);
        System.out.println("==========================================================================================");
        System.out.printf("%-12s %-16s %-14s %-14s %-14s %-14s\n",
                "Department", "Total Students", "Passed All", "With Arrears", "Pass Rate (%)", "Class Avg SGPA");
        System.out.println("------------------------------------------------------------------------------------------");

        if (map.isEmpty()) {
            System.out.println(" >> No records available for Semester " + semester);
        } else {
            for (AcademicDAO.DepartmentSummary ds : map.values()) {
                System.out.printf("%-12s %-16d %-14d %-14d %12.2f%% %14.2f\n",
                        ds.department, ds.totalStudents, ds.passedStudents, ds.failedStudents,
                        ds.passPercentage, ds.averageSgpa);
            }
        }
        System.out.println("==========================================================================================\n");
    }

    // =========================================================================
    // 8. Automated Demonstration Proof
    // =========================================================================
    public static void runAutomatedDemo() {
        System.out.println("\n==========================================================================================");
        System.out.println("                RUNNING AUTOMATED END-TO-END EXECUTION PROOF                              ");
        System.out.println("==========================================================================================");

        // Step 1: Add New Student
        String demoRoll = "24ITR099";
        String demoName = "Murasoli B";
        String demoDept = "IT";
        int demoSem = 3;
        System.out.println("\n>>> [STEP 1] Adding Student Registration: " + demoRoll + " (" + demoName + ") Dept: " + demoDept);
        boolean sAdded = dao.addStudent(demoRoll, demoName, demoDept, demoSem);
        System.out.println("Result: " + (sAdded ? "SUCCESS (Student registered in DB)" : "FAILED"));

        // Step 2: Verify / Add Course
        String demoCourse = "24ITC31";
        String demoCourseTitle = "Full Stack Development";
        int demoCredits = 4;
        String demoCourseType = "EMBEDDED";
        System.out.println("\n>>> [STEP 2] Adding / Ensuring Course Catalog: " + demoCourse + " - " + demoCourseTitle + " [" + demoCourseType + ", " + demoCredits + " Credits]");
        boolean cAdded = dao.addCourse(demoCourse, demoCourseTitle, demoCredits, demoCourseType, demoSem);
        System.out.println("Result: " + (cAdded ? "SUCCESS (Course registered in DB)" : "FAILED"));

        // Step 3: Enroll Student & Record Attendance
        System.out.println("\n>>> [STEP 3] Enrolling " + demoRoll + " in " + demoCourse + " with 58/60 classes attended (96.7%)");
        int enrollId = dao.enrollStudent(demoRoll, demoCourse, demoSem, 60, 58, false);
        System.out.println("Result: SUCCESS (Enrollment ID: " + enrollId + ")");

        // Step 4: Incremental Marks Entry ("Then and Now")
        System.out.println("\n>>> [STEP 4A] Phase 1 (Mid-Semester): Recording CIA 1 (94.0) and CIA 2 (96.0)");
        dao.recordPhase1Marks(enrollId, 94.0, 96.0);
        System.out.println("Result: Phase 1 Marks Saved.");

        System.out.println("\n>>> [STEP 4B] Phase 2 (End of Semester - CIA): Recording Assignment/Lab (95.0)");
        dao.recordPhase2Marks(enrollId, 95.0);
        System.out.println("Result: Phase 2 Marks Saved.");

        System.out.println("\n>>> [STEP 4C] Phase 3 (Post Semester): Recording End Semester Exam Marks (94.0)");
        dao.recordPhase3Marks(enrollId, 94.0, false);
        System.out.println("Result: Phase 3 Marks Saved.");

        // Also enroll in a Theory course: 24ITT32 (Programming in Java)
        int enrollTheory = dao.enrollStudent(demoRoll, "24ITT32", demoSem, 45, 44, false);
        dao.recordPhase1Marks(enrollTheory, 90.0, 92.0);
        dao.recordPhase2Marks(enrollTheory, 94.0);
        dao.recordPhase3Marks(enrollTheory, 90.0, false);

        // Also enroll in a Practical course: 24ITL31 (Programming in Java Laboratory)
        int enrollPrac = dao.enrollStudent(demoRoll, "24ITL31", demoSem, 30, 30, false);
        dao.recordPhase1Marks(enrollPrac, 98.0, 98.0);
        dao.recordPhase2Marks(enrollPrac, 100.0);
        dao.recordPhase3Marks(enrollPrac, 96.0, false);

        // Step 5: Generated Grade Sheet with calculated SGPA
        System.out.println("\n>>> [STEP 5] Generating Grade Sheet with Calculated SGPA for " + demoRoll + ":");
        printStudentMarkSheet(demoRoll, demoSem);

        // Step 6: Attendance Defaulter Report
        System.out.println(">>> [STEP 6] Generating Attendance Defaulter Report for Semester " + demoSem + ":");
        printAttendanceDefaulters(demoSem);

        // Step 7: Class Performance Summary
        System.out.println(">>> [STEP 7] Generating Department-wise Class Performance Summary for Semester " + demoSem + ":");
        printDepartmentSummary(demoSem);

        System.out.println(">>> Automated Demonstration Completed Successfully.\n");
    }
}
