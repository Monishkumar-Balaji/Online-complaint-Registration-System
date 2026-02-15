package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.Year;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


@WebServlet("/RegisterComplaint")
@jakarta.servlet.annotation.MultipartConfig
public class RegisterComplaint extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String url="jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser="root";
    private final String dbPass="1111";

    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException,IOException{
    	
    	request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out=response.getWriter();

        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute("user_id")==null){
            out.print("{\"status\":\"error\"}");
            return;
        }

        int userId=(int)session.getAttribute("user_id");
        System.out.println("SESSION USER: " + session.getAttribute("user_id"));

        String category=request.getParameter("category");
        String description=request.getParameter("description");

        if(category==null || description==null){
            out.print("{\"status\":\"error\"}");
            return;
        }

        category=category.trim();
        description=description.trim();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(url,dbUser,dbPass);

            con.setAutoCommit(true);   // force commit

            System.out.println("USER ID = " + userId);
            System.out.println("CATEGORY = " + category);
            System.out.println("DESC = " + description);

            // DUPLICATE CHECK
            String check="SELECT id FROM complaints WHERE user_id=? AND category=? AND description=? AND status!='Withdrawn'";
            PreparedStatement psCheck=con.prepareStatement(check);
            psCheck.setInt(1,userId);
            psCheck.setString(2,category);
            psCheck.setString(3,description);

            ResultSet rsCheck=psCheck.executeQuery();
            if(rsCheck.next()){
                out.print("{\"status\":\"duplicate\"}");
                return;
            }

            // INSERT
            String sql="INSERT INTO complaints(user_id,category,description) VALUES(?,?,?)";
            PreparedStatement ps=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1,userId);
            ps.setString(2,category);
            ps.setString(3,description);

            int rows = ps.executeUpdate();
            System.out.println("ROWS INSERTED = " + rows);

            if(rows==0){
                out.print("{\"status\":\"error\",\"msg\":\"no rows inserted\"}");
                return;
            }

            ResultSet rs=ps.getGeneratedKeys();
            if(!rs.next()){
                out.print("{\"status\":\"error\",\"msg\":\"no key generated\"}");
                return;
            }

            int id=rs.getInt(1);

            String code="CMP-"+Year.now().getValue()+"-"+String.format("%04d",id);
            Timestamp created=new Timestamp(System.currentTimeMillis());

            out.print("{");
            out.print("\"status\":\"success\",");
            out.print("\"rawId\":"+id+",");
            out.print("\"id\":\""+code+"\",");
            out.print("\"category\":\""+category+"\",");
            out.print("\"description\":\""+description.replace("\"","\\\"")+"\",");
            out.print("\"statusText\":\"Pending\",");
            out.print("\"remarks\":\"\",");
            out.print("\"time\":\""+created+"\"");
            out.print("}");

            con.close();

        }catch(Exception e){
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"msg\":\""+e.getMessage().replace("\"","")+"\"}");
        }

    }
}
