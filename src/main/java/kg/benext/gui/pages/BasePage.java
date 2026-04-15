package kg.benext.gui.pages;

public abstract class BasePage<T extends BasePage<T>> {
    public abstract T waitForPageToBeLoaded();
}