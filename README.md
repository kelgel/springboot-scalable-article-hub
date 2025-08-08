# 📰 Scalable Article Hub

A scalable backend system for managing and serving large volumes of articles.  
This project aims to explore high-performance backend architecture using Spring Boot, JPA, and JWT-based authentication.

---

## 🚀 Project Status – Week 1 ✅

- ✅ User & Post entity design (JPA)
- ✅ Basic CRUD for User and Post
- ✅ JWT-based authentication implemented
- ✅ Swagger (OpenAPI) documentation integrated
- ✅ Postman API testing completed
- ✅ @Transactional issue resolved and documented

## 🚀 Project Status – Week 2 ✅

- ✅ **Large Dataset Generation & Insertion**
    - Generated 10,000+ articles using **Faker** and inserted them into MySQL
    - Indexed all records into **Elasticsearch** for search performance testing

- ✅ **Search API Implementation**
    - **MySQL Search**: JPA with `findByTitleContainingOrContentContaining` + pagination & sorting
    - **Elasticsearch Search**: Java API Client with `MultiMatchQuery` (title, content) + author/date filters

- ✅ **Performance Testing with Postman**
    - Designed **5 test scenarios** changing `keyword`, `page`, and `size` parameters
    - Measured response time for both MySQL and Elasticsearch    

- ✅ **Performance Comparison Results**  

  | Request No.         | MySQL Time (ms) | Elasticsearch Time (ms) |
  |---------------------|----------------|--------------------------|
  | 1 (keyword=love)    | 193            | 37                       |
  | 2 (keyword=kill)    | 186            | 31                       |
  | 3 (keyword=story)   | 143            | 25                       |
  | 4 (page=1)          | 104            | 25                       |
  | 5 (size=5)          | 137            | 26                       |
  | **Average**         | **152.6**      | **28.8**                 |

    ✅ **Conclusion**: Elasticsearch achieved ~5x faster average response times compared to MySQL.

---

## 🛠 Tech Stack

| Layer         | Tech Used                                     |
|---------------|-----------------------------------------------|
| Backend       | Spring Boot, Spring Security                  |
| Database      | MySQL (JPA, Hibernate), Elasticsearch 8.x     |
| Auth          | JWT (access token only)                       |
| Docs/Test     | Swagger (springdoc), Postman                  |
| Build Tool    | Maven                                         |

---

## 📂 Current Structure
```aiignore
src/
└── main/
    ├── java/com/euni/articlehub/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── document/          # Elasticsearch documents
    │   ├── dto/
    │   ├── entity/
    │   ├── filter/
    │   └── util/
    └── resources/
        ├── application.yml
        └── static/templates (future)    
```
---

## ⚙️ How to Run

```bash
# Clone the project
git clone https://github.com/your-username/scalable-article-hub.git

# Navigate into the project
cd scalable-article-hub

# Run the Spring Boot application
./mvnw spring-boot:run
```

Swagger UI available at:

👉 http://localhost:8080/swagger-ui/index.html

Kibana (Elasticsearch monitoring) available at:

👉 http://localhost:5601

---

## 💼 About This Project

This project is part of my backend engineering portfolio, aimed at demonstrating:
- Scalable API development with Java & Spring Boot

- Performance optimization using Elasticsearch

- Secure architecture with JWT authentication

- Practical API testing using Postman