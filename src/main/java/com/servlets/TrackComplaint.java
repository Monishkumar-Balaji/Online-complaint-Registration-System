package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TrackComplaint")
public class TrackComplaint extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private final String url = "jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser = "root";
    private final String dbPass = "1111";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String enteredId = request.getParameter("complaint_id");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // extract numeric part
            String numberPart = enteredId.substring(enteredId.lastIndexOf("-") + 1);
            int id = Integer.parseInt(numberPart);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT * FROM complaints WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String category = rs.getString("category");
                String desc = rs.getString("description");
                String status = rs.getString("status");
                String remarks = rs.getString("remarks");

                out.println("<h2>Complaint Details</h2>");
                out.println("<p><b>ID:</b> " + enteredId + "</p>");
                out.println("<p><b>Category:</b> " + category + "</p>");
                out.println("<p><b>Description:</b> " + desc + "</p>");
                out.println("<p><b>Status:</b> " + status + "</p>");
                out.println("<p><b>Remarks:</b> " + (remarks == null ? "-" : remarks) + "</p>");

            } else {
                out.println("<h3>No complaint found</h3>");
            }

            con.close();

        } catch (Exception e) {
            out.println("<h3>Invalid Complaint ID</h3>");
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String enteredId = request.getParameter("code");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            String numberPart = enteredId.substring(enteredId.lastIndexOf("-") + 1);
            int id = Integer.parseInt(numberPart);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT * FROM complaints WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                out.println("<h2>Complaint Details</h2>");
                out.println("<p><b>ID:</b> " + enteredId + "</p>");
                out.println("<p><b>Category:</b> " + rs.getString("category") + "</p>");
                out.println("<p><b>Description:</b> " + rs.getString("description") + "</p>");
                out.println("<p><b>Status:</b> " + rs.getString("status") + "</p>");

                String remarks = rs.getString("remarks");
                out.println("<p><b>Remarks:</b> " + (remarks == null ? "-" : remarks) + "</p>");

            } else {
                out.println("<h3>No complaint found</h3>");
            }

            con.close();

        } catch (Exception e) {
            out.println("<h3>Invalid Complaint ID</h3>");
        }
    }
}
