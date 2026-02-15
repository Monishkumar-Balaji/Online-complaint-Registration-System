package com.servlets;

import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String url="jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser="root";
    private final String dbPass="1111";

    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException{

        String username=request.getParameter("username");
        String password=request.getParameter("password");

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            String sql="SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);

            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                HttpSession session=request.getSession();
                session.setAttribute("admin", true);
                session.setAttribute("admin_name", username);

                response.sendRedirect("admin_dashboard.html");
            }else{
                response.sendRedirect("admin_login.html?msg=error");
            }

            con.close();

        }catch(Exception e){
            response.sendRedirect("admin_login.html?msg=error");
        }
    }
}
