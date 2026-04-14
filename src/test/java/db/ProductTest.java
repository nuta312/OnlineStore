package db;

import kg.benext.db.entity.MtDocProduct;
import kg.benext.db.entity.model.Product;
import kg.benext.db.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ProductTest {

    ProductRepository productRepository = new ProductRepository();

    @Test
    void getAllProductsTest() {
        List<MtDocProduct> products = productRepository.getAllProducts();
        System.out.println("Всего продуктов: " + products.size());

        for (MtDocProduct p : products) {
            System.out.println(p.getData());
        }
    }

    @Test
    void createProductTest() {
        MtDocProduct product = new MtDocProduct();

        Product data = new Product();
        data.setName("Nike Air Max");
        data.setPrice(150.0);
        data.setBrandName("Nike");
        data.setDescription("Comfortable running shoes");

        product.setData(data);
        product.setMtDotnetType("");

        productRepository.createProduct(product);
        System.out.println("Продукт создан с ID: " + product.getId());
    }
}