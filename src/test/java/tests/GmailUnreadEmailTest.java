package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;
import utils.DriverManager;

import java.time.Duration;
import java.util.List;

public class GmailUnreadEmailTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = DriverManager.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void testLogAndDeleteFirstUnreadEmail() throws InterruptedException {
        driver.get("https://mail.google.com/");

        try {
            WebElement emailInput = driver.findElement(By.id("identifierId"));
            emailInput.sendKeys(System.getenv("GMAIL_EMAIL"));
            driver.findElement(By.id("identifierNext")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            driver.findElement(By.name("password")).sendKeys(System.getenv("GMAIL_PASSWORD"));
            driver.findElement(By.id("passwordNext")).click();

            wait.until(ExpectedConditions.titleContains("Inbox"));
        } catch (Exception e) {
            System.out.println("Already logged in, skipping login steps.");
        }

        List<WebElement> unreadEmails = driver.findElements(By.cssSelector("tr.zE"));
        if (!unreadEmails.isEmpty()) {
            WebElement firstUnread = unreadEmails.get(0);
            String subject = firstUnread.findElement(By.cssSelector(".bog")).getText();
            System.out.println("Subject of the unread email: " + subject);

            WebElement firstUnreadCheckbox = firstUnread.findElement(By.xpath(".//div[@role='checkbox']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstUnreadCheckbox);
            Thread.sleep(500);

            int initialUnreadCount = driver.findElements(By.cssSelector("tr.zE")).size();

            WebElement deleteButtonInsideRow = firstUnread.findElement(By.cssSelector("li.bqX.bru div.KCRnif"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButtonInsideRow);

            System.out.println("DELETED FROM ROW, waiting for update...");
            Thread.sleep(2000); 

            int afterUnreadCount = driver.findElements(By.cssSelector("tr.zE")).size();

            if (afterUnreadCount < initialUnreadCount) {
                System.out.println("Successfully deleted the unread email.");
            } else {
                throw new RuntimeException("Failed to delete the unread email after waiting!");
            }
        } else {
            System.out.println("No unread emails found.");
        }
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
