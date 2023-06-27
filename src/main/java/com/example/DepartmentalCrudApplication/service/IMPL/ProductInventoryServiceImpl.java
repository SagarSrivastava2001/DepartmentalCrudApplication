package com.example.DepartmentalCrudApplication.service.IMPL;

import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.CustomerRepository;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(ProductInventoryServiceImpl.class);

    public ProductInventoryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(Product_Inventory product) {
        productRepository.save(product);
        logger.info("Added product. Product ID: {}", product.getProductId());
    }

    @Override
    public List<Product_Inventory> allProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product_Inventory> getProductById(Long id){
        Optional<Product_Inventory> product;
        try {
            product = productRepository.findById(id);

            if(product.isPresent() == false){
                throw new ProductNotFoundException("Product Not Available");
            }
        } catch (ProductNotFoundException e) {
            logger.error("Product Not Available: {}", e.getMessage());
            product = Optional.empty();
        }
        return product;
    }

    @Override
    public Map<String, String> discount(){
        Map<String, String> dis = new HashMap<>();
        dis.put(" Total Price "," Discount ");
        dis.put(" Above 2000 "," 5% ");
        dis.put(" Above 4000 "," 10% ");
        dis.put(" Above 8000 "," 20% ");
        return dis;
    }

    @Override
    public Boolean updateProductDetails(Long id,Product_Inventory product) throws ProductNotFoundException{

        try{
            Optional<Product_Inventory> prevProduct = productRepository.findById(id);

            if(prevProduct.isPresent() == false){
                throw new ProductNotFoundException("Product ID : " + id);
            }

            prevProduct.get().setAvailability(product.getAvailability());
            prevProduct.get().setCount(prevProduct.get().getCount() + product.getCount());

            productRepository.save(prevProduct.get());
            logger.info("Updated product details. Product ID: {}", id);

            HashMap<Long, LinkedList<Customer>> backordersRecord = customerService.optimisedBackOrders();
            if(backordersRecord == null || backordersRecord.isEmpty()){
                return false;
            }
            else{
                LinkedList<Customer> l = backordersRecord.get(id);

                if(l == null){
                    return false;
                }
                else{
                    long newQuantity = product.getCount();
                    List<Customer> filteredCustomers = l.stream()
                            .filter(customer -> customer.getOrderDetails().getQuantity() <= newQuantity)
                            .collect(Collectors.toList());

                    filteredCustomers.forEach(customer -> {
                        customerService.addCustomer(customer);
                        l.remove(customer);
                        if(l.size() == 0){
                            backordersRecord.remove(id);
                        }
                        else{
                            backordersRecord.put(id, l);
                        }
                        prevProduct.get().setCount(newQuantity - customer.getOrderDetails().getQuantity());

                        if (newQuantity - customer.getOrderDetails().getQuantity() == 0) {
                            prevProduct.get().setAvailability(false);
                        }
                    });
                }
            }
            productRepository.save(prevProduct.get());
            return true;
        }
        catch (ProductNotFoundException e){
            logger.error("Product Not Available: {}", e.getMessage());
            return false;
        }
    }
}