package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/GetUserComplaints")
public class GetUserComplaints extends HttpServlet {

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
        if(session==null || session.getAttribute("user_id")==null){
            out.print("[]");
            return;
        }

        int userId=(int)session.getAttribute("user_id");

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            String sql="SELECT * FROM complaints WHERE user_id=? ORDER BY created_at DESC";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setInt(1,userId);

            ResultSet rs=ps.executeQuery();

            out.print("[");
            boolean first=true;

            while(rs.next()){

                if(!first) out.print(",");
                first=false;

                int id=rs.getInt("id");

                String code="CMP-"+java.time.Year.now().getValue()+"-"+String.format("%04d",id);

                String category = rs.getString("category");
                if(category==null) category="";

                String desc = rs.getString("description");
                if(desc==null) desc="";
                desc = desc.replace("\"","\\\"").replace("\n"," ");

                String status = rs.getString("status");
                if(status==null) status="Pending";

                String remarks = rs.getString("remarks");
                if(remarks==null) remarks="";
                remarks = remarks.replace("\"","\\\"");

                Timestamp time = rs.getTimestamp("created_at");

                out.print("{");
                out.print("\"rawId\":"+id+",");
                out.print("\"id\":\""+code+"\",");
                out.print("\"category\":\""+category+"\",");
                out.print("\"description\":\""+desc+"\",");
                out.print("\"status\":\""+status+"\",");
                out.print("\"remarks\":\""+remarks+"\",");
                out.print("\"time\":\""+time+"\"");
                out.print("}");
            }

            out.print("]");
            con.close();

        }catch(Exception e){
            out.print("[]");
        }
    }
}
