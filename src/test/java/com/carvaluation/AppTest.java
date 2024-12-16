package com.carvaluation;

import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.Reporter;
import org.testng.annotations.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class AppTest {
    private WebDriver driver;
    private App carPage;
    private Map<String, CarDetails> expectedDetails;
    private List<String> registrations;
    private StringBuilder testResults = new StringBuilder();
    
    private final By errorMessageLocator = By.cssSelector("#wbac-app-container > div > div > vehicle-registration-check > section.primary-section.height-xs-full.center-content-sm.raised-section > div > div.container > div > div.col-12.col-lg-12.offset-lg-0.page-header > h1");
    private final String ERROR_MESSAGE = "Sorry, we couldn't find your car";
    private int passCount = 0;
    private int failCount = 0;

    @BeforeClass
    public void setup() {
        try {
            registrations = CarFileUtils.extractRegistrationsFromFile("car_input.txt");
            expectedDetails = CarFileUtils.readExpectedOutput("car_output.txt");
            
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--start-maximized");
            
            driver = new EdgeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            
            driver.get("https://www.webuyanycar.com");
            handleCookieConsent();
            carPage = new App(driver);
            
            testResults.append("\n=== TEST RESULTS ===\n");
            
        } catch (Exception e) {
            if (driver != null) {
                driver.quit();
            }
            throw e;
        }
    }

    private void handleCookieConsent() {
        try {
            By cookieAcceptButton = By.id("onetrust-accept-btn-handler");
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.elementToBeClickable(cookieAcceptButton))
                .click();
        } catch (Exception e) {
            // Continue if cookie banner is not present
        }
    }

    private boolean isCarNotFound() {
        try {
            WebElement errorElement = new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.presenceOfElementLocated(errorMessageLocator));
            return errorElement.getText().contains(ERROR_MESSAGE);
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void navigateBackToValuationPage() {
        driver.get("https://www.webuyanycar.com");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(carPage.getRegInput()));
        } catch (Exception e) {
            // If waiting fails, continue anyway
        }
    }

    @Test(dataProvider = "registrations")
    public void testCarValuation(String registration) {
        testResults.append("\nTesting Registration: ").append(registration).append("\n");
        
        try {
            carPage.enterRegistration(registration);
            carPage.enterMileage("50000");
            carPage.clickSubmit();
            
            if (isCarNotFound()) {
                failCount++;
                testResults.append("FAILED: Not found\n");
                navigateBackToValuationPage();
                return;
            }
            
            Map<String, String> vehicleDetails = carPage.getVehicleDetails();
            CarDetails actualDetails = new CarDetails(
                vehicleDetails.get("Registration"),
                vehicleDetails.get("Manufacturer"),
                vehicleDetails.get("Model"),
                vehicleDetails.get("Year")
            );
            
            CarDetails expected = expectedDetails.get(registration);
            
            if (expected != null && expected.matches(actualDetails)) {
                passCount++;
                testResults.append("PASSED: Details match\n");
                testResults.append("Expected: ").append(expected).append("\n");
                testResults.append("Actual: ").append(actualDetails).append("\n");
            } else {
                failCount++;
                testResults.append("FAILED: Details don't match\n");
                testResults.append("Expected: ").append(expected).append("\n");
                testResults.append("Actual: ").append(actualDetails).append("\n");
            }
            
        } catch (TimeoutException e) {
            failCount++;
            testResults.append("FAILED: Not found\n");
        } catch (Exception e) {
            failCount++;
            testResults.append("FAILED: Not found\n");
        } finally {
            navigateBackToValuationPage();
        }
    }

    @DataProvider(name = "registrations")
    public Object[][] getRegistrations() {
        return registrations.stream()
            .map(reg -> new Object[]{reg})
            .toArray(Object[][]::new);
    }

    @AfterClass
    public void tearDown() {
        testResults.append("\n=== FINAL SUMMARY ===\n");
        testResults.append(String.format("Total Tests: %d\n", registrations.size()));
        testResults.append(String.format("Passed: %d\n", passCount));
        testResults.append(String.format("Failed: %d\n", failCount));
        
        System.out.println(testResults.toString());
        Reporter.log(testResults.toString());
        
        if (driver != null) {
            driver.quit();
        }
    }
}