package com.servlets;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/UpdateComplaint")
@jakarta.servlet.annotation.MultipartConfig
public class UpdateComplaint extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String url="jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser="root";
    private final String dbPass="1111";

    protected void doPost(HttpServletRequest request,HttpServletResponse response)
    throws ServletException,IOException{

        response.setContentType("application/json");
        PrintWriter out=response.getWriter();

        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute("admin")==null){
            out.print("{\"status\":\"error\"}");
            return;
        }

        try{
            int id=Integer.parseInt(request.getParameter("id"));
            String status=request.getParameter("status");
            String remarks=request.getParameter("remarks");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            String sql="UPDATE complaints SET status=?, remarks=? WHERE id=?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setString(1,status);
            ps.setString(2,remarks);
            ps.setInt(3,id);

            ps.executeUpdate();
            con.close();

            out.print("{\"status\":\"success\"}");

        }catch(Exception e){
            out.print("{\"status\":\"error\"}");
        }
    }
}
