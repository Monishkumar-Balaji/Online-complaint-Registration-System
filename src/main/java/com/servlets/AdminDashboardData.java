package com.servlets;

import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/AdminData")
public class AdminDashboardData extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String url="jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser="root";
    private final String dbPass="1111";

    protected void doGet(HttpServletRequest request,HttpServletResponse response)
    throws ServletException,IOException{
    	
        response.setContentType("application/json");
        PrintWriter out=response.getWriter();

        HttpSession session=request.getSession(false);

        if(session==null || session.getAttribute("admin")==null){
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            String sql="""
            SELECT complaints.*, users.name 
            FROM complaints
            JOIN users ON complaints.user_id = users.id
            ORDER BY created_at DESC
            """;

            PreparedStatement ps=con.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();

            out.print("[");
            boolean first=true;

            while(rs.next()){
                if(!first) out.print(",");
                first=false;

                int id=rs.getInt("id");
                String code="CMP-"+java.time.Year.now().getValue()+"-"+String.format("%04d",id);

                String desc=rs.getString("description").replace("\"","\\\"");
                String remarks=rs.getString("remarks");
                if(remarks==null) remarks="";

                out.print("{");
                out.print("\"rawId\":"+id+",");
                out.print("\"id\":\""+code+"\",");
                out.print("\"user\":\""+rs.getString("name")+"\",");
                out.print("\"category\":\""+rs.getString("category")+"\",");
                out.print("\"description\":\""+desc+"\",");
                out.print("\"status\":\""+rs.getString("status")+"\",");
                out.print("\"remarks\":\""+remarks.replace("\"","\\\"")+"\",");
                out.print("\"time\":\""+rs.getTimestamp("created_at")+"\"");
                out.print("}");
            }

            out.print("]");
            con.close();

        }catch(Exception e){
            out.print("[]");
        }
    }
}
