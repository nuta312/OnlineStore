package gui;

import com.codeborne.selenide.Selenide;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserSize;

public class BaseGUI {
    @BeforeAll
    public static void setUp() {
        browser = "chrome";
        browserSize = "1920x1080";
    }

    @BeforeEach
    void openBaseUrl() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
    }

}
