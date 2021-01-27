///////////////////////////////////////////
//
//  EasyOnlineClasses Release v1.0
//  Grigori Dmitrii
//
///////////////////////////////////////////

// EXIT STATUSES
// 100-200 : methods
// 200-300 : main
// 400 : unknown exception

package org.example;

//import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

//import javax.swing.*;
import javax.swing.JOptionPane;

//import java.awt.*;
import java.awt.Toolkit;

//import java.io.*;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Duration;

//import java.util.*;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {
    // variables
    private static String string_date;
    private static String string_currentDate;
    private static WebDriver driver;
    private static Logger logger;
    private static WebDriverWait exWait;
    private static File ckFile_studii;
    private static File ckFile_meet;
    private static boolean ckFile_studii_exists;
    private static boolean ckFile_exists;
    private static boolean ckFile_corrupted;
    private static boolean confirmBox_released;
    private static byte paths;

    // methods
    private static void getTime(){
        ///////////////////////////////////////////////////////////////

        //GET CURRENT TIME FROM SYSTEM

        ///////////////////////////////////////////////////////////////

        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
            string_date = dateFormat.format(date);

            dateFormat = new SimpleDateFormat("MM-dd");
            string_currentDate = dateFormat.format(date);

        } catch (Exception ex) {
            errorBox("Unable to get system date. Error message: " + ex.getMessage());

            Exit(100, false);
        }
    }

    private static String currentDate() {
            String currentDate = null;
            String currentMonth = null;

            try {
                StringTokenizer token = new StringTokenizer(string_currentDate, "-"); // MM-dd
                String currentMonth_token = token.nextToken();

                int currentDay_int = Integer.parseInt(token.nextToken()); // 09 -> 9
                String currentDay = String.valueOf(currentDay_int);

                switch (currentMonth_token) {
                    case "01":
                        currentMonth = "ianuarie";
                        break;

                    case "02":
                        currentMonth = "februarie";
                        break;

                    case "03":
                        currentMonth = "martie";
                        break;

                    case "04":
                        currentMonth = "aprilie";
                        break;

                    case "05":
                        currentMonth = "mai";
                        break;

                    case "09":
                        currentMonth = "septembrie";
                        break;

                    case "10":
                        currentMonth = "octombrie";
                        break;

                    case "11":
                        currentMonth = "noiembrie";
                        break;

                    case "12":
                        currentMonth = "decembrie";
                        break;

                    default:
                        logger.severe("Exception while parsing current month. Check currentDate() method.");
                        errorBox("Exception while parsing current month. Error code 101");

                        Exit(101, true);
                        break;
                }

                assert currentMonth != null;
                currentDate = currentDay.concat(" ").concat(currentMonth);


            }catch (Exception ex){

                logger.severe("Exception in currentDate() method. Exception message: " + ex.getMessage());
                errorBox("Exception in currentDate() method. Exception message: " + ex.getMessage());

                Exit(102, true);
            }

            return currentDate;
        }

    private static void logFile(){
        ///////////////////////////////////////////////////////////////

        //CREATE A LOG FILE

        ///////////////////////////////////////////////////////////////

        logger = Logger.getLogger("MyLog");
        FileHandler fh = null;
        try {
            if (paths == 0) {
                fh = new FileHandler("src/main/resources/LOG_" + string_date + ".log"); //test
            }

            if (paths == 1) {
                fh = new FileHandler("logs/LOG_" + string_date + ".log"); //build
            }

            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();

            assert fh != null;
            fh.setFormatter(formatter);
            logger.setUseParentHandlers(false);

        } catch (SecurityException | IOException ex) {
            errorBox("Error while writing the log file. Error message: " + ex.getMessage());

            Exit(103, false);
        }
    }

    private static void WebDriver(){
        ///////////////////////////////////////////////////////////////

        //CONFIGURING CHROME DRIVER

        ///////////////////////////////////////////////////////////////

        if (paths == 0) {
            System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver_88.0.4324.96_edited.exe"); //test path
        }

        if (paths == 1) {
            System.setProperty("webdriver.chrome.driver", "resources/chromedriver_88.0.4324.96_edited.exe"); //build path
        }

        ChromeOptions chrome_options = new ChromeOptions();

        // ARGUMENTS
        chrome_options.addArguments("--window-size=1920,1080");
        chrome_options.addArguments("--start-maximized");
        chrome_options.addArguments("--incognito");
        chrome_options.addArguments("--disable-blink-features=AutomationControlled");
        chrome_options.addArguments("use-fake-ui-for-media-stream");
        chrome_options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        // EXPERIMENTAL OPTIONS
        chrome_options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});


        // CONFIGURING DRIVER
        try {
            driver = new ChromeDriver(chrome_options);
            driver.manage().window().maximize();
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

        } catch (java.lang.IllegalStateException ex) {
            logger.severe("Invalid path for chromedriver. Exception message: " + ex.getMessage());
            errorBox("Invalid path for chromedriver. Exception message: " + ex.getMessage());

            System.exit(104);
        }

        // IMPLICIT WAIT
        //driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //useless piece of wait


    }

    private static String generateXPATH(WebElement childElement, String current) {
        //
        // GENERATE XPATH FROM ELEMENT
        //

        String childTag = childElement.getTagName();
        if(childTag.equals("html")) {
            return "/html[1]"+current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) {
                count++;
            }
            if (childElement.equals(childrenElement)) {
                return generateXPATH(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }

    private static void Exit(int exit_status, boolean driver_exists){
        //
        // COMPLETE APPLICATION SHUTDOWN
        //

        // useless information and it doesn't even work properly.
        //BuildInfo info = new BuildInfo();
        //String infoString = info.toString();

        logger.info("System info: os.name: \"" + System.getProperty("os.name") + "\", os.arch: \"" + System.getProperty("os.arch") +
                "\", os.version: \"" + System.getProperty("os.version") + "\", java.version: \"" + System.getProperty("java.version") + "\".");

        if (driver_exists) {
            driver.quit();
            logger.info("Driver was successfully killed.");
        }

        logger.info("Process finished with exit code " + exit_status);
        System.exit(exit_status);

    }

    private static void CookieFile_Studii() {
        ///////////////////////////////////////////////////////////////

        //CREATING COOKIE FILE

        ///////////////////////////////////////////////////////////////

        try {
            logger.info("Creating cookie_studii.data...");
            FileWriter ckFileWrite = new FileWriter(ckFile_studii);
            BufferedWriter ckFileBW = new BufferedWriter(ckFileWrite);
            logger.info("cookie_studii.data created.");

            Cookie ck = null;
            try {
                logger.info("Getting the right cookie...");
                ck = driver.manage().getCookieNamed("auth");
                logger.info("Got the cookie.");

            }catch (Exception ex){
                logger.severe("Invalid cookie name. Exception message: " + ex.getMessage());
                errorBox("Invalid cookie name. Exception message: " + ex.getMessage());

                Exit(105, true);
            }

            logger.info("Writing the data in cookie_studii.data...");
            assert ck != null;
            ckFileBW.write((ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";" + ck.getExpiry() + ";" + ck.isSecure()));
            ckFileBW.newLine();
            logger.info("Wrote the data in cookie_studii.data");

            logger.info("Closing cookie_studii.data...");
            ckFileBW.close();
            ckFileWrite.close();
            logger.info("cookie_studii.data closed.");

        }catch (IOException ex1){
            logger.severe("Exception in cookie parsing segment. Exception message :" + ex1.getMessage());
            errorBox("Exception in cookie parsing segment. Exception message :" + ex1.getMessage());

            Exit(106, true);
        }
    }

    private static void CookieFile_Meet() {
        ///////////////////////////////////////////////////////////////

        //CREATING COOKIE FILE

        ///////////////////////////////////////////////////////////////

        try {
            logger.info("Creating cookie_meet.data...");
            FileWriter ckFileWrite = new FileWriter(ckFile_meet);
            BufferedWriter ckFileBW = new BufferedWriter(ckFileWrite);
            logger.info("cookie_meet.data created.");

            logger.info("Catching the cookies...");
            int i = 1;
            for(Cookie ck : driver.manage().getCookies()){

                ckFileBW.write((ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";" + ck.getExpiry() + ";" + ck.isSecure()));
                ckFileBW.newLine();
                logger.info("Cookie #" + i + " caught.");
                i++;
            }
            logger.info("Finished catching all the cookies.");

            logger.info("Closing cookie_meet.data...");
            ckFileBW.close();
            ckFileWrite.close();
            logger.info("cookie_meet.data closed.");

        }catch (IOException ex1){
            logger.severe("Exception in cookie parsing segment. Exception message :" + ex1.getMessage());
            errorBox("Exception in cookie parsing segment. Exception message :" + ex1.getMessage());

            Exit(107, true);
        }
    }

    private static void CookieFile_Parsing(String cookieDomain){
        ///////////////////////////////////////////////////////////////

        //PARSING COOKIE FROM COOKIE FILE

        ///////////////////////////////////////////////////////////////

        if (paths == 0){
            ckFile_studii = new File("src/main/resources/cookies_studii.data"); //test
            ckFile_meet = new File("src/main/resources/cookies_meet.data"); //test
        }

        if (paths == 1){
            ckFile_studii = new File("cookies/cookies_studii.data"); //build
            ckFile_meet = new File("cookies/cookies_meet.data"); //build
        }

        File ckFile = null;
        String cookieName = null;

        if (cookieDomain.equals("studii")) {
            ckFile = ckFile_studii;
            cookieName = "cookie_studii.data";

            if (ckFile.exists()){ // if the file is missing, no input is required on meet
                ckFile_studii_exists = true;
            }

        }
        else if (cookieDomain.equals("meet")) {
            ckFile = ckFile_meet;
            cookieName = "cookie_meet.data";
        }
        else {
            logger.severe("Invalid hard-coded cookie domain.");

            ckFile_corrupted = true;
        }

        assert ckFile != null;
        ckFile_exists = ckFile.exists();

        if (ckFile_exists){

            logger.info(cookieName + " exists. Parsing the cookie data...");

            try {
                logger.info("Opening " + cookieName + " for reading...");
                FileReader fileReader = new FileReader(ckFile);
                BufferedReader BuffReader = new BufferedReader(fileReader);
                logger.info("Opened " + cookieName + " for reading.");

                String strline;
                int i = 1;

                outerloop:
                while ((strline = BuffReader.readLine()) != null){

                    StringTokenizer token = new StringTokenizer(strline, ";");

                    //(ck.getName() + ";" + ck.getValue() + ";" + ck.getDomain() + ";" + ck.getPath() + ";" + ck.getExpiry() + ";" + ck.isSecure()); // reference
                    while (token.hasMoreTokens()){

                        String ckName = token.nextToken();
                        String ckValue = token.nextToken();
                        String ckDomain = token.nextToken();
                        String ckPath = token.nextToken();
                        String ckExpiry = token.nextToken();
                        Date ckExpiry_date = null;

                        try // ckExpiry must be Date and not String - Can't parse Date from file directly.
                        {
                            ckExpiry_date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(ckExpiry);
                                    /* SimpleDateFormat reference
                                    EEE - Thu
                                    MMM - Dec
                                    dd - 30
                                    HH:mm:ss - 23:59:59
                                    zzz - EET
                                    yyyy - 2020
                                     */

                        } catch (java.text.ParseException ex){
                            logger.severe("ParseException in cookies segment. Exception message: " + ex.getMessage());
                            //errorBox("ParseException in cookies segment. Exception message: " + ex.getMessage());

                            ckFile_corrupted = true;
                            break outerloop;
                        }

                        boolean ckSecure = Boolean.parseBoolean(token.nextToken());

                        Cookie ck = new Cookie(ckName, ckValue, ckDomain, ckPath, ckExpiry_date, ckSecure);

                        logger.info("Adding cookie #" + i + " to the page...");
                        driver.manage().addCookie(ck);
                        logger.info("Cookie added.");

                        i++;
                    }
                }

                logger.info("Closing "+ cookieName + "...");
                BuffReader.close();
                fileReader.close();
                logger.info("Cookie file closed.");

                logger.info("Refreshing the page...");
                driver.navigate().refresh();
                logger.info("Page refreshed.");

            } catch (IOException ex){
                logger.severe("Exception in cookies segment. Exception message: " + ex.getMessage());
                //errorBox("Exception in cookies segment. Exception message: " + ex.getMessage());

                ckFile_corrupted = true;
            }

        }
    }

    private static int inputBox() {
        Toolkit.getDefaultToolkit().beep();

        logger.info("inputBox \"Please enter the lesson index from this interval: [1-7]\" appeared on the user screen.");

        String Lesson_index_string;
        int Lesson_index = 0;
        int i = 9;

        while(true) {

            try {
                Lesson_index_string = JOptionPane.showInputDialog(null, "Please enter the lesson number you want to connect to from this interval: [1-7]"
                        , "EasyOnlineClasses", JOptionPane.INFORMATION_MESSAGE);

                if (i == 0){
                    infoBox("You failed to enter a valid number. Please read the documentation or try again.");

                    Exit(107, false);
                }

                if (Lesson_index_string == null){
                    logger.info("Cancel or exit button was pressed by the user. Shutting down...");
                    Exit(108, false);
                }

                assert Lesson_index_string != null;
                if (Lesson_index_string.matches("[1-7]+") && Lesson_index_string.length() == 1){
                    // correct input.
                    logger.info("Parsing " + Lesson_index_string +" to int...");
                    Lesson_index = Integer.parseInt(Lesson_index_string);
                    logger.info("Parsed.");
                }
                else {

                    infoBox("Incorrect value. You have " + i + " tries left.");
                    i--;

                    continue;
                }

            } catch (Exception ex){
                logger.severe("Unknown exception in inputBox() method. Exception message: " + ex.getMessage());

                Exit(109, true);
            }

            logger.info("Correct input was introduced by the user. Moving on...");
                
            break;
        }

        return Lesson_index;
    }

    private static void infoBox(String s)  {
        Toolkit.getDefaultToolkit().beep();

        logger.info("infoBox \""+ s + "\" appeared on the user screen.");

        Object[] options = {"OK"};

        int response = JOptionPane.showOptionDialog(null, s, "EasyOnlineClasses",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (response == 0){
            logger.info("\"OK\" option was selected.");
        }
        else {
            logger.info("Message box was closed.");
        }
    }

    private static void infoBox_custom(String s) {
        Toolkit.getDefaultToolkit().beep();

        logger.info("infoBox \""+ s + "\" appeared on the user screen.");

        Object[] options = {"Why so rude?"};

        int response = JOptionPane.showOptionDialog(null, s, "EasyOnlineClasses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (response == 0){
            logger.info("\"Why so rude\" option was selected.");
        }
        else {
            logger.info("Message box was closed.");
        }
    }

    private static void errorBox(String s) {

        Toolkit.getDefaultToolkit().beep();
        logger.info("errorBox appeared on the user screen.");

        Object[] options = {"That's sad."};

        int response = JOptionPane.showOptionDialog(null, s, "EasyOnlineClasses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options , options[0]);

        if (response == 0){
            logger.info("\"OK\" option was selected. Terminating all processes...");
        }
        else {
            logger.info("Message box was closed. Terminating all processes...");
        }


    }

    private static void warningBox(String s) {
        Toolkit.getDefaultToolkit().beep();

        logger.info("warningBox appeared on the user screen.");

        Object[] options = {"OK"};

        int response = JOptionPane.showOptionDialog(null, s, "EasyOnlineClasses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options , options[0]);

        if (response == 0){
            logger.info("\"OK\" option was selected.");
        }
        else {
            logger.info("Message box was closed.");
        }
    }

    private static void confirmBox(String s)  {
        Toolkit.getDefaultToolkit().beep();

        logger.info("confirmBox \"" + s + "\" appeared on the user screen.");

        Object[] options = {"Yes", "No, I'll do it myself."};

        int response = JOptionPane.showOptionDialog(null, s, "EasyOnlineClasses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (response == 0){
            logger.info("\"Yes\" option was selected.");

            confirmBox_released = true;
        }
        else if (response == 1){
            logger.info("\"No\" option was selected. Processes ");

        }
        else{
            logger.info("Message box was closed. ");
        }
    }

    private static void joinMeet(String s){

        boolean error_presence = false; // to prevent a spam of errorBox

        logger.info("Accessing the Google Meet page...");
        driver.get(s);
        logger.info("Accessed the Google Meet page.");

        logger.info("Checking if cookie_meet.data exists...");
        CookieFile_Parsing("meet");

        if (ckFile_exists) // goes to meet page one more time after parsing so the account activates
        {
            driver.get(s);
        }



        if (!ckFile_exists || ckFile_corrupted) {

            if (ckFile_studii_exists) {
                logger.info("cookie_meet.data is missing or is corrupted. Asking the user to pass login credentials...");

                infoBox("Please enter your login and password. You have 5 minutes");

                exWait = new WebDriverWait(driver, 300); // 5 minutes until the user logs in.

                logger.info("Waiting until the main page loads after login...");
                exWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='u7qdSd w60wqc']"))); //main meet page, find by logo. CHROME ONLY
                logger.info("Page loaded.");
            }

            CookieFile_Meet();

        }


        logger.info("Waiting until the main meet logo loads...");
        exWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='u7qdSd w60wqc']"))); //main meet page, find by logo. CHROME ONLY
        logger.info("Logo loaded.");


        exWait = new WebDriverWait(driver, 30);

        List <WebElement> Micro_camera = null;


        try{
            exWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[jsname='BOHaEe']")));
            Micro_camera = driver.findElements(By.cssSelector("[jsname='BOHaEe']"));

        } catch (TimeoutException ex){
            logger.severe("No elements were found with cssSelector [jsname='BOHaEe']. Exception message: " + ex.getMessage());

            errorBox("Couldn't turn off microphone and camera. Please continue the process manually.");
            error_presence = true;
        }


        if (!error_presence) {

            try {
                // microphone div
                logger.info("Turning off the microphone. No one wants it turned on of course.");

                //chrome
                Micro_camera.get(0).click();

                logger.info("Turned off the microphone.");

            } catch (ElementClickInterceptedException ex1){
                logger.severe("microphone div is not clickable. Exception message: " + ex1.getMessage());
                errorBox("Microphone button is not clickable. Please continue the process manually. Check the last log file.");
                error_presence = true;
            }

            try {
                // camera div
                logger.info("Turning the camera off. This one is dangerous if not turned off...");

                //chrome
                Micro_camera.get(1).click();

                logger.info("Turned the camera off.");

            } catch (ElementClickInterceptedException ex1) {
                logger.severe("microphone div is not clickable. Exception message: " + ex1.getMessage());

                errorBox("Camera button is not clickable. Please continue the process manually. Check the last log file.");
                error_presence = true;
            }
        }

        if (!error_presence) {
            confirmBox("Are you sure you want to join the lesson right now?");

            if (confirmBox_released) {
                try {
                    // "ask to join" span

                    //chrome
                    logger.info("Searching \"ask to join\" span to click...");
                    //driver.findElement(By.xpath("//*[@id=\"yDmH0d\"]/c-wiz/div/div/div[8]/div[3]/div/div/div[2]/div/div[1]/div[2]/div/div[2]/div/div[1]/div[1]/span/span")).click();
                    driver.findElement(By.cssSelector("[class='NPEfkd RveJvd snByac']")).click();
                    logger.info("\"ask to join\" span clicked.");

                } catch (Exception ex) {
                    logger.severe("Exception occured while interacting with \"ask to join\" span. Exception message: " + ex.getMessage());
                        errorBox("Exception occured while interacting with \"ask to join\" span. Please continue the process manually. Check the last log file.");
                }
            }
        }

        // intentional wait of 120 minutes for user to finish his online classes.

        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofMinutes(120))
                .pollingEvery(Duration.ofSeconds(5)) // pooling every 5 sec so it knows when to exit and not to stress the system too much (default 500 ms)
                .ignoring(NoSuchElementException.class);

        try {
            logger.info("Starting a wait of 120 minutes.");
            fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("what did you expected to see here?")));

        } catch (TimeoutException ex){
            logger.info("TimeoutException on a fake wait. Did the user fell asleep or illegal use of automated browser?" );
            infoBox_custom("EasyOnlineClasses session ended due to timeout of 120 minutes. Did you fall asleep or you're illegally using the automated browser?");

            Exit(403, true);
        }
    }

    private static void joinZoom(String s){
        logger.info("Accessing the Zoom page...");
        driver.get(s);
        logger.info("Done.");

        infoBox("Please launch the zoom meeting by yourself. The browser will close in 5 minutes.");

        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofMinutes(5))
                .pollingEvery(Duration.ofSeconds(5)) // pooling every 5 sec so it knows when to exit and not to stress the system too much (default 500 ms)
                .ignoring(NoSuchElementException.class);

        try{
            logger.info("Starting a wait of 5 minutes...");
            fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("hello there, my fellow user.")));

        } catch (TimeoutException ex){
            logger.info("5 minutes timeout occurred.");
            Exit(110, true);
        }

    }

    private static long waitUntilLessonStart(int Lesson_index){
        String time = null;
        try {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
            time = dateFormat.format(date);

        } catch (Exception ex) {
            errorBox("Unable to get system date. Error message: " + ex.getMessage());

            Exit(111, true);
        }

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        //TODO: user manually passes the lesson intervals in conf.properties

        // -5 minutes
        String firstLesson = "08-25-00";
        String secondLesson = "09-20-00";
        String thirdLesson = "10-15-00";
        String fourthLesson = "11-20-00";
        String fifthLesson = "12-25-00";
        String sixthLesson = "13-20-00";
        String seventhLesson = "14-10-00";

        assert time != null;
        StringTokenizer time_token = new StringTokenizer(time, "-");

        int time_HH = Integer.parseInt(time_token.nextToken());
        int time_mm = Integer.parseInt(time_token.nextToken());
        int time_ss = Integer.parseInt(time_token.nextToken());

        long time_ms = (time_HH * hoursInMilli) + (time_mm * minutesInMilli) + (time_ss * secondsInMilli);


        StringTokenizer lesson_token;
        int Lesson_HH;
        int Lesson_mm;
        int Lesson_ss;
        long lesson_ms = 0;

        switch (Lesson_index){
            case 1 :
                lesson_token = new StringTokenizer(firstLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 2 :
                lesson_token = new StringTokenizer(secondLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 3 :
                lesson_token = new StringTokenizer(thirdLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 4 :
                lesson_token = new StringTokenizer(fourthLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 5 :
                lesson_token = new StringTokenizer(fifthLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 6 :
                lesson_token = new StringTokenizer(sixthLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            case 7 :
                lesson_token = new StringTokenizer(seventhLesson, "-");

                Lesson_HH = Integer.parseInt(lesson_token.nextToken());
                Lesson_mm = Integer.parseInt(lesson_token.nextToken());
                Lesson_ss = Integer.parseInt(lesson_token.nextToken());

                lesson_ms = (Lesson_HH * hoursInMilli) + (Lesson_mm * minutesInMilli) + (Lesson_ss * secondsInMilli);

                break;

            default:
                logger.severe("Invalid Lesson_index in waitUntilLessonStart() method.");
                Exit(112, true);

                break;
        }

        long wait = 0;

        if (time_ms <= lesson_ms) {
            wait = lesson_ms - time_ms;
        }

        if (wait >= 1800000){ //don't wait if it is more than 30 mins
            wait = 0;
        }

        return wait;
    }

    //main
    public static void main(String[] args){

        // PATHS  (HARD-CODED)
        // test - 0
        // build - 1
        paths = 0;

        try {
            // VOID METHODS BEFORE START
            getTime();
            logFile();

            // MESSAGE BOX WITH LESSON INDEX
            int Lesson_index = inputBox();

            // MAIN START
            WebDriver();

            // EXPLICIT WAIT
            exWait = new WebDriverWait(driver, 60);

            // BUILD VERSION INFO
            logger.info("EasyOnlineClasses Release v1.0");


            try {

                logger.info("Accessing https://studii.md");
                driver.get("https://studii.md"); // accessing the site
                logger.info("Accessed https://studii.md");

                logger.info("Deleting all cookies from https://studii.md.");
                driver.manage().deleteAllCookies();
                logger.info("Done.");

                logger.info("Checking if cookies_studii.data exists...");

                CookieFile_Parsing("studii");

                if (!ckFile_exists || ckFile_corrupted)  // getting the cookie if the file does not exist or is corrupted
                {
                    logger.info("cookie_studii.data is missing or is corrupted. Creating a new one...");

                    exWait = new WebDriverWait(driver, 15);

                    while (true) {
                        try {
                            logger.info("Switching to \"topbar-panel\" iframe...");

                            // accessing the iframe where "login" is located
                            exWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("topbar-panel"));

                            logger.info("Switched to \"topbar-panel\" iframe.");

                        } catch (org.openqa.selenium.NoSuchFrameException ex) {
                            logger.severe("Invalid IFRAME name or id. Error message: " + ex.getMessage());
                            errorBox("Invalid IFRAME name or id. Error message: " + ex.getMessage());

                            Exit(200, true);

                        } catch (StaleElementReferenceException ex1) {
                            logger.info("StaleElementReferenceException. Trying again.");
                            driver.navigate().refresh();

                            continue;
                        }

                        try {
                            logger.info("Clicking \"login\" button...");

                            exWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[id='user-login']")));
                            driver.findElement(By.cssSelector("[id='user-login']")).click();

                            logger.info("\"login\" button is clicked.");

                            break;

                        } catch (ElementClickInterceptedException ex1) {
                            logger.severe("Login element in iframe is not clickable. Error message: " + ex1.getMessage());
                            errorBox("Login element in iframe is not clickable. Error message: " + ex1.getMessage());

                            Exit(201, true);

                        } catch (StaleElementReferenceException ex2) {
                            logger.info("StaleElementReferenceException. Trying again.");
                            driver.navigate().refresh();

                        } catch (TimeoutException ex3) {
                            logger.info("Timeout Exception. Trying again...");
                            driver.navigate().refresh();
                        }

                    }
                    exWait = new WebDriverWait(driver, 30);

                    logger.info("Switching back to default html...");
                    driver.switchTo().defaultContent();
                    logger.info("Switched back to default html.");

                    try {
                        logger.info("Clicking \"enter with google account\" button...");

                        exWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[class='login__socials__item is-google']")));
                        driver.findElement(By.cssSelector("[class='login__socials__item is-google']")).click();

                        logger.info("\"enter with google account\" button is clicked.");

                    } catch (TimeoutException ex) {
                        logger.severe("\"Enter with google account\" button is not found. Error message: " + ex.getMessage());
                        errorBox("\"Enter with google account\" button is not found. Error message: " + ex.getMessage());

                        Exit(202, true);

                    } catch (ElementClickInterceptedException ex1) {
                        logger.severe("\"Enter with google account\" button is not clickable. Error message: " + ex1.getMessage());
                        errorBox("\"Enter with google account\" button is not clickable. Error message: " + ex1.getMessage());

                        Exit(203, true);
                    }

                    infoBox("Please enter your login credentials. You have 5 minutes."); // too evil?


                    exWait = new WebDriverWait(driver, 300); // 5 minutes for user to pass login credentials manually.

                    try {
                        logger.info("Waiting until the user logs in google accounts to proceed...");
                        exWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("root"))); //wait until the main page loads.
                        logger.info("\"Studii\" window loaded. Huh, 5 minutes were enough.");

                    } catch (org.openqa.selenium.TimeoutException ex) {
                        logger.severe("TimeoutExeption on manual login in google accounts." + ex.getMessage());
                        errorBox("You did not use your 5 minutes properly. Shame on you.");

                        Exit(204, true);
                    }

                    CookieFile_Studii();
                    // END OF COOKIE SEGMENT
                }

                String currentDate = currentDate();
                //String currentDate = "12 ianuarie"; //test on a specific date

                exWait = new WebDriverWait(driver, 15);

                List<WebElement> Lesson_date_list = null;
                try {
                    logger.info("Searching \"" + currentDate + "\" in html.");
                    exWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()= '" + currentDate + "']")));
                    Lesson_date_list = driver.findElements(By.xpath("//span[text()= '" + currentDate + "']"));

                } catch (TimeoutException ex){
                    logger.severe("Did not find any lessons with current date. Exception message: " + ex.getMessage());
                    errorBox("Did not find any lessons with current date.");
                }

                WebElement Lesson_date = null;

                assert Lesson_date_list != null;
                if (Lesson_date_list.size() == 0) { // not even likely to happen
                    logger.severe("The school day with this date " + currentDate + " was not found in the current page.");
                    errorBox("The school day with this date " + currentDate + " was not found in the current page.");

                    Exit(205, true);
                }

                if (Lesson_date_list.size() == 1) {
                    logger.info("There was 1 date found in html.");
                    Lesson_date = Lesson_date_list.get(0); // date is located in the right position
                }

                if (Lesson_date_list.size() == 2) {
                    logger.info("There were 2 dates found in html. Choosing the right one.");
                    Lesson_date = Lesson_date_list.get(1); // caught 2 xpaths with the same date - getting the second one
                }


                logger.info("Generating the XPATH for the school day...");
                assert Lesson_date != null;
                String Lesson_date_xpath = generateXPATH(Lesson_date, ""); // /html/body/div/div/div[1]/div/div[4]/div[3]/div[1]/span - 3rd day container for reference
                logger.info("Generated the XPATH for the school day.");

                try {
                    logger.info("Pressing the lesson container...");

                    exWait.until(ExpectedConditions.elementToBeClickable(By.xpath(Lesson_date_xpath + "/parent::div/following-sibling::div/div[" + Lesson_index + "]")));
                    // /html/body/div/div/div[1]/div/div[4]/div[3]/div[1]/span/parent::div/following-sibling::div/div[x] - xpath for reference
                    driver.findElement(By.xpath(Lesson_date_xpath + "/parent::div/following-sibling::div/div[" + Lesson_index + "]")).click();

                    logger.info("Pressed the lesson container.");

                } catch (TimeoutException ex) {
                    logger.severe("The lesson_index is invalid. Error message: " + ex.getMessage());
                    errorBox("The lesson_index is invalid. Error message: " + ex.getMessage());

                    Exit(206, true);

                } catch (ElementClickInterceptedException ex1) {
                    logger.severe("Unable to press the lesson container. Error message: " + ex1.getMessage());
                    errorBox("Unable to press the lesson container. Error message: " + ex1.getMessage());

                    Exit(207, true);
                }

                String Lesson_link;
                logger.info("Starting to search in containers...");
                while (true) {

                    driver.manage().timeouts().implicitlyWait(2500, TimeUnit.MILLISECONDS); //2.5 s wait for each request => refresh every 5 seconds

                    try {
                        //logger.info("Looking for the link in \"Lesson topic\" container..."); // floods the logs
                        Lesson_link = driver.findElement(By.xpath("/html/body/div/div/div[1]/div/div[4]/div/div[1]/div[2]/span[2]/a")).getText(); // xpath for link

                        if (Lesson_link.startsWith("https://meet.google.com")) {
                            logger.info("Found the Google Meet link in \"Lesson topic\" container.");

                            break;
                        }

                        if (Lesson_link.startsWith("https://us04web.zoom")){
                            logger.info("Found the Zoom link in \"Lesson topic\" container.");

                            break;
                        }

                    } catch (NoSuchElementException e1) {
                        try {
                            //logger.info("Did not find the link in \"Lesson topic\" container."); // floods the logs

                            //logger.info("Looking for the link in \"Homework\" container...");
                            Lesson_link = driver.findElement(By.xpath("/html/body/div/div/div[1]/div/div[4]/div/div[1]/div[3]/span[2]/a")).getText(); // xpath for link

                            if (Lesson_link.startsWith("https://meet.google.com")) {
                                logger.info("Found the Google Meet link in \"Homework\" container.");

                                break;
                            }

                            if (Lesson_link.startsWith("https://us04web.zoom")){
                                logger.info("Found the Zoom link in \"Homework\" container.");

                                break;
                            }

                        } catch (NoSuchElementException e2) {
                            //logger.info("Did not find the link in \"Homework\" container."); // floods the logs
                            logger.info("Refreshing the page...");

                            driver.navigate().refresh();
                        }
                    }
                }

                long wait_s = waitUntilLessonStart(Lesson_index) / 1000; //waitUntilLessonStart() returns ms. we need s

                exWait = new WebDriverWait(driver, wait_s);

                //Lesson_link = "https://meet.google.com/xxx-xxx-xxx"; // hard code link for testing.

                if (Lesson_link.startsWith("https://meet.google.com")) {

                    if (wait_s > 0) {
                        try {
                            logger.info("Starting a wait of " + wait_s + " s until 5 minutes before the lesson start...");
                            exWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("Waiting...")));

                        } catch (TimeoutException ex) {
                            logger.info("Finished waiting.");
                        }
                    }
                    else {
                        logger.info("Wait is not required. Continuing...");
                    }

                    joinMeet(Lesson_link);

                }
                else if (Lesson_link.startsWith("https://us04web.zoom")){

                    if (wait_s > 0) {
                        try {
                            logger.info("Starting a wait of " + wait_s + " ms until 5 minutes before the lesson start...");
                            exWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("Waiting...")));

                        } catch (TimeoutException ex) {
                            logger.info("Finished waiting.");
                        }
                    }
                    else {
                        logger.info("Wait is not required. Continuing...");
                    }

                    joinZoom(Lesson_link);

                }
                else {
                    logger.severe("Unknown link found. The link was - " + Lesson_link);
                    errorBox("Unknown link found. The link is " + Lesson_link);

                    Exit(208, true);
                }

            } catch (Exception e) {

                if (e.getMessage().startsWith("chrome not reachable") || e.getMessage().startsWith("no such window")) {
                    logger.info("Browser closed by user.");

                    Exit(209, true);
                }
                else {
                    logger.severe("UNKNOWN EXCEPTION. Error message: " + e.getMessage());
                    errorBox("UNKNOWN EXCEPTION. Error message: " + e.getMessage());

                    Exit(400, true);
                }
            }

            finally {

                Exit(210, true);
            }
        }
        catch (SessionNotCreatedException ex){
            logger.severe("Session was not created. Error message: " + ex.getMessage());

            Exit(211, false);
        }
        catch (WebDriverException ex1){
            logger.severe("Couldn't load the page. Check your internet connection. Error message: " + ex1.getMessage());

            Exit(212, true);
        }
    }
}