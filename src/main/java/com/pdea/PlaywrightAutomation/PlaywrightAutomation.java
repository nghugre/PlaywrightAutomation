package com.pdea.PlaywrightAutomation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.SelectOption;

public class PlaywrightAutomation {

    Playwright playwright;
    Browser browser;
    String caps;
    String cdpUrl;
    Page page;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setup() throws UnsupportedEncodingException {
        JsonObject capabilities = new JsonObject();
        JsonObject ltOptions = new JsonObject();
        String user = "nitin_ghugare";
        String accessKey = "GB0X4pJZeWJGoh8vnRcG7vdTDbe02AapWYBOS50udqFCPIsjKp";

        ltOptions.addProperty("name", "Playwright Automation");
        ltOptions.addProperty("build", "Playwright Exam Assignment");
        ltOptions.addProperty("user", user);
        ltOptions.addProperty("accessKey", accessKey);
        ltOptions.addProperty("visual", true);
        ltOptions.addProperty("video", true);
        ltOptions.addProperty("platformName", "Windows 10");  
        ltOptions.addProperty("browserName", "chrome"); 
        ltOptions.addProperty("browserVersion", "latest"); 
        
        capabilities.add("LT:Options", ltOptions);

        playwright = Playwright.create();
        BrowserType chromium = playwright.chromium();
        // Launch the browser
        caps = URLEncoder.encode(capabilities.toString(), "utf-8");
        cdpUrl = "wss://cdp.lambdatest.com/playwright?capabilities=" + caps;
        browser = playwright.chromium().connect(cdpUrl);

        page = browser.newPage();
    }

    @Test(priority = 1)
    public void test_scenario1() throws Exception {
        page.navigate("https://www.lambdatest.com/selenium-playground");
        page.click("//*[text()='Simple Form Demo']");

        softAssert.assertTrue(page.url().contains("simple-form-demo"),
                "Failed : The page URL does not contain 'simple-form-demo'");

        String message = "Welcome to LambdaTest";

        page.fill("#user-message", message);

        page.click("//*[@id='showInput']");

        String displayedMessage = page.innerText("#message");

        softAssert.assertEquals(displayedMessage, message,
                "Failed : The message displayed does not match with the one that was entered");
        System.out.println(displayedMessage);
        softAssert.assertAll();
    }

    @Test(priority = 2)
    public void test_scenario2() throws Exception {
        page.navigate("https://www.lambdatest.com/selenium-playground");

        page.waitForSelector("text=Drag & Drop Sliders");

        page.click("text=Drag & Drop Sliders");

        Locator slider = page.locator("//input[@value=15]");
        BoundingBox initialBBox = slider.boundingBox();

        // Dragging the slider to 95
        slider.hover();
        page.mouse().move(initialBBox.x + initialBBox.width / 2, initialBBox.y + initialBBox.height / 2); // Hover at
        page.mouse().down();
        page.mouse().move(initialBBox.x + 465, initialBBox.y); 
        page.mouse().up();

        Locator outputElement = page.locator("#rangeSuccess");

        String outputText = outputElement.textContent();

        softAssert.assertEquals(outputText, "95", "Slider value does not equal 95");
        softAssert.assertAll();
    }

    @Test(priority = 3)
    public void test_scenario3() throws Exception {
        page.navigate("https://www.lambdatest.com/selenium-playground");

        page.locator("text=Input Form Submit").click();

        Locator submitBtn = page.locator("//button[text()='Submit']");
        submitBtn.click();

        String validationMsg = page.locator("//input[@name='name']").getAttribute("validationMessage");
        String expectedErrorMsg = "Please fill out this field.";
        softAssert.assertEquals(validationMsg, expectedErrorMsg, "Error message not as expected");

        page.fill("#name", "Nitin Ghugare");

        page.fill("//input[@placeholder='Email']", "nitinghugare@gmail.com");

        page.fill("//input[@placeholder='Password']", "Mumbai@23");

        page.fill("//input[@placeholder='Company']", "Persistent Systems");

        page.fill("#websitename", "www.persistent.com");

        page.selectOption("select[name='country']", new SelectOption().setLabel("United States"));

        page.fill("#inputCity", "New York City");

        page.fill("#inputAddress1", "Test road");

        page.fill("#inputAddress2", "Test city");

        page.fill("//input[@placeholder='State']", "Texas");

        page.fill("//input[@name='zip']", "213067");

        submitBtn.click();

        softAssert.assertTrue(
                page.locator("text=Thanks for contacting us, we will get back to you shortly.").isVisible(),
                "Failed : Success message is not visible");

        softAssert.assertAll();
    }

    @AfterMethod
    public void quitDriver() {
        browser.close();
        playwright.close();
    }
}
