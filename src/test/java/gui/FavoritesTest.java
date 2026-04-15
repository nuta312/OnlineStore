package gui;

import com.codeborne.selenide.Selenide;
import kg.benext.gui.pages.Favorites;
import org.junit.jupiter.api.Test;

public class FavoritesTest {
    Favorites favorites = new Favorites();

    @Test
    void test() {
        Selenide.open("http://5.129.193.163/favorites");
        favorites.auth();
        int count = favorites.getAllItems().size();
        System.out.println("Количество элементов: " + count);
        favorites.addBucket("Nike Yoga Luxe Top");
    }
}