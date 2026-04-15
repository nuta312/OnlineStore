package api;

import kg.benext.api.model.request.FavoriteRequest;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.*;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogTest extends BaseAPI {

    String baseUrl = ConfigurationManager.getBaseConfig().baseUrl();
    BrandService brandService = new BrandService(baseUrl);
    CategoryService categoryService = new CategoryService(baseUrl);
    FavoriteService favoriteService = new FavoriteService(baseUrl);
    ProductService productService = new ProductService(baseUrl);
    String token;

    @BeforeEach
    void setUp() {
        token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
    }

    @Test
    void getBrandsTest() {
        BrandListResponse response = brandService.getBrands();

        step("Статус 200", () ->
                assertEquals(200, brandService.getResponse().getStatusCode())
        );
        step("Список брендов не пустой", () ->
                assertFalse(response.getBrands().isEmpty())
        );
    }

    @Test
    void getCategoriesTest() {
        List<CategoryResponse> categories = categoryService.getCategories();

        step("Статус 200", () ->
                assertEquals(200, categoryService.getResponse().getStatusCode())
        );
        step("Список категорий не пустой", () ->
                assertFalse(categories.isEmpty())
        );
        step("У каждой категории есть id и name", () -> {
            for (CategoryResponse category : categories) {
                assertNotNull(category.getId());
                assertNotNull(category.getName());
            }
        });
    }

    @Test
    void getFavoritesTest() {
        favoriteService.withToken(token);
        FavoriteListResponse response = favoriteService.getFavorites();

        step("Статус 200", () ->
                assertEquals(200, favoriteService.getResponse().getStatusCode())
        );
        step("Список избранного не null", () ->
                assertNotNull(response.getFavorites())
        );
    }

    @Test
    void addAndRemoveFromFavoritesTest() {
        favoriteService.withToken(token);
        productService.withToken(token);

        // Получаем случайный продукт из списка
        ProductListResponse products = productService.getProducts();
        UUID randomProductId = products.getProducts()
                .get(new java.util.Random().nextInt(products.getProducts().size()))
                .getId();

        // Добавляем в избранное
        SuccessResponse addResponse = favoriteService.addToFavorites(
                FavoriteRequest.builder().productId(randomProductId).build()
        );
        step("Добавление: статус 200", () ->
                assertEquals(200, favoriteService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(addResponse.getIsSuccess())
        );

        // Удаляем из избранного
        SuccessResponse removeResponse = favoriteService.removeFromFavorites(randomProductId.toString());
        step("Удаление: статус 200", () ->
                assertEquals(200, favoriteService.getResponse().getStatusCode())
        );
        step("isSuccess = true", () ->
                assertTrue(removeResponse.getIsSuccess())
        );
    }

    @Test
    void getProductsByCategoryTest() {
        // Получаем случайную категорию
        List<CategoryResponse> categories = categoryService.getCategories();
        UUID randomCategoryId = categories
                .get(new java.util.Random().nextInt(categories.size()))
                .getId();

        ProductListResponse response = productService.getProductsByCategory(randomCategoryId.toString());

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Список продуктов не null", () ->
                assertNotNull(response.getProducts())
        );
    }
}