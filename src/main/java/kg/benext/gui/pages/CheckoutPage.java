package kg.benext.gui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class CheckoutPage extends BasePage<CheckoutPage> {

    private final SelenideElement inputFirstName     = $x("//input[@name='firstName']");
    private final SelenideElement inputLastName      = $x("//input[@name='lastName']");
    private final SelenideElement inputEmail         = $x("//input[@name='email']");
    private final SelenideElement inputPhone         = $x("//input[@name='phone']");

// способы доставки
    private final SelenideElement selfDeliveryBtn       = $x("//span[text()='Самовывоз']");
    private final SelenideElement byCourierDeliveryBtn  = $x("//span[text()='Доставка курьером']");

// адрес
    private final SelenideElement inputAddress       = $x("//input[@name='addressLine']");
    private final SelenideElement inputApartment     = $x("//input[@name='apartment']");
    private final SelenideElement inputFloor         = $x("//input[@name='floor']");
    private final SelenideElement inputEntryphone    = $x("//input[@name='intercom']");
    private final SelenideElement inputComment       = $x("//textarea[@name='comment']");

// оплата картой
    private final SelenideElement inputCardNumber    = $x("//input[@name='cardNumber']");
    private final SelenideElement inputExpirationDate = $x("//input[@name='expiration']");
    private final SelenideElement inputCvv           = $x("//input[@name='cvv']");
    private final SelenideElement inputCardUserName  = $x("//input[@name='cardName']");

    private final SelenideElement deliveryPrice      = $x("//p[contains(text(),'Доставка')]/following-sibling::p");
    private final SelenideElement totalPrice         = $x("//p[contains(text(),'Итого')]/following-sibling::p");
    private final SelenideElement toPayBtn           = $x("//button/span[contains(text(),'Оплатить')]");
    private final SelenideElement sslBadge           = $x("//p[contains(text(),'Безопасная оплата через SSL')]");

    @Override
    public CheckoutPage waitForPageToBeLoaded() {
        inputFirstName.shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    public CheckoutPage checkContactFieldsVisible() {
        inputFirstName.shouldBe(visible);
        inputLastName.shouldBe(visible);
        inputEmail.shouldBe(visible);
        inputPhone.shouldBe(visible);
        return this;
    }

    public CheckoutPage checkDeliveryOptionsVisible() {
        selfDeliveryBtn.shouldBe(visible);
        byCourierDeliveryBtn.shouldBe(visible);
        return this;
    }

    public CheckoutPage checkAddressFieldsVisible() {
        inputAddress.shouldBe(visible);
        inputApartment.shouldBe(visible);
        inputFloor.shouldBe(visible);
        inputEntryphone.shouldBe(visible);
        inputComment.shouldBe(visible);
        return this;
    }

    public CheckoutPage checkCardFieldsVisible() {
        inputCardNumber.shouldBe(visible);
        inputExpirationDate.shouldBe(visible);
        inputCvv.shouldBe(visible);
        inputCardUserName.shouldBe(visible);
        return this;
    }

    public CheckoutPage checkPayBtnVisible() {
        toPayBtn.shouldBe(visible).shouldBe(enabled);
        return this;
    }


    public CheckoutPage checkSelfDeliveryIsFree() {
        selfDeliveryBtn.shouldBe(visible).click();
        deliveryPrice.shouldHave(text("0"));
        return this;
    }

    public CheckoutPage checkCourierDeliveryPrice() {
        byCourierDeliveryBtn.shouldBe(visible).click();
        deliveryPrice.shouldHave(text("200"));
        return this;
    }

    // Смена доставки меняет итоговую сумму
    public CheckoutPage checkTotalChangesOnDeliverySwitch() {
        // Выбираем самовывоз — запоминаем итого
        selfDeliveryBtn.shouldBe(visible).click();
        String totalWithSelfDelivery = totalPrice.shouldBe(visible).getText();
        // Переключаем на курьера — итого должно вырасти
        byCourierDeliveryBtn.shouldBe(visible).click();
        totalPrice.shouldNotHave(text(totalWithSelfDelivery));
        return this;
    }

    // Поля доступны для ввода
    public CheckoutPage checkAllFieldsAreEditable() {
        inputFirstName.shouldBe(visible).shouldNotBe(disabled).shouldNotBe(readonly);
        inputLastName.shouldBe(visible).shouldNotBe(disabled).shouldNotBe(readonly);
        inputEmail.shouldBe(visible).shouldNotBe(disabled).shouldNotBe(readonly);
        inputPhone.shouldBe(visible).shouldNotBe(disabled).shouldNotBe(readonly);
        inputCardNumber.shouldBe(visible).shouldNotBe(disabled).shouldNotBe(readonly);
        return this;
    }


    public CheckoutPage fillContactInfo(String firstName, String lastName,
                                        String email, String phone) {
        inputFirstName.shouldBe(visible).setValue(firstName);
        inputLastName.shouldBe(visible).setValue(lastName);
        inputEmail.shouldBe(visible).setValue(email);
        inputPhone.shouldBe(visible).setValue(phone);
        return this;
    }

    public CheckoutPage selectSelfDelivery() {
        selfDeliveryBtn.shouldBe(visible).click();
        return this;
    }

    public CheckoutPage selectCourierDelivery() {
        byCourierDeliveryBtn.shouldBe(visible).click();
        return this;
    }

    public CheckoutPage fillAddress(String address) {
        inputAddress.shouldBe(visible).setValue(address);
        return this;
    }

    public CheckoutPage fillCardInfo(String cardNumber, String expiration,
                                     String cvv, String cardName) {
        inputCardNumber.shouldBe(visible).setValue(cardNumber);
        inputExpirationDate.shouldBe(visible).setValue(expiration);
        inputCvv.shouldBe(visible).setValue(cvv);
        inputCardUserName.shouldBe(visible).setValue(cardName);
        return this;
    }

    public CheckoutPage clickPayBtn() {
        toPayBtn.shouldBe(visible).shouldBe(enabled).click();
        return this;
    }

    public CheckoutPage fillAndPayWithSelfDelivery() {
        fillContactInfo("Иван", "Иванов",
                "test@test.com", "+996700000000");
        selectSelfDelivery();
        fillCardInfo("4111111111111111",
                "12/26", "123", "IVAN IVANOV");
        clickPayBtn();
        return this;
    }

    public CheckoutPage fillAndPayWithCourierDelivery() {
        fillContactInfo("Иван", "Иванов",
                "test@test.com", "+996700000000");
        selectCourierDelivery();
        fillAddress("ул. Манаса 45");
        fillCardInfo("4111111111111111",
                "12/26", "123", "IVAN IVANOV");
        clickPayBtn();
        return this;
    }
}