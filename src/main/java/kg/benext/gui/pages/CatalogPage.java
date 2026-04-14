package kg.benext.gui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CatalogPage extends BasePage<CatalogPage> {
    SelenideElement searchInput = $("[placeholder='Search products...']");
    SelenideElement allCategories = $(byText("All Categories"));
    SelenideElement minInput = $("[placeholder='Min']");
    SelenideElement maxInput = $("[placeholder='Max']");
    SelenideElement resetFiltersButton = $(byText("Reset Filters"));

    ElementsCollection ratingFilters = $$("button").filterBy(text("Up"));
    SelenideElement fourStarsAndUp = ratingFilters.get(0);
    SelenideElement threeStarsAndUp = ratingFilters.get(1);
    SelenideElement twoStarsAndUp = ratingFilters.get(2);
    SelenideElement oneStarAndUp = ratingFilters.get(3);

    ElementsCollection products = $$("[role='article']");

    public SelenideElement category(String name) {
        return $(byText(name));
    }

    public SelenideElement dropdown(String name) {
        return category(name).closest("div").$("[aria-expanded]");
    }

    public SelenideElement subcategory(String category, String sub) {
        return dropdown(category)
                .parent()
                .$("[role='region']")
                .$(byText(sub));
    }


    @Override
    public CatalogPage waitForPageToBeLoaded() {
        $("header").$(byText("be-next")).shouldBe(visible);
        return this;
    }

    public CatalogPage inputTextToSearch(String text) {
        searchInput.shouldBe(visible).setValue(text);
        return this;
    }

    public CatalogPage chooseAllCategories() {
        allCategories.shouldBe(visible).shouldBe(clickable).click();
        return this;
    }

    public CatalogPage chooseCategory(String category) {
        category(category).shouldBe(clickable).click();
        return this;
    }

    public CatalogPage chooseSubCategory(String category, String sub) {
        subcategory(category, sub).shouldBe(clickable).click();
        return this;
    }

    public CatalogPage setMinValue(int minValue) {
        minInput.clear();
        minInput.setValue(String.valueOf(minValue));
        return this;
    }

    public CatalogPage setMaxValue(int maxValue) {
        maxInput.clear();
        maxInput.setValue(String.valueOf(maxValue));
        return this;
    }

    public CatalogPage setValues(int minValue, int maxValue) {
        setMinValue(minValue).setMaxValue(maxValue);
        return this;
    }

    public CatalogPage chooseRating(int stars) {
        switch (stars) {
            case 4:
                fourStarsAndUp.click();
                break;
            case 3:
                threeStarsAndUp.click();
                break;
            case 2:
                twoStarsAndUp.click();
                break;
            case 1:
                oneStarAndUp.click();
                break;
        }
        return this;
    }

    public CatalogPage resetFilters() {
        resetFiltersButton.shouldBe(clickable).click();
        return this;
    }

//    public CatalogPage addProductToCart(String product) {
//
//    }
}
