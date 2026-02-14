package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/UserDashboard")
public class UserDashboard extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String url = "jdbc:mysql://localhost:3306/complaint_system";
    private final String dbUser = "root";
    private final String dbPass = "1111";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("user_id");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>User Dashboard</title>");

        // =========================
        // STYLES
        // =========================
        out.println("""
        <style>
        #toast{
            position:fixed;
            bottom:20px;
            right:20px;
            background:#333;
            color:white;
            padding:15px;
            border-radius:8px;
            display:none;
        }
        table{border-collapse:collapse;width:100%;}
        th,td{padding:8px;border:1px solid #ccc;}
        </style>
        """);

        // =========================
        // SEARCH SCRIPT
        // =========================
        out.println("""
        <script>
        function searchTable(){
            let input=document.getElementById("search").value.toLowerCase();
            let rows=document.querySelectorAll("#complaintTable tbody tr");

            rows.forEach(row=>{
                let text=row.innerText.toLowerCase();
                row.style.display=text.includes(input)? "":"none";
            });
        }
        </script>
        """);

        // =========================
        // AJAX + TOAST SCRIPT
        // =========================
        out.println("""
        <script>
        function showToast(msg){
            let t=document.getElementById("toast");
            t.innerText=msg;
            t.style.display="block";
            setTimeout(()=>{t.style.display="none"},3000);
        }

        function addRow(data){
    let table = document.querySelector("#complaintTable tbody");

    let row = document.createElement("tr");

    row.innerHTML = `
        <td>${data.id}</td>
        <td>${data.category}</td>
        <td>${data.description}</td>
        <td style="color:orange;font-weight:bold">${data.state}</td>
        <td>-</td>
        <td class="timeCell">Just now</td>
        <td><button disabled>Withdraw</button></td>
    `;

    table.prepend(row);

    // change after 2 minutes
    setTimeout(()=>{
        row.querySelector(".timeCell").innerText = data.time;
    }, 120000); // 2 minutes
}


        window.addEventListener("DOMContentLoaded",()=>{
            document.getElementById("complaintForm")
            .addEventListener("submit",function(e){

                e.preventDefault();
                let formData=new FormData(this);

                fetch("RegisterComplaint",{
                    method:"POST",
                    body:formData
                })
                .then(res=>res.json())
                .then(data=>{
                    if(data.status==="success"){
                        showToast("Complaint submitted: "+data.id);
                        addRow(data);
                        document.getElementById("complaintForm").reset();
                    }
                });
            });
        });
        </script>
        """);

        out.println("</head><body>");

        out.println("<div id='toast'></div>");

        // =========================
        // FORM
        // =========================
        out.println("<h2>Register Complaint</h2>");

        out.println("""
        <form id="complaintForm">
        Category:
        <select name="category">
            <option>Exams</option>
            <option>Library</option>
            <option>Hostel</option>
            <option>Mess</option>
            <option>Transport</option>
            <option>Faculty</option>
            <option>Attendence</option>
            <option>Events</option>
            <option>Fees</option>
            <option>Others</option>
        </select>
        <br><br>

        Description:<br>
        <textarea name="description" required></textarea>
        <br><br>

        <button type="submit">Submit Complaint</button>
        </form>
        """);

        out.println("<hr>");
        out.println("<h2>My Complaints</h2>");

        out.println("""
        Search:
        <input type="text" id="search" onkeyup="searchTable()" placeholder="Search...">
        <br><br>
        """);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT * FROM complaints WHERE user_id=? ORDER BY created_at DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            out.println("<table id='complaintTable'>");
            out.println("""
            <thead>
            <tr>
            <th>ID</th>
            <th>Category</th>
            <th>Description</th>
            <th>Status</th>
            <th>Remarks</th>
            <th>Date</th>
            <th>Action</th>
            </tr>
            </thead>
            <tbody>
            """);

            while (rs.next()) {

                int id = rs.getInt("id");
                String category = rs.getString("category");
                String desc = rs.getString("description");
                String status = rs.getString("status");
                String remarks = rs.getString("remarks");
                Timestamp time = rs.getTimestamp("created_at");

                String code = "CMP-" + java.time.Year.now().getValue()
                        + "-" + String.format("%04d", id);

                String color = switch (status) {
                    case "Pending" -> "orange";
                    case "In Progress" -> "blue";
                    case "Resolved" -> "green";
                    case "Withdrawn" -> "red";
                    default -> "black";
                };

                out.println("<tr>");
                out.println("<td>" + code + "</td>");
                out.println("<td>" + category + "</td>");
                out.println("<td>" + desc + "</td>");
                out.println("<td style='color:" + color + ";font-weight:bold'>" + status + "</td>");
                out.println("<td>" + (remarks == null ? "-" : remarks) + "</td>");
                out.println("<td>" + time + "</td>");

                if (!status.equals("Resolved") && !status.equals("Withdrawn")) {
                    out.println("<td>");
                    out.println("<form action='WithdrawComplaint' method='post'>");
                    out.println("<input type='hidden' name='id' value='" + id + "'>");
                    out.println("<button>Withdraw</button>");
                    out.println("</form>");
                    out.println("</td>");
                } else {
                    out.println("<td>-</td>");
                }

                out.println("</tr>");
            }

            out.println("</tbody></table>");

            con.close();

        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }

        out.println("</body></html>");
    }
}
