package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/UserRegister")
public class UserRegister extends HttpServlet {

    private final String url = "jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser = "root";
    private final String dbPass = "1111";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);

            // check duplicate email
            String check = "SELECT id FROM users WHERE email=?";
            PreparedStatement ps1 = con.prepareStatement(check);
            ps1.setString(1, email);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                out.println("<h3>Email already registered</h3>");
                return;
            }

            String sql = "INSERT INTO users(name,email,password) VALUES(?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            out.println("<h2>Registration successful!</h2>");
            out.println("<a href='login.html'>Login now</a>");

            con.close();

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
