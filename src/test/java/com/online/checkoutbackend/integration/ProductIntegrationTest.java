    package com.online.checkoutbackend.integration;

    import com.online.checkoutbackend.model.Product;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.annotation.DirtiesContext;
    import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.Arrays;
    import java.util.List;

    import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @AutoConfigureMockMvc
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Ensures database resets
    public class ProductIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        private List<Product> products;

        @BeforeEach
        void setup() {
            products = Arrays.asList(
                    new Product("Laptop", 1200.00, 10),
                    new Product("Phone", 800.00, 15)
            );
        }

        @Test
        void testAddProducts() throws Exception {
            for (Product product : products) {
                mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(product)))
                       .andExpect(status().isOk())
                       .andExpect(jsonPath("$.name").value(product.getName()))
                       .andExpect(jsonPath("$.price").value(product.getPrice()))
                       .andExpect(jsonPath("$.stock").value(product.getStock()));
            }
        }
        @Test
        void testDecreaseStock() throws Exception {
            // Add a product with initial stock
            Product product = new Product("Headphones", 100.0, 10); // Stock: 10
            String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(product)))
                                     .andExpect(status().isOk())
                                     .andReturn().getResponse().getContentAsString();

            Product savedProduct = objectMapper.readValue(response, Product.class);
            Long productId = savedProduct.getId();

            // Decrease stock (valid case: decrease by 3)
            mockMvc.perform(MockMvcRequestBuilders.post("/api/products/decrease-stock/{id}/{quantity}", productId, 3)
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk());

            // Fetch product and verify stock decreased
            String updatedResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + productId)
                                                                           .contentType(MediaType.APPLICATION_JSON))
                                            .andExpect(status().isOk())
                                            .andReturn().getResponse().getContentAsString();

            Product updatedProduct = objectMapper.readValue(updatedResponse, Product.class);
            assertThat(updatedProduct.getStock()).isEqualTo(7); // 10 - 3 = 7

            // Test decreasing stock more than available (should return 400 Bad Request)
            mockMvc.perform(MockMvcRequestBuilders.post("/api/products/decrease-stock/{id}/{quantity}", productId, 8)
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())  // ✅ Expecting 400 Bad Request
                   .andExpect(result -> assertThat(result.getResolvedException())
                           .isInstanceOf(ResponseStatusException.class))
                   .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                           .contains("Not enough stock available"));
        }

        @Test
        void testGetAllProducts() throws Exception {
            for (Product product : products) {
                mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(product)))
                       .andExpect(status().isOk());
            }

            mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.size()").value(2)); // Expect 2 products
        }

        @Test
        void testGetProductById() throws Exception {
            String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(products.get(0))))
                                     .andExpect(status().isOk())
                                     .andReturn().getResponse().getContentAsString();

            Product savedProduct = objectMapper.readValue(response, Product.class);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + savedProduct.getId())
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.name").value(savedProduct.getName()))
                   .andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
                   .andExpect(jsonPath("$.stock").value(savedProduct.getStock()));
        }

        @Test
        void testUpdateProduct() throws Exception {
            String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(products.get(0))))
                                     .andExpect(status().isOk())
                                     .andReturn().getResponse().getContentAsString();

            Product savedProduct = objectMapper.readValue(response, Product.class);

            savedProduct.setPrice(1100.00);
            savedProduct.setStock(8);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + savedProduct.getId())
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(objectMapper.writeValueAsString(savedProduct)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.price").value(1100.00))
                   .andExpect(jsonPath("$.stock").value(8));
        }

        @Test
        void testDeleteProduct() throws Exception {
            String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content(objectMapper.writeValueAsString(products.get(0))))
                                     .andExpect(status().isOk())
                                     .andReturn().getResponse().getContentAsString();

            Product savedProduct = objectMapper.readValue(response, Product.class);
            Long productID = savedProduct.getId();

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + productID)
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk());

            mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                                                  .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.size()").value(0));
        }
    }
