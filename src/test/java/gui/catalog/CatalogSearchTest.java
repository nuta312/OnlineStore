package gui.catalog;

import com.codeborne.selenide.Selenide;
import gui.BaseGUI;
import kg.benext.common.utils.file.ConfigurationManager;
import kg.benext.gui.pages.CatalogPage;
import kg.benext.gui.pages.HomePage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CatalogSearchTest extends BaseGUI {
    CatalogPage catalogPage = new CatalogPage();
    HomePage homePage = new HomePage();

    @SneakyThrows
    @Test
    void searchTest() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
        homePage.clickToCatalog();
        catalogPage
                .chooseAllCategories()
                .chooseCategory("Electronics")
                .chooseCategory("Automotive")
                .chooseCategory("Toys & Kids")
                .chooseCategory("Beauty & Health")
                .chooseCategory("Home & Living")
                .chooseCategory("Fashion")
                .chooseCategory("Sports & Outdoors")
                .setValues(999, 1500)
                .chooseRating(1)
                .chooseRating(2)
                .chooseRating(3)
                .chooseRating(4)
                .inputTextToSearch("Apple AirTag Car Tracker")
                .resetFilters();

    }
}
