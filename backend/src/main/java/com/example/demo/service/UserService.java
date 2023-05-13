package com.example.demo.service;

import com.example.demo.EntityDTO.UserAuthDTO;
import com.example.demo.EntityDTO.JwtResponse;
import com.example.demo.EntityDTO.UserRegDTO;
import com.example.demo.autorization.JwtTokenProvider;
import com.example.demo.entity.*;

import com.example.demo.repository.OrderRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AuthenticationManager authenticationManager;
    private final OrderRepository orderRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public UserService(UserRepository userRepository, ProductRepository productRepository, AuthenticationManager authenticationManager, OrderRepository orderRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.authenticationManager = authenticationManager;
        this.orderRepository = orderRepository;
        this.jwtTokenProvider = jwtTokenProvider;

    }


    public Optional<User> getByLogin(@NotNull String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public void addProductToCart(Long userId, Long productPropertyId, double size) {
        Optional<User> user = userRepository.findById(userId);
        System.out.println("Пользователь с ID " + userId + " не найден");

        Order order = getCartingOrder(user.get());
        order.getProducts().add(productRepository.getProductBySizeAndProductPropertyId(size, productPropertyId));
        orderRepository.save(order);

    }

    public Order getCartingOrder(User user) {
        //если у юзера есть заказ в статусе картинг, то мы его возвращаем, иначе создаем
        for (Order order : user.getOrders()) {
            if (order.getOrderStatus().equals(OrderStatus.CARTING)) {
                return order;
            }
        }
        List<Product> products = new ArrayList<>();
        Order order = Order.builder().orderStatus(OrderStatus.CARTING).products(products).build();
        user.getOrders().add(order);
        return order;
    }

    public JwtResponse authorizeUser(UserAuthDTO userAuthDTO) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userAuthDTO.getUsername(), userAuthDTO.getPassword()));
        final User user = getByLogin(userAuthDTO.getUsername()).get();
        return new JwtResponse(jwtTokenProvider.generateAccessToken(user), jwtTokenProvider.generateRefreshToken(user));
    }


    public void registrateUser(UserRegDTO userRegDTO) {
        if (userRepository.findByPhoneNumber(userRegDTO.getPhoneNumber()).isPresent()) {
            //сделать
        } else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepository.save(User.builder()
                    .role(Role.USER)
                    .name(userRegDTO.getName())
                    .password(encoder.encode(userRegDTO.getPassword()))
                    .phoneNumber(userRegDTO.getPhoneNumber())
                    .build());
        }
    }
}
