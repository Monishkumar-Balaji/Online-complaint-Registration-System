let complaintsData = [];
let currentCategory = "All";
let sortDirection = 1;
let showResolvedOnly = false;

let showResolved = false;
let chartInstance = null;

window.onload = () => {
 load();

 let search = document.getElementById("search");
 if(search){
   search.addEventListener("keyup", applyFilters);
 }

 let toggle=document.getElementById("resolvedToggle");
 if(toggle){
   toggle.addEventListener("click", toggleResolved);
 }
 setInterval(load, 15000);
};


function load(){
 fetch("AdminData")
 .then(r=>r.json())
 .then(data=>{
   complaintsData=data;

   updateAnalytics();
   updateCategoryChart();
   applyFilters();
 });
}

function applyFilters(){

 let searchVal="";
 let searchEl=document.getElementById("search");
 if(searchEl) searchVal=searchEl.value.toLowerCase();

 let filtered = complaintsData.filter(c=>{

   // category filter
   if(currentCategory!=="All" && c.category!==currentCategory)
      return false;

   // search
   let text = (
     c.id + c.user + c.category +
     c.description + c.status +
     (c.remarks||"")
   ).toLowerCase();

   return text.includes(searchVal);
 });

 renderTables(filtered);
}


function renderTables(data){

 let activeBody=document.querySelector("#table tbody");
 let resolvedBody=document.querySelector("#resolvedTable tbody");

 activeBody.innerHTML="";
 resolvedBody.innerHTML="";

 data.forEach(c=>{

   let row=document.createElement("tr");

   let statusDropdown=`
   <select class="${getStatusClass(c.status)}"
   onchange="update(${c.rawId}, this.value, this.parentElement.nextElementSibling.firstElementChild.value)">
     <option ${c.status==="Pending"?"selected":""}>Pending</option>
     <option ${c.status==="In Progress"?"selected":""}>In Progress</option>
     <option ${c.status==="Resolved"?"selected":""}>Resolved</option>
   </select>`;

   row.innerHTML=`
   <td>${c.id}</td>
   <td>${c.user}</td>
   <td>${c.category}</td>
   <td>${c.description}</td>
   <td>${statusDropdown}</td>

   <td>
   <input value="${c.remarks||""}"
   onchange="update(${c.rawId}, this.parentElement.previousElementSibling.firstElementChild.value, this.value)">
   </td>

   <td>${c.time}</td>
   `;

   if(c.status==="Resolved"){
      resolvedBody.appendChild(row);
   }else{
      activeBody.appendChild(row);
   }

 });
}


function render(data){

 let tbody=document.querySelector("#table tbody");
 tbody.innerHTML="";

 data.forEach(c=>{

   let row=document.createElement("tr");

   row.innerHTML=`
   <td>${c.id}</td>
   <td>${c.user}</td>
   <td>${c.category}</td>
   <td>${c.description}</td>

   <td>
      <span class="${getStatusClass(c.status)}">${c.status}</span><br>

      <select onchange="update(${c.rawId}, this.value, this.parentElement.nextElementSibling.firstElementChild.value)">
        <option ${c.status==="Pending"?"selected":""}>Pending</option>
        <option ${c.status==="In Progress"?"selected":""}>In Progress</option>
        <option ${c.status==="Resolved"?"selected":""}>Resolved</option>
        <option ${c.status==="Withdrawn"?"selected":""}>Withdrawn</option>
      </select>
   </td>

   <td>
      <input value="${c.remarks||""}"
      onchange="update(${c.rawId}, this.parentElement.previousElementSibling.querySelector('select').value, this.value)">
   </td>

   <td>${c.time}</td>
   `;

   tbody.appendChild(row);
 });
}


function getStatusClass(status){
 if(status==="Pending") return "status-pending";
 if(status==="In Progress") return "status-progress";
 if(status==="Resolved") return "status-resolved";
 if(status==="Withdrawn") return "status-withdrawn";
 return "";
}

function update(id,status,remarks){
 let form=new FormData();
 form.append("id",id);
 form.append("status",status);
 form.append("remarks",remarks);

 fetch("UpdateComplaint",{method:"POST",body:form})
 .then(()=>load());
}

/* CATEGORY FILTER */
function toggleCategoryMenu(){
 let menu=document.getElementById("categoryMenu");
 menu.style.display = menu.style.display==="block" ? "none":"block";
}

function filterCategory(cat){
 currentCategory = cat;
 document.getElementById("categoryMenu").style.display="none";
 applyFilters();
}

/* SORTING */
function sortById(){
 complaintsData.sort((a,b)=> sortDirection*(a.rawId-b.rawId));
 sortDirection*=-1;
 applyFilters();
}

function sortByDate(){
 complaintsData.sort((a,b)=> sortDirection*(new Date(a.time)-new Date(b.time)));
 sortDirection*=-1;
 applyFilters();
}

function sortByStatus(){
 complaintsData.sort((a,b)=> sortDirection*(a.status.localeCompare(b.status)));
 sortDirection*=-1;
 applyFilters();
}

document.getElementById("resolvedToggle").addEventListener("click", ()=>{
    showResolvedOnly = !showResolvedOnly;

    document.getElementById("resolvedToggle").innerText =
        showResolvedOnly ? "▲ Back to Active Complaints"
                         : "▼ Show Resolved Complaints";

    applyFilters();
});

function toggleResolved(){

 showResolved = !showResolved;

 document.getElementById("resolvedToggle").innerText =
  showResolved ? "▲ Hide Resolved Complaints"
               : "▼ Show Resolved Complaints";

 document.getElementById("resolvedTable").style.display =
  showResolved ? "table" : "none";

 applyFilters();
}

function updateAnalytics(){

 let pending = 0;
 let progress = 0;

 complaintsData.forEach(c=>{
   if(c.status==="Pending") pending++;
   if(c.status==="In Progress") progress++;
 });

 let totalActive = pending + progress;

 document.getElementById("totalCount").innerText = totalActive;
 document.getElementById("pendingCount").innerText = pending;
 document.getElementById("progressCount").innerText = progress;
}



function updateCategoryChart(){

 let canvas = document.getElementById("categoryChart");
 if(!canvas) return;

 let categoryCount = {};

 complaintsData.forEach(c=>{

   // ONLY ACTIVE
   if(c.status==="Resolved" || c.status==="Withdrawn")
      return;

   categoryCount[c.category] =
     (categoryCount[c.category]||0)+1;
 });

 let labels = Object.keys(categoryCount);
 let values = Object.values(categoryCount);

 if(chartInstance) chartInstance.destroy();

 chartInstance = new Chart(canvas,{
   type:'pie',
   data:{
     labels:labels,
     datasets:[{
       data:values,
       backgroundColor:[
         "#f39c12", "#3498db", "#2ecc71",
         "#9b59b6", "#e74c3c", "#1abc9c"
       ]
     }]
   },
   options:{
     plugins:{
       legend:{ position:"bottom" },
       title:{
         display:true,
         text:"Active Complaints by Category"
       }
     }
   }
 });
}