package gui.catalog;

import gui.BaseGUI;
import kg.benext.common.model.User;
import kg.benext.common.utils.data.RandomData;
import kg.benext.gui.pages.HomePage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CatalogTest extends BaseGUI {
    @Test
    @DisplayName("Search and filter test")
    void searchAndFilterTest() {
        new HomePage()
                .clickToCatalog().waitForPageToBeLoaded()
                .searchProduct("iPhone 14")
                .selectCategory("Electronics")
                .setMinPrice("200")
                .setMaxPrice("1000")
                .selectRatingByStars(4);
    }

    @Test
    @DisplayName("Search and add product by name to basket test")
    void addProductToCartTest() {
        User user = RandomData.defaultUser();
        new HomePage()
                .clickToLogin().waitForPageToBeLoaded()
                .inputEmail(user.getEmail())
                .inputPassword(user.getPassword())
                .clickToSignIn()
                .clickToCatalog().waitForPageToBeLoaded()
                .addProductToFavorites("iPhone 14")
                .addProductToCart("iPhone 14");
    }

    @Test
    @DisplayName("Test to click next page button")
    void catalogNextPageTest() {
        new HomePage()
                .clickToCatalog().waitForPageToBeLoaded()
                .clickToSortDropDown()
                .selectSortOption("Price: Low to High")
                .clickToSortDropDown()
                .selectSortOption("Top Rated")
                .clickToSortDropDown()
                .selectSortOption("Newest")
                .clickToSortDropDown()
                .selectSortOption("Price: High to Low")
                .clickToNextBtn();
    }
}