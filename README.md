# springboot-inventory-system
This is a Spring Boot-based Inventory Management System that handles items, orders, and inventory transactions. It supports CRUD operations, stock validation, and error handling using Spring Boot, JPA, and H2 Database.

Features
- Item Management (/item)
  - Add, update, delete, and fetch items.
  - Validates item name and price
- Inventory Management (/inventory)
  - Track stock levels (Topup and Withdraw).
  - Prevents negative stock
- Order Management (/order)
  - Checks stock before placing an order
  - Automatically updates inventory.
- Validation & Exception Handling
- Unit test

Tech Stack
- Backend: Java 17, Spring Boot, Spring Data JPA
- Database: H2 (in-memory, for quick testing)
- Testing: JUnit, Mockito
- Build Tool: Maven
- Version Control: Git, GitHub
