package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

@WebServlet("/WithdrawComplaint")
@MultipartConfig
public class WithdrawComplaint extends HttpServlet {

    private final String url="jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser="root";
    private final String dbPass="1111";

    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException{

        response.setContentType("application/json");
        PrintWriter out=response.getWriter();

        try{

            HttpSession session=request.getSession(false);
            if(session==null || session.getAttribute("user_id")==null){
                out.print("{\"status\":\"error\",\"msg\":\"no session\"}");
                return;
            }

            int userId=(int)session.getAttribute("user_id");

            String idStr = request.getParameter("id");
            System.out.println("WITHDRAW ID RAW: " + idStr);

            if(idStr==null){
                out.print("{\"status\":\"error\",\"msg\":\"id null\"}");
                return;
            }

            int id=Integer.parseInt(idStr);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            String sql="UPDATE complaints SET status='Withdrawn' WHERE id=? AND user_id=?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setInt(1,id);
            ps.setInt(2,userId);

            int rows=ps.executeUpdate();

            System.out.println("ROWS UPDATED: "+rows);

            if(rows>0)
                out.print("{\"status\":\"success\"}");
            else
                out.print("{\"status\":\"error\",\"msg\":\"no rows\"}");

            con.close();

        }catch(Exception e){
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"msg\":\""+e.getMessage()+"\"}");
        }
    }
}
