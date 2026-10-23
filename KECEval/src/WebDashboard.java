import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * WebDashboard - Embedded HTTP Server and Interactive Web Dashboard
 * Kongu Engineering College (Autonomous) - Regulation 2024
 */
public class WebDashboard {

    private static final int PORT = 8080;
    private static final AcademicDAO dao = new AcademicDAO();

    public static void main(String[] args) {
        int activePort = startServer(PORT, true);
        if (activePort > 0) {
            System.out.println(" >> Press [Ctrl+C] to exit, or type 'q' + Enter to stop.");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if ("q".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line)) {
                    System.out.println("Stopping Web Dashboard server...");
                    System.exit(0);
                }
            }
        }
    }

    public static int startServer(int preferredPort, boolean openBrowser) {
        HttpServer server = null;
        int activePort = preferredPort;

        for (int p = preferredPort; p < preferredPort + 25; p++) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                activePort = p;
                break;
            } catch (IOException e) {
                // Port in use, continue searching
            }
        }

        if (server == null) {
            System.err.println("[ERROR] Could not bind to any port in range " + preferredPort + " - " + (preferredPort + 25));
            return -1;
        }

        try {
            // Static Web Page
            server.createContext("/", new StaticHandler());

            // API Endpoints
            server.createContext("/api/overview", new OverviewHandler());
            server.createContext("/api/students", new StudentsHandler());
            server.createContext("/api/courses", new CoursesHandler());
            server.createContext("/api/report", new ReportHandler());
            server.createContext("/api/defaulters", new DefaultersHandler());
            server.createContext("/api/summary", new SummaryHandler());
            server.createContext("/api/marks/save", new SaveMarksHandler());
            server.createContext("/api/condonation/toggle", new ToggleCondonationHandler());
            server.createContext("/api/enroll", new EnrollHandler());

            server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
            server.start();

            String url = "http://localhost:" + activePort;
            System.out.println("=====================================================================");
            System.out.println(" >> KEC (R2024) Student Evaluation Dashboard is LIVE!");
            System.out.println(" >> URL: " + url);
            if (activePort != preferredPort) {
                System.out.println(" >> Note: Port " + preferredPort + " was busy, automatically bound to " + activePort);
            }
            System.out.println("=====================================================================");

            if (openBrowser) {
                try {
                    if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        java.awt.Desktop.getDesktop().browse(new URI(url));
                    } else {
                        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
                    }
                } catch (Exception e) {
                    System.out.println("Please open your browser at: " + url);
                }
            }
            return activePort;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to start HTTP server: " + e.getMessage());
            return -1;
        }
    }

    // =========================================================================
    // HTTP Handlers
    // =========================================================================

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if ("/favicon.ico".equalsIgnoreCase(path) || path.endsWith(".ico")) {
                File f = new File("assets/kec_icon.ico");
                if (!f.exists()) f = new File("web/favicon.ico");
                if (f.exists()) {
                    byte[] b = Files.readAllBytes(f.toPath());
                    exchange.getResponseHeaders().set("Content-Type", "image/x-icon");
                    exchange.sendResponseHeaders(200, b.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(b);
                    }
                    return;
                }
            }
            if (path.endsWith(".png")) {
                String fname = path.substring(path.lastIndexOf('/') + 1);
                File f = new File("assets/" + fname);
                if (!f.exists()) f = new File("web/" + fname);
                if (f.exists()) {
                    byte[] b = Files.readAllBytes(f.toPath());
                    exchange.getResponseHeaders().set("Content-Type", "image/png");
                    exchange.sendResponseHeaders(200, b.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(b);
                    }
                    return;
                }
            }
            String html = getDashboardHtml();
            sendResponse(exchange, 200, html, "text/html; charset=UTF-8");
        }
    }

    static class OverviewHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            int sem = q.containsKey("sem") ? Integer.parseInt(q.get("sem")) : 3;
            String filterDept = q.get("dept");
            boolean hasDept = (filterDept != null && !filterDept.trim().isEmpty() && !"ALL".equalsIgnoreCase(filterDept.trim()));
            String targetDept = hasDept ? filterDept.trim().toUpperCase() : null;

            List<AcademicDAO.Student> students = dao.getAllStudents();
            List<AcademicDAO.Course> courses = dao.getAllCourses();
            List<AcademicDAO.EnrollmentRecord> defaulters = dao.getAttendanceDefaulters(sem);
            Map<String, AcademicDAO.DepartmentSummary> summaries = dao.getClassPerformanceSummary(sem);

            if (hasDept) {
                students.removeIf(s -> !targetDept.equalsIgnoreCase(s.department));
                defaulters.removeIf(r -> !targetDept.equalsIgnoreCase(r.department));
            }

            int totalStudents = students.size();
            int totalCourses = 0;
            for (AcademicDAO.Course c : courses) {
                if (c.semester == sem) {
                    if (!hasDept || sem <= 2) {
                        totalCourses++;
                    } else {
                        String codeUp = c.courseCode.toUpperCase();
                        boolean match = false;
                        if (targetDept.equals("IT")) match = codeUp.contains("IT") || codeUp.contains("MN") || codeUp.contains("MA");
                        else if (targetDept.equals("CSE")) match = codeUp.contains("CS") || codeUp.contains("MA");
                        else if (targetDept.equals("ECE")) match = codeUp.contains("EC") || codeUp.contains("MA");
                        else if (targetDept.equals("EEE")) match = codeUp.contains("EE") || codeUp.contains("MA");
                        else if (targetDept.equals("MECH")) match = codeUp.contains("ME") || codeUp.contains("MA");
                        else if (targetDept.equals("CIVIL")) match = codeUp.contains("CE") || codeUp.contains("MA");
                        else if (targetDept.equals("AIDS")) match = codeUp.contains("AD") || codeUp.contains("MA");
                        else if (targetDept.equals("CHEM")) match = codeUp.contains("CH") || codeUp.contains("MA");
                        if (match) totalCourses++;
                    }
                }
            }
            if (totalCourses == 0) totalCourses = courses.size();

            int defCount = 0;
            for (AcademicDAO.EnrollmentRecord r : defaulters) {
                double pct = r.totalClasses > 0 ? (r.attendedClasses * 100.0 / r.totalClasses) : 0;
                if (pct < 65.0) defCount++;
            }

            int overallPassed = 0;
            int totalEnrolledStudentsInSem = 0;
            double sgpaSum = 0.0;

            for (AcademicDAO.DepartmentSummary ds : summaries.values()) {
                if (!hasDept || targetDept.equalsIgnoreCase(ds.department)) {
                    overallPassed += ds.passedStudents;
                    totalEnrolledStudentsInSem += ds.totalStudents;
                    sgpaSum += (ds.averageSgpa * ds.totalStudents);
                }
            }

            double overallPassRate = totalEnrolledStudentsInSem > 0 ? (overallPassed * 100.0 / totalEnrolledStudentsInSem) : 0;
            double avgSgpa = totalEnrolledStudentsInSem > 0 ? (sgpaSum / totalEnrolledStudentsInSem) : 0;

            // Grade distribution for the selected department
            Map<String, Integer> gradeDist = new LinkedHashMap<>();
            gradeDist.put("O", 0);
            gradeDist.put("A+", 0);
            gradeDist.put("A", 0);
            gradeDist.put("B+", 0);
            gradeDist.put("B", 0);
            gradeDist.put("RA", 0);
            gradeDist.put("SA", 0);
            gradeDist.put("AB", 0);

            for (AcademicDAO.Student s : students) {
                AcademicDAO.SemesterReport rep = dao.generateStudentSemesterReport(s.rollNo, sem);
                if (rep != null) {
                    for (AcademicDAO.CourseEvaluationResult c : rep.courses) {
                        String g = c.letterGrade != null ? c.letterGrade : "--";
                        if (gradeDist.containsKey(g)) {
                            gradeDist.put(g, gradeDist.get(g) + 1);
                        }
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"totalStudents\":").append(totalStudents).append(",");
            sb.append("\"totalCourses\":").append(totalCourses).append(",");
            sb.append("\"defaultersCount\":").append(defCount).append(",");
            sb.append("\"overallPassRate\":").append(String.format(Locale.US, "%.2f", overallPassRate)).append(",");
            sb.append("\"avgSgpa\":").append(String.format(Locale.US, "%.2f", avgSgpa)).append(",");

            // Grade distribution JSON
            sb.append("\"grades\":{");
            boolean firstG = true;
            for (Map.Entry<String, Integer> entry : gradeDist.entrySet()) {
                if (!firstG) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
                firstG = false;
            }
            sb.append("},");

            // Department summaries JSON
            sb.append("\"departments\":[");
            boolean firstD = true;
            for (AcademicDAO.DepartmentSummary ds : summaries.values()) {
                if (hasDept && !targetDept.equalsIgnoreCase(ds.department)) continue;
                if (!firstD) sb.append(",");
                sb.append("{");
                sb.append("\"dept\":\"").append(ds.department).append("\",");
                sb.append("\"total\":").append(ds.totalStudents).append(",");
                sb.append("\"passed\":").append(ds.passedStudents).append(",");
                sb.append("\"failed\":").append(ds.failedStudents).append(",");
                sb.append("\"passRate\":").append(String.format(Locale.US, "%.2f", ds.passPercentage)).append(",");
                sb.append("\"avgSgpa\":").append(String.format(Locale.US, "%.2f", ds.averageSgpa));
                sb.append("}");
                firstD = false;
            }
            sb.append("]");

            // Course summaries JSON
            List<AcademicDAO.CourseSummary> courseSummaries = dao.getCoursePerformanceSummary(sem);
            if (hasDept && sem > 2) {
                courseSummaries.removeIf(cs -> {
                    String codeUp = cs.courseCode.toUpperCase();
                    boolean match = false;
                    if (targetDept.equals("IT")) match = codeUp.contains("IT") || codeUp.contains("MN") || codeUp.contains("MA");
                    else if (targetDept.equals("CSE")) match = codeUp.contains("CS") || codeUp.contains("MA");
                    else if (targetDept.equals("ECE")) match = codeUp.contains("EC") || codeUp.contains("MA");
                    else if (targetDept.equals("EEE")) match = codeUp.contains("EE") || codeUp.contains("MA");
                    else if (targetDept.equals("MECH")) match = codeUp.contains("ME") || codeUp.contains("MA");
                    else if (targetDept.equals("CIVIL")) match = codeUp.contains("CE") || codeUp.contains("MA");
                    else if (targetDept.equals("AIDS")) match = codeUp.contains("AD") || codeUp.contains("MA");
                    else if (targetDept.equals("CHEM")) match = codeUp.contains("CH") || codeUp.contains("MA");
                    return !match;
                });
            }
            sb.append(",\"courseSummaries\":[");
            boolean firstCS = true;
            for (AcademicDAO.CourseSummary cs : courseSummaries) {
                if (!firstCS) sb.append(",");
                sb.append("{");
                sb.append("\"courseCode\":\"").append(escapeJson(cs.courseCode)).append("\",");
                sb.append("\"courseTitle\":\"").append(escapeJson(cs.courseTitle)).append("\",");
                sb.append("\"courseType\":\"").append(escapeJson(cs.courseType)).append("\",");
                sb.append("\"credits\":").append(cs.credits).append(",");
                sb.append("\"total\":").append(cs.totalStudents).append(",");
                sb.append("\"passed\":").append(cs.passedStudents).append(",");
                sb.append("\"passRate\":").append(String.format(Locale.US, "%.2f", cs.passPercentage)).append(",");
                sb.append("\"avgScore\":").append(String.format(Locale.US, "%.2f", cs.averageScore));
                sb.append("}");
                firstCS = false;
            }
            sb.append("]");

            sb.append("}");
            sendResponse(exchange, 200, sb.toString(), "application/json");
        }
    }

    static class StudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<AcademicDAO.Student> list = dao.getAllStudents();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    AcademicDAO.Student s = list.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"rollNo\":\"").append(escapeJson(s.rollNo)).append("\",")
                      .append("\"name\":\"").append(escapeJson(s.name)).append("\",")
                      .append("\"department\":\"").append(escapeJson(s.department)).append("\",")
                      .append("\"semester\":").append(s.currentSemester)
                      .append("}");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> data = parseJsonForm(body);
                String roll = data.get("rollNo");
                String name = data.get("name");
                String dept = data.get("department");
                int sem = Integer.parseInt(data.getOrDefault("semester", "3"));

                boolean ok = dao.addStudent(roll, name, dept, sem);
                sendResponse(exchange, ok ? 200 : 400, "{\"success\":" + ok + "}", "application/json");
            }
        }
    }

    static class CoursesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<AcademicDAO.Course> list = dao.getAllCourses();
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    AcademicDAO.Course c = list.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"courseCode\":\"").append(escapeJson(c.courseCode)).append("\",")
                      .append("\"title\":\"").append(escapeJson(c.title)).append("\",")
                      .append("\"credits\":").append(c.credits).append(",")
                      .append("\"courseType\":\"").append(escapeJson(c.courseType)).append("\",")
                      .append("\"semester\":").append(c.semester)
                      .append("}");
                }
                sb.append("]");
                sendResponse(exchange, 200, sb.toString(), "application/json");
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = readBody(exchange);
                Map<String, String> data = parseJsonForm(body);
                String code = data.get("courseCode");
                String title = data.get("title");
                int credits = Integer.parseInt(data.getOrDefault("credits", "3"));
                String type = data.getOrDefault("courseType", "THEORY");
                int sem = Integer.parseInt(data.getOrDefault("semester", "3"));

                boolean ok = dao.addCourse(code, title, credits, type, sem);
                sendResponse(exchange, ok ? 200 : 400, "{\"success\":" + ok + "}", "application/json");
            }
        }
    }

    static class ReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            String rollNo = q.get("rollNo");
            if (rollNo == null || rollNo.trim().isEmpty()) {
                rollNo = q.get("rollNumber");
            }
            int sem = -1;
            if (q.containsKey("sem") && !"ALL".equalsIgnoreCase(q.get("sem"))) {
                try {
                    sem = Integer.parseInt(q.get("sem"));
                } catch (NumberFormatException ignored) {}
            }
            if (sem <= 0) {
                AcademicDAO.Student s = dao.getStudent(rollNo);
                sem = (s != null && s.currentSemester > 0) ? s.currentSemester : 3;
            }

            AcademicDAO.SemesterReport rep = dao.generateStudentSemesterReport(rollNo, sem);
            if (rep == null) {
                sendResponse(exchange, 404, "{\"error\":\"Student not found\"}", "application/json");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"rollNo\":\"").append(escapeJson(rep.student.rollNo)).append("\",");
            sb.append("\"name\":\"").append(escapeJson(rep.student.name)).append("\",");
            sb.append("\"department\":\"").append(escapeJson(rep.student.department)).append("\",");
            sb.append("\"semester\":").append(rep.semester).append(",");
            sb.append("\"totalEnrolledCredits\":").append(rep.totalEnrolledCredits).append(",");
            sb.append("\"totalEarnedCredits\":").append(rep.totalEarnedCredits).append(",");
            sb.append("\"weightedPoints\":").append(String.format(Locale.US, "%.2f", rep.totalGradePointsEarned)).append(",");
            sb.append("\"sgpa\":").append(String.format(Locale.US, "%.2f", rep.sgpa)).append(",");
            sb.append("\"percentage\":").append(String.format(Locale.US, "%.2f", rep.percentage)).append(",");
            sb.append("\"allPassed\":").append(rep.allCoursesPassed).append(",");

            sb.append("\"courses\":[");
            for (int i = 0; i < rep.courses.size(); i++) {
                AcademicDAO.CourseEvaluationResult c = rep.courses.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"courseCode\":\"").append(escapeJson(c.courseCode)).append("\",");
                sb.append("\"courseTitle\":\"").append(escapeJson(c.courseTitle)).append("\",");
                sb.append("\"courseType\":\"").append(escapeJson(c.courseType)).append("\",");
                sb.append("\"credits\":").append(c.credits).append(",");
                sb.append("\"totalClasses\":").append(c.totalClasses).append(",");
                sb.append("\"attendedClasses\":").append(c.attendedClasses).append(",");
                sb.append("\"attendancePct\":").append(String.format(Locale.US, "%.1f", c.attendancePct)).append(",");
                sb.append("\"attendanceStatus\":\"").append(escapeJson(c.attendanceStatus)).append("\",");
                sb.append("\"cia1\":").append(c.cia1 != null ? String.format(Locale.US, "%.1f", c.cia1) : "null").append(",");
                sb.append("\"cia2\":").append(c.cia2 != null ? String.format(Locale.US, "%.1f", c.cia2) : "null").append(",");
                sb.append("\"assignment\":").append(c.assignment != null ? String.format(Locale.US, "%.1f", c.assignment) : "null").append(",");
                sb.append("\"scaledCia\":").append(String.format(Locale.US, "%.2f", c.scaledCia)).append(",");
                sb.append("\"ciaWeight\":").append(String.format(Locale.US, "%.0f", c.ciaWeightage)).append(",");
                sb.append("\"ese\":").append(c.eseMarks != null ? String.format(Locale.US, "%.1f", c.eseMarks) : "null").append(",");
                sb.append("\"scaledEse\":").append(String.format(Locale.US, "%.2f", c.scaledEse)).append(",");
                sb.append("\"eseWeight\":").append(String.format(Locale.US, "%.0f", c.eseWeightage)).append(",");
                sb.append("\"isAbsent\":").append(c.isEseAbsent).append(",");
                sb.append("\"totalMarks\":").append(String.format(Locale.US, "%.2f", c.totalMarks)).append(",");
                sb.append("\"grade\":\"").append(escapeJson(c.letterGrade)).append("\",");
                sb.append("\"gradePoint\":").append(c.gradePoint).append(",");
                sb.append("\"result\":\"").append(escapeJson(c.resultStatus)).append("\"");
                sb.append("}");
            }
            sb.append("]");

            sb.append("}");
            sendResponse(exchange, 200, sb.toString(), "application/json");
        }
    }

    static class DefaultersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            int sem = 3;
            if (q.containsKey("sem")) {
                String sVal = q.get("sem");
                if ("ALL".equalsIgnoreCase(sVal)) {
                    sem = 0;
                } else {
                    try { sem = Integer.parseInt(sVal); } catch (Exception ignored) {}
                }
            }

            List<AcademicDAO.EnrollmentRecord> list = dao.getAttendanceDefaulters(sem);
            String dept = q.get("dept");
            if (dept != null && !dept.trim().isEmpty() && !"ALL".equalsIgnoreCase(dept.trim())) {
                String dUpper = dept.trim().toUpperCase();
                list.removeIf(r -> !dUpper.equalsIgnoreCase(r.department));
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                AcademicDAO.EnrollmentRecord r = list.get(i);
                if (i > 0) sb.append(",");
                double pct = r.totalClasses > 0 ? (r.attendedClasses * 100.0 / r.totalClasses) : 0;
                sb.append("{");
                sb.append("\"enrollmentId\":").append(r.enrollmentId).append(",");
                sb.append("\"rollNo\":\"").append(escapeJson(r.rollNo)).append("\",");
                sb.append("\"studentName\":\"").append(escapeJson(r.studentName)).append("\",");
                sb.append("\"department\":\"").append(escapeJson(r.department)).append("\",");
                sb.append("\"courseCode\":\"").append(escapeJson(r.courseCode)).append("\",");
                sb.append("\"courseTitle\":\"").append(escapeJson(r.courseTitle)).append("\",");
                sb.append("\"totalClasses\":").append(r.totalClasses).append(",");
                sb.append("\"attendedClasses\":").append(r.attendedClasses).append(",");
                sb.append("\"attendancePct\":").append(String.format(Locale.US, "%.1f", pct)).append(",");
                sb.append("\"isCondoned\":").append(r.isCondoned).append(",");
                sb.append("\"isDefaulter\":").append(pct < 65.0);
                sb.append("}");
            }
            sb.append("]");
            sendResponse(exchange, 200, sb.toString(), "application/json");
        }
    }

    static class SummaryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
            int sem = q.containsKey("sem") ? Integer.parseInt(q.get("sem")) : 3;
            Map<String, AcademicDAO.DepartmentSummary> map = dao.getClassPerformanceSummary(sem);

            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (AcademicDAO.DepartmentSummary ds : map.values()) {
                if (!first) sb.append(",");
                sb.append("{");
                sb.append("\"department\":\"").append(escapeJson(ds.department)).append("\",");
                sb.append("\"totalStudents\":").append(ds.totalStudents).append(",");
                sb.append("\"passedStudents\":").append(ds.passedStudents).append(",");
                sb.append("\"failedStudents\":").append(ds.failedStudents).append(",");
                sb.append("\"passPercentage\":").append(String.format(Locale.US, "%.2f", ds.passPercentage)).append(",");
                sb.append("\"averageSgpa\":").append(String.format(Locale.US, "%.2f", ds.averageSgpa));
                sb.append("}");
                first = false;
            }
            sb.append("]");
            sendResponse(exchange, 200, sb.toString(), "application/json");
        }
    }

    static class SaveMarksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            String body = readBody(exchange);
            Map<String, String> data = parseJsonForm(body);

            String rollNo = data.get("rollNo");
            String courseCode = data.get("courseCode");
            int sem = Integer.parseInt(data.getOrDefault("semester", "3"));
            int enrollId = dao.getEnrollmentId(rollNo, courseCode, sem);

            Integer totalCl = parseIntegerOrNull(data.get("totalClasses"));
            Integer attCl = parseIntegerOrNull(data.get("attendedClasses"));
            boolean isCondoned = Boolean.parseBoolean(data.getOrDefault("isCondoned", "false"));

            if (enrollId <= 0) {
                int tot = (totalCl != null && totalCl > 0) ? totalCl : 60;
                int att = (attCl != null && attCl >= 0) ? attCl : (int)Math.round(tot * 0.9);
                enrollId = dao.enrollStudent(rollNo, courseCode, sem, tot, att, isCondoned);
            } else if (totalCl != null && attCl != null && totalCl > 0) {
                dao.updateAttendance(enrollId, totalCl, attCl, isCondoned);
            }

            if (enrollId <= 0) {
                sendResponse(exchange, 400, "{\"success\":false,\"error\":\"Failed to locate or enroll student into course\"}", "application/json");
                return;
            }

            Double c1 = parseDoubleOrNull(data.get("cia1"));
            Double c2 = parseDoubleOrNull(data.get("cia2"));
            if (c1 != null || c2 != null) {
                dao.recordPhase1Marks(enrollId, c1, c2);
            }

            Double asg = parseDoubleOrNull(data.get("assignment"));
            if (asg != null) {
                dao.recordPhase2Marks(enrollId, asg);
            }

            boolean isAbsent = Boolean.parseBoolean(data.getOrDefault("isAbsent", "false"));
            Double ese = parseDoubleOrNull(data.get("eseMarks"));
            if (isAbsent || ese != null) {
                dao.recordPhase3Marks(enrollId, ese, isAbsent);
            }

            sendResponse(exchange, 200, "{\"success\":true,\"enrollmentId\":" + enrollId + "}", "application/json");
        }
    }

    static class ToggleCondonationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            String body = readBody(exchange);
            Map<String, String> data = parseJsonForm(body);
            int enrollId = Integer.parseInt(data.get("enrollmentId"));
            boolean cond = Boolean.parseBoolean(data.get("isCondoned"));

            boolean ok = dao.setCondonation(enrollId, cond);
            sendResponse(exchange, ok ? 200 : 400, "{\"success\":" + ok + "}", "application/json");
        }
    }

    static class EnrollHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }
            String body = readBody(exchange);
            Map<String, String> data = parseJsonForm(body);
            String roll = data.get("rollNo");
            String code = data.get("courseCode");
            String codes = data.get("courseCodes");
            int sem = Integer.parseInt(data.getOrDefault("semester", "3"));
            int total = Integer.parseInt(data.getOrDefault("totalClasses", "50"));
            int attended = Integer.parseInt(data.getOrDefault("attendedClasses", "45"));
            boolean cond = Boolean.parseBoolean(data.getOrDefault("isCondoned", "false"));

            if (codes != null && !codes.trim().isEmpty()) {
                List<String> toEnroll = new ArrayList<>();
                if ("ALL".equalsIgnoreCase(codes.trim())) {
                    AcademicDAO.Student student = dao.getStudent(roll);
                    String dept = (student != null && student.department != null) ? student.department.toUpperCase() : "";
                    for (AcademicDAO.Course c : dao.getAllCourses()) {
                        if (c.semester == sem) {
                            String codeUp = c.courseCode.toUpperCase();
                            boolean matches = false;
                            if (sem <= 2) {
                                matches = true; // Common courses for sem 1 and 2
                            } else if (dept.equals("IT")) {
                                matches = codeUp.contains("IT") || codeUp.contains("MN") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("CSE")) {
                                matches = codeUp.contains("CS") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("ECE")) {
                                matches = codeUp.contains("EC") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("EEE")) {
                                matches = codeUp.contains("EE") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("MECH")) {
                                matches = codeUp.contains("ME") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("CIVIL")) {
                                matches = codeUp.contains("CE") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("AIDS")) {
                                matches = codeUp.contains("AD") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else if (dept.equals("CHEM")) {
                                matches = codeUp.contains("CH") || codeUp.contains("MA") || codeUp.contains("GE");
                            } else {
                                matches = true;
                            }
                            if (matches) {
                                toEnroll.add(c.courseCode);
                            }
                        }
                    }
                    if (toEnroll.isEmpty()) {
                        for (AcademicDAO.Course c : dao.getAllCourses()) {
                            if (c.semester == sem) toEnroll.add(c.courseCode);
                        }
                    }
                } else {
                    for (String part : codes.split(",")) {
                        if (!part.trim().isEmpty()) toEnroll.add(part.trim().toUpperCase());
                    }
                }
                int count = 0;
                for (String cCode : toEnroll) {
                    int id = dao.enrollStudent(roll, cCode, sem, total, attended, cond);
                    if (id > 0) count++;
                }
                sendResponse(exchange, 200, "{\"success\":true,\"enrolledCount\":" + count + "}", "application/json");
                return;
            }

            int id = dao.enrollStudent(roll, code, sem, total, attended, cond);
            sendResponse(exchange, id > 0 ? 200 : 400, "{\"success\":" + (id > 0) + ",\"enrollmentId\":" + id + "}", "application/json");
        }
    }

    // =========================================================================
    // Utilities
    // =========================================================================

    private static void sendResponse(HttpExchange exchange, int status, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static Map<String, String> parseJsonForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;
        body = body.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            String[] tokens = body.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String token : tokens) {
                String[] pair = token.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (pair.length == 2) {
                    String key = pair[0].trim().replace("\"", "");
                    String val = pair[1].trim().replace("\"", "");
                    map.put(key, val);
                }
            }
        }
        return map;
    }

    private static Double parseDoubleOrNull(String str) {
        if (str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str.trim())) return null;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseIntegerOrNull(String str) {
        if (str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str.trim())) return null;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // =========================================================================
    // HTML/CSS/JavaScript Dashboard UI Loader
    // =========================================================================
    private static String getDashboardHtml() {
        String[] candidatePaths = {
            "web/dashboard.html",
            "dashboard.html",
            "src/dashboard.html",
            "C:/MY DRIVE/KECEval/web/dashboard.html",
            "C:/MY DRIVE/KECEval/dashboard.html"
        };

        for (String path : candidatePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try {
                    return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                } catch (IOException ignored) {}
            }
        }

        return "<!DOCTYPE html><html><body><h2>KEC Evaluation Dashboard</h2><p>web/dashboard.html not found.</p></body></html>";
    }
}
