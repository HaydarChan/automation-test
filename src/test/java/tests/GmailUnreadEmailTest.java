package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testLogFiveUnreadEmailTitles() {
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
        int maxEmails = Math.min(unreadEmails.size(), 5);

        if (maxEmails > 0) {
            System.out.println("Subjects of up to 5 unread emails:");
            for (int i = 0; i < maxEmails; i++) {
                WebElement email = unreadEmails.get(i);
                String subject = email.findElement(By.cssSelector(".bog")).getText();
                System.out.println((i + 1) + ". " + subject);
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
