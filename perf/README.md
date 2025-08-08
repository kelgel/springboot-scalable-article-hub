# 📊 Performance Testing – Scalable Article Hub

This folder contains **JMeter test plans, results, and reports** used for performance evaluation of the Scalable Article Hub project.

---

## 📁 Folder Structure

```aiignore
perf/
├── KeywordReport_html/ # HTML report for keyword-based search performance
├── report_compare_50/ # HTML report for MySQL vs Elasticsearch comparison (50 users)
├── report_html/ # HTML report for general performance test
├── Compare MySQL vs ES.jmx # JMeter test plan for DB engine comparison
├── keywords.csv # Test keywords dataset
├── keywordTest.jmx # Keyword-based search performance test
├── KeywordResult.jtl # Raw results for keyword test
├── perf.jmx # General performance test plan
├── result.jtl # Raw results for perf.jmx
├── result_compare_50.jtl # Raw results for MySQL vs ES comparison
├── testplan.jmx # Main performance test plan (single endpoint)
├── userLoads.jmx # Multi-thread-group test plan for user load scaling
└── README.md # This documentation
```

---

## 🧪 Test Plan Overview

### 1. `perf.jmx`
- Purpose: **General API performance test**
- Target: Main search endpoint
- Configuration:
    - Single Thread Group
    - Adjustable user count & loop count
    - Outputs: `result.jtl` + `report_html`

### 2. `userLoads.jmx`
- Purpose: **User load scaling test**
- Target: Search API under various loads
- Configuration:
    - Four Thread Groups: 1, 20, 100, 300 users
    - Each group runs the same keyword search scenario
    - Outputs: Keyword-based performance metrics

### 3. `Compare MySQL vs ES.jmx`
- Purpose: Compare **MySQL Search API** vs **Elasticsearch Search API**
- Configuration:
    - One Thread Group, 50 users × 5 loops
    - Two HTTP Samplers (MySQL Search, ES Search)
    - Outputs: `result_compare_50.jtl` + `report_compare_50`

---

## 📊 How to Run Tests

### GUI Mode
1. Open JMeter
2. Load `.jmx` file (e.g., `perf.jmx`)
3. Configure Thread Group parameters (users, loops)
4. Run and view results in **Summary Report** or **HTML Report Dashboard**

### CLI Mode
```bash
jmeter -n \
  -t perf/compare_mysql_es.jmx \
  -l perf/result_compare_50.jtl \
  -e -o perf/report_compare_50
```

---
🚫 Files Ignored in Git
- *.jtl – Raw JMeter result files

- HTML report folders (*_html/) – Can be regenerated from .jtl

---

📌 Notes
- All .jmx files are version-controlled

- Result files (.jtl) and reports are excluded via .gitignore

- To reproduce results, simply run the .jmx plans with JMeter