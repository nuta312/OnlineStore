package kg.benext.gui.pages;

import com.codeborne.selenide.SelenideElement;
import kg.benext.db.entity.model.Order;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class HomePage extends BasePage<HomePage>{
    private final SelenideElement catalogLink = $("a[href='/catalog']");
    private final SelenideElement favoritesLink = $("a[href='/favorites']");
    private final SelenideElement ordersLink = $("a[href='/orders']");
    private final SelenideElement profileLink = $("a[href='/profile']");
    private final SelenideElement loginBtn = $("a[href='/sign-in']");
    private final SelenideElement userProfileImg = $("img[alt='User profile']");
    private final SelenideElement subscribe = $("input[placeholder='Email address']");
    private final SelenideElement submitButton = $("form button");

    @Override
    public HomePage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

    public CatalogPage clickToCatalog() {
        catalogLink.shouldBe(visible).shouldBe(clickable).click();
        return new CatalogPage();
    }

    public Favorites clickToFavorites() {
        favoritesLink.shouldBe(visible).shouldBe(clickable).click();
        return new Favorites();
    }

    public OrdersPage clickToOrders() {
        ordersLink.shouldBe(visible).shouldBe(clickable).click();
        return new OrdersPage();
    }

    public ProfilePage clickToProfile() {
        profileLink.shouldBe(visible).click();
        return new ProfilePage();
    }

    public LoginPage clickToLogin() {
        loginBtn.shouldBe(visible).shouldBe(clickable).click();
        return new LoginPage();
    }

    public HomePage verifyProfileImageIsDisplayed() {
        userProfileImg.shouldBe(visible);
        return this;
    }

    public HomePage inputEmailToSubscribe(String email) {
        subscribe.shouldBe(visible).sendKeys(email);
        return this;
    }

    public HomePage clickToSubscribe() {
        submitButton.shouldBe(visible).click();
        return this;
    }

    public HomePage toSubscribe(String email) {
        inputEmailToSubscribe(email).clickToSubscribe();
        return this;
    }
}