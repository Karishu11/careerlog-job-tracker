const API_URL = "http://localhost:8080/api/applications";
let chart;
let allApplications = [];

// Fetch and render all jobs
async function fetchApplications() {
  const res = await fetch(API_URL);
  allApplications = await res.json();
  renderTable(allApplications);
  populateMonthDropdown(allApplications);
}

// Render table with optional filter
function renderTable(applications) {
  const tbody = document.getElementById("applicationsTable");
  tbody.innerHTML = "";

  applications.forEach(app => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${app.companyName}</td>
      <td>${app.jobTitle}</td>
      <td>${app.dateApplied}</td>
      <td><span class="status-badge status-${app.status}">${app.status}</span></td>
      <td><a href="${app.jobLink}" target="_blank">🔗</a></td>
      <td>
        <button class="action edit" onclick="editJob(${app.id})">Edit</button>
        <button class="action delete" onclick="deleteJob(${app.id})">Delete</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Search filter
document.getElementById("searchInput").addEventListener("input", (e) => {
  const q = e.target.value.toLowerCase();
  const filtered = allApplications.filter(app =>
    app.companyName.toLowerCase().includes(q) ||
    app.jobTitle.toLowerCase().includes(q) ||
    app.status.toLowerCase().includes(q)
  );
  renderTable(filtered);
});

// Save or update job
document.getElementById("jobForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const jobId = document.getElementById("jobId").value;
  const jobData = {
    companyName: document.getElementById("company").value,
    jobTitle: document.getElementById("title").value,
    jobLink: document.getElementById("link").value,
    dateApplied: document.getElementById("dateApplied").value,
    status: document.getElementById("status").value,
    notes: document.getElementById("notes").value
  };

  if (jobId) {
    await fetch(`${API_URL}/${jobId}`, { method: "PUT", headers: {"Content-Type":"application/json"}, body: JSON.stringify(jobData) });
  } else {
    await fetch(API_URL, { method: "POST", headers: {"Content-Type":"application/json"}, body: JSON.stringify(jobData) });
  }

  e.target.reset();
  document.getElementById("jobId").value = "";
  fetchApplications();
});

async function deleteJob(id) {
  await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  fetchApplications();
}

async function editJob(id) {
  const res = await fetch(`${API_URL}/${id}`);
  const job = await res.json();
  document.getElementById("jobId").value = job.id;
  document.getElementById("company").value = job.companyName;
  document.getElementById("title").value = job.jobTitle;
  document.getElementById("link").value = job.jobLink;
  document.getElementById("dateApplied").value = job.dateApplied;
  document.getElementById("status").value = job.status;
  document.getElementById("notes").value = job.notes;
}

// Convert YYYY-MM to Month Year (e.g., 2025-08 → August 2025)
function formatMonth(ym) {
  const [year, month] = ym.split("-");
  const date = new Date(year, month - 1);
  return date.toLocaleString("default", { month: "long", year: "numeric" });
}

// Populate month dropdown
function populateMonthDropdown(applications) {
  const monthSelect = document.getElementById("monthSelect");
  const months = [...new Set(applications.map(a => a.dateApplied.slice(0,7)))];
  monthSelect.innerHTML = months.map(m => `<option value="${m}">${formatMonth(m)}</option>`).join("");
}

// Monthly overview
document.getElementById("viewMonthlyBtn").addEventListener("click", async () => {
  const month = document.getElementById("monthSelect").value;
  const res = await fetch(API_URL);
  const data = await res.json();
  const filtered = data.filter(a => a.dateApplied.startsWith(month));

  const counts = {};
  filtered.forEach(a => counts[a.status] = (counts[a.status] || 0) + 1);

  // Update chart
  if (chart) chart.destroy();
  chart = new Chart(document.getElementById("monthlyChart"), {
    type: "pie",
    data: {
      labels: Object.keys(counts),
      datasets: [{
        data: Object.values(counts),
        backgroundColor: ["blue", "purple", "orange", "green", "red"]
      }]
    },
    options: {
      plugins: {
        tooltip: {
          callbacks: {
            label: function(context) {
              let total = context.dataset.data.reduce((a, b) => a + b, 0);
              let value = context.raw;
              let percentage = ((value / total) * 100).toFixed(1);
              return `${context.label}: ${value} (${percentage}%)`;
            }
          }
        },
        datalabels: {
          color: "#fff",
          formatter: (value, context) => {
            let total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
            let percentage = ((value / total) * 100).toFixed(1);
            return `${percentage}%`;
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });

  document.getElementById("monthlyTotal").textContent = `Total Jobs Applied: ${filtered.length}`;
  document.getElementById("monthlyJobs").innerHTML = filtered.map(f => `<li>${f.companyName} - ${f.jobTitle} (${f.status})</li>`).join("");
});

// Dark Mode
document.getElementById("toggleDarkMode").addEventListener("click", () => {
  document.body.classList.toggle("dark");
  const btn = document.getElementById("toggleDarkMode");
  btn.textContent = document.body.classList.contains("dark") ? "☀️" : "🌙";
});

// Init
fetchApplications();
