# ING HUB Loan API
## _Study Case_
A Backend Loan API for a bank so that their employees can create, list and pay loans for their customers.
(Default user: admin, password:admin)
### Setting Up the Project

```sh
mvn clean install
mvn spring-boot:run
```
### API Documentation
http://localhost:8080/swagger-ui/index.html

### Test
```sh
mvn test
```
### Planned Improvements

- Enhance role-based authorization with finer-grained permissions and support for OAuth2 and JWT token authentication.
- Implement API versioning to ensure backward compatibility while adding new features.
- Dockerize the application for easier deployment and scaling across different environments.
- Add more unit, integration, end-to-end, performance, and security tests to improve test coverage.
- Set up centralized logging and API monitoring for easier troubleshooting and performance tracking.

### License
MIT


