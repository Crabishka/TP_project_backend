package com.example.demo.service;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public List<ProductProperty> getAllProductsProperty() throws Exception {
        List<ProductProperty> products = productPropertiesRepository.findAll();
        if (products.isEmpty()) {
            throw new Exception("No products found");
        }
        return products;
    }

    @Transactional
    public ProductProperty getProductById(Long id) {
        Optional<ProductProperty> productProperty = productPropertiesRepository.findById(id);
        return productProperty.get();

    }


    @Transactional
    public ProductSizeDTO getProductSizes(ZonedDateTime date, ProductProperty productProperty) {

        Map<Double, Boolean> sizeMap = new HashMap<>();
        List<Double> distinctSize = productRepository.findDistinctSize(productProperty.getId());
        for (Double size : distinctSize) {
            sizeMap.put(size, false);
        }
        List<Order> orders = getActiveOrderInDate(date);
        List<Integer> ids = new ArrayList<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getProductProperty().getId().equals(productProperty.getId())) {
                    ids.add(product.getId());
                }
            }
        }
        ids.add(0);
        List<Product> productList = productRepository.findAllByProductPropertyIdAndIdNotIn(productProperty.getId(),
                ids);


        for (Product product : productList) {
            sizeMap.put(product.getSize(), true);
        }

        Map<Double, Boolean> sortedMap = new TreeMap<>(Comparator.comparingDouble(Double::doubleValue));
        sortedMap.putAll(sizeMap);
        ProductSizeDTO productSizeDTO = new ProductSizeDTO(sortedMap);
        return productSizeDTO;
    }

    public List<Order> getActiveOrderInDate(ZonedDateTime date) {
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(OrderStatus.ACTIVE);
        orderStatusList.add(OrderStatus.WAITING_FOR_RECEIVING);
        orderStatusList.add(OrderStatus.WAITING_FOR_PAYMENT);
        orderStatusList.add(OrderStatus.FITTING);
        orderStatusList.add(OrderStatus.CARTING);
        return orderRepository.findOrdersByOrderTimeAndOrderStatusIn(date, orderStatusList);
    }

    public List<Product> getFreeProductByDateAndProductPropertyAndSize(
            ZonedDateTime date,
            Long productPropertyId,
            double size) {
        // получили все заказы на этот день, которые могут влиять на товар
        List<Order> orders = getActiveOrderInDate(date);
        // получили все id товара, которые заняты
        List<Integer> ids = new ArrayList<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getProductProperty().getId().equals(productPropertyId)) {
                    ids.add(product.getId());
                }
            }
        }
        ids.add(0); // JPA Specification 4.6.9
        return productRepository.findAllByProductPropertyIdAndIdNotInAndSize(productPropertyId,
                ids, size);
    }

    @Transactional
    public List<LocalDate> getEmployedDates(double size, Long productId) {
        List<LocalDate> employedDates = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getSize() == size && product.getProductProperty().getId() == productId) {
                    LocalDate orderTime = LocalDate.from(order.getOrderTime());
                    employedDates.add(orderTime);

                }
            }
        }


        return employedDates;
    }

    @Transactional
    public ProductProperty createProductProperty(ProductProperty productProperty) {
        productPropertiesRepository.save(productProperty);
        return productProperty;
    }

    @Transactional
    public Product createProduct(Product product) {
        productRepository.save(product);
        return product;
    }


}
