package gui;

import kg.benext.gui.pages.CheckoutPage;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@DisplayName("Оформление заказа — UI Тесты")
public class CheckoutTest extends BaseGUI {

    CheckoutPage checkoutPage = new CheckoutPage();

    @BeforeEach
    void openCheckout() {
        open("http://5.129.193.163/checkout");
        if ($("button.bg-primary").exists()) {
            $("button.bg-primary").shouldBe(visible).click();
            $("input[name='email']").shouldBe(visible).sendKeys("altynai@gmail.com");
            $("input[name='password']").shouldBe(visible).sendKeys("password2");
            $("form button").shouldBe(visible).click();
        }
        checkoutPage.waitForPageToBeLoaded();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-001: Поля контактных данных отображаются")
    void checkContactFieldsVisible() {
        checkoutPage.checkContactFieldsVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-002: Кнопки выбора доставки отображаются")
    void checkDeliveryOptionsVisible() {
        checkoutPage.checkDeliveryOptionsVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-003: Поля адреса отображаются")
    void checkAddressFieldsVisible() {
        checkoutPage.checkAddressFieldsVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-004: Поля оплаты картой отображаются")
    void checkCardFieldsVisible() {
        checkoutPage.checkCardFieldsVisible();
    }

    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-005: Кнопка «Оплатить» видима и активна")
    void checkPayBtnVisible() {
        checkoutPage.checkPayBtnVisible();
    }


    @Test
    @Tag("smoke")
    @DisplayName("TC-CHECKOUT-007: Все поля доступны для ввода")
    void checkAllFieldsAreEditable() {
        checkoutPage.checkAllFieldsAreEditable();
    }


    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-008: Самовывоз — доставка бесплатная")
    void checkSelfDeliveryIsFree() {
        checkoutPage.checkSelfDeliveryIsFree();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-009: Доставка курьером — стоимость 200 KGS")
    void checkCourierDeliveryPrice() {
        checkoutPage.checkCourierDeliveryPrice();
    }

    @Test
    @Tag("regression")
    @DisplayName("TC-CHECKOUT-010: Смена способа доставки меняет итоговую сумму")
    void checkTotalChangesOnDeliverySwitch() {
        checkoutPage.checkTotalChangesOnDeliverySwitch();
    }

    @Test
    @Tag("e2e")
    @DisplayName("TC-CHECKOUT-E2E-001: Заполнить форму с самовывозом → оплатить")
    void e2ePayWithSelfDelivery() {
        checkoutPage.fillAndPayWithSelfDelivery();
    }

    @Test
    @Tag("e2e")
    @DisplayName("TC-CHECKOUT-E2E-002: Заполнить форму с курьером → оплатить")
    void e2ePayWithCourierDelivery() {
        checkoutPage.fillAndPayWithCourierDelivery();
    }
}