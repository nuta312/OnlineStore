package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage extends BasePage<RegisterPage>{
    private final SelenideElement fullName = $("input[name='fullName']");
    private final SelenideElement email = $("input[name='email']");
    private final SelenideElement password = $("input[name='password']");
    private final SelenideElement confirmPassword = $("input[name='confirmPassword']");
    private final SelenideElement terms = $("#terms");
    private final SelenideElement createAccount = $x("//button[text()='Create Account']");

    @Override
    public RegisterPage waitForPageToBeLoaded() {
        $("h1").shouldHave(exactText("Create Account"));
        return this;
    }

    public RegisterPage inputFullName(String fullName) {
        this.fullName.shouldBe(visible).sendKeys(fullName);
        return this;
    }

    public RegisterPage inputEmail(String email) {
        this.email.shouldBe(visible).sendKeys(email);
        return this;
    }

    public RegisterPage inputPassword(String password) {
        this.password.shouldBe(visible).sendKeys(password);
        return this;
    }

    public RegisterPage inputConfirmPassword(String confirmPassword) {
        this.confirmPassword.shouldBe(visible).sendKeys(confirmPassword);
        return this;
    }

    public RegisterPage acceptTerms() {
        terms.shouldBe(visible).click();
        return this;
    }

    public HomePage clickToCreateAccountBtn() {
        createAccount.shouldBe(visible).click();
        return new HomePage();
    }


}