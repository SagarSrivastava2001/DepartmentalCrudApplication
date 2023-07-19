package com.example.DepartmentalCrudApplication.serviceIMPL;

import com.example.DepartmentalCrudApplication.dao.BackorderDao;
import com.example.DepartmentalCrudApplication.dto.ProductInventoryUpdateDTO;
import com.example.DepartmentalCrudApplication.exceptions.ProductNotFoundException;
import com.example.DepartmentalCrudApplication.model.Customer;
import com.example.DepartmentalCrudApplication.model.Product_Inventory;
import com.example.DepartmentalCrudApplication.repository.CustomerRepository;
import com.example.DepartmentalCrudApplication.repository.ProductRepository;
import com.example.DepartmentalCrudApplication.service.CustomerService;
import com.example.DepartmentalCrudApplication.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BackorderDao backorderDao;

    private static final Logger logger = LoggerFactory.getLogger(ProductInventoryServiceImpl.class);

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
    public Boolean updateProductDetails(Long id,@RequestBody ProductInventoryUpdateDTO updateDTO) throws ProductNotFoundException{
        try{

            Optional<Product_Inventory> prevProduct = productRepository.findById(id);

            if(prevProduct.isPresent() == false){
                throw new ProductNotFoundException("Product ID : " + id);
            }

            prevProduct.get().setAvailability(updateDTO.getAvailability());
            prevProduct.get().setCount(prevProduct.get().getCount() + updateDTO.getCount());
            prevProduct.get().setExpiry(updateDTO.getExpiry());

            productRepository.save(prevProduct.get());
            logger.info("Updated product details. Product ID: {}", id);

            HashMap<Long, LinkedList<Customer>> backordersRecord = customerService.optimisedBackOrders();
            if(backordersRecord == null || backordersRecord.isEmpty()){
                return false;
            }
            else{
                backorderDao.retrieveBackorders(id);
            }
            return true;
        }
        catch (ProductNotFoundException e){
            logger.error("Product Not Available: {}", e.getMessage());
            return false;
        }
    }
}