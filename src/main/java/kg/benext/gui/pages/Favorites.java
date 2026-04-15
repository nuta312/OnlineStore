package kg.benext.gui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class Favorites {
    SelenideElement goToCatalogBtn = $("main a[href='/catalog']");
    ElementsCollection allItems = $$("div[role='article']");

    @Step("Click catalog btn when no items")
    public Favorites clickCatalog() {
        goToCatalogBtn.shouldBe(clickable);
        goToCatalogBtn.click();
        return this;
    }

    @Step("Auth")
    public Favorites auth() {
        $("button.bg-primary").click();
        $("input[name='email']").sendKeys("amanturov2471@gmail.com");
        $("input[name='password']").sendKeys("naryn25");
        $("form button").click();
        $("div[role='article']").shouldBe(visible);
        return this;
    }

    @Step("Get all items")
    public ElementsCollection getAllItems() {
        return allItems;
    }

    @Step("Remove from favorites by name")
    public Favorites removeFromFavorites(String itemName) {
        $x("//img[@alt='" + itemName +
                "']/ancestor::div[@role='article']//button[@title='Remove from favorites']")
                .click();
        return this;
    }

    @Step("Add to cart by name")
    public Favorites addBucket(String itemName) {
        $x("//img[@alt='" + itemName +
                "']/ancestor::div[@role='article']//button[@title='Add to Cart']")
                .click();
        return this;
    }
}