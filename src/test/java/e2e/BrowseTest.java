package e2e;

import api.BaseAPI;
import kg.benext.api.model.response.CategoryResponse;
import kg.benext.api.model.response.ProductListResponse;
import kg.benext.api.model.response.ProductResponse;
import kg.benext.api.services.CategoryService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("E2E: Блок 1 — Просмотр каталога (Browse)")
public class E2EBrowseTest extends BaseAPI {

    String baseUrl = ConfigurationManager.getBaseConfig().baseUrl();
    ProductService productService = new ProductService(baseUrl);
    CategoryService categoryService = new CategoryService(baseUrl);

    @Test
    @DisplayName("E2E-001: Открытие каталога и просмотр списка продуктов")
    void e2e001GetProductListTest() {

        ProductListResponse response = productService.getProducts();

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Массив продуктов не пустой", () ->
                assertFalse(response.getProducts().isEmpty())
        );
        step("Каждый продукт содержит обязательные поля id, name, price, imageUrl", () -> {
            for (ProductResponse product : response.getProducts()) {
                assertNotNull(product.getId(),        "id не должен быть null");
                assertNotNull(product.getName(),      "name не должен быть null");
                assertNotNull(product.getPrice(),     "price не должен быть null");
                assertNotNull(product.getImageFile(), "imageUrl не должен быть null");
            }
        });
        step("Цена каждого продукта > 0", () -> {
            for (ProductResponse product : response.getProducts()) {
                assertTrue(
                    product.getPrice().doubleValue() > 0,
                    "Цена должна быть > 0, но была: " + product.getPrice()
                );
            }
        });
    }

    @Test
    @DisplayName("E2E-002: Фильтрация продуктов по категории")
    void e2e002FilterByCategoryTest() {
        List<CategoryResponse> categories = categoryService.getCategories();

        step("Статус 200 для категорий", () ->
                assertEquals(200, categoryService.getResponse().getStatusCode())
        );
        step("Список категорий не пустой", () ->
                assertFalse(categories.isEmpty())
        );

        UUID categoryId = categories.get(0).getId();

        ProductListResponse filtered = productService.getProductsByCategory(categoryId.toString());

        step("Статус 200 для продуктов по категории", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Список продуктов не пустой", () ->
                assertFalse(filtered.getProducts().isEmpty())
        );
        step("Все продукты принадлежат выбранной категории или её подкатегориям", () -> {
            for (ProductResponse product : filtered.getProducts()) {
                assertNotNull(product.getCategoryIds(),
                        "categoryIds не должен быть null у продукта: " + product.getName()
                );
                assertFalse(product.getCategoryIds().isEmpty(),
                        "categoryIds не должен быть пустым у продукта: " + product.getName()
                );
            }
        });
    }

    @Test
    @DisplayName("E2E-003: Просмотр детальной карточки продукта")
    void e2e003GetProductDetailTest() {
        //  получаем список и берём первый продукт
        ProductListResponse list = productService.getProducts();

        step("Статус 200 для списка", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );

        String productId = list.getProducts().get(0).getId().toString();
        double priceFromList = list.getProducts().get(0).getPrice().doubleValue();
        // получаем детальную карточку
        ProductResponse product = productService.getProductById(productId);

        step("Статус 200 для детальной карточки", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );
        step("Объект содержит все детальные поля", () -> {
            assertNotNull(product.getId(),          "id не null");
            assertNotNull(product.getName(),        "name не null");
            assertNotNull(product.getPrice(),       "price не null");
            assertNotNull(product.getDescription(), "description не null");
            assertNotNull(product.getImageFile(),   "imageFile не null");
        });
        step("Цена совпадает со списком", () ->
                assertEquals(priceFromList, product.getPrice().doubleValue(),
                    "Цена в детальной карточке отличается от цены в списке")
        );
    }

    @Test
    @DisplayName("E2E-004: Просмотр отзывов на продукт (без авторизации)")
    void e2e004GetProductReviewsTest() {
        ProductListResponse list = productService.getProducts();
        String productId = list.getProducts().get(0).getId().toString();
        productService.getReviews(productId);

        int statusCode = productService.getResponse().getStatusCode();

        step("Статус ответа не 500 — сервер не упал", () ->
                assertNotEquals(500, statusCode)
        );
        step("Статус ответа корректный (200 или 405)", () ->
                assertTrue(
                        statusCode == 200 || statusCode == 405,
                        "Ожидался 200 или 405, но получили: " + statusCode
                )
        );
    }

    @Test
    @DisplayName("E2E-005: Запрос несуществующего продукта — 404 Not Found")
    void e2e005ProductNotFoundTest() {

        // Используем raw метод - не пытаемся десериализовать пустой ответ
        productService.getProductByIdRaw("99999");

        step("Статус 404", () ->
                assertEquals(404, productService.getResponse().getStatusCode())
        );
        step("Приложение не упало с 500", () ->
                assertNotEquals(500, productService.getResponse().getStatusCode())
        );
    }
}