package gui;

import com.codeborne.selenide.Selenide;
import gui.extensions.AllureUIExtension;
import gui.extensions.RetryExtension;
import kg.benext.common.utils.file.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

// AllureUIExtension — скриншот при падении + network логи + app логи
// RetryExtension    — автоматический повтор теста если упал (задаётся @Retry)
@ExtendWith({AllureUIExtension.class, RetryExtension.class})
public class BaseGUI {

    @BeforeEach
    void openBaseUrl() {
        Selenide.open(ConfigurationManager.getBaseConfig().baseUrl());
    }
}