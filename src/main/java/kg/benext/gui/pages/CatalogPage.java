package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class CatalogPage {
    private final SelenideElement searchInput = $("[placeholder='Search products...']");

    @Step("Search product by name")
    public CatalogPage searchProduct(String productName) {
        searchInput.shouldBe(visible).sendKeys(productName);
        return this;
    }

    @Step("Check product card: name={productName}, price={price}")
    public CatalogPage checkProductCard(String productName, String price, String imageUrl) {
        // Находим карточку через img по alt названия товара
        SelenideElement card = $("img[alt='" + productName + "']").closest(".rounded-xl");

        // Проверяем название
        card.$("h3").shouldHave(text(productName));

        // Проверяем цену
        card.$("p").shouldHave(text(price));

        // Проверяем изображение
        card.$("img[alt='" + productName + "']")
                .shouldBe(visible)
                .shouldHave(attribute("src", imageUrl));

        return this;
    }
}