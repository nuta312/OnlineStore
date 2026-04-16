package kg.benext.gui.pages;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class ProfilePage extends BasePage<ProfilePage>{
    @Override
    public ProfilePage waitForPageToBeLoaded() {
        $("h2").$(byText("Личные данные")).shouldBe(visible);
        return this;
    }
}
