package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage>{
    private final SelenideElement registerBtn = $("a[href='/register']");


    @Override
    public LoginPage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

    public RegisterPage clickToRegisterBtn() {
        registerBtn.shouldBe(visible).click();
        return new RegisterPage();
    }
}
