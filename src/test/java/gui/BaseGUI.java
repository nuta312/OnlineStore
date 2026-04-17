package gui;

import com.codeborne.selenide.Selenide;
import gui.extensions.AllureUIExtension;
import gui.extensions.RetryExtension;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AllureUIExtension.class)
@ExtendWith(RetryExtension.class)
public class BaseGUI {

    @BeforeEach
    void openBaseUrl() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
    }
}