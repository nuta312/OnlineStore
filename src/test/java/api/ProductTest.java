package api;

import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.CreateProductResponse;
import kg.benext.api.model.response.ProductListResponse;
import kg.benext.api.model.response.ProductResponse;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest extends BaseAPI{

    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());

    @Test
    void getProductTest(){
        ProductResponse iPhone =
        productService.getProductById("2a46cfb8-0a97-43d1-a60c-90f6e55ad047");
        System.out.println("iPhone is: " + iPhone);
    }

    @Test
    public void getProductsTest() {
        ProductListResponse response = productService.getProducts();

        step("Проверка HTTP статус-кода — ожидается 200 OK", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );

        step("Проверка что список продуктов не null", () ->
                assertNotNull(response.getProducts())
        );

        step("Проверка что список продуктов не пустой", () ->
                assertFalse(response.getProducts().isEmpty())
        );

        step("Проверка размера страницы пагинации — ожидается 10 элементов", () ->
                assertEquals(10, response.getPagination().getPageSize())
        );
    }

    @Test
    public void createProductTest() {
        ProductRequest request = TestDataGenerator.randomProductRequest();
        CreateProductResponse response = productService.createProduct(request);

        assertEquals(201, productService.getResponse().getStatusCode());
        assertNotNull(response.getId());
    }
}
