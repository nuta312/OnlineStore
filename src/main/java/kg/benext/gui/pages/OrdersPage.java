package kg.benext.gui.pages;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class OrdersPage extends BasePage<OrdersPage>{
    @Override
    public OrdersPage waitForPageToBeLoaded() {
        $("h1").$(byText("Мои заказы")).shouldBe(visible);
        return this;
    }
}
