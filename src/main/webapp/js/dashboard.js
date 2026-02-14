window.addEventListener("DOMContentLoaded", () => {
    loadComplaints();

    // submit complaint
    const form = document.getElementById("complaintForm");
    if(form){
        form.addEventListener("submit", submitComplaint);
    }

    // search box
    const searchBox = document.getElementById("search");
    if(searchBox){
        searchBox.addEventListener("keyup", filterTable);
    }
});


// =============================
// LOAD EXISTING COMPLAINTS
// =============================
function loadComplaints(){
    fetch("GetUserComplaints")
    .then(res => res.json())
    .then(list => {

        let tbody = document.querySelector("#complaintTable tbody");
        tbody.innerHTML = "";

        list.forEach(c => {
            insertRow(c, false);
        });
    });
}


// =============================
// SUBMIT NEW COMPLAINT
// =============================
function submitComplaint(e){
    e.preventDefault();

    let form = document.getElementById("complaintForm");
    let formData = new FormData(form);

    fetch("RegisterComplaint", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
	.then(data => {

	    if(data.status === "duplicate"){
	        showToast("Duplicate complaint already submitted");
	        return;
	    }

	    if(data.status === "success"){
    }});
}


// =============================
// INSERT ROW INTO TABLE
// =============================
function insertRow(c, isNew){

    let tbody = document.querySelector("#complaintTable tbody");

    let row = document.createElement("tr");

    let color = getColor(c.status || "Pending");

    let timeText = isNew ? "Just now" : c.time;

    row.innerHTML = `
        <td>${c.id}</td>
        <td>${c.category}</td>
        <td>${c.description}</td>
        <td style="color:${color};font-weight:bold">${c.status || "Pending"}</td>
        <td>${c.remarks || "-"}</td>
        <td class="timeCell">${timeText}</td>
        <td>
            ${(c.status==="Pending" || !c.status) 
                ? `<button onclick="withdrawComplaint(${c.rawId || extractId(c.id)})">Withdraw</button>`
                : "-"}
        </td>
    `;

    tbody.prepend(row);

    // after 2 minutes replace "Just now"
    if(isNew){
        setTimeout(()=>{
            row.querySelector(".timeCell").innerText = c.time;
        },120000);
    }
}


// =============================
// WITHDRAW
// =============================
function withdrawComplaint(id){

    let formData = new FormData();
    formData.append("id", id);

    fetch("WithdrawComplaint", {
        method: "POST",
        body: formData
    })
    .then(() => loadComplaints());
}


// =============================
// SEARCH FILTER
// =============================
function filterTable(){

    let input = document.getElementById("search").value.toLowerCase();
    let rows = document.querySelectorAll("#complaintTable tbody tr");

    rows.forEach(row => {
        let text = row.innerText.toLowerCase();
        row.style.display = text.includes(input) ? "" : "none";
    });
}


// =============================
// TOAST
// =============================
function showToast(msg){

    let toast = document.getElementById("toast");

    toast.innerText = msg;
    toast.style.display = "block";

    setTimeout(()=>{
        toast.style.display = "none";
    },3000);
}


// =============================
function getColor(status){
    if(status === "Pending") return "orange";
    if(status === "In Progress") return "blue";
    if(status === "Resolved") return "green";
    if(status === "Withdrawn") return "red";
    return "black";
}


// extract numeric id from CMP code
function extractId(code){
    let parts = code.split("-");
    return parseInt(parts[2]);
}
