package com.servlets;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final String url = "jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser = "root";
    private final String dbPass = "1111";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT id,name FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");

                // 🔐 create session
                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);
                session.setAttribute("user_name", name);

                // redirect to complaint page
                response.sendRedirect("dashboard.html");

            } else {
                response.getWriter().println("<h3>Invalid login</h3>");
            }

            con.close();

        } catch (Exception e) {
            response.getWriter().println(e.getMessage());
        }
    }
}
