import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RunSchema {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false&allowMultiQueries=true&serverTimezone=UTC";
        String user = "root";
        String pass = "0000";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[INFO] Connecting to MySQL server...");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 Statement stmt = conn.createStatement()) {

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader("sql/kec_schema.sql"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // ignore comment lines
                        String trimmed = line.trim();
                        if (trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                            continue;
                        }
                        sb.append(line).append("\n");
                    }
                }

                String allSql = sb.toString();
                // Split by semicolon that is followed by newline or end of file
                String[] statements = allSql.split(";\\s*(\\r?\\n|$)");

                System.out.println("[INFO] Executing schema SQL statements (" + statements.length + " chunks)...");
                int executed = 0;
                for (String sql : statements) {
                    String clean = sql.trim();
                    if (!clean.isEmpty()) {
                        stmt.execute(clean);
                        executed++;
                    }
                }

                System.out.println("[SUCCESS] Successfully executed " + executed + " SQL statements from sql/kec_schema.sql!");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to execute schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
