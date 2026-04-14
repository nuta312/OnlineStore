package kg.benext.gui.pages;

public abstract class BasePage<T extends BasePage> {
    public abstract  T waitForPageToBeLoaded();
}
