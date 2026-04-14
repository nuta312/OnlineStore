package gui;

import org.junit.jupiter.api.BeforeAll;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserSize;

public class BaseGUI {
    @BeforeAll
    public static void setUp() {
        browser = "chrome";
        browserSize = "1920x1080";
    }
}
