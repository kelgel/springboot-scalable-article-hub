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

---

## 🛠 Tech Stack

| Layer         | Tech Used                        |
|---------------|----------------------------------|
| Backend       | Spring Boot, Spring Security     |
| Database      | MySQL (JPA, Hibernate)           |
| Auth          | JWT (access token only)          |
| Docs/Test     | Swagger (springdoc), Postman     |
| Build Tool    | Maven                            |

---

## 📂 Current Structure
```aiignore
src/
└── main/
├── java/com/euni/articlehub/
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── dto/
│ ├── entity/
│ └── util/
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

---

## 💼 About This Project

This project is part of my backend engineering portfolio, aimed at demonstrating scalable API development, secure architecture, and modern best practices using Java & Spring Boot.

