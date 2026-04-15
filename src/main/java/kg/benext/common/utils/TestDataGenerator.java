package kg.benext.common.utils;

import kg.benext.api.model.request.OrderRequest;
import kg.benext.api.model.request.ProductRequest;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {

    private static final Random RANDOM = new Random();

    // ─────────────────────────────────────────────
    //  PRODUCT
    // ─────────────────────────────────────────────

    private static final String[] BRANDS = {
            "Apple", "Samsung", "Xiaomi", "Nike", "Adidas", "Sony", "LG", "Huawei", "OnePlus", "Asus"
    };

    private static final String[] PRODUCT_NAMES = {
            "Pro Max", "Ultra", "Lite", "Plus", "Edition", "Series X", "Neo", "Air", "Flex", "Zoom"
    };

    private static final String[] CATEGORIES = {
            "Electronics", "Automotive", "Shoes", "Accessories", "Sports", "Home", "Beauty", "Toys"
    };

    private static final String[] LANGUAGES = {"en", "ru", "kg"};

    private static final String[] PLACEHOLDER_IMAGES = {
            "https://fikiwiki.com/uploads/posts/2022-02/1644901930_1-fikiwiki-com-p-kartinki-interesnie-i-krasivie-1.jpg",
            "https://img.belta.by/images/storage/news/with_archive/2022/000029_1653572687_504261_big.jpg",
            "https://wl-adme.cf.tsp.li/resize/728x/jpg/cfb/0d0/638dad5ed8a52e9904d62f3bd8.jpg"
    };

    /**
     * Generates a fully random ProductRequest.
     */
    public static ProductRequest randomProductRequest() {
        String brand = randomElement(BRANDS);
        String suffix = randomElement(PRODUCT_NAMES);
        String name = brand + " " + suffix + " " + RANDOM.nextInt(900, 999);

        return ProductRequest.builder()
                .name(name)
                .description("Auto-generated product: " + name)
                .imageFile(randomElement(PLACEHOLDER_IMAGES))
                .price((long) RANDOM.nextInt(50, 2000))
                .categoryIds(List.of(UUID.randomUUID()))
                .brandName(brand.toLowerCase())
                .translations(List.of(randomTranslation()))
                .build();
    }

    /**
     * Returns a builder pre-filled with random values — override only what you need.
     *
     * <pre>
     * ProductRequest req = TestDataGenerator.productRequestBuilder()
     *         .name("Custom Name")
     *         .build();
     * </pre>
     */
    public static ProductRequest.ProductRequestBuilder productRequestBuilder() {
        ProductRequest filled = randomProductRequest();
        return ProductRequest.builder()
                .name(filled.getName())
                .description(filled.getDescription())
                .imageFile(filled.getImageFile())
                .price(filled.getPrice())
                .categoryIds(filled.getCategoryIds())
                .brandName(filled.getBrandName())
                .translations(filled.getTranslations());
    }

    public static ProductRequest.TranslationRequest randomTranslation() {
        String lang = randomElement(LANGUAGES);
        return ProductRequest.TranslationRequest.builder()
                .languageCode(lang)
                .name("Name-" + lang + "-" + randomString(4))
                .description("Desc-" + randomString(8))
                .build();
    }

    // ─────────────────────────────────────────────
    //  ORDER
    // ─────────────────────────────────────────────

    private static final String[] FIRST_NAMES = {
            "Aibek", "Sanzhar", "Bakyt", "Marat", "Daniyar", "Alina", "Gulnara", "Zarina", "Aziz", "Nurlan"
    };

    private static final String[] LAST_NAMES = {
            "Amanturov", "Sydykov", "Mamytov", "Usenov", "Kadyrov",
            "Isaeva", "Bolotova", "Toktosunov", "Askarov", "Dzhaksybekov"
    };

    private static final String[] CITIES = {"Бишкек", "Ош", "Жалал-Абад", "Каракол", "Токмок"};

    /**
     * Generates a fully random OrderRequest.
     */
    public static OrderRequest randomOrderRequest() {
        String customerId = UUID.randomUUID().toString();
        OrderRequest.AddressRequest address = randomAddress();

        return OrderRequest.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .userName(customerId)
                .orderName("Order-" + randomString(6))
                .shippingAddress(address)
                .billingAddress(address)
                .payment(randomPayment())
                .Items(List.of(randomOrderItem()))
                .status(RANDOM.nextInt(1, 5))
                .deliveryMethod(RANDOM.nextInt(1, 4))
                .deliveryCost((double) RANDOM.nextInt(100, 500))
                .locale("en")
                .build();
    }

    /**
     * Returns a builder pre-filled with random values — override only what you need.
     *
     * <pre>
     * OrderRequest req = TestDataGenerator.orderRequestBuilder()
     *         .status(1)
     *         .build();
     * </pre>
     */
    public static OrderRequest.OrderRequestBuilder orderRequestBuilder() {
        OrderRequest filled = randomOrderRequest();
        return OrderRequest.builder()
                .id(filled.getId())
                .customerId(filled.getCustomerId())
                .userName(filled.getUserName())
                .orderName(filled.getOrderName())
                .shippingAddress(filled.getShippingAddress())
                .billingAddress(filled.getBillingAddress())
                .payment(filled.getPayment())
                .Items(filled.getItems())
                .status(filled.getStatus())
                .deliveryMethod(filled.getDeliveryMethod())
                .deliveryCost(filled.getDeliveryCost())
                .locale(filled.getLocale());
    }

    public static OrderRequest.AddressRequest randomAddress() {
        String firstName = randomElement(FIRST_NAMES);
        String lastName  = randomElement(LAST_NAMES);
        return OrderRequest.AddressRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase()
                        + RANDOM.nextInt(100, 999) + "@test.com")
                .addressLine("ул. " + randomString(6) + " " + RANDOM.nextInt(1, 200))
                .country("Kyrgyzstan")
                .state(randomElement(CITIES))
                .zipCode(String.valueOf(RANDOM.nextInt(720000, 730000)))
                .build();
    }

    public static OrderRequest.PaymentRequest randomPayment() {
        return OrderRequest.PaymentRequest.builder()
                .cardName(randomString(8))
                .cardNumber(randomDigits(16))
                .expiration(String.format("%02d/%02d", RANDOM.nextInt(1, 13), RANDOM.nextInt(24, 30)))
                .cvv(randomDigits(3))
                .paymentMethod(RANDOM.nextInt(1, 4))
                .build();
    }

    public static OrderRequest.OrderItemRequest randomOrderItem() {
        return OrderRequest.OrderItemRequest.builder()
                .productId(UUID.randomUUID())
                .quantity(RANDOM.nextInt(1, 6))
                .price(RANDOM.nextDouble(10.0, 500.0))
                .productName(randomElement(BRANDS) + " " + randomElement(PRODUCT_NAMES))
                .build();
    }

    // ─────────────────────────────────────────────
    //  PRIMITIVES
    // ─────────────────────────────────────────────

    public static String randomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String randomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomEmail() {
        return randomString(6) + "@" + randomString(4) + ".com";
    }

    public static UUID randomUUID() {
        return UUID.randomUUID();
    }

    public static long randomPrice(long min, long max) {
        return min + (long) (RANDOM.nextDouble() * (max - min));
    }

    private static <T> T randomElement(T[] array) {
        return array[RANDOM.nextInt(array.length)];
    }
}