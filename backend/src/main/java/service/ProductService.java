package service;

import EntityDTO.ProductSizeDTO;
import entity.Order;
import entity.Product;
import entity.ProductProperty;
import repository.OrderRepository;
import repository.ProductPropertiesRepository;
import repository.ProductRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

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

    public ProductSizeDTO getProductSizes(ZonedDateTime date, ProductProperty productProperty) {
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

        ProductSizeDTO productSizeDTO = new ProductSizeDTO(sizeMap);

        return productSizeDTO;
    }

    public List<LocalDate> getEmployedDates(int size) {
        List<LocalDate> employedDates = new ArrayList<>();
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getSize() == size) {
                    LocalDate orderTime = LocalDate.from(order.getOrderTime());
                    employedDates.add(orderTime);

                }
            }
        }
        return employedDates;
    }

}