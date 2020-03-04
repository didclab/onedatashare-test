package org.onedatashare;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**  This test assumes
 *  you have vanditsa@buffalo.edu
 *  with password asdasd as your login account.
 */

public class ChromeFrontendTest {
    private final String baseUrl;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String organization;
    private int msWaitLong;
    private static int MAX_T;

    // to test for sftp, add the credential and uncomment test for method SingleFTPTest
    private String testingSFTP;
    private String testingUsername;
    private String testingPassword;

    public ChromeFrontendTest() throws IOException {
        super();



        Properties prop = new Properties();
        InputStream in = new FileInputStream("constants.properties");
        System.out.println(in);
        prop.load(in);
        baseUrl = prop.getProperty("baseUrl");
        System.out.println(baseUrl);
        username = prop.getProperty("username");
        System.out.println(username);
        password = prop.getProperty("password");
        firstName = prop.getProperty("firstName");
        lastName = prop.getProperty("lastName");
        organization = prop.getProperty("organization");
        msWaitLong = Integer.parseInt(prop.getProperty("msWaitLong"));
        MAX_T = Integer.parseInt(prop.getProperty("MAX_T"));
        testingSFTP = prop.getProperty("testingSFTP");
        testingUsername = prop.getProperty("testingUsername");
        testingPassword = prop.getProperty("testingPassword");

    }
    /** Front end test for testing setup
     * @throws Exception
     */
    @Test
    public void setUp() throws Exception {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(baseUrl);
            assertEquals(driver.getTitle(), "OneDataShare");
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    /** Front end test for login
     * @throws Exception
     */
    @Test(dependsOnMethods = {"setUp"})
    public void LoginTest() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);
        try {
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));

            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("NavQueue")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/transfer");
            driver.findElement(By.id("NavQueue")).click();
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/queue");
            assertEquals(driver.getTitle(), "OneDataShare - Queue");
            driver.findElement(By.id("NavTransfer")).click();
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/transfer");
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            driver.findElement(By.id("NavEmail")).click();
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/user");
            assertEquals(driver.getTitle(), "OneDataShare - Account");
            assertEquals(driver.findElement(By.id("UserEmail")).findElement(By.tagName("p")).getText(), username);
            System.out.println(firstName);
            assertEquals(driver.findElement(By.id("UserFirstName")).findElement(By.tagName("p")).getText(), firstName);
            assertEquals(driver.findElement(By.id("UserLastName")).findElement(By.tagName("p")).getText(), lastName);
            assertEquals(driver.findElement(By.id("UserOrganization")).findElement(By.tagName("p")).getText(), organization);
            driver.findElement(By.id("NavLogout")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("NavRegister")));
            assertTrue(driver.getCurrentUrl().equals("http://localhost:8080/") || driver.getCurrentUrl().equals("http://localhost:8080"));
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    @Test(dependsOnMethods = {"LoginTest"})
    public void IsAdminTest() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);
        WebElement ele;
        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));

            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("NavDropdown")));
            assertTrue(driver.findElement(By.id("NavDropdown")).isDisplayed());
            driver.findElement(By.id("NavDropdown")).click();

            assertTrue(driver.findElement(By.id("NavAdminClients")).isDisplayed());
            driver.findElement(By.id("NavAdminClients")).click();
            assertEquals(driver.getTitle(), "OneDataShare - Client Info");

            assertTrue(driver.findElement(By.id("NavDropdown")).isDisplayed());
            driver.findElement(By.id("NavDropdown")).click();

            assertTrue(driver.findElement(By.id("NavAdminHistory")).isDisplayed());
            driver.findElement(By.id("NavAdminHistory")).click();
            assertEquals(driver.getTitle(), "OneDataShare - History");


            driver.findElement(By.id("HistoryUsername")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historyusername" + i)).getText();
                    String second =  driver.findElement(By.id("historyusername" + (i + 1))).getText();
                    //System.out.println(first+" "+second);

                    assertTrue( first.compareTo(second) >= 0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("HistoryUsername")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historyusername" + i)).getText();
                    String second =  driver.findElement(By.id("historyusername" + (i + 1))).getText();
                    //System.out.println(first+" "+second);

                    assertTrue( first.compareTo(second) <= 0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            //ele = driver.findElement(By.id("historyid" + 0));
            driver.findElement(By.id("HistoryJobID")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueNextButton")));
            Thread.sleep(msWaitLong/2);

            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("historyid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("historyid" + (i + 1))).getText());
                    System.out.println(first+" "+second);
                    assertTrue( first >= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }


            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");


            //ele = driver.findElement(By.id("historyid" + 0));
            driver.findElement(By.id("HistoryJobID")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueNextButton")));
            Thread.sleep(msWaitLong/2);
            //wait.until(ExpectedConditions.invisibilityOf(ele));

            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("historyid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("historyid" + (i + 1))).getText());
                    System.out.println(first+" "+second);
                    assertTrue( first <= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("HistoryProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#historyprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#historyprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //   assertTrue(compareProgress(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("HistoryProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#historyprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#historyprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //   assertTrue(compareProgress(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("HistorySpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historyspeed" + i)).getText();
                    String second = driver.findElement(By.id("historyspeed" + (i + 1))).getText();

                    assertTrue(compareSpeed(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("HistorySpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historyspeed" + i)).getText();
                    String second = driver.findElement(By.id("historyspeed" + (i + 1))).getText();
                    assertTrue(compareSpeed(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("HistorySD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historysource" + i)).getText();
                    String second = driver.findElement(By.id("historysource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) >= 0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("HistorySD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("historysource" + i)).getText();
                    String second = driver.findElement(By.id("historysource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) <=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    public int compareProgress(String before, String after){
        /*if(before.equals(after)){
            return 0;
        }
        if(before.equals("Complete")){
            return 1; // forward
        }
        if(after.equals("Complete")){
            return -1;
        }
        if(before.equals("Failed")){
            return 1; // forward
        }
        if(after.equals("Failed")){
            return -1; // forward
        }
        System.out.println(before);
        String beforeSplit = before.split(" %")[1];
        String afterSplit = after.split(" %")[1];
        return Integer.parseInt(beforeSplit) - Integer.parseInt(afterSplit);*/
        return before.compareTo(after);
    }

    public float compareSpeed(String before, String after){
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        hm.put("B/s", 0);
        hm.put("KB/s", 1);
        hm.put("MB/s", 2);
        hm.put("GB/s", 3);
        String[] beforeSplit = before.split(" ");
        String[] afterSplit = before.split(" ");
        if(before.equals("N/A")){
            return -1;
        }
        if(after.equals("N/A")){
            return 1;
        }
        int bs = hm.get(beforeSplit[1]);
        int as = hm.get(afterSplit[1]);
        if(bs == as){
            return Float.parseFloat(beforeSplit[0]) - Float.parseFloat(afterSplit[0]);
        }else{
            return bs-as;
        }
    }
    /** Front end test for left side sort bar
     * @throws Exception
     */
    @Test(dependsOnMethods = {"LoginTest"})
    public void SortingTestLeft() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);
        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftFTP")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.findElement(By.id("leftFTP")).getText(), "FTP");
            driver.findElement(By.id("leftFTP")).click();

            assertEquals(driver.findElement(By.id("leftAdd")).getText(), "Add New FTP");
            driver.findElement(By.id("leftAdd")).click();

            driver.findElement(By.id("leftLoginURI")).click();
            driver.findElement(By.id("leftLoginURI")).clear();
            driver.findElement(By.id("leftLoginURI")).sendKeys("speedtest.tele2.net");
            driver.findElement(By.id("leftLoginAuth")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftFilename")));
            driver.findElement(By.id("leftFilename")).click();
            assertEquals(driver.findElement(By.id("filenameleft1")).getText(), "5MB.zip");
            driver.findElement(By.id("leftFilename")).click();
            Thread.sleep(msWaitLong/10);
            assertEquals(driver.findElement(By.id("filenameleft1")).getText(), "1000GB.zip");
            driver.findElement(By.id("leftDate")).click();
            assertEquals(driver.findElement(By.id("filenameleft0")).getText(), "upload");
            driver.findElement(By.id("leftDate")).click();
            assertEquals(driver.findElement(By.id("dateleft1")).getText(), "2/19/2016, 12:00:00 AM");
            driver.findElement(By.id("leftSize")).click();
            assertEquals(driver.findElement(By.id("sizeleft0")).getText(), "1000.0 GB");
            driver.findElement(By.id("leftSize")).click();
            assertEquals(driver.findElement(By.id("sizeleft0")).getText(), "N/A");
            assertEquals(driver.findElement(By.id("sizeleft1")).getText(), "1.0 kB");
            driver.findElement(By.id("NavQueue")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("QueueID")));
            assertEquals(driver.getTitle(), "OneDataShare - Queue");
            driver.findElement(By.id("QueueID")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("queueid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("queueid" + (i + 1))).getText());
                    System.out.println(first+" "+second);
                    assertTrue( first >= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("QueueID")));
            driver.findElement(By.id("QueueID")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueNextButton")));

            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("queueid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("queueid" + (i + 1))).getText());
                    System.out.println(first+" "+second);
                    assertTrue( first <= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#queueprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#queueprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //   assertTrue(compareProgress(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#queueprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#queueprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //   assertTrue(compareProgress(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueSpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuespeed" + i)).getText();
                    String second = driver.findElement(By.id("queuespeed" + (i + 1))).getText();

                    assertTrue(compareSpeed(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("QueueSpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuespeed" + i)).getText();
                    String second = driver.findElement(By.id("queuespeed" + (i + 1))).getText();
                    assertTrue(compareSpeed(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("QueueSD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuesource" + i)).getText();
                    String second = driver.findElement(By.id("queuesource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) >= 0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueSD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuesource" + i)).getText();
                    String second = driver.findElement(By.id("queuesource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) <=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }


    /** Front end test for right side sort bar
     * @throws Exception
     */
    @Test(dependsOnMethods = {"LoginTest"})
    public void SortingTestRight() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);

        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightFTP")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.findElement(By.id("rightFTP")).getText(), "FTP");

            driver.findElement(By.id("rightFTP")).click();
            assertEquals(driver.findElement(By.id("rightAdd")).getText(), "Add New FTP");

            driver.findElement(By.id("rightAdd")).click();
            driver.findElement(By.id("rightLoginURI")).click();
            driver.findElement(By.id("rightLoginURI")).clear();
            driver.findElement(By.id("rightLoginURI")).sendKeys("speedtest.tele2.net");
            driver.findElement(By.id("rightLoginAuth")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightFilename")));

            driver.findElement(By.id("rightFilename")).click();
            assertEquals(driver.findElement(By.id("filenameright1")).getText(), "5MB.zip");
            driver.findElement(By.id("rightFilename")).click();
            assertEquals(driver.findElement(By.id("filenameright1")).getText(), "1000GB.zip");
            driver.findElement(By.id("rightDate")).click();
            assertEquals(driver.findElement(By.id("filenameright0")).getText(), "upload");
            driver.findElement(By.id("rightDate")).click();
            assertEquals(driver.findElement(By.id("dateright1")).getText(), "2/19/2016, 12:00:00 AM");
            driver.findElement(By.id("rightSize")).click();
            assertEquals(driver.findElement(By.id("sizeright0")).getText(), "1000.0 GB");
            driver.findElement(By.id("rightSize")).click();
            assertEquals(driver.findElement(By.id("sizeright0")).getText(), "N/A");
            assertEquals(driver.findElement(By.id("sizeright1")).getText(), "1.0 kB");

            driver.findElement(By.id("NavQueue")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("QueueID")));
            assertEquals(driver.getTitle(), "OneDataShare - Queue");
            driver.findElement(By.id("QueueID")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("queueid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("queueid" + (i + 1))).getText());
                    System.out.println("line271" + first + " " + second);
                    assertTrue( first >= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("QueueID")));
            driver.findElement(By.id("QueueID")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueNextButton")));
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    int first = Integer.parseInt(driver.findElement(By.id("queueid" + i)).getText());
                    int second =  Integer.parseInt(driver.findElement(By.id("queueid" + (i + 1))).getText());
                    System.out.println(first+" "+second);
                    assertTrue( first <= second );
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#queueprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#queueprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //    assertTrue(compareProgress(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueProgress")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.cssSelector("#queueprocess" + i + " .progress .progress-bar")).getText();
                    String second = driver.findElement(By.cssSelector("#queueprocess" + (i + 1) + " .progress .progress-bar")).getText();
                    //    assertTrue(compareProgress(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueSpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuespeed" + i)).getText();
                    String second = driver.findElement(By.id("queuespeed" + (i + 1))).getText();

                    assertTrue(compareSpeed(first, second)>=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("QueueSpeed")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuespeed" + i)).getText();
                    String second = driver.findElement(By.id("queuespeed" + (i + 1))).getText();
                    assertTrue(compareSpeed(first, second)<=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");

            driver.findElement(By.id("QueueSD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for(int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuesource" + i)).getText();
                    String second = driver.findElement(By.id("queuesource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) >=0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
            driver.findElement(By.id("queueFirstPageButton")).click();
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0)");
            driver.findElement(By.id("QueueSD")).click();
            while(driver.findElement(By.id("queueNextButton")).isEnabled()) {
                for (int i = 0; i < 9; i++) {
                    String first = driver.findElement(By.id("queuesource" + i)).getText();
                    String second = driver.findElement(By.id("queuesource" + (i + 1))).getText();
                    //assertTrue(first.compareTo(second) <= 0);
                }
                driver.findElement(By.id("queueNextButton")).click();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }
    /** Front end test for left side search bar
     * @throws Exception
     */
    @Test(dependsOnMethods = {"LoginTest"})
    public void SearchingTestLeft() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);

        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));

            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftFTP")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.findElement(By.id("leftFTP")).getText(), "FTP");

            driver.findElement(By.id("leftFTP")).click();

            driver.findElement(By.id("leftAdd")).click();
            driver.findElement(By.id("leftLoginURI")).click();
            driver.findElement(By.id("leftLoginURI")).clear();
            driver.findElement(By.id("leftLoginURI")).sendKeys("speedtest.tele2.net");
            driver.findElement(By.id("leftLoginAuth")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftSearch")));
            driver.findElement(By.id("leftSearch")).click();
            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft1")));
            assertTrue(driver.findElement(By.id("filenameleft1")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000gb");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameleft1")).isEmpty());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000");
            driver.findElement(By.id("leftIgnoreCase")).click();
            driver.findElement(By.id("leftSearch")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft1")));
            assertTrue(driver.findElement(By.id("filenameleft1")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000gb");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft1")));
            assertTrue(driver.findElement(By.id("filenameleft1")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("10*");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameleft0")).isEmpty());
            assertTrue(driver.findElements(By.id("filenameleft1")).isEmpty());

            driver.findElement(By.id("leftRegex")).click();
            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("u*");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft0")));
            assertTrue(driver.findElement(By.id("filenameleft0")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft0")));
            assertTrue(driver.findElement(By.id("filenameleft0")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000*");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft1")));
            assertTrue(driver.findElement(By.id("filenameleft1")).isDisplayed());

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000gb.zipa*");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft1")));
            assertTrue(driver.findElement(By.id("filenameleft1")).isDisplayed());


            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("1000gb.zip.");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameleft1")).isEmpty());
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }
    /** Front end test for right side search bar
     * @throws Exception
     */
    @Test(dependsOnMethods = {"LoginTest"})
    public void SearchingTestRight() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);

        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightFTP")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.findElement(By.id("rightFTP")).getText(), "FTP");

            driver.findElement(By.id("rightFTP")).click();
            driver.findElement(By.id("rightAdd")).click();
            driver.findElement(By.id("rightLoginURI")).click();
            driver.findElement(By.id("rightLoginURI")).clear();
            driver.findElement(By.id("rightLoginURI")).sendKeys("speedtest.tele2.net");
            driver.findElement(By.id("rightLoginAuth")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightSearch")));
            assertEquals(driver.getTitle(), "OneDataShare - Transfer");
            assertEquals(driver.findElement(By.id("leftFTP")).getText(), "FTP");

            driver.findElement(By.id("rightSearch")).click();
            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright1")));
            assertTrue(driver.findElement(By.id("filenameright1")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000gb");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameright1")).isEmpty());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000");
            driver.findElement(By.id("rightIgnoreCase")).click();
            driver.findElement(By.id("rightSearch")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright1")));
            assertTrue(driver.findElement(By.id("filenameright1")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000gb");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright1")));
            assertTrue(driver.findElement(By.id("filenameright1")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("10*");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameright0")).isEmpty());
            assertTrue(driver.findElements(By.id("filenameright1")).isEmpty());

            driver.findElement(By.id("rightRegex")).click();
            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("u*");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright0")));
            assertTrue(driver.findElement(By.id("filenameright0")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright0")));
            assertTrue(driver.findElement(By.id("filenameright0")).isDisplayed());

            driver.findElement(By.id("rightSearch")).click();
            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000*");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright1")));
            assertTrue(driver.findElement(By.id("filenameright1")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000gb.zipa*");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameright1")));
            assertTrue(driver.findElement(By.id("filenameright1")).isDisplayed());

            driver.findElement(By.id("rightSearch")).clear();
            driver.findElement(By.id("rightSearch")).sendKeys("1000gb.zip.");
            driver.findElement(By.id("rightSearch")).sendKeys(Keys.ENTER);
            assertTrue(driver.findElements(By.id("filenameright1")).isEmpty());

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    @Test(dependsOnMethods = {"LoginTest"})
    public void SingleFTPTransferLeft() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);

        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightFTP")));
            driver.findElement(By.id("rightFTP")).click();
            driver.findElement(By.id("rightAdd")).click();
            driver.findElement(By.id("rightLoginURI")).click();
            driver.findElement(By.id("rightLoginURI")).clear();
            driver.findElement(By.id("rightLoginURI")).sendKeys("speedtest.tele2.net/upload");
            driver.findElement(By.id("rightLoginAuth")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftFTP")));
            driver.findElement(By.id("leftFTP")).click();

            driver.findElement(By.id("leftAdd")).click();
            driver.findElement(By.id("leftLoginURI")).click();
            driver.findElement(By.id("leftLoginURI")).clear();
            driver.findElement(By.id("leftLoginURI")).sendKeys("speedtest.tele2.net");
            driver.findElement(By.id("leftLoginAuth")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft4")));
            //Transfer
            driver.findElement(By.id("filenameleft4")).click();
            driver.findElement(By.id("sendFromLeftToRight")).click();

            driver.findElement(By.id("NavQueue")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueprocess0")));
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/queue");
            assertEquals(driver.getTitle(), "OneDataShare - Queue");

            assertTrue(!driver.findElement(By.id("queueprocess0")).getText().equals("Completed"));

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    @Test(dependsOnMethods = {"LoginTest"})
    public void SingleFTPTest() throws Exception {
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, msWaitLong);

        try{
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rightFTP")));
            driver.findElement(By.id("rightFTP")).click();
            driver.findElement(By.id("rightAdd")).click();
            driver.findElement(By.id("rightLoginURI")).click();
            driver.findElement(By.id("rightLoginURI")).clear();
            driver.findElement(By.id("rightLoginURI")).sendKeys("speedtest.tele2.net/upload");
            driver.findElement(By.id("rightLoginAuth")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftSFTP")));
            driver.findElement(By.id("leftSFTP")).click();

            driver.findElement(By.id("leftAdd")).click();
            driver.findElement(By.id("leftLoginURI")).click();
            driver.findElement(By.id("leftLoginURI")).clear();
            driver.findElement(By.id("leftLoginURI")).sendKeys(testingSFTP);

            driver.findElement(By.id("leftLoginUsername")).click();
            driver.findElement(By.id("leftLoginUsername")).clear();
            driver.findElement(By.id("leftLoginUsername")).sendKeys(testingUsername);

            driver.findElement(By.id("leftLoginPassword")).click();
            driver.findElement(By.id("leftLoginPassword")).clear();
            driver.findElement(By.id("leftLoginPassword")).sendKeys(testingPassword);

            driver.findElement(By.id("leftLoginAuth")).click();

            //mkdir
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftMkdirButton")));
            driver.findElement(By.id("leftMkdirButton")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("leftMkdirSubmit")));
            driver.findElement(By.id("leftMkdirName")).click();
            driver.findElement(By.id("leftMkdirName")).sendKeys("testDir");
            driver.findElement(By.id("leftMkdirSubmit")).click();

            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("testDir");
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#tableHeaderleft + table tbody tr")));
            assertEquals(driver.findElements(By.cssSelector("#tableHeaderleft + table tbody tr")).size(), 1);

            //delete
            driver.findElements(By.cssSelector("#tableHeaderleft + table tbody tr")).get(0).click();
            driver.findElement(By.id("leftDeleteButton")).click();
            driver.switchTo().alert().accept();
            //

            driver.findElement(By.id("leftRefreshButton")).click();
            assertTrue(driver.findElements(By.cssSelector("#tableHeaderleft + table tbody tr")).isEmpty());
            driver.findElement(By.id("leftSearch")).clear();
            driver.findElement(By.id("leftSearch")).sendKeys("onedatashare");
            driver.findElement(By.id("leftRefreshButton")).click();
            driver.findElement(By.id("leftSearch")).sendKeys(Keys.ENTER);

            //Transfer
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filenameleft15")));
            driver.findElement(By.id("filenameleft15")).click();
            driver.findElement(By.id("sendFromLeftToRight")).click();
            driver.findElement(By.id("NavQueue")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("queueprocess0")));
            assertEquals(driver.getCurrentUrl(), "http://localhost:8080/queue");
            assertEquals(driver.getTitle(), "OneDataShare - Queue");

            assertTrue(!driver.findElement(By.id("queueprocess0")).getText().equals("Completed"));
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            driver.quit();
        }
    }

    /** Initialize 30 random clicks for 5 times on the page after login
     * @throws Exception
     */
    //@Test(dependsOnMethods = {"LoginTest"})
    public void StabilizeTest() throws Exception {
        for(int j = 0; j < 5; j++) {
            WebDriver driver = new FirefoxDriver();
            WebDriverWait wait = new WebDriverWait(driver, msWaitLong);
            driver.get(baseUrl);
            driver.findElement(By.linkText("Sign in")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            assertEquals(driver.getCurrentUrl(), baseUrl+"/account/signIn");
            driver.findElement(By.id("email")).click();
            driver.findElement(By.id("email")).clear();
            driver.findElement(By.id("email")).sendKeys(username);
            driver.findElement(By.id("email")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            driver.findElement(By.id("Password")).click();
            driver.findElement(By.id("Password")).clear();
            driver.findElement(By.id("Password")).sendKeys(password);
            driver.findElement(By.id("Password")).sendKeys(Keys.ENTER);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
            Random random = new Random();
            for (int i = 0; i < 30; i++) {
                List<WebElement> allElements = driver.findElements(By.cssSelector("*"));
                System.out.println(allElements.size());
                int randomInt = random.nextInt(allElements.size());
                try {
                    allElements.get(randomInt).click();
                } catch (ElementNotInteractableException e) {
                } catch (JavascriptException e) {}
                catch(StaleElementReferenceException e){}
            }
            driver.quit();
        }
    }

    /** Runnable instance for stress testing
     */
    class Task implements Runnable
    {
        private String name;

        public Task(String s)
        {
            name = s;
        }

        // Prints task name and sleeps for 1s
        // This Whole process is repeated 5 times
        public void run() throws RuntimeException
        {
            try {
                LoginTest();
            }catch(Exception e){
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
    }


    /** This test is for stress testing with multiple users.
     *  It simulates login from MAX_T different chrome instances.
     *  It is only enabled in some cases
     * @throws Exception
     */
    //@Test
    public void MultipleUser() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        for(int i =0; i < MAX_T; i++){
            Runnable r1 = new Task("task"+i);
            pool.execute(r1);
        }
        pool.awaitTermination(20, TimeUnit.MINUTES);
        pool.shutdown();
    }

}