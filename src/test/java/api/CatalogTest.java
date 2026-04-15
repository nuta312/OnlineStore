package api;

import kg.benext.api.model.request.FavoriteRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.*;
import kg.benext.common.utils.file.ConfigurationManager;
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
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
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
    void addToFavoritesTest() {
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        favoriteService.withToken(token);

        SuccessResponse response = favoriteService.addToFavorites(
                FavoriteRequest.builder()
                        .productId(UUID.fromString("ffc91150-2f5f-474e-bcd5-c84d0842bb46"))
                        .build()
        );

        assertEquals(200, favoriteService.getResponse().getStatusCode());
        assertTrue(response.getIsSuccess());
    }

    @Test
    void removeFromFavoritesTest() {
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        favoriteService.withToken(token);

        SuccessResponse response = favoriteService.removeFromFavorites(
                "ffc91150-2f5f-474e-bcd5-c84d0842bb46"
        );

        assertEquals(200, favoriteService.getResponse().getStatusCode());
        assertTrue(response.getIsSuccess());
    }

    @Test
    void getProductsByCategoryTest() {
        String categoryId = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
        ProductListResponse response = productService.getProductsByCategory(categoryId);

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Список продуктов не null", () ->
                assertNotNull(response.getProducts())
        );
    }
}
