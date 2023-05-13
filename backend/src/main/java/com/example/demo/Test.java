package com.example.demo;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductProperty;
import com.example.demo.repository.ProductPropertiesRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
public class Test {

    private final ProductPropertiesRepository productPropertiesRepository;
    private final ProductRepository productRepository;
    Random random;

    public Test(ProductPropertiesRepository productPropertiesRepository, ProductRepository productRepository) {
        this.productPropertiesRepository = productPropertiesRepository;
        this.productRepository = productRepository;
        random = new Random();
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            String hexNumber = Integer.toHexString(random.nextInt(16777216));
            ProductProperty save = productPropertiesRepository.save(ProductProperty
                    .builder()
                    .cost((i+1) * 100)
                    .description(generateRandomLetters(200))
                    .photo("https://via.placeholder.com/600/" + hexNumber)
                    .title(generateRandomLetters(10))
                    .build());
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    productRepository.save(Product
                            .builder()
                            .productProperty(save)
                            .size(30 + k)
                            .orders(new ArrayList<>())
                            .build());
                }
            }
        }
    }


    public static String generateRandomLetters(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomChar;
            int randomNum = random.nextInt(27); // Диапазон от 0 до 26 (включая пробел)
            if (randomNum == 26) {
                randomChar = ' '; // Если случайное число равно 26, добавляем пробел
            } else {
                randomChar = (char) (randomNum + 'a'); // Иначе, преобразуем случайное число в символ латинского алфавита
            }
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
