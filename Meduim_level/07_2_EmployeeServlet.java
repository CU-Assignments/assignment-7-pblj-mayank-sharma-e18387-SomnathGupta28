import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection getConnection() throws Exception {
        Properties props = new Properties();
        props.load(getServletContext().getResourceAsStream("/WEB-INF/db-config.properties"));
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String employeeId = request.getParameter("employeeId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = getConnection()) {
            if (employeeId != null && !employeeId.isEmpty()) {
                // Search for a specific employee by ID
                String query = "SELECT * FROM employees WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, Integer.parseInt(employeeId));
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        out.println("<h3>Employee Found:</h3>");
                        out.println("<p>ID: " + rs.getInt("id") + "</p>");
                        out.println("<p>Name: " + rs.getString("name") + "</p>");
                        out.println("<p>Department: " + rs.getString("department") + "</p>");
                        out.println("<p>Email: " + rs.getString("email") + "</p>");
                    } else {
                        out.println("<p>No employee found with ID: " + employeeId + "</p>");
                    }
                }
            } else {
                // Display all employees
                String query = "SELECT * FROM employees";
                try (PreparedStatement stmt = conn.prepareStatement(query);
                     ResultSet rs = stmt.executeQuery()) {
                    out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Department</th><th>Email</th></tr>");
                    while (rs.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rs.getInt("id") + "</td>");
                        out.println("<td>" + rs.getString("name") + "</td>");
                        out.println("<td>" + rs.getString("department") + "</td>");
                        out.println("<td>" + rs.getString("email") + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}