package e2e;

import api.BaseAPI;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import kg.benext.api.services.ProductService;
import kg.benext.api.model.response.ProductListResponse;
import kg.benext.api.model.response.ProductResponse;

import kg.benext.common.utils.file.ConfigurationManager;

import kg.benext.common.utils.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.*;

import static io.qameta.allure.Allure.step;

// ─────────────────────────────────────────────────────────────────────────────
// АННОТАЦИИ КЛАССА
// ─────────────────────────────────────────────────────────────────────────────
@Epic("E-Commerce")              // Верхний уровень Allure иерархии: весь магазин
@Feature("Guest Access")         // Фича: доступ гостя (без авторизации)
@Story("E2E-020: Guest Flow")    // Конкретный сценарий: гость просматривает но не покупает
@DisplayName("E2E-020 | Гость: просмотр каталога + запрет на корзину и оформление заказа")

// @TestMethodOrder — определяем ПОРЯДОК выполнения тестов.
// OrderAnnotation.class — тесты выполняются в порядке @Order(1), @Order(2)...
// Это важно для E2E: шаг 1 должен идти раньше шага 3.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

// extends BaseAPI — наследуем @ExtendWith(AllureLogsExtension.class)
// AllureLogsExtension перехватывает все log.info/warn и добавляет в Allure отчёт
public class GuestAccessTest extends BaseAPI {

    // ─────────────────────────────────────────────────────────────────────────
    // ПОЛЯ КЛАССА
    // ─────────────────────────────────────────────────────────────────────────

    // Читаем базовый URL из app.properties: base.url=http://5.129.193.163
    // Хранится как константа — используется во всех запросах через given().baseUri(BASE_URL)
    private static final String BASE_URL =
            ConfigurationManager.getBaseConfig().baseUrl();

    // ProductService БЕЗ токена — используем только для публичных запросов (шаги 1-2).
    // ВАЖНО: НЕ вызываем .withToken() — это имитирует гостя.
    // ProductService наследует HttpRequest, который по умолчанию НЕ добавляет Authorization.
    private final ProductService productService = new ProductService(BASE_URL);

    // Поле для передачи ID продукта между тестами (шаг 1 → шаг 2).
    // static — сохраняется между разными @Test методами одного класса.
    // Без static каждый @Test получает новый экземпляр класса и поле = null.
    private static String firstProductId;

    // ─────────────────────────────────────────────────────────────────────────
    // SETUP — настройка REST Assured перед каждым тестом
    // ─────────────────────────────────────────────────────────────────────────
    @BeforeEach
    void setUp() {
        // Говорим REST Assured: даже если Content-Type не JSON — всё равно парсь как JSON.
        // Нужно потому что 401 ответы иногда приходят без Content-Type заголовка.
        RestAssured.defaultParser = Parser.JSON;

        // ВАЖНО: Намеренно НЕ устанавливаем токен.
        // Гость = пользователь без JWT токена.
        // Мы проверяем именно это состояние — отсутствие авторизации.
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ШАГ 1: Гость МОЖЕТ просматривать каталог (200 OK)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(1) // Выполняется первым — нам нужен ID продукта для шага 2
    @Severity(SeverityLevel.CRITICAL) // HIGH в задании = CRITICAL в Allure
    @DisplayName("Шаг 1: [Browse] GET /api/catalog/products → 200 OK (публичный)")
    void step1_guestCanBrowseProductList() {

        step("Гость делает GET /api/catalog/products без токена", () -> {

            // productService создан БЕЗ withToken() — запрос идёт без Authorization заголовка.
            // getProducts() → GET http://5.129.193.163/api/catalog/products
            ProductListResponse response = productService.getProducts();

            // ── ПРОВЕРКА СТАТУСА: 200 OK ──────────────────────────────────────
            // Каталог — публичный ресурс. ЛЮБОЙ пользователь (включая гостя)
            // должен иметь доступ к просмотру товаров.
            // Если здесь 401 — значит каталог закрыт для всех, это баг!
            assertEquals(200,
                    productService.getResponse().getStatusCode(),
                    "GET /api/catalog/products без токена должен вернуть 200 OK. " +
                    "Каталог — публичный ресурс, доступный всем гостям.");

            // ── ПРОВЕРКА ТЕЛА ОТВЕТА ──────────────────────────────────────────
            // 200 без тела — это тоже баг. Гость должен видеть реальные товары.
            assertNotNull(response.getProducts(),
                    "Список продуктов не должен быть null при 200 OK");

            // Каталог должен содержать хотя бы 1 товар
            assertFalse(response.getProducts().isEmpty(),
                    "Каталог должен содержать товары. Гость должен видеть список.");

            // Сохраняем ID первого продукта для шага 2.
            // static поле — переживает между @Test методами.
            firstProductId = response.getProducts().get(0).getId().toString();

            // Добавляем данные в Allure отчёт для наглядности
            Allure.addAttachment("Результат шага 1",
                    "✅ Статус: 200 OK\n" +
                    "✅ Товаров в каталоге: " + response.getProducts().size() + "\n" +
                    "✅ Первый товар ID: " + firstProductId + "\n" +
                    "Вывод: гость МОЖЕТ просматривать каталог");
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ШАГ 2: Гость МОЖЕТ просматривать отдельный товар (200 OK)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(2) // Выполняется вторым — зависит от firstProductId из шага 1
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Шаг 2: [Browse] GET /api/catalog/products/{id} → 200 OK (публичный)")
    void step2_guestCanViewProductDetails() {

        step("Гость делает GET /api/catalog/products/{id} без токена", () -> {

            // Проверяем что шаг 1 выполнился и ID сохранён.
            // assertNotNull здесь — страховка: если шаг 1 упал, шаг 2 тоже упадёт с понятной ошибкой.
            assertNotNull(firstProductId,
                    "firstProductId не должен быть null — убедитесь что шаг 1 выполнился успешно");

            // Получаем детали продукта БЕЗ токена.
            // getProductById() → GET /api/catalog/products/{id}
            // ProductService НЕ имеет токена — запрос от лица гостя.
            ProductResponse productDetails = productService.getProductById(firstProductId);

            // ── ПРОВЕРКА СТАТУСА: 200 OK ──────────────────────────────────────
            // Страница продукта тоже публичная — гость должен видеть детали товара.
            assertEquals(200,
                    productService.getResponse().getStatusCode(),
                    "GET /api/catalog/products/{id} без токена должен вернуть 200 OK. " +
                    "Детали товара — публичный ресурс.");

            // ── ПРОВЕРКА ТЕЛА ОТВЕТА ──────────────────────────────────────────
            assertNotNull(productDetails,
                    "Детали продукта не должны быть null при 200 OK");
            assertNotNull(productDetails.getId(),
                    "ID продукта в деталях не должен быть null");
            assertEquals(firstProductId, productDetails.getId().toString(),
                    "ID в деталях должен совпадать с запрошенным ID");
            assertNotNull(productDetails.getName(),
                    "Имя продукта не должно быть null");
            assertNotNull(productDetails.getPrice(),
                    "Цена продукта не должна быть null");

            Allure.addAttachment("Результат шага 2",
                    "✅ Статус: 200 OK\n" +
                    "✅ Товар: " + productDetails.getName() + "\n" +
                    "✅ Цена: " + productDetails.getPrice() + "\n" +
                    "Вывод: гость МОЖЕТ просматривать детали товара");
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ШАГ 3: Гость НЕ МОЖЕТ получить корзину (401 Unauthorized)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Шаг 3: [Basket] GET /api/basket без токена → 401 Unauthorized")
    void step3_guestCannotGetBasket() {

        step("Гость делает GET /api/basket без JWT токена", () -> {

            // Здесь используем given() напрямую — НЕ через BasketService.
            // Почему? BasketService.withToken() добавил бы токен.
            // Нам нужен чистый запрос БЕЗ Authorization заголовка.
            // given() — начало REST Assured fluent chain:
            //   given() = настройка (заголовки, тело, параметры)
            //   .when() = действие (GET, POST, PUT, DELETE)  — можно опустить
            //   .then() = проверки (статус, тело)            — можно опустить
            Response response = given()
                    .baseUri(BASE_URL)              // http://5.129.193.163
                    .contentType(ContentType.JSON)  // Content-Type: application/json
                    .accept(ContentType.JSON)       // Accept: application/json
                    // НАМЕРЕННО НЕ добавляем: .header("Authorization", "Bearer ...")
                    // Это имитирует гостя — нет токена = нет авторизации
                    .get("/api/basket");             // GET /api/basket

            // ── ПРОВЕРКА СТАТУСА: 401 Unauthorized ───────────────────────────
            // 401 = "Ты не представился". Сервер не знает КТО ты.
            // Корзина — личный ресурс, привязанный к пользователю.
            // Без JWT токена сервер не может определить ЧЬЮ корзину вернуть.
            assertEquals(401,
                    response.getStatusCode(),
                    "GET /api/basket без токена должен вернуть 401 Unauthorized. " +
                    "Корзина — защищённый ресурс. " +
                    "Фактический статус: " + response.getStatusCode());

            // ── ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА: НЕ 200 ─────────────────────────────
            // Если сервер вернул 200 без токена — это КРИТИЧЕСКИЙ баг безопасности!
            // Любой человек мог бы видеть чужие корзины.
            assertNotEquals(200,
                    response.getStatusCode(),
                    "КРИТИЧЕСКИЙ БАГ БЕЗОПАСНОСТИ: GET /api/basket вернул 200 без токена! " +
                    "Корзина не должна быть доступна неавторизованным пользователям.");

            Allure.addAttachment("Результат шага 3",
                    "✅ Статус: " + response.getStatusCode() + " (ожидаем 401)\n" +
                    "✅ Корзина недоступна для гостя\n" +
                    "Вывод: гость НЕ МОЖЕТ получить корзину без токена");
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ШАГ 4: Гость НЕ МОЖЕТ добавить товар в корзину (401 Unauthorized)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Шаг 4: [Basket] POST /api/basket без токена → 401 Unauthorized")
    void step4_guestCannotAddToBasket() {

        step("Гость делает POST /api/basket без JWT токена", () -> {

            // Формируем минимально корректное JSON тело для POST /api/basket.
            // Нам нужно тело чтобы запрос был "настоящим" — сервер должен
            // отклонить его именно из-за отсутствия токена, а не из-за пустого тела.
            // UUID.randomUUID() — случайный ID продукта для тестового запроса
            String minimalBasketJson = """
                    {
                        "items": [
                            {
                                "productId": "%s",
                                "productName": "Test Product",
                                "quantity": 1,
                                "price": 100.0,
                                "basePrice": 100.0,
                                "color": "red",
                                "imageFile": "https://test.com/image.jpg"
                            }
                        ]
                    }
                    """.formatted(TestDataGenerator.randomUUID()); // Вставляем случайный UUID

            // POST запрос без токена — имитируем попытку гостя добавить товар в корзину
            Response response = given()
                    .baseUri(BASE_URL)              // Базовый URL сервера
                    .contentType(ContentType.JSON)  // Тело в JSON формате
                    .accept(ContentType.JSON)       // Ожидаем JSON в ответе
                    .body(minimalBasketJson)         // JSON с товаром для корзины
                    // Без .header("Authorization", ...) — нет токена = гость
                    .post("/api/basket");            // POST /api/basket

            // ── ПРОВЕРКА СТАТУСА: 401 Unauthorized ───────────────────────────
            // Сервер должен проверить токен ДО обработки тела запроса.
            // Это принцип "Authentication before Authorization":
            // сначала кто ты (Auth) → потом что можешь делать (Authz).
            assertEquals(401,
                    response.getStatusCode(),
                    "POST /api/basket без токена должен вернуть 401 Unauthorized. " +
                    "Добавление в корзину — защищённая операция. " +
                    "Фактический статус: " + response.getStatusCode());

            // Гость не должен получить ни 200 Created, ни 201 Created
            assertNotEquals(200, response.getStatusCode(),
                    "POST /api/basket не должен возвращать 200 без токена");
            assertNotEquals(201, response.getStatusCode(),
                    "POST /api/basket не должен возвращать 201 без токена — " +
                    "это означало бы что корзина создана для анонимного пользователя");

            Allure.addAttachment("Результат шага 4",
                    "✅ Статус: " + response.getStatusCode() + " (ожидаем 401)\n" +
                    "✅ Добавление в корзину заблокировано для гостя\n" +
                    "Вывод: гость НЕ МОЖЕТ добавить товар в корзину без токена");
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ШАГ 5: Гость НЕ МОЖЕТ оформить заказ (401 Unauthorized)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Шаг 5: [Checkout] POST /api/basket/checkout без токена → 401 Unauthorized")
    void step5_guestCannotCheckout() {

        step("Гость делает POST /api/basket/checkout без JWT токена", () -> {

            // Формируем корректный JSON для checkout запроса.
            // Используем реальную структуру CheckoutRequest.BasketCheckoutDto
            // чтобы сервер отклонил запрос только из-за отсутствия токена,
            // а не из-за невалидного тела (это разные виды ошибок — 400 vs 401).
            String checkoutJson = """
                    {
                        "basketCheckoutDto": {
                            "firstName": "Guest",
                            "lastName": "User",
                            "emailAddress": "guest@test.com",
                            "addressLine": "ул. Тестовая 1",
                            "country": "Kyrgyzstan",
                            "state": "Бишкек",
                            "zipCode": "720000",
                            "cardName": "GUEST USER",
                            "cardNumber": "4111111111111111",
                            "expiration": "12/28",
                            "cvv": "123",
                            "paymentMethod": 1,
                            "deliveryMethod": "Pickup",
                            "deliveryCost": 0.0
                        }
                    }
                    """;

            // POST /api/basket/checkout — самый критичный защищённый эндпоинт.
            // Это финальный шаг покупки, создающий заказ и списывающий деньги.
            // Если этот эндпоинт доступен без токена — это КРИТИЧЕСКИЙ баг безопасности!
            Response response = given()
                    .baseUri(BASE_URL)              // http://5.129.193.163
                    .contentType(ContentType.JSON)  // Content-Type: application/json
                    .accept(ContentType.JSON)       // Accept: application/json
                    .body(checkoutJson)              // Полное тело checkout запроса
                    // Без .header("Authorization", ...) — нет токена = гость
                    .post("/api/basket/checkout");  // POST /api/basket/checkout

            // ── ПРОВЕРКА СТАТУСА: 401 Unauthorized ───────────────────────────
            // Checkout = создание заказа = потенциальная финансовая операция.
            // Это САМЫЙ защищённый эндпоинт в системе.
            // Без авторизации — абсолютно недопустимо!
            assertEquals(401,
                    response.getStatusCode(),
                    "POST /api/basket/checkout без токена ОБЯЗАН вернуть 401 Unauthorized. " +
                    "Оформление заказа — строго защищённая операция! " +
                    "Фактический статус: " + response.getStatusCode());

            // Если checkout вернул 200 без токена — это баг уровня P0 (critical security breach)
            assertNotEquals(200, response.getStatusCode(),
                    "КРИТИЧЕСКИЙ БАГ P0: POST /api/basket/checkout вернул 200 без токена! " +
                    "Заказ мог быть создан от имени анонимного пользователя!");

            // Если 201 — значит заказ создан. Ещё хуже!
            assertNotEquals(201, response.getStatusCode(),
                    "КРИТИЧЕСКИЙ БАГ P0: POST /api/basket/checkout вернул 201 без токена! " +
                    "Это означает что анонимный пользователь создал реальный заказ!");

            Allure.addAttachment("Результат шага 5",
                    "✅ Статус: " + response.getStatusCode() + " (ожидаем 401)\n" +
                    "✅ Оформление заказа заблокировано для гостя\n" +
                    "Вывод: гость НЕ МОЖЕТ оформить заказ без токена");
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ФИНАЛЬНАЯ СВОДКА — @AfterAll выводит итог всего теста E2E-020
    // @AfterAll выполняется ОДИН РАЗ после ВСЕХ тестов класса
    // static — обязательно для @AfterAll (JUnit требование)
    // ─────────────────────────────────────────────────────────────────────────
    @AfterAll
    static void printSummary() {
        // Добавляем итоговый отчёт в Allure
        Allure.addAttachment("E2E-020 Итоговая сводка",
                "═══════════════════════════════════════════\n" +
                "           РЕЗУЛЬТАТЫ E2E-020: ГОСТЬ\n" +
                "═══════════════════════════════════════════\n" +
                "Шаг 1: GET /api/catalog/products        → ✅ 200 OK (публичный)\n" +
                "Шаг 2: GET /api/catalog/products/{id}   → ✅ 200 OK (публичный)\n" +
                "Шаг 3: GET /api/basket                  → ✅ 401 Unauthorized\n" +
                "Шаг 4: POST /api/basket                 → ✅ 401 Unauthorized\n" +
                "Шаг 5: POST /api/basket/checkout        → ✅ 401 Unauthorized\n" +
                "═══════════════════════════════════════════\n" +
                "Вывод: Гость может СМОТРЕТЬ, но НЕ МОЖЕТ КУПИТЬ.\n" +
                "Система корректно разделяет публичные и защищённые ресурсы.");
    }
}