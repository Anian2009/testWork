package com.example;

import com.example.model.Path4log;
import com.google.common.collect.Comparators;
import com.google.common.collect.Ordering;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SomeTest {
    private WebDriver driver;
    private StringBuilder stringBuilder;

    @BeforeClass
    public void browser() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromeDriver\\chromedriver.exe");
        driver = new ChromeDriver();
        stringBuilder = new StringBuilder(256);

        try (FileWriter fileWriter = new FileWriter(Path4log.PATH_4_LOG, false)) {
            fileWriter.write(String.valueOf(stringBuilder));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Test
    public void validateMainPage() {
        stringBuilder.append("\n\n--------------------------------------------------------------\n\n");
        stringBuilder.append("1    Open main page.\n");
        driver.navigate().to("http://prestashop-automation.qatestlab.com.ua/ru/");

        stringBuilder.append("     Main page opened. Page title is.").append(driver.getTitle()).append("\n");
        Assert.assertEquals(driver.getTitle(), "prestashop-automation", "Something is wrong");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "validateMainPage")
    public void theCorrespondenceOfTheIndicatedAndDisplayedCurrency() {
        stringBuilder.append("2    Get the set currency. ");

        WebElement setCurrency = (new WebDriverWait(driver, 10)).until(ExpectedConditions.
                presenceOfElementLocated(By.xpath("//span[@class='expand-more _gray-darker hidden-sm-down']")));
        stringBuilder.append("The set currency is - ")
                .append(setCurrency.getText().charAt(setCurrency.getText().length() - 1)).append("\n");

        stringBuilder.append("     Get a list of popular items. ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        stringBuilder.append("The list includes ").append(listOfGoods.size()).append(" items.\n");

        stringBuilder.append("     Compare the currency and currency in popular goods.\n");
        for (WebElement goods : listOfGoods) {
            Assert.assertEquals(goods.getText().charAt(goods.getText().length() - 1),
                    setCurrency.getText().charAt(setCurrency.getText().length() - 1));
        }
        stringBuilder.append("     The displayed currency corresponds to the established currency.\n");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "theCorrespondenceOfTheIndicatedAndDisplayedCurrency")
    public void setTheCurrency() {
        Actions action = new Actions(driver);
        WebElement dropDownList = driver.findElement(By.xpath("//div[@id='_desktop_currency_selector']//i"));
        stringBuilder.append("3    Сlick on the drop down list of currencies.\n");
        action.click(dropDownList).build().perform();

        WebElement dollarFromDropDownList = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title='Доллар США']")));
        stringBuilder.append("     Сhoose the currency in dollar in the drop down list.\n");
        action.click(dollarFromDropDownList).build().perform();

        stringBuilder.append("     Check that the currency has changed. Expected currency - USD $. Actual currency - ");
        WebElement newCurrency = (new WebDriverWait(driver, 10)).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//span[@class='expand-more _gray-darker hidden-sm-down']")));
        stringBuilder.append(newCurrency.getText()).append("\n");

        Assert.assertEquals(newCurrency.getText(),
                "USD $", "The currency has not changed according to the set currency");
        stringBuilder.append("     The currency changed.\n");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "setTheCurrency")
    public void searchTheDirectory() {
        stringBuilder.append("4    In the search field, enter 'dress' and send a request.\n");
        WebElement input = driver.findElement(By.xpath("//input[@name='s']"));
        input.sendKeys("dress");
        input.submit();

        stringBuilder.append("     Checked whether the request is sent to the server.\n");
        Assert.assertTrue(driver.getCurrentUrl().contains("s=dress"), "Error. The search did not take place");
        stringBuilder.append("     The request is sent to the server.\n");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    public void checkingTheSearchResult() {
        stringBuilder.append("5    Get search results - ");
        WebElement searchResult = driver.findElement(By.xpath("//*[contains(text(), 'Товаров: ')]"));
        stringBuilder.append(searchResult.getText()).append("\n");

        stringBuilder.append("     Get a list of products on the page - ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        stringBuilder.append(listOfGoods.size()).append(" goods.\n");

        stringBuilder.append("     Сompare search results with a list of goods.\n");
        Assert.assertEquals(Integer.parseInt(searchResult.getText().replaceAll("[^0-9]", "")), listOfGoods.size(),
                "Search results are not valid");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    public void theCorrespondenceOfTheIndicatedAndDisplayedCurrency2() {
        stringBuilder.append("6    Check the correspondence of the currency on the product to the established currency. " +
                "Repeat item 2.\n");
        theCorrespondenceOfTheIndicatedAndDisplayedCurrency();
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    public void sortSearchResults() {
        stringBuilder.append("7    Open the drop-down list of order sorting goods.\n");
        driver.findElement(By.xpath("//div[@id='js-product-list-top']//i")).click();

        stringBuilder.append("     Sort the order of sorting from cheap to expensive.\n");
        driver.findElement(By.xpath("//*[contains(text(), 'от низкой ')]")).click();

        stringBuilder.append("     Waiting for JQuery to finish work.\n");

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until((ExpectedCondition<Boolean>) driver -> (Boolean) ((JavascriptExecutor) driver).
                executeScript("return jQuery.active == 0"));

        stringBuilder.append("     Сheck that the order of sorting is selected.\n");
        WebElement order = driver.findElement(By.xpath("//div[@id='js-product-list-top']//a[@class='select-title']"));

        Assert.assertTrue(order.getText().contains("от низкой к "), "Sort order is not displayed correctly.");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "sortSearchResults")
    public void checkingTheOrderOfSortingGoodsInTheList() {
        stringBuilder.append("8    Get a list of sorted items. ");
        List<WebElement> listOfGoods = driver.findElements(By.xpath("//span[@class='price']"));
        stringBuilder.append("The list includes ").append(listOfGoods.size()).append(" items.\n");

        List<Double> array = new ArrayList<>(listOfGoods.size());
        for (WebElement element : listOfGoods) {
            array.add(Double.parseDouble(element.getText().replaceAll("[^0-9,]", "")
                    .replace(',', '.')));
        }

        stringBuilder.append("     Сheck whether goods in the list are in the natural order.\n");
        Assert.assertTrue(Comparators.isInOrder(array, Ordering.natural()), "Goods NOT sorted.");
        stringBuilder.append("     Goods sorted.\n");
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "sortSearchResults")
    public void searchForDiscountProducts() {
        stringBuilder.append("9    Get a list of discount products.\n     For each element of list:\n");
        List<WebElement> list = driver.findElements(By.xpath("//span[@class='discount-percentage']"));
        for (WebElement item : list) {

            stringBuilder.append("          Сhecked that the discount is indicated with '%'.\n");
            Assert.assertTrue(item.getText().contains("%"), "The discount is incorrect. Missing '%' sign");

            stringBuilder.append("          Get the right prices before and after the discount.\n");
            Double regularPrice = Double.parseDouble(item.findElement(By.xpath("preceding-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            Double price = Double.parseDouble(item.findElement(By.xpath("following-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            stringBuilder.append("          Check price before and after the discount. And their accordance.\n\n");
            Assert.assertTrue(regularPrice > price, "Reduced price above the price without a discount.\n");
        }
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @Test(dependsOnMethods = "searchTheDirectory")
    public void heckTheDiscount() {
        stringBuilder.append("10   Get a list of discount products.\n     For each element of list:\n");
        List<WebElement> list = driver.findElements(By.xpath("//span[@class='discount-percentage']"));
        for (WebElement item : list) {

            stringBuilder.append("          Get the right prices before and after the discount.\n");
            Double regularPrice = Double.parseDouble(item.findElement(By.xpath("preceding-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            Double price = Double.parseDouble(item.findElement(By.xpath("following-sibling::*"))
                    .getText().replaceAll("[^0-9,]", "").replace(',', '.'));

            stringBuilder.append("          Check the discount.\n\n");
            Integer discount = (int) Math.round(((regularPrice - price) * 100 / regularPrice));

            Assert.assertTrue(discount == Integer.parseInt(item.getText().replaceAll("[^0-9,]", "")),
                    "Reduced price above the price without a discount.\n");
        }
        stringBuilder.append("--------------------------------------------------------------\n\n");
    }

    @AfterClass
    public void cilBrowser() {
        driver.quit();

        try (FileWriter fileWriter = new FileWriter(Path4log.PATH_4_LOG, true)) {
            fileWriter.write(String.valueOf(stringBuilder));
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
