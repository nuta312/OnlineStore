package gui;

import gui.extensions.Retry;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.gui.pages.CheckoutPage;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@DisplayName("Оформление заказа — UI Тесты")
public class CheckoutTest extends BaseGUI {

    CheckoutPage checkoutPage = new CheckoutPage();

    @BeforeEach
    void openCheckout() {
        open(ConfigurationManager.getBaseConfig().baseUrl() + "/checkout");
        if ($("button.bg-primary").exists()) {
            $("button.bg-primary").shouldBe(visible).click();
            $("input[name='email']").shouldBe(visible).sendKeys("altynai@gmail.com");
            $("input[name='password']").shouldBe(visible).sendKeys("password2");
            $("form button").shouldBe(visible).click();
        }
        checkoutPage.waitForPageToBeLoaded();
    }

// ─── smoke ────────────────────────────────────────────────────────────────────

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-001: Поля контактных данных отображаются")
    void checkContactFieldsVisible() {
        checkoutPage.checkContactFieldsVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-002: Кнопки выбора доставки отображаются")
    void checkDeliveryOptionsVisible() {
        checkoutPage.checkDeliveryOptionsVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-003: Поля адреса отображаются")
    void checkAddressFieldsVisible() {
        checkoutPage.checkAddressFieldsVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-004: Поля оплаты картой отображаются")
    void checkCardFieldsVisible() {
        checkoutPage.checkCardFieldsVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-005: Кнопка «Оплатить» видима и активна")
    void checkPayBtnVisible() {
        checkoutPage.checkPayBtnVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-006: Бейдж «Безопасная оплата через SSL» отображается")
    void checkSSLBadgeVisible() {
        checkoutPage.checkSSLBadgeVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-007: Все поля доступны для ввода")
    void checkAllFieldsAreEditable() {
        checkoutPage.checkAllFieldsAreEditable();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-008: Итоговая сумма отображается")
    void checkTotalPriceVisible() {
        checkoutPage.checkTotalPriceVisible();
    }

    @Test
    @Retry(3)
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-009: Стоимость доставки отображается")
    void checkDeliveryPriceVisible() {
        checkoutPage.checkDeliveryPriceVisible();
    }

// ─── regression ───────────────────────────────────────────────────────────────

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-010: Самовывоз — доставка бесплатная")
    void checkSelfDeliveryIsFree() {
        checkoutPage.checkSelfDeliveryIsFree();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-011: Доставка курьером — стоимость 200 KGS")
    void checkCourierDeliveryPrice() {
        checkoutPage.checkCourierDeliveryPrice();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-012: Смена способа доставки меняет итоговую сумму")
    void checkTotalChangesOnDeliverySwitch() {
        checkoutPage.checkTotalChangesOnDeliverySwitch();
    }

    @Test
    @Retry(3)
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-013: Текст SSL бейджа корректный")
    void checkSSLBadgeText() {
        checkoutPage.checkSSLBadgeText();
    }

// ─── e2e ──────────────────────────────────────────────────────────────────────

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CHECKOUT-E2E-001: Заполнить форму с самовывозом → оплатить")
    void e2ePayWithSelfDelivery() {
        checkoutPage.fillAndPayWithSelfDelivery();
    }

    @Test
    @Retry(2)
    @Tag("e2e")
    @DisplayName("TC-CHECKOUT-E2E-002: Заполнить форму с курьером → оплатить")
    void e2ePayWithCourierDelivery() {
        checkoutPage.fillAndPayWithCourierDelivery();
    }
}