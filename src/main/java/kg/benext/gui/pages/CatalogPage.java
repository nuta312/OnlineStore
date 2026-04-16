package kg.benext.gui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Cat;
import io.qameta.allure.Step;

import javax.xml.catalog.Catalog;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class CatalogPage extends BasePage<CatalogPage> {
    private final SelenideElement searchInput = $("[placeholder='Search products...']");
    private final SelenideElement allCategories = $("//button[.='All Categories']");
    private final SelenideElement minPriceInput = $("input[placeholder='Min']");
    private final SelenideElement maxPriceInput = $("input[placeholder='Max']");
    private final ElementsCollection products = $$("div[role='article']");
    private final SelenideElement prevButton = $$("button").findBy(Condition.text("chevron_left"));
    private final SelenideElement nextButton = $$("button").findBy(Condition.text("chevron_right"));
    private final SelenideElement dropdown = $("button[role='combobox']");
    private final ElementsCollection options = $("div[data-radix-select-viewport]").$$("div[role='option']");
    private final SelenideElement newest = $$("div[role='option']")
                    .findBy(Condition.text("Sort by: Newest"));
    private final SelenideElement lowToHigh = $$("div[role='option']")
                    .findBy(Condition.text("Price: Low to High"));
    private final SelenideElement highToLow = $$("div[role='option']")
                    .findBy(Condition.text("Price: High to Low"));
    private final SelenideElement topRated = $$("div[role='option']")
                    .findBy(Condition.text("Top Rated"));
    private final SelenideElement nameAZ = $$("div[role='option']")
                    .findBy(Condition.text("Name: A → Z"));
    private final SelenideElement nameZA = $$("div[role='option']")
                    .findBy(Condition.text("Name: Z → A"));

    @Override
    public CatalogPage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

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

    @Step("Get product card by name")
    public SelenideElement getProductByName(String productName) {
        return products.findBy(Condition.text(productName));
    }

    @Step("click on all categories")
    public CatalogPage selectAllCategories() {
        allCategories.shouldBe(visible).click();
        return this;
    }

    @Step("Select category: {categoryName}")
    public CatalogPage selectCategory(String categoryName) {
        $x("//button[.='" + categoryName + "']")
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Set min price: {value}")
    public CatalogPage setMinPrice(String value) {
        minPriceInput.shouldBe(visible).setValue(value);
        return this;
    }

    @Step("Set max price: {value}")
    public CatalogPage setMaxPrice(String value) {
        maxPriceInput.shouldBe(visible).setValue(value);
        return this;
    }

    @Step("Set price range: {min} - {max}")
    public CatalogPage setPriceRange(String min, String max) {
        setMinPrice(min);
        setMaxPrice(max);
        return this;
    }

    @Step("Select rating: {stars} stars & up")
    public CatalogPage selectRatingByStars(int stars) {
        $x("(//div[contains(@class,'text-yellow-400')])[" + stars + "]/ancestor::button")
                .shouldBe(visible)
                .click();
        return this;
    }
    @Step("Add product to cart")
    public CatalogPage addProductToCart(String productName) {
        getProductByName(productName)
                .$("button[title='Add to Cart']")
                .click();
        return this;
    }

    @Step("Add product to favorites")
    public CatalogPage addProductToFavorites(String productName) {
        getProductByName(productName)
                .$("button[title='Add to favorites']")
                .click();
        return this;
    }

    public CatalogPage clickToPrevBtn() {
        prevButton.shouldBe(visible).click();
        return this;
    }

    public CatalogPage clickToNextBtn() {
        nextButton.shouldBe(visible).click();
        return this;
    }

    public CatalogPage clickToSortDropDown() {
        dropdown.click();
        return this;
    }

    public CatalogPage selectSortOption(String optionText) {
        $$("div[role='option']")
                .findBy(Condition.text(optionText))
                .click();
        return this;
    }
}