package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class HomePage extends BasePage<HomePage>{
    private final SelenideElement catalogLink = $("a[href='/catalog']");
    private final SelenideElement loginBtn = $("a[href='/sign-in']");
    private final SelenideElement userProfileImg = $("img[alt='User profile']");




    @Override
    public HomePage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

    public HomePage clickToCatalog() {
        catalogLink.shouldBe(visible).shouldBe(clickable).click();
        return this;
    }

    public LoginPage clickToLogin() {
        loginBtn.shouldBe(visible).shouldBe(clickable).click();
        return new LoginPage();
    }

    public HomePage verifyProfileImageIsDisplayed() {
        userProfileImg.shouldBe(visible);
        return this;
    }
}