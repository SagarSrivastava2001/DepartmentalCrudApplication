package com.example.DepartmentalCrudApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
/*
	Flow of CRUD Application - Model Class -> Repository Interface -> Service Layer -> REST Controller

	Features:

	- Send message responses to body on Swagger				(Done)
    - Discount on the total price of the product range.		(Done)
    - (Update) If the customer is present in the customer table
       then update its order details on the same id 		(Done)
	- If product is available then remove customer from backorders and add it to the customer table. (Done)
	- Update Product Inventory Details and make availability as true after that. (Done)
	- Show necessary fields in the JSON while updating the details (Done)
	- Validation on inputs							- Done
	- Exception Handling							- Done
	- JUnit Testing (Coverage greater than 90%)		- Done
	- Owasp dependency check, checkstyle, spotbugs	- Done
 */

@SpringBootApplication
public class DepartmentalCrudApplication{
	public static void main(String[] args) {
		SpringApplication.run(DepartmentalCrudApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
