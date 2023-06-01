package com.example.demo.service;

import com.example.demo.EntityDTO.UserAuthDTO;
import com.example.demo.EntityDTO.JwtResponse;
import com.example.demo.EntityDTO.UserRefreshDTO;
import com.example.demo.EntityDTO.UserRegDTO;
import com.example.demo.autorization.JwtTokenProvider;
import com.example.demo.entity.*;

import com.example.demo.repository.OrderRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AuthenticationManager authenticationManager;
    private final OrderRepository orderRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final OrderService orderService;

    public UserService(UserRepository userRepository, ProductRepository productRepository, AuthenticationManager authenticationManager, OrderRepository orderRepository, JwtTokenProvider jwtTokenProvider, OrderService orderService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.authenticationManager = authenticationManager;
        this.orderRepository = orderRepository;
        this.jwtTokenProvider = jwtTokenProvider;

        this.orderService = orderService;
    }

    @Transactional
    public Optional<User> getByLogin(@NotNull String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional
    public void addProductToCart(Long userId,
                                 Long productPropertyId,
                                 double size,
                                 ZonedDateTime dateTime) throws Exception {
        Order order = orderService.getActiveOrder(userId);
        if (order == null) {
            order = getCartingOrder(userRepository.findById(userId).get());
        }
        if (order.getOrderStatus() != OrderStatus.CARTING) {
            throw new Exception("Order is already exist");
        }
        if (order.getProducts().size() == 4) {
            throw new Exception("Max count");
        }
        Product product = productRepository.getFirstBySizeAndProductPropertyId(size, productPropertyId);
        order.getProducts().add(product);
        order.setOrderTime(dateTime);
        order.setTotalCost(order.getTotalCost() + product.getProductProperty().getCost());
        orderRepository.save(order);
    }


    @Transactional
    public Order getCartingOrder(User user) {

        for (Order order : user.getOrders()) {
            if (order.getOrderStatus().equals(OrderStatus.CARTING)) {
                return order;
            }
        }
        List<Product> products = new ArrayList<>();
        Order order = Order.builder()
                .orderStatus(OrderStatus.CARTING)
                .products(products)
                .user(user)
                .build();
        user.getOrders().add(order);
        return order;
    }

    @Transactional
    public JwtResponse authorizeUser(UserAuthDTO userAuthDTO) throws AuthenticationException {
        String phoneNumber = userAuthDTO.getUsername();
        if (phoneNumber.startsWith("+7")) {
            userAuthDTO.setUsername(phoneNumber.replace("+7", "8"));
        }
        userAuthDTO.setPassword(userAuthDTO.getPassword());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userAuthDTO.getUsername(), userAuthDTO.getPassword()));
        final User user = getByLogin(userAuthDTO.getUsername()).get();
        return new JwtResponse(jwtTokenProvider.generateAccessToken(user), jwtTokenProvider.generateRefreshToken(user));
    }

    @Transactional
    public void registrantUser(UserRegDTO userRegDTO) {
        String phoneNumber = userRegDTO.getPhoneNumber();
        if (phoneNumber.startsWith("+7")) {
            userRegDTO.setPhoneNumber(phoneNumber.replace("+7", "8"));
        }
        if (userRepository.findByPhoneNumber(userRegDTO.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "HTTP Status will be NOT FOUND (CODE 404)\n");
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

    public UserRefreshDTO refreshToken(String refreshToken) {
        String phoneNumber = jwtTokenProvider.getUsernameFromJwt(refreshToken);
        User dbUser = userRepository.findByPhoneNumber(phoneNumber).get();
        return createTokensForUser(dbUser);
    }

    private UserRefreshDTO createTokensForUser(User user) {
        return new UserRefreshDTO(jwtTokenProvider.generateAccessToken(user), jwtTokenProvider.generateRefreshToken(user));
    }
}
