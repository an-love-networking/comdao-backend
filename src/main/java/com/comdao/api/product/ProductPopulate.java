package com.comdao.api.product;

import com.comdao.api.category.CategoryRepository;
import com.comdao.api.category.CategoryService;
import com.comdao.api.category.dto.CategoryCreationDto;
import com.comdao.api.category.entities.Category;
import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.entities.Product;
import com.comdao.api.product.entities.enums.Badge;
import com.comdao.api.product.exceptions.ProductDuplicationCreationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProductPopulate {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Value("${app.s3-base-url}")
    private String minioUrl;


    @Transactional
    public void initializeDatabase() {
        // 1. Retrieve the ordered map
        Map<ProductCreationDto, String> productMap = getProductImageMap();

        // 2. Sets to collect sequential product IDs for each category
        Set<Long> bestSellerIds = new LinkedHashSet<>();
        Set<Long> setIds = new LinkedHashSet<>();
        Set<Long> bedIds = new LinkedHashSet<>();
        Set<Long> toppingIds = new LinkedHashSet<>();
        Set<Long> drinkIds = new LinkedHashSet<>();

        // 3. Define static matching labels outside the loop for optimization
        List<String> setLabels = Arrays.asList(
                "Lạp xưởng + bò + đậu", "Set mix ngẫu nhiên", "Set rong biển",
                "Cơm mắm tép chưng thịt", "Cơm trứng tráng thịt bằm",
                "Cơm đùi gà sốt mắm", "Spaghetti", "Bún trộn đặc biệt"
        );

        List<String> bestSellerLabels = Arrays.asList(
                "Lạp xưởng + bò + đậu", "Set mix ngẫu nhiên", "Set rong biển",
                "Spaghetti", "Bún trộn đặc biệt"
        );

        List<String> bedLabels = Arrays.asList(
                "Cơm đảo", "Cơm trắng", "Cơm đảo (mang về)",
                "Cơm trắng (mang về)", "Cơm thêm (cho suất mang về)"
        );

        List<String> toppingLabels = Arrays.asList(
                "Lạp xưởng", "Thịt bò", "Gà xiên", "Đùi gà", "Chả cá", "Thịt băm",
                "Thịt luộc", "Thịt kho tàu", "Thịt nướng", "Trứng ốp la", "Mọc",
                "Chả nem", "Lườn ngỗng", "Chả lá lốt", "Nem nướng",
                "Thịt heo xào mộc nhĩ nấm hương", "Đậu rán"
        );

        List<String> drinkLabels = Arrays.asList("Trà đá", "Coca");

        // 4. Corrected Single Process Loop
        for (Map.Entry<ProductCreationDto, String> entry : productMap.entrySet()) {
            ProductCreationDto dto = entry.getKey();
//            if (productRepository.existsByLabel(dto.getLabel()))
//                continue;
            String resourcePath = entry.getValue();
            String currentLabel = dto.getLabel();

            try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    System.err.println("Skipping product: Image not found at " + resourcePath);
                    continue;
                }

                // Determine content type
                String contentType = "image/jpeg";
                if (resourcePath.endsWith(".webp")) contentType = "image/webp";
                else if (resourcePath.endsWith(".png")) contentType = "image/png";

                String filename = resourcePath.substring(resourcePath.lastIndexOf("/") + 1);
                MultipartFile multipartFile = new MockMultipartFile("file", filename, contentType, inputStream);

                // Save product to database
                Product product = productRepository.findByLabel(currentLabel).orElse(null);
                if (product == null) {
                    product = productService.createProduct(dto, multipartFile);
                    log.info("New product created {}", product);
                } else {
                    String path = new URI(product.getImageUrl()).getPath();
                    product.setImageUrl(minioUrl + path);
                    log.info("Update product url to {}", product.getImageUrl());
                }
                product = productRepository.save(product);

                // Distribute current ID based on label mapping groups

                if (!categoryRepository.existsByLabel("set")) {
                    if (setLabels.contains(product.getLabel())) {
                        System.out.println("Adding " + product.getLabel() + " to set");
                        setIds.add(product.getId());
                    }
                }
                if (!categoryRepository.existsByLabel("best-seller")) {
                    if (bestSellerLabels.contains(product.getLabel())) {
                        System.out.println("Adding " + product.getLabel() + " to best-seller");
                        bestSellerIds.add(product.getId());
                    }
                }
                if (!categoryRepository.existsByLabel("bed")) {
                    if (bedLabels.contains(product.getLabel())) {
                        System.out.println("Adding " + product.getLabel() + " to bed");
                        bedIds.add(product.getId());
                    }
                }
                if (!categoryRepository.existsByLabel("topping")) {
                    if (toppingLabels.contains(product.getLabel())) {
                        System.out.println("Adding " + product.getLabel() + " to topping");
                        toppingIds.add(product.getId());
                    }
                }
                if (!categoryRepository.existsByLabel("drink")) {
                    if (drinkLabels.contains(product.getLabel())) {
                        System.out.println("Adding " + product.getLabel() + " to drink");
                        drinkIds.add(product.getId());
                    }
                }

            } catch (Exception e) {
                System.err.println("Failed to upload product: " + currentLabel);
                e.printStackTrace();
            }
        }

        // 5. --- Create and Save Categories using the generated Product ID Sets ---
        try {
            Category category;
            if (!categoryRepository.existsByLabel("best-seller")) {
                CategoryCreationDto catBestSeller = new CategoryCreationDto("best-seller", "Best seller", null, bestSellerIds);
                category = categoryService.createCategory(catBestSeller);
                System.out.println(category);
            }

            if (!categoryRepository.existsByLabel("set")) {
                CategoryCreationDto catSet = new CategoryCreationDto("set", "Set", null, setIds);
                category = categoryService.createCategory(catSet);
                System.out.println(category);
            }

            if (!categoryRepository.existsByLabel("bed")) {
                CategoryCreationDto catBed = new CategoryCreationDto("bed", "Cơm", null, bedIds);
                category = categoryService.createCategory(catBed);
                System.out.println(category);
            }

            if (!categoryRepository.existsByLabel("topping")) {
                CategoryCreationDto catTopping = new CategoryCreationDto("topping", "Topping", null, toppingIds);
                category = categoryService.createCategory(catTopping);
                System.out.println(category);
            }

            if (!categoryRepository.existsByLabel("drink")) {
                CategoryCreationDto catDrink = new CategoryCreationDto("drink", "Đồ uống", null, drinkIds);
                category = categoryService.createCategory(catDrink);
                System.out.println(category);
            }

            System.out.println("All categories successfully synchronized to database.");
        } catch (Exception e) {
            System.err.println("Error creating categories");
            e.printStackTrace();
        }
    }

    public Map<ProductCreationDto, String> getProductImageMap() {
        Map<ProductCreationDto, String> productMap = new LinkedHashMap<>();

// 1. Sets / Combos (Full meals served per customized box/portion)
        productMap.put(
                new ProductCreationDto("Lạp xưởng + bò + đậu", "Lạp xưởng + bò + đậu", 35000.0, "suất", Badge.HOT),
                "/img/lap-xuong-bo-dau.jpg"
        );
        productMap.put(
                new ProductCreationDto("Set mix ngẫu nhiên", "Set mix ngẫu nhiên", 33000.0, "suất", Badge.HOT),
                "/img/tu-mix.jpg"
        );
        productMap.put(
                new ProductCreationDto("Set rong biển", "Set rong biển", 33000.0, "suất", Badge.NEW),
                "/img/set-rong-bien.jpg"
        );
        productMap.put(
                new ProductCreationDto("Cơm mắm tép chưng thịt", "Cơm mắm tép chưng thịt", 33000.0, "suất", null),
                "/img/com-mam-tep-chung-thit.webp"
        );
        productMap.put(
                new ProductCreationDto("Cơm trứng tráng thịt bằm", "Cơm trứng tráng thịt bằm", 33000.0, "suất", null),
                "/img/com-trung-trang-thit-bam.jfif"
        );
        productMap.put(
                new ProductCreationDto("Cơm đùi gà sốt mắm", "Cơm đùi gà sốt mắm", 40000.0, "suất", null),
                "/img/com-dui-ga-sot-mam.jfif"
        );
        productMap.put(
                new ProductCreationDto("Spaghetti", "Spaghetti", 40000.0, "đĩa", null),
                "/img/spaghetti.jpg"
        );
        productMap.put(
                new ProductCreationDto("Bún trộn đặc biệt", "Bún trộn đặc biệt", 40000.0, "bát", null),
                "/img/bun-tron-dac-biet.jpg"
        );

        // 2. Rice Main Components (Plain bases and extra add-ons)
        productMap.put(
                new ProductCreationDto("Cơm đảo", "Cơm đảo", 23000.0, "đĩa", null),
                "/img/Com-dao.jpg"
        );
        productMap.put(
                new ProductCreationDto("Cơm trắng", "Cơm trắng", 23000.0, "bát", null),
                "/img/Com-trang.jpg"
        );
        productMap.put(
                new ProductCreationDto("Cơm đảo (mang về)", "Cơm đảo (mang về)", 23000.0, "hộp", null),
                "/img/Com-dao.jpg"
        );
        productMap.put(
                new ProductCreationDto("Cơm trắng (mang về)", "Cơm trắng (mang về)", 23000.0, "hộp", null),
                "/img/Com-trang.jpg"
        );
        productMap.put(
                new ProductCreationDto("Cơm thêm (cho suất mang về)", "Cơm thêm (cho suất mang về)", 10000.0, "phần", null),
                "/img/Com-dao.jpg"
        );

        // 3. Toppings (A la carte side items added by piece, weight, or individual side plates)
        productMap.put(
                new ProductCreationDto("Lạp xưởng", "Lạp xưởng", 5000.0, "thanh", null),
                "/img/lap-xuong.jpg"
        );
        productMap.put(
                new ProductCreationDto("Thịt bò", "Thịt bò", 5000.0, "đĩa", null),
                "/img/thit-bo.jpg"
        );
        productMap.put(
                new ProductCreationDto("Gà xiên", "Gà xiên", 5000.0, "xiên", null),
                "/img/ga-xien.jpg"
        );
        productMap.put(
                new ProductCreationDto("Đùi gà", "Đùi gà", 5000.0, "cái", null),
                "/img/dui-ga.jpg"
        );
        productMap.put(
                new ProductCreationDto("Chả cá", "Chả cá", 5000.0, "đĩa", null),
                "/img/cha-ca.jpg"
        );
        productMap.put(
                new ProductCreationDto("Thịt băm", "Thịt băm", 5000.0, "phần", null),
                "/img/thit-bam.jfif"
        );
        productMap.put(
                new ProductCreationDto("Thịt luộc", "Thịt luộc", 5000.0, "đĩa", null),
                "/img/thit-luoc.jfif"
        );
        productMap.put(
                new ProductCreationDto("Thịt kho tàu", "Thịt kho tàu", 5000.0, "bát nhỏ", null),
                "/img/thit-kho-tau.jpg"
        );
        productMap.put(
                new ProductCreationDto("Thịt nướng", "Thịt nướng", 5000.0, "xiên", null),
                "/img/thit-nuong.jpg"
        );
        productMap.put(
                new ProductCreationDto("Trứng ốp la", "Trứng ốp la", 5000.0, "quả", null),
                "/img/trung-op-la.webp"
        );
        productMap.put(
                new ProductCreationDto("Mọc", "Mọc", 5000.0, "viên", null),
                "/img/moc.jfif"
        );
        productMap.put(
                new ProductCreationDto("Chả nem", "Chả nem", 5000.0, "cuộn", null),
                "/img/cha-nem.jpg"
        );
        productMap.put(
                new ProductCreationDto("Lườn ngỗng", "Lườn ngỗng", 5000.0, "đĩa", null),
                "/img/luon-ngong.jfif"
        );
        productMap.put(
                new ProductCreationDto("Chả lá lốt", "Chả lá lốt", 5000.0, "cái", null),
                "/img/cha-la-lot.jfif"
        );
        productMap.put(
                new ProductCreationDto("Nem nướng", "Nem nướng", 5000.0, "cái", null),
                "/img/nem-nuong.jpg"
        );
        productMap.put(
                new ProductCreationDto("Thịt heo xào mộc nhĩ nấm hương", "Thịt heo xào mộc nhĩ nấm hương", 5000.0, "đĩa", null),
                "/img/thit-heo-xao-moc-nhi-nam-huong.jpg"
        );
        productMap.put(
                new ProductCreationDto("Đậu rán", "Đậu rán", 5000.0, "đĩa", null),
                "/img/dau-ran.jpg"
        );

        // 4. Drinks
        productMap.put(
                new ProductCreationDto("Trà đá", "Trà đá", 0.0, "cốc", Badge.HOT),
                "/img/tra-da.webp"
        );
        productMap.put(
                new ProductCreationDto("Coca", "Coca", 10000.0, "lon", null),
                "/img/coca.jpg"
        );

        return productMap;
    }

    public void createProduct(
            @Valid ProductCreationDto newProduct,
            MultipartFile file
    )
            throws ProductDuplicationCreationException, IOException {
        System.out.println("Entered image upload controller");
        Product product = productService.createProduct(newProduct, file);
        System.out.println("Created product " + product.getLabel());
    }

    @Bean
    public CommandLineRunner init() {
        return args -> {
            initializeDatabase();
        };
    }
}
