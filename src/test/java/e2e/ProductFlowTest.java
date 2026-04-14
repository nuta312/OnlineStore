package e2e;

import com.codeborne.selenide.Selenide;
import kg.benext.api.model.request.ProductRequest;
import kg.benext.api.model.response.CreateProductResponse;
import kg.benext.api.services.ProductService;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.db.entity.MtDocProduct;
import kg.benext.db.repository.ProductRepository;
import kg.benext.gui.pages.CatalogPage;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductFlowTest {
    ProductService productService = new ProductService(ConfigurationManager.getBaseConfig().baseUrl());
    ProductRepository productRepository = new ProductRepository();

    @Test
    void productFlowTest(){
        ProductRequest request = ProductRequest.builder()
                .name("Xiaomi redmi note 400")
                .description("Smartphone")
                .imageFile("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRcdn2_RRCDZpGpnjxiY6rCQhkb-F7q-UJmdw&s")
                .price(200L)
                .categoryIds(List.of(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")))
                .brandName("Xiaomi")
                .translations(List.of(
                        ProductRequest.TranslationRequest.builder()
                                .languageCode("en")
                                .name("english")
                                .description("test")
                                .build()
                ))
                .build();

        CreateProductResponse productResponse = productService.createProduct(request);

        assertEquals(201, productService.getResponse().getStatusCode());
        assertNotNull(productResponse.getId());

        MtDocProduct product = productRepository.waitForProduct(productResponse.getId());
        System.out.println("Продукт появился: " + product.getData());

        Selenide.open("http://5.129.193.163/catalog");

//        CatalogPage catalogPage = new CatalogPage();
//        catalogPage
//                .inputTextToSearch(request.getName())
//                .checkProductCard(
//                        request.getName(),
//                        request.getPrice() + "\u00a0KGS",  // 200 KGS
//                        request.getImageFile()
//                );
//        Selenide.sleep(5000);
    }

}
