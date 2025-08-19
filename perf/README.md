# 📊 Performance Testing – Scalable Article Hub

This folder contains **JMeter test plans, results, and reports** used for performance evaluation of the Scalable Article Hub project.

---

## 📁 Folder Structure

```aiignore
perf/
├── keywords.csv # Test keywords dataset
├── ids_hot.csv # Test ids dataset
├── ids_random.csv # Test random dataset
├── ids_zipf.csv # Test zipf dataset
├── Compare MySQL vs ES.jmx # JMeter test plan for DB engine comparison
├── keywordTest.jmx # Keyword-based search performance test
├── perf.jmx # General performance test plan
├── testplan.jmx # Main performance test plan (single endpoint)
├── userLoads.jmx # Multi-thread-group test plan for user load scaling
├── ArticleHub Cold.jmx # JMeter test plan for cache (cold)
├── ArticleHub Hot.jmx # JMeter test plan for cache (hot)
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

## ⚡ Cache Experiment

This section contains Redis caching experiments that evaluate hit/miss ratios under different access patterns.

🧪 Test Setup
```aiignore
Tool: JMeter 5.6

Cache: Redis 7.2 (Docker)

Workload: 100 requests per scenario


Patterns Tested:

  🔥 Hot (same key repeatedly)
  
  🎲 Random (uniform random keys)
  
  📊 Zipf (skewed popularity distribution)
```
### ✅ Key Findings

Hot access quickly achieves nearly 100% hit ratio once cache is warmed.

Random access benefits less from caching (≈50% hit rate).

Zipf distribution shows strong cache efficiency due to skewed popularity (91% warm hit rate).

Cold starts always begin with 0–50% hit ratios depending on access locality.

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