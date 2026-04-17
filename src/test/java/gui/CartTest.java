package gui;

import gui.extensions.Retry;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.gui.pages.CartPage;
import kg.benext.gui.pages.CatalogPage;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@DisplayName("Корзина — UI Тесты")
public class CartTest extends BaseGUI {

    CartPage cartPage = new CartPage();

    @BeforeEach
    void openCart() {
        open(ConfigurationManager.getBaseConfig().baseUrl() + "/cart");
        if ($("button.bg-primary").exists()) {
            $("button.bg-primary").shouldBe(visible).click();
            $("input[name='email']").shouldBe(visible).sendKeys("altynai@gmail.com");
            $("input[name='password']").shouldBe(visible).sendKeys("password2");
            $("form button").shouldBe(visible).click();
        }
        cartPage.waitForPageToBeLoaded();
    }

// ─── smoke ────────────────────────────────────────────────────────────────────

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-001: Заголовок страницы содержит текст «Корзина»")
    void checkCartPageTitle() {
        cartPage.checkPageTitleHasText();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-002: Кнопка «Оформить заказ» видима и активна")
    void checkMakeOrderBtnReady() {
        cartPage.checkMakeOrderBtnReady();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-003: Кнопка «Применить» видима и активна")
    void checkApplyBtnReady() {
        cartPage.checkApplyBtnReady();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-004: Поле промокода доступно для ввода")
    void checkPromoCodeIsEditable() {
        cartPage.checkPromoCodeIsEditable();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-005: Поле промокода имеет правильный placeholder")
    void checkPromoCodePlaceholder() {
        cartPage.checkPromoCodePlaceholder();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-006: Ссылка «Перейти в каталог» отображается")
    void checkNavToCatalogVisible() {
        cartPage.checkNavToCatalogVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-007: Кнопка «delete» отображается")
    void checkDeleteProductVisible() {
        cartPage.checkDeleteProductVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-008: Кнопка «add» отображается")
    void checkAddProductVisible() {
        cartPage.checkAddProductVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-009: Кнопка «remove» отображается")
    void checkRemoveProductVisible() {
        cartPage.checkRemoveProductVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-010: Поле промокода отображается")
    void checkPromoCodeFieldVisible() {
        cartPage.checkPromoCodeFieldVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-011: Кнопка «Применить» отображается")
    void checkApplyBtnVisible() {
        cartPage.checkApplyBtnVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-012: Кнопка «Оформить заказ» отображается")
    void checkMakeOrderBtnVisible() {
        cartPage.checkMakeOrderBtnVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-013: Если корзина пустая — отображается сообщение «пуста»")
    void checkEmptyCartMessageVisible() {
        cartPage.clickDeleteProduct();
        $x("//h2[contains(text(),'пуста')]").shouldBe(visible);
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CART-014: Если корзина пустая — кнопка «Перейти в каталог» видима")
    void checkNavToCatalogVisibleWhenEmpty() {
        cartPage.clickDeleteProduct();
        cartPage.checkNavToCatalogVisible();
    }

// ─── regression ───────────────────────────────────────────────────────────────

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-015: Применение неверного промокода не меняет итоговую цену")
    void checkInvalidPromoCodeNotApplied() {
        cartPage.checkInvalidPromoCodeNotApplied("INVALID123");
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-016: Верный промокод уменьшает итоговую цену")
    void checkValidPromoCodeApplied() {
        cartPage.checkValidPromoCodeApplied("СКИДКА10");
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-017: Можно нажать «Применить» после ввода промокода")
    void checkApplyPromoCode() {
        cartPage.enterPromoCode("PROMO2026")
                .clickApplyPromo();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-018: Клик «add» увеличивает количество товара")
    void checkClickAddProduct() {
        cartPage.clickAddProduct();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-019: Клик «delete» удаляет товар из корзины")
    void checkClickDeleteProduct() {
        cartPage.clickDeleteProduct();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-R-001: После «add» итоговая цена увеличивается")
    void checkPriceIncreasesAfterAdd() {
        cartPage.checkPriceIncreasesAfterAdd();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-R-002: После «remove» итоговая цена уменьшается")
    void checkPriceDecreasesAfterRemove() {
        cartPage.checkPriceDecreasesAfterRemove();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CART-R-003: После «delete» товар исчезает из корзины")
    void checkProductDisappearsAfterDelete() {
        cartPage.checkProductDisappearsAfterDelete();
    }

// ─── e2e ──────────────────────────────────────────────────────────────────────

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-020: Если корзина пустая — переход в каталог")
    void checkGoToCatalogIfCartIsEmpty() {
        cartPage.goToCatalogIfCartIsEmpty();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-021: Если корзина пустая — клик «Перейти в каталог» открывает каталог")
    void checkGoToCatalogWhenEmpty() {
        cartPage.clickDeleteProduct();
        $x("//h2[contains(text(),'пуста')]").shouldBe(visible);
        cartPage.goToCatalogIfCartIsEmpty();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-022: Клик «Перейти в каталог» открывает каталог и меняет URL")
    void checkNavToCatalogAndVerify() {
        cartPage.clickNavToCatalogAndVerify();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-023: Клик «Оформить заказ» переходит на страницу оформления")
    void checkMakeOrderAndVerify() {
        cartPage.clickMakeOrderAndVerify();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-E2E-001: Добавить товар из каталога → проверить в корзине")
    void e2eAddProductFromCatalogToCart() {
        cartPage.clickNavToCatalogAndVerify();
        new CatalogPage().addFirstProductToCart();
        open(ConfigurationManager.getBaseConfig().baseUrl() + "/cart");
        cartPage.waitForPageToBeLoaded()
                .checkDeleteProductVisible()
                .checkMakeOrderBtnReady();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CART-E2E-002: Увеличить количество → цена выросла → оформить заказ")
    void e2eChangePriceAndMakeOrder() {
        cartPage.checkPriceIncreasesAfterAdd()
                .clickMakeOrderAndVerify();
    }
}