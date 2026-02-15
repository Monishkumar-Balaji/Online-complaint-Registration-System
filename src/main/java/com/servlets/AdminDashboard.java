package com.servlets;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Servlet implementation class AdminDashboard
 */
@SuppressWarnings("unused")
@WebServlet("/adminDashboard")
public class AdminDashboard extends HttpServlet{
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 protected void doGet(HttpServletRequest req,HttpServletResponse res)
 throws IOException,ServletException{

   HttpSession s=req.getSession(false);
   if(s==null || s.getAttribute("admin")==null){
       res.sendRedirect("admin_login.html");
       return;
   }

   res.getWriter().println("<h1>Admin Logged In</h1>");
 }
}

