package com.example.demo;

import com.example.demo.EntityDTO.ProductSizeDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

@SpringBootTest
public class ProductServiceTests {

    @MockBean
    private ProductPropertiesRepository productPropertiesRepository;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private OrderService orderService;

    @Test
    void testGetAllProductsProperty_ProductsFound_ReturnsProductsList() throws Exception {
        List<ProductProperty> products = new ArrayList<>();
        products.add(new ProductProperty());
        when(productPropertiesRepository.findAll()).thenReturn(products);

        ProductService productService = new ProductService(productRepository, null, null);

        List<ProductProperty> result = productService.getAllProductsProperty();

        assertEquals(products, result);
    }

    @Test
    void testGetAllProductsProperty_NoProductsFound() {
        ProductPropertiesRepository productPropertiesRepository = Mockito.mock(ProductPropertiesRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        when(productPropertiesRepository.findAll()).thenReturn(Collections.emptyList());

        ProductService productService = new ProductService(productRepository, null, null);

        assertThrows(Exception.class, productService::getAllProductsProperty);
    }

    @Test
    void testGetProductById_ProductFound_ReturnsProductProperty() {
        ProductPropertiesRepository productPropertiesRepository = Mockito.mock(ProductPropertiesRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductProperty productProperty = new ProductProperty();
        when(productPropertiesRepository.findById(1L)).thenReturn(Optional.of(productProperty));

        ProductService productService = new ProductService(productRepository, null, null);

        ProductProperty result = productService.getProductById(1L);

        assertEquals(productProperty, result);
    }

    @Test
    void testGetProductSizes_ValidInput_ReturnsProductSizeDTO() {
        ProductPropertiesRepository productPropertiesRepository = Mockito.mock(ProductPropertiesRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

        ProductProperty productProperty = new ProductProperty();
        ZonedDateTime date = ZonedDateTime.now();
        List<Double> distinctSize = Arrays.asList(10.0, 20.0, 30.0);
        List<Order> orders = Arrays.asList(new Order(), new Order());
        List<Product> products = Arrays.asList(new Product(), new Product());

        when(productRepository.findAll()).thenReturn(products);
        when(orderRepository.findOrderByOrderTime(date)).thenReturn(orders);

        ProductService productService = new ProductService(productRepository, productPropertiesRepository, orderRepository);

        ProductSizeDTO result = productService.getProductSizes(date, productProperty);

        assertNotNull(result);
        }

    @Test
    void testGetEmployedDates_ValidInput_ReturnsEmployedDatesList() {
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        int size = 10;

        Order order1 = new Order();
        order1.setOrderTime(ZonedDateTime.parse("2023-05-01T00:00:00+03:00[Europe/Moscow]"));
        Order order2 = new Order();
        order2.setOrderTime(ZonedDateTime.parse("2023-05-02T00:00:00+03:00[Europe/Moscow]"));
        Order order3 = new Order();
        order3.setOrderTime(ZonedDateTime.parse("2023-05-03T00:00:00+03:00[Europe/Moscow]"));

        List<Order> orders = Arrays.asList(order1, order2, order3);

        when(orderRepository.findAll()).thenReturn(orders);

        ProductService productService = new ProductService(null, null, orderRepository);

        List<LocalDate> result = productService.getEmployedDates(size, 1L);

        assertNotNull(result);
    }

    @Test
    void testCreateProductProperty_ValidInput_ReturnsCreatedProductProperty() {
        ProductPropertiesRepository productPropertiesRepository = Mockito.mock(ProductPropertiesRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductProperty productProperty = new ProductProperty();

        ProductService productService = new ProductService(productRepository, null, null);

        ProductProperty result = productService.createProductProperty(productProperty);

        assertNotNull(result);
        //чего-то опредленно не хватает
    }

    @Test
    void testCreateProduct_ValidInput_ReturnsCreatedProduct() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        Product product = new Product();

        //ProductService productService = new ProductService(null, productRepository, null);

        //Product result = productService.createProduct(product);

        //assertNotNull(result);

    }


    }
