import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.List;


public class FirstTest {

    private AppiumDriver driver;

    @Before
    public void setUp() throws Exception
    {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName","Android");
        capabilities.setCapability("deviceName","AndroidTestDevice");
        capabilities.setCapability("platformVersion","10");
        capabilities.setCapability("automationName","Appium");
        capabilities.setCapability("appPackage","org.wikipedia");
        capabilities.setCapability("appActivity",".main.MainActivity");
        capabilities.setCapability("app","D:/Tima/Обучение/org.wikipedia.apk");
        capabilities.setCapability("deviceOrientation", "PORTRAIT");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @After
    public void tearDown()
    {

        driver.quit();

    }




    @Test
    public void testChangeScreenOrientationOnSearch() {
        waitForElementAndClick(
                By.xpath("//*[contains(@text,'Search Wikipedia')]"),
                "Cannot find Search Wikipedia input",
                5
        );
        String search_line = "Java";

        waitForElementAndSendKeys(
                By.id( "org.wikipedia:id/search_src_text"),
                search_line,
                "Cannot find Search Wikipedia input",
                5
        );
        waitForElementAndClick(
                By.xpath("//*[@resource-id='org.wikipedia:id/page_list_item_container']//*[@text='Object-oriented programming language']"),
                "Cannot find 'Object-oriented programmig language' topic searching by" + search_line,
                20
        );
        String  title_befor_rotation = waitForElementAndGetAttibute(
                By.id("org.wikipedia:id/view_page_title_text"),
                "text",
                "Cannot find of article",
                20
        );
        driver.rotate(ScreenOrientation.LANDSCAPE);
        String  title_after_rotation = waitForElementAndGetAttibute(
                By.id("org.wikipedia:id/view_page_title_text"),
                "text",
                "Cannot find of article",
                20
        );
        Assert.assertEquals(
                "Article title have been changed after rotation",
                title_befor_rotation,
                title_after_rotation
        );
        driver.rotate(ScreenOrientation.PORTRAIT);
        String  title_after_second_rotation = waitForElementAndGetAttibute(
                By.id("org.wikipedia:id/view_page_title_text"),
                "text",
                "Cannot find of article",
                20
        );
        Assert.assertEquals(
                "Article title have been changed after rotation",
                title_befor_rotation,
                title_after_second_rotation
        );

    }


    private List<WebElement> waitForAllElementsPresent(By by, String error_message, long timeoutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.withMessage(error_message + "\n");

        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(by)
        );
    }
    private WebElement assertElementHasText(By by, String value, String error_message)
    {
        WebElement element = waitForElementPresent(by,"Cannot find element");
        String article_title = element.getAttribute("text");
        Assert.assertEquals(error_message,value,article_title);
        return element;
    }

    private WebElement waitForElementPresent(By by, String error_message, long timeoutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.withMessage(error_message + "\n");

        return wait.until(
                ExpectedConditions.presenceOfElementLocated(by)
        );
    }

    private WebElement waitForElementPresent(By by, String error_message)
    {
         return  waitForElementPresent(by,error_message,5);
    }

    private WebElement waitForElementAndClick(By by, String error_message, long timeoutInSeconds)
    {
       WebElement element = waitForElementPresent(by,error_message,timeoutInSeconds);
       element.click();
       return element;
    }
    private WebElement waitForElementAndSendKeys(By by, String value, String error_message, long timeoutInSeconds)
    {
        WebElement element = waitForElementPresent(by,error_message,timeoutInSeconds);
        element.sendKeys(value);
        return element;
    }


    private boolean waitForElementNotPresent (By by, String error_message, long timeoutInSeconds){
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.withMessage(error_message + "\n");
        return wait.until(
                ExpectedConditions.invisibilityOfElementLocated(by)
        );
    }

    private WebElement waitForElementAndClear(By by, String error_message, long timeoutInSeconds)
    {
        WebElement element = waitForElementPresent(by,error_message,timeoutInSeconds);
        element.clear();
        return element;
    }

    protected void swipeUp(int timeOfSwipe){
        TouchAction action = new TouchAction(driver);
        Dimension size = driver.manage().window().getSize();
        int x = size.width/2;
        int start_y = (int) (size.height * 0.8);
        int end_y = (int) (size.height * 0.2);

        action
                .press(x, start_y)
                .waitAction(timeOfSwipe)
                .moveTo(x, end_y)
                .release()
                .perform();
    }
    protected void swipeUpQuick(){
        swipeUp(200);
    }
    protected void swipeUpToFindElement(By by, String error_mesage, int max_swipes){

        int already_swipesd=0;
        while (driver.findElements(by).size()==0){
            if (already_swipesd>max_swipes){
                waitForElementPresent(by, "Cannot find element by swiping up. \n"+ error_mesage, 0);
                return;
            }
            swipeUpQuick();
            ++already_swipesd;
        }
    }
    protected void swipeElementToLeft(By by, String error_massage){
        WebElement element = waitForElementPresent(
                by,
                error_massage,
                10);
        int left_x = element.getLocation().getX();
        int rigth_x = left_x + element.getSize().getWidth();
        int upper_y = element.getLocation().getY();
        int lower_y = element.getSize().getHeight()+upper_y;
        int middle_y = (upper_y +lower_y)/2;


        TouchAction action = new TouchAction(driver);
        action
                .press(rigth_x,middle_y)
                .waitAction(150)
                .moveTo(left_x,middle_y)
                .release()
                .perform();
    }

    private  int getAmountOfElements(By by)
    {
        List elements = driver.findElements(by);
        return  elements.size();
    }

    private  void  assertElementNotPresent(By by, String error_message)
    {
        int amount_of_elements = getAmountOfElements(by);
        if (amount_of_elements >0){
            String default_message = "An element" + by.toString() + "supposed to be not present";
            throw new AssertionError(default_message+ " "+ error_message);
        }
    }

    private  String waitForElementAndGetAttibute(By by,String atteibute, String error_message, long timeoutInSeconds){
         WebElement element=  waitForElementPresent(by, error_message, timeoutInSeconds);
         return element.getAttribute(atteibute);
    }
}
