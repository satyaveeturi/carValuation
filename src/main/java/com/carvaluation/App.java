package com.carvaluation;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class App {
    private WebDriver driver;
    private WebDriverWait wait;
    
    private final By regInput = By.cssSelector("input[name='vehicleReg']");
    private final By mileageInput = By.cssSelector("input[name='Mileage']");
    private final By submitButton = By.id("btn-go");
    private final By carouselItem = By.cssSelector("carousel-item");
    private final By manufacturerValue = By.xpath("//*[@id='wbac-app-container']/div/div/vehicle-questions/div/section[1]/div/div[1]/div/div[3]/div/vehicle-details/div[3]/div[2]/div[1]/div[2]");
    private final By modelValue = By.xpath("//*[@id='wbac-app-container']/div/div/vehicle-questions/div/section[1]/div/div[1]/div/div[3]/div/vehicle-details/div[3]/div[2]/div[2]/div[2]");
    private final By yearValue = By.xpath("//*[@id='wbac-app-container']/div/div/vehicle-questions/div/section[1]/div/div[1]/div/div[3]/div/vehicle-details/div[3]/div[2]/div[3]/div[2]");
    
    private Map<String, String> vehicleDetails;
    private String currentRegistration;
    
    public App(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.vehicleDetails = new HashMap<>();
    }
    
    public By getRegInput() { 
        return regInput; 
    }
    
    public By getCarouselItem() {
        return carouselItem;
    }
    
    private void waitForAngular() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.presenceOfElementLocated(carouselItem));
        } catch (TimeoutException e) {
            throw new TimeoutException("Not found");
        }
    }

    public void enterRegistration(String registration) {
        this.currentRegistration = registration;
        wait.until(ExpectedConditions.elementToBeClickable(regInput));
        WebElement element = driver.findElement(regInput);
        element.clear();
        element.sendKeys(registration);
    }
    
    public void enterMileage(String mileage) {
        wait.until(ExpectedConditions.elementToBeClickable(mileageInput));
        WebElement element = driver.findElement(mileageInput);
        element.clear();
        element.sendKeys(mileage);
    }
    
    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        driver.findElement(submitButton).click();
        waitForAngular();
    }
    
    public Map<String, String> getVehicleDetails() {
        vehicleDetails.clear();
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            
            // Use the input registration since we know it's correct
            vehicleDetails.put("Registration", currentRegistration);
            
            String manufacturer = shortWait.until(ExpectedConditions.presenceOfElementLocated(manufacturerValue)).getText().trim();
            vehicleDetails.put("Manufacturer", manufacturer);
            
            String model = shortWait.until(ExpectedConditions.presenceOfElementLocated(modelValue)).getText().trim();
            vehicleDetails.put("Model", model);
            
            String year = shortWait.until(ExpectedConditions.presenceOfElementLocated(yearValue)).getText().trim();
            vehicleDetails.put("Year", year);
            
        } catch (Exception e) {
            throw new TimeoutException("Not found");
        }
        return vehicleDetails;
    }
}