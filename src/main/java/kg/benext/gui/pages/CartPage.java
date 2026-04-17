package kg.benext.gui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import kg.benext.common.utils.file.ConfigurationManager;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class CartPage extends BasePage<CartPage> {

    private final SelenideElement pageTitle      = $x("//h1[contains(text(),'Корзина')]");
    private final SelenideElement emptyCartMsg   = $x("//h2[contains(text(),'пуста')]");
    private final SelenideElement navToCatalog   = $x("//span[text()='Перейти в каталог']");
    private final SelenideElement deleteProduct  = $x("//span[text()='delete']");
    private final SelenideElement addProduct     = $x("//span[text()='add']");
    private final SelenideElement removeProduct  = $x("//span[text()='remove']");
    private final SelenideElement inputPromoCode = $x("//input[@placeholder='Промокод']");
    private final SelenideElement applyBtn       = $x("//button[text()='Применить']");
    private final SelenideElement makeOrderBtn   = $x("//span[text()='Оформить заказ']");
    private final SelenideElement totalPrice     = $x("//p[contains(text(),'Итого')]/following-sibling::p");

    @Override
    public CartPage waitForPageToBeLoaded() {
        pageTitle.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    // smoke
    public CartPage checkPageTitleHasText() {
        pageTitle.shouldHave(text("Корзина"));
        return this;
    }

    public CartPage checkMakeOrderBtnReady() {
        makeOrderBtn.shouldBe(visible).shouldBe(enabled);
        return this;
    }

    public CartPage checkApplyBtnReady() {
        applyBtn.shouldBe(visible).shouldBe(enabled);
        return this;
    }

    public CartPage checkPromoCodeIsEditable() {
        inputPromoCode.shouldBe(visible)
                .shouldNotBe(disabled)
                .shouldNotBe(readonly);
        return this;
    }

    public CartPage checkPromoCodePlaceholder() {
        inputPromoCode.shouldHave(attribute("placeholder", "Промокод"));
        return this;
    }

    public CartPage checkNavToCatalogVisible() {
        navToCatalog.shouldBe(visible);
        return this;
    }

    public CartPage checkDeleteProductVisible() {
        deleteProduct.shouldBe(visible);
        return this;
    }

    public CartPage checkAddProductVisible() {
        addProduct.shouldBe(visible);
        return this;
    }

    public CartPage checkRemoveProductVisible() {
        removeProduct.shouldBe(visible);
        return this;
    }

    public CartPage checkPromoCodeFieldVisible() {
        inputPromoCode.shouldBe(visible);
        return this;
    }

    public CartPage checkApplyBtnVisible() {
        applyBtn.shouldBe(visible);
        return this;
    }

    public CartPage checkMakeOrderBtnVisible() {
        makeOrderBtn.shouldBe(visible);
        return this;
    }

    // regression
    public CartPage checkInvalidPromoCodeNotApplied(String invalidCode) {
        String priceBefore = totalPrice.shouldBe(visible).getText();
        inputPromoCode.shouldBe(visible).setValue(invalidCode);
        applyBtn.shouldBe(visible).shouldBe(enabled).click();
        totalPrice.shouldHave(text(priceBefore));
        return this;
    }

    public CartPage checkValidPromoCodeApplied(String validCode) {
        String priceBefore = totalPrice.shouldBe(visible).getText();
        inputPromoCode.shouldBe(visible).setValue(validCode);
        applyBtn.shouldBe(visible).shouldBe(enabled).click();
        String priceAfter = totalPrice.shouldBe(visible).getText();
        if (priceAfter.equals(priceBefore)) {
            throw new AssertionError(
                    "Скидка не применилась! Цена до: " + priceBefore + " Цена после: " + priceAfter
            );
        }
        return this;
    }

    public CartPage checkPriceIncreasesAfterAdd() {
        String priceBefore = totalPrice.shouldBe(visible).getText();
        addProduct.shouldBe(visible).click();
        totalPrice.shouldNotHave(text(priceBefore));
        return this;
    }

    public CartPage checkPriceDecreasesAfterRemove() {
        String priceBefore = totalPrice.shouldBe(visible).getText();
        addProduct.shouldBe(visible).click();
        removeProduct.shouldBe(visible).click();
        totalPrice.shouldHave(text(priceBefore));
        return this;
    }

    public CartPage checkProductDisappearsAfterDelete() {
        deleteProduct.shouldBe(visible).click();
        emptyCartMsg.shouldBe(visible);
        return this;
    }


    public CartPage enterPromoCode(String code) {
        inputPromoCode.shouldBe(visible).setValue(code);
        return this;
    }

    public CartPage clickApplyPromo() {
        applyBtn.shouldBe(visible).shouldBe(enabled).click();
        return this;
    }

    public CartPage clickDeleteProduct() {
        deleteProduct.shouldBe(visible).click();
        return this;
    }

    public CartPage clickAddProduct() {
        if (emptyCartMsg.exists()) {
            $x("//a[text()='Каталог']").shouldBe(visible).click();
            new CatalogPage().addFirstProductToCart();
            open(ConfigurationManager.getBaseConfig().baseUrl() + "/cart");
            pageTitle.shouldBe(visible, Duration.ofSeconds(10));
        }
        addProduct.shouldBe(visible, Duration.ofSeconds(10))
                .shouldBe(enabled)
                .click();
        return this;
    }

    // e2e
    public CartPage goToCatalogIfCartIsEmpty() {
        if (emptyCartMsg.exists()) {
            navToCatalog.shouldBe(visible).shouldBe(enabled).click();
            Selenide.webdriver().shouldHave(urlContaining("/catalog"), Duration.ofSeconds(10));
        }
        return this;
    }

    public CartPage clickNavToCatalogAndVerify() {
        if (emptyCartMsg.exists()) {
            navToCatalog.shouldBe(visible).shouldBe(enabled).click();
        } else {
            $x("//a[text()='Каталог']").shouldBe(visible).shouldBe(enabled).click();
        }
        Selenide.webdriver().shouldHave(urlContaining("/catalog"), Duration.ofSeconds(10));
        return this;
    }

    public CartPage clickMakeOrderAndVerify() {
        if (emptyCartMsg.exists()) {
            navToCatalog.shouldBe(visible).shouldBe(enabled).click();
            Selenide.webdriver().shouldHave(urlContaining("/catalog"), Duration.ofSeconds(10));
            new CatalogPage().addFirstProductToCart();
            open(ConfigurationManager.getBaseConfig().baseUrl() + "/cart");
            pageTitle.shouldBe(visible, Duration.ofSeconds(10));
        }
        makeOrderBtn.shouldBe(visible).shouldBe(enabled).click();
        Selenide.webdriver().shouldHave(urlContaining("/checkout"), Duration.ofSeconds(10));
        return this;
    }
}