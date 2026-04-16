package kg.benext.gui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CatalogPage extends BasePage<CatalogPage> {

    private final SelenideElement searchInput    = $("[placeholder='Search products...']");
    private final SelenideElement firstCard      = $$("div[role='article']").first();
    private final SelenideElement addToCartBtn   = $x("//button[@title='Add to Cart']");
    private final SelenideElement allCategories  = $x("//button[.='All Categories']");
    private final SelenideElement minPriceInput  = $("input[placeholder='Min']");
    private final SelenideElement maxPriceInput  = $("input[placeholder='Max']");
    private final ElementsCollection products    = $$("div[role='article']");
    private final SelenideElement prevButton     = $$("button").findBy(Condition.text("chevron_left"));
    private final SelenideElement nextButton     = $$("button").findBy(Condition.text("chevron_right"));
    private final SelenideElement dropdown       = $("button[role='combobox']");

    // ══════════════════════════════════════════════════════
    // ЗАГРУЗКА СТРАНИЦЫ
    // ══════════════════════════════════════════════════════

    @Override
    public CatalogPage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

    // ══════════════════════════════════════════════════════
    // ДЕЙСТВИЯ
    // ══════════════════════════════════════════════════════

    @Step("Add first product to cart")
    public CatalogPage addFirstProductToCart() {
        firstCard.shouldBe(visible);
        addToCartBtn.shouldBe(visible).shouldBe(enabled).click();
        return this;
    }

    @Step("Search product by name")
    public CatalogPage searchProduct(String productName) {
        searchInput.shouldBe(visible).sendKeys(productName);
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

    // ══════════════════════════════════════════════════════
    // ПРОВЕРКИ
    // ══════════════════════════════════════════════════════

    @Step("Get product card by name")
    public SelenideElement getProductByName(String productName) {
        return products.findBy(Condition.text(productName));
    }

    @Step("Check product card: name={productName}, price={price}")
    public CatalogPage checkProductCard(String productName, String price, String imageUrl) {
        SelenideElement card = $("img[alt='" + productName + "']").closest(".rounded-xl");
        card.$("h3").shouldHave(text(productName));
        card.$("p").shouldHave(text(price));
        card.$("img[alt='" + productName + "']")
                .shouldBe(visible)
                .shouldHave(attribute("src", imageUrl));
        return this;
    }
}