package e2e;

import kg.benext.api.model.request.BasketRequest;
import kg.benext.api.model.request.CheckoutRequest;
import kg.benext.db.repository.OrderRepository;
import org.bson.Document;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.*;
import kg.benext.api.services.AuthService;
import kg.benext.api.services.BasketService;
import kg.benext.api.services.OrderService;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.TestDataGenerator;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ProductOrderTest {

    @BeforeEach
    void setUp() {
        String token = AuthService.getToken("amanturov2471@gmail.com", "naryn25");
        basketService.withToken(token);
        productService.withToken(token);
        orderService.withToken(token);
    }

    BasketService basketService = new BasketService(ConfigurationManager.getBaseConfig().baseUrl());
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    OrderService orderService = new OrderService(ConfigurationManager.getBaseConfig().baseUrl());
    Random random = new Random();
    OrderRepository orderRepository = new OrderRepository();

    @Test
    void e2eTest(){
        // Генерируем случайные данные для продукта и создаём его через API
        // POST /api/catalog/products
        ProductRequest productRequest = TestDataGenerator.randomProductRequest();
        CreateProductResponse createProduct = productService.createProduct(productRequest);
        // Проверяем что продукт создан успешно
        assertEquals(201, productService.getResponse().getStatusCode(),
                "Продукт должен быть создан с кодом 201");
        assertNotNull(createProduct.getId(),
                "ID созданного продукта не должен быть null");

        // Получаем список всех продуктов через API
        // GET /api/catalog/products
        ProductListResponse products = productService.getProducts();
        // Проверяем что список продуктов получен успешно
        assertEquals(200, productService.getResponse().getStatusCode(),
                "Получение продуктов должно вернуть 200");
        assertNotNull(products.getProducts(),
                "Список продуктов не должен быть null");
        assertFalse(products.getProducts().isEmpty(),
                "Список продуктов не должен быть пустым");

        // Выбираем случайный продукт из списка
        // random.nextInt(size) — случайное число от 0 до size-1
        ProductResponse randomProduct = products.getProducts()
                .get(random.nextInt(products.getProducts().size()));
        // Проверяем что выбранный продукт валидный
        assertNotNull(randomProduct.getId(),
                "ID случайного продукта не должен быть null");
        assertNotNull(randomProduct.getName(),
                "Название продукта не должно быть null");
        assertNotNull(randomProduct.getPrice(),
                "Цена продукта не должна быть null");

        // Строим запрос для добавления продукта в корзину
        // items — список товаров, quantity — случайное кол-во от 1 до 4
        // price.doubleValue() — конвертируем Long в Double
        BasketRequest basketRequest = BasketRequest.builder()
                .items(List.of(
                        BasketRequest.BasketItem.builder()
                                .productId(randomProduct.getId())
                                .productName(randomProduct.getName())
                                .quantity(random.nextInt(1, 5))
                                .price(randomProduct.getPrice().doubleValue())
                                .basePrice(randomProduct.getPrice().doubleValue())
                                .color(TestDataGenerator.randomString(5))
                                .imageFile(randomProduct.getImageFile())
                                .build()
                ))
                .build();

        // Сохраняем корзину через API
        // POST /api/basket
        // storeBasket — сохраняет или обновляет корзину текущего пользователя
        StoreBasketResponse response = basketService.storeBasket(basketRequest);
        // Проверяем что корзина сохранена успешно
        assertEquals(201, basketService.getResponse().getStatusCode(),
                "Корзина должна быть сохранена с кодом 201");
        assertNotNull(response.getUserName(),
                "userName корзины не должен быть null");

        // Строим запрос для оформления заказа
        // basketCheckoutDto содержит адрес доставки и данные карты
        // все данные случайные через TestDataGenerator
        CheckoutRequest checkoutRequest = CheckoutRequest.builder()
                .basketCheckoutDto(CheckoutRequest.BasketCheckoutDto.builder()
                        .firstName(TestDataGenerator.randomString(6))   // случайное имя
                        .lastName(TestDataGenerator.randomString(6))    // случайная фамилия
                        .emailAddress(TestDataGenerator.randomEmail())  // случайный email
                        .addressLine("ул. " + TestDataGenerator.randomString(6)) // случайный адрес
                        .country("Kyrgyzstan")
                        .state("Бишкек")
                        .zipCode(TestDataGenerator.randomDigits(6))     // случайный индекс
                        .cardName(TestDataGenerator.randomString(8))    // имя на карте
                        .cardNumber(TestDataGenerator.randomDigits(16)) // номер карты 16 цифр
                        .expiration("12/33")                            // срок карты
                        .cvv(TestDataGenerator.randomDigits(3))         // CVV 3 цифры
                        .paymentMethod(1)                               // 1 = оплата картой
                        .deliveryMethod("Pickup")                       // самовывоз
                        .deliveryCost(0.0)                              // бесплатная доставка
                        .build())
                .build();

        // ID пользователя в Firebase — по нему ищем заказ в MongoDB
        String customerId = "iT4MJATMOtUYui1m7GTk7kuLZHz2";

        // Оформляем заказ через API
        // POST /api/basket/checkout
        // сервер: берёт корзину → создаёт заказ в MongoDB → очищает корзину
        basketService.checkout(checkoutRequest);
        // Проверяем что checkout прошёл успешно
        assertEquals(200, basketService.getResponse().getStatusCode(),
                "Checkout должен вернуть 200");

        // Ждём появления заказа в MongoDB (до 1 минуты, каждые 5 секунд)
        // Заказ создаётся асинхронно через Kafka — поэтому нужно ждать
        Document order = orderRepository.waitForOrderByCustomerId(customerId);
        // Проверяем что заказ появился в MongoDB
        assertNotNull(order,
                "Заказ должен появиться в MongoDB");
        // Проверяем что customerId в заказе совпадает с нашим
        assertEquals(customerId,
                order.get("customerId", Document.class).getString("value"),
                "customerId в заказе должен совпадать");
        // Проверяем что в заказе есть товары
        assertNotNull(order.get("orderItems"),
                "orderItems не должен быть null");
        assertFalse(((List<?>) order.get("orderItems")).isEmpty(),
                "Список товаров в заказе не должен быть пустым");
    }
}
