package kg.benext.common.constants;

public class Endpoints {

    // Catalog - Products
    public static final String PRODUCTS = "/api/catalog/products/%s";
    public static final String PRODUCTS_LIST = "/api/catalog/products";
    public static final String PRODUCTS_BY_CATEGORY = "/api/catalog/products/category/%s";
    public static final String PRODUCTS_UPDATE = "/api/catalog/products";

    // Catalog - Reviews
    public static final String REVIEWS = "/api/catalog/reviews/%s";

    // Catalog - Categories
    public static final String CATEGORIES = "/api/catalog/categories";

    // Catalog - Favorites
    public static final String FAVORITES = "/api/catalog/favorites";
    public static final String FAVORITES_DELETE = "/api/catalog/favorites/%s";

    // Catalog - Brands
    public static final String BRANDS = "/api/catalog/brands";

    // Ordering
    public static final String ORDERS = "/api/ordering/orders";

    // Basket
    public static final String BASKET = "/api/basket";
    public static final String BASKET_CHECKOUT = "/api/basket/checkout";
    public static final String BASKET_DELIVERY_METHODS = "/api/basket/delivery-methods";

}
