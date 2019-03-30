package com.example;

import com.example.model.WriteLog;
import com.google.common.collect.Comparators;
import com.google.common.collect.Ordering;
import io.qameta.allure.junit4.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class SomeTest extends WriteLog {
    private WebDriver driver;


    @BeforeClass
    public void browser() {
        System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromeDriver\\chromedriver.exe");
        driver = new ChromeDriver();



        driver.navigate().to("http://prestashop-automation.qatestlab.com.ua/ru/");
    }

    @Test
    @DisplayName("Open a test page.")
    public void validateMainPage() {
        log("1    Open main page.\n");

        log("     Main page opened. Page title is."+driver.getTitle()+"\n");
        Assert.assertEquals(driver.getTitle(), "prestashop-automation", "Something is wrong");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "validateMainPage")
    @DisplayName("Сhecking the correctness of displaying prices for products with the specified price.")
    public void theCorrespondenceOfTheIndicatedAndDisplayedCurrency() {
        log("2    Get the set currency. ");

        WebElement setCurrency = (new WebDriverWait(driver, 10)).until(ExpectedConditions.
                presenceOfElementLocated(By.xpath("//span[@class='expand-more _gray-darker hidden-sm-down']")));
        log("The set currency is - "+
                +setCurrency.getText().charAt(setCurrency.getText().length() - 1)+"\n");

        log("     Get a list of popular items. ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        log("The list includes "+listOfGoods.size()+" items.\n");

        log("     Compare the currency and currency in popular goods.\n");
        for (WebElement goods : listOfGoods) {
            Assert.assertEquals(goods.getText().charAt(goods.getText().length() - 1),
                    setCurrency.getText().charAt(setCurrency.getText().length() - 1));
        }
        log("     The displayed currency corresponds to the established currency.\n");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "theCorrespondenceOfTheIndicatedAndDisplayedCurrency")
    @DisplayName("Change the type of currency.")
    public void setTheCurrency() {
        Actions action = new Actions(driver);
        WebElement dropDownList = driver.findElement(By.xpath("//div[@id='_desktop_currency_selector']//i"));
        log("3    Сlick on the drop down list of currencies.\n");
        action.click(dropDownList).build().perform();

        WebElement dollarFromDropDownList = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Доллар США']")));
        log("     Сhoose the currency in dollar in the drop down list.\n");
        action.click(dollarFromDropDownList).build().perform();

        log("     Check that the currency has changed. Expected currency - USD $. Actual currency - ");
        WebElement newCurrency = (new WebDriverWait(driver, 10)).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//span[@class='expand-more _gray-darker hidden-sm-down']")));
        log(newCurrency.getText()+"\n");

        Assert.assertEquals(newCurrency.getText(),
                "USD $", "The currency has not changed according to the set currency");
        log("     The currency changed.\n");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "setTheCurrency")
    @DisplayName("Search for goods in the catalog.")
    public void searchTheDirectory() {
        log("4    In the search field, enter 'dress' and send a request.\n");
        WebElement input = driver.findElement(By.xpath("//input[@name='s']"));
        input.sendKeys("dress");
        input.submit();

        log("     Checked whether the request is sent to the server.\n");
        Assert.assertTrue(driver.getCurrentUrl().contains("s=dress"), "Error. The search did not take place");
        log("     The request is sent to the server.\n");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    @DisplayName("Checking the number of items found matches the search resultsg.")
    public void checkingTheSearchResult() {
        log("5    Get search results - ");
        WebElement searchResult = driver.findElement(By.xpath("//*[contains(text(), 'Товаров: ')]"));
        log(searchResult.getText()+"\n");

        log("     Get a list of products on the page - ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        log(listOfGoods.size()+" goods.\n");

        log("     Сompare search results with a list of goods.\n");
        Assert.assertEquals(Integer.parseInt(searchResult.getText().replaceAll("[^0-9]", "")), listOfGoods.size(),
                "Search results are not valid");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    @DisplayName("Check that the currency on the price tags is in dollars.")
    public void theCorrespondenceOfTheIndicatedAndDisplayedCurrency2() {
        log("6    Check the correspondence of the currency on the product to the established currency. " +
                "Repeat item 2.\n");
        theCorrespondenceOfTheIndicatedAndDisplayedCurrency();
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    @DisplayName("Modify sort order.")
    public void sortSearchResults() {
        log("7    Open the drop-down list of order sorting goods.\n");
        driver.findElement(By.xpath("//div[@id='js-product-list-top']//i")).click();

        log("     Sort the order of sorting from cheap to expensive.\n");
        driver.findElement(By.xpath("//*[contains(text(), 'от низкой ')]")).click();

        log("     Waiting for JQuery to finish work.\n");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) driver -> (Boolean) ((JavascriptExecutor) driver).
                executeScript("return jQuery.active == 0"));

        log("     Сheck that the order of sorting is selected.\n");
        WebElement order = driver.findElement(By.xpath("//div[@id='js-product-list-top']//a[@class='select-title']"));

        Assert.assertTrue(order.getText().contains("от низкой к "), "Sort order is not displayed correctly.");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "sortSearchResults")
    @DisplayName("Check that the goods are sorted correctly.")
    public void checkingTheOrderOfSortingGoodsInTheList() {
        log("8    Get a list of sorted items. ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        log("The list includes "+listOfGoods.size()+" items.\n");

        List<Double> array = new ArrayList<>(listOfGoods.size());
        for (WebElement element : listOfGoods) {
            array.add(Double.parseDouble(element.getText().replaceAll("[^0-9,]", "")
                    .replace(',', '.')));
        }

        log("     Сheck whether goods in the list are in the natural order.\n");
        Assert.assertTrue(Comparators.isInOrder(array, Ordering.natural()), "Goods NOT sorted.");
        log("     Goods sorted.\n");
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "sortSearchResults")
    @DisplayName("Check that the discount product contains the old price, new price and discount in %")
    public void searchForDiscountProducts() {
        log("9    Get a list of discount products.\n     For each element of list:\n");
        List<WebElement> list = driver.findElements(By.xpath("//span[@class='discount-percentage']"));
        for (WebElement item : list) {

            log("          Сhecked that the discount is indicated with '%'.\n");
            Assert.assertTrue(item.getText().contains("%"), "The discount is incorrect. Missing '%' sign");

            log("          Get the right prices before and after the discount.\n");
            Double regularPrice = Double.parseDouble(item.findElement(By.xpath("preceding-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            Double price = Double.parseDouble(item.findElement(By.xpath("following-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            log("          Check price before and after the discount. And their accordance.\n\n");
            Assert.assertTrue(regularPrice > price, "Reduced price above the price without a discount.\n");
        }
        log("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    @DisplayName("Check that the discount is correct.")
    public void heckTheDiscount() {
        log("10   Get a list of discount products.\n     For each element of list:\n");
        List<WebElement> list = driver.findElements(By.xpath("//span[@class='discount-percentage']"));
        for (WebElement item : list) {

            log("          Get the right prices before and after the discount.\n");
            Double regularPrice = Double.parseDouble(item.findElement(By.xpath("preceding-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            Double price = Double.parseDouble(item.findElement(By.xpath("following-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            log("          Check the discount.\n\n");
            int discount = (int) Math.round(((regularPrice - price) * 100 / regularPrice));

            Assert.assertTrue(discount == Integer.parseInt(item.getText().replaceAll("[^0-9,]", "")),
                    "Reduced price above the price without a discount.\n");
        }
        log("--------------------------------------------------------------\n\n");
    }

    @AfterClass
    public void cilBrowser() {
        driver.quit();
        inFile();
    }
}