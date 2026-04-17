package e2e;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import api.BaseAPI;
import kg.benext.api.model.response.CategoryResponse;
import kg.benext.api.model.response.ProductListResponse;
import kg.benext.api.model.response.ProductResponse;
import kg.benext.api.services.CategoryService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

@Epic("E-Commerce")
@Feature("Browse Catalog")
@DisplayName("E2E: Блок 1 — Просмотр каталога (Browse)")
public class BrowseTest extends BaseAPI {

    String baseUrl = ConfigurationManager.getBaseConfig().baseUrl();
    ProductService productService = new ProductService(baseUrl);
    CategoryService categoryService = new CategoryService(baseUrl);

    @Test
    @Tag("e2e")
    @DisplayName("E2E-001: Открытие каталога и просмотр списка продуктов")
    void e2e001GetProductListTest() {

        ProductListResponse response = productService.getProducts();

        step("Статус 200", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );

        // исправлено: assertNotNull перед isEmpty() — иначе NPE если products == null
        step("Список продуктов не null и не пустой", () -> {
            assertNotNull(response.getProducts(), "Список продуктов не должен быть null");
            assertFalse(response.getProducts().isEmpty(), "Список продуктов не должен быть пустым");
        });

        step("Каждый продукт содержит обязательные поля id, name, price, imageFile", () -> {
            for (ProductResponse product : response.getProducts()) {
                assertNotNull(product.getId(),        "id не должен быть null");
                assertNotNull(product.getName(),      "name не должен быть null");
                assertNotNull(product.getPrice(),     "price не должен быть null");
                assertNotNull(product.getImageFile(), "imageFile не должен быть null");
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
    @Tag("e2e")
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

        // исправлено: проверяем что categoryId не null перед использованием
        step("ID первой категории не null", () ->
                assertNotNull(categoryId, "ID категории не должен быть null")
        );

        ProductListResponse filtered = productService.getProductsByCategory(categoryId.toString());

        step("Статус 200 для продуктов по категории", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );

        step("Список продуктов не null и не пустой", () -> {
            assertNotNull(filtered.getProducts(), "Список продуктов не должен быть null");
            assertFalse(filtered.getProducts().isEmpty(), "Список продуктов не должен быть пустым");
        });

        // исправлено: теперь проверяем что продукт РЕАЛЬНО принадлежит выбранной категории
        step("Все продукты принадлежат выбранной категории", () -> {
            for (ProductResponse product : filtered.getProducts()) {
                assertNotNull(product.getCategoryIds(),
                        "categoryIds не должен быть null у продукта: " + product.getName());
                assertFalse(product.getCategoryIds().isEmpty(),
                        "categoryIds не должен быть пустым у продукта: " + product.getName());
                assertTrue(product.getCategoryIds().contains(categoryId),
                        "Продукт '" + product.getName() + "' не принадлежит категории " + categoryId);
            }
        });
    }

    @Test
    @Tag("e2e")
    @DisplayName("E2E-003: Просмотр детальной карточки продукта")
    void e2e003GetProductDetailTest() {
        ProductListResponse list = productService.getProducts();

        step("Статус 200 для списка", () ->
                assertEquals(200, productService.getResponse().getStatusCode())
        );

        // исправлено: защитная проверка + сохраняем в переменную (было два get(0))
        step("Список содержит минимум 1 продукт", () ->
                assertFalse(list.getProducts().isEmpty(), "Список продуктов пуст")
        );

        ProductResponse firstProduct = list.getProducts().get(0);
        String productId = firstProduct.getId().toString();
        double priceFromList = firstProduct.getPrice().doubleValue();

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
    @Tag("e2e")
    @DisplayName("E2E-004: Просмотр отзывов на продукт (без авторизации)")
    void e2e004GetProductReviewsTest() {
        ProductListResponse list = productService.getProducts();

        step("Список содержит минимум 1 продукт", () ->
                assertFalse(list.getProducts().isEmpty(), "Список продуктов пуст")
        );

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
    @Tag("e2e")
    @DisplayName("E2E-005: Запрос несуществующего продукта — 404 Not Found")
    void e2e005ProductNotFoundTest() {

        // raw метод — не пытаемся десериализовать пустой ответ
        productService.getProductByIdRaw("99999");

        step("Статус 404", () ->
                assertEquals(404, productService.getResponse().getStatusCode())
        );

        step("Приложение не упало с 500", () ->
                assertNotEquals(500, productService.getResponse().getStatusCode())
        );
    }
}