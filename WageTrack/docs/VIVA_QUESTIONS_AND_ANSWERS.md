# VIVA QUESTIONS AND ANSWERS

1. **What is Spring Boot?**  
   Spring Boot is a Java framework that helps create stand-alone applications with less configuration.

2. **Why did you use Spring Boot?**  
   It provides embedded server support, dependency injection, and convenient integration with web and database libraries.

3. **What is Spring MVC?**  
   It is a web framework that separates request handling into controllers and supporting components.

4. **What is a REST API?**  
   A REST API lets applications communicate using HTTP methods and resource-based URLs.

5. **What is GET used for?**  
   GET retrieves data without changing the stored resource.

6. **What is POST used for?**  
   POST submits data, commonly to create a resource.

7. **What is PUT used for?**  
   PUT updates a resource at a known URL.

8. **What is DELETE used for?**  
   DELETE requests removal of a resource. In WageTrack, worker and worksite delete requests deactivate records to preserve history.

9. **What is JPA?**  
   Java Persistence API is a standard for mapping Java objects to relational database data.

10. **What is Hibernate?**  
    Hibernate is an ORM framework and a common JPA implementation.

11. **What is an entity?**  
    An entity is a Java class mapped to a database table.

12. **What is a repository?**  
    A repository provides database operations. WageTrack repositories extend `JpaRepository`.

13. **What is the service layer?**  
    It contains application rules and workflows, such as validating attendance and calculating wages.

14. **What is the controller layer?**  
    It receives HTTP requests, validates request DTOs, and returns HTTP responses.

15. **What is dependency injection?**  
    It means required objects are supplied to a class instead of the class creating them itself.

16. **Why use constructor injection?**  
    It makes dependencies explicit and supports easier testing.

17. **What does `@Autowired` do?**  
    It asks Spring to inject a matching dependency. Constructor injection is used here without needing the annotation.

18. **What is MySQL?**  
    MySQL is a relational database management system that stores structured data in tables.

19. **What is a primary key?**  
    A primary key uniquely identifies each row in a table.

20. **What is a foreign key?**  
    A foreign key links a row to a related row in another table and helps preserve referential integrity.

21. **What is one-to-many?**  
    One parent can be associated with many child records, such as one worker with many attendance records.

22. **What is many-to-one?**  
    Many child records can reference one parent, such as many attendance rows referring to one worksite.

23. **What is a DTO?**  
    A Data Transfer Object carries data into or out of the API without exposing the entity directly.

24. **Why use DTOs?**  
    They control the API structure, support request validation, and help avoid recursive JSON from entity relationships.

25. **What is validation?**  
    Validation checks that input meets required rules before it is processed.

26. **What does `@RestController` mean?**  
    It marks a class as a web controller whose methods return response data, usually JSON.

27. **What does `@RequestMapping` do?**  
    It maps a URL path or common route prefix to a controller or handler.

28. **What is `@PathVariable`?**  
    It reads a value from the URL path, such as the worker ID in `/api/workers/1`.

29. **What is `@RequestParam`?**  
    It reads a query-string value, such as `weekStartDate` in the weekly payroll endpoint.

30. **What is `@RequestBody`?**  
    It converts the JSON request body into a Java object.

31. **What is `@RestControllerAdvice`?**  
    It provides centralized handling of exceptions thrown by REST controllers.

32. **Why is exception handling needed?**  
    It returns clear, consistent errors instead of exposing raw server failures.

33. **What is business logic?**  
    Business logic is the set of rules that define how the application behaves, such as half-day wage calculation.

34. **Why do business rules belong in the service layer?**  
    Services can enforce rules consistently regardless of which controller or API request calls them.

35. **What is the difference between JPA and JDBC?**  
    JDBC works more directly with SQL and result sets. JPA maps objects to relational data and reduces routine mapping code.

36. **What does `spring.jpa.hibernate.ddl-auto=update` do?**  
    It asks Hibernate to update the schema to reflect entity changes. It is convenient for a student project, while production systems usually use controlled migrations.

37. **What is HTTP 200?**  
    It means the request succeeded.

38. **What is HTTP 201?**  
    It means a resource was created successfully.

39. **What is HTTP 400?**  
    It means the request contains invalid input or violates a request-level business rule.

40. **What is HTTP 404?**  
    It means the requested resource was not found.

41. **What is HTTP 409?**  
    It means the request conflicts with existing data, such as duplicate attendance.

42. **What is Postman?**  
    Postman is a tool for sending API requests and inspecting responses.

43. **How is overtime pay calculated?**  
    Overtime pay equals overtime hours multiplied by hourly wage and the configured multiplier.

44. **How is hourly wage calculated?**  
    WageTrack divides daily wage by the configured standard workday hours, which defaults to eight.

45. **How is weekly wage calculated?**  
    The service adds each attendance record's regular wage and overtime pay for the requested inclusive date range.

46. **Why use `BigDecimal` for wages?**  
    It provides decimal arithmetic appropriate for monetary calculations and avoids common binary floating-point rounding issues.

47. **Why is attendance unique by worker and date?**  
    It prevents a worker from being counted more than once on the same date, even if a different worksite is selected.

48. **Why is worker deletion a deactivation?**  
    Old attendance and payment records need to retain a valid worker reference for audit and payroll history.

49. **How is payment status decided?**  
    Zero paid is PENDING, a positive amount below payable is PARTIALLY_PAID, and an amount equal to payable is PAID.

50. **Can the client send any payable amount while creating a payment?**  
    No. The service calculates payable from attendance and rejects an amount paid above that total.
