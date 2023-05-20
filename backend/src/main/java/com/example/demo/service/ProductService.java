package com.example.demo.service;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPropertiesRepository productPropertiesRepository;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository, ProductPropertiesRepository productPropertiesRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.productPropertiesRepository = productPropertiesRepository;
        this.orderRepository = orderRepository;
    }

    public List<ProductProperty> getAllProductsProperty() throws Exception {
        List<ProductProperty> products = productPropertiesRepository.findAll();
        if (products.isEmpty()) {
            throw new Exception("No products found");
        }
        return products;
    }


    public ProductProperty getProductById(Long id) {
       Optional<ProductProperty> productProperty = productPropertiesRepository.findById(id);
        return productProperty.get();

    }

    public ProductSizeDTO getProductSizes(ZonedDateTime date, ProductProperty productProperty) {//вот тут посортируем, как и в методе ниже
        Map<Double, Boolean> sizeMap = new HashMap<>();
        List<Double> distinctSize = productRepository.findDistinctSize();
        for (Double size: distinctSize){
            sizeMap.put(size, false);
        }
        List<Order> orders = orderRepository.findOrderByOrderTime(date);
        List<Product> products = new ArrayList<>();
        for(Order order: orders){
            for(Product product: order.getProducts()){
                if (product.getProductProperty().equals(productProperty)){
                    products.add(product);
                }
            }
        }
        List<Product> productList = productRepository.findAll();
        productList.remove(products);
        for(Product product: productList){
            sizeMap.put(product.getSize(), true);
        }

        Map<Double, Boolean> sortedMap = new TreeMap<>(Comparator.comparingDouble(Double::doubleValue));
        sortedMap.putAll(sizeMap);

        ProductSizeDTO productSizeDTO = new ProductSizeDTO(sortedMap);//тык...



        return productSizeDTO;
    }

    public List<ZonedDateTime> getEmployedDates(int size) {
        List<ZonedDateTime> employedDates = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getSize() == size) {
                    ZonedDateTime orderTime = ZonedDateTime.from(order.getOrderTime());
                    employedDates.add(orderTime);

                }
            }
        }

        Comparator<ZonedDateTime> comparator = Comparator.naturalOrder();
        Collections.sort(employedDates, comparator);


        return employedDates;
    }

    public ProductProperty createProductProperty(ProductProperty productProperty){
        productPropertiesRepository.save(productProperty);
        return productProperty;
    }

    public Product createProduct(Product product){
        productRepository.save(product);
        return product;
    }


}
