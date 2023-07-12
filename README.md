# Departmental Store

This project is a departmental store management system that allows you to manage the inventory, customers, and orders of a store. It provides functionalities to track products, customer details, and order information.

## Tables

The project includes the following tables:

1. **Product Inventory**: This table stores information about the products available in the store's inventory.

   - Product ID
   - Product description
   - Product name
   - Price
   - Expiry date
   - Count (quantity)
   - Availability

2. **Customer**: This table contains the details of the store's customers.

   - Customer ID
   - Customer full name
   - Customer address
   - Contact number

3. **Order**: This table records the orders placed by customers.

   - Order ID
   - Product ID
   - Customer ID
   - Order timestamp
   - Quantity

## Business Problem

The project addresses the following business problem:

- Users can place an order for one product at a time, but the quantity can be more than one.
- Backorders: If the inventory count is zero or the product is unavailable, the system keeps a record of such orders to prioritize delivery.

## Features

The project includes the following features:

- **Swagger**: The project integrates Swagger to provide a user-friendly interface for testing and documenting the APIs.
- **Discounts**: A discount feature has been implemented where customers can avail discounts on the total price of a product range.
- **Update Order Details**: If a customer is already present in the customer table, their order details are updated using the same ID.
- **Handling Backorders**: If a product becomes available, customers from the backorders list are removed and added to the customer table for delivery.
- **Update Inventory**: The project allows updating the product inventory details and sets the availability to true once the update is complete.
- **JSON Response Fields**: The necessary fields are included in the JSON response while updating the details.
- **Validation**: The project incorporates validation checks on input data to ensure data integrity and correctness.
- **Exception Handling**: Proper exception handling is implemented to handle errors and provide meaningful error messages.
- **Unit Testing**: JUnit tests are written to ensure the functionality of the system, aiming for test coverage greater than 90%.
- **Dependency Checks**: The project includes Owasp dependency check, Checkstyle, and Spotbugs for ensuring code quality and security.


## Getting Started

To run this project locally, follow these steps:

1. Clone the repository.
2. Install the required dependencies and libraries.
3. Configure the database connection.
4. Build and run the project.
5. Access the application through the provided endpoints.
6. Use Swagger to interact with the APIs and test different functionalities.
