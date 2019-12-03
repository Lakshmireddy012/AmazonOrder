package com.example.demo.controllers.automation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demo.model.ChatInfo;
import com.example.demo.model.Message;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FetchMessages {

	String username;
	String password;
	static String propFilePath="files/cdiscoundcredentials.properties";
	static String filepath="files/FetchMessages.csv";
	static CommonMethods commonMethods=new CommonMethods();
	WebDriverWait wait;
	WebDriver driver;
	
	Map<String , ChatInfo> chatInfoMap=new HashMap<String, ChatInfo>();
	
	public static void main(String[] args) {
		new FetchMessages().startFetchMessages();
	}
	
	public void startFetchMessages() {
		driver=commonMethods.initDriver();
		wait = new WebDriverWait(driver, 15);
		try {
			initProps();
			login();
			ArrayList<String> urlsToFetchMessages=readUrlsCSVData(filepath);
			for (String url : urlsToFetchMessages) {
				try {
					fetchMessages(url);
					
				} catch (Exception e) {
					System.out.println("exception "+e);
				}
			}
			driver.quit();
			saveToFirebase();
		} catch (Exception e) {
			System.out.println("error"+e);
		}
		
		
	}
	
	public void login() {
		driver.get("https://seller.cdiscount.com/login");
		commonMethods.waitAndGet(driver, wait, By.id("Login")).sendKeys(username);
		commonMethods.waitAndGet(driver, wait, By.id("Password")).sendKeys(password);
		commonMethods.waitAndGet(driver, wait, By.id("save")).click();		
	}
	public void initProps() {
		Properties prop=commonMethods.loadProps(propFilePath);
		username=prop.getProperty("username");
		password=prop.getProperty("password");
	}
	
	public ArrayList<String> readUrlsCSVData(String filepath) {
		BufferedReader br = null;
		ArrayList<String> urlsToFetchMessages=new ArrayList<String>(); 
        String line = "";
		try { 
			  
			br = new BufferedReader(new FileReader(filepath));
            while ((line = br.readLine()) != null) {
            	urlsToFetchMessages.add(line);          
            }
	    } 
	    catch (Exception e) { 
	    	e.printStackTrace(); 
	    } 
		return urlsToFetchMessages;
	}
	
	public void fetchMessages(String url) {
		String orderNumber=url.substring(url.lastIndexOf("/")+1, url.length());
		System.out.println("url "+url  + " orderNumber "+orderNumber);
		driver.get(url);
		List<WebElement> inbox=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='MessagesListContainer']/li/a"));
		System.out.println("inbox "+ inbox.size());
		for (int j = 1; j <= inbox.size(); j++) {
			List<WebElement> msgDate=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='MessagesListContainer']/li["+j+"]/a//div[@class='mListDate']"));
			List<WebElement> name=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='MessagesListContainer']/li["+j+"]/a//h2"));
			List<WebElement> subject=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='MessagesListContainer']/li["+j+"]/a//div[@class='mSubject']"));
			List<WebElement> orderRelated=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='MessagesListContainer']/li["+j+"]/a//div[@class='mSubject selectableText']"));
			String msgDateVal= null;
			String nameVal= null;
			String subjectVal= null;
			String orderRelatesVal= null;
			if(msgDate.size()>0)
				 msgDateVal=msgDate.get(0).getText();
			if(msgDate.size()>0)
			 nameVal=name.get(0).getText();
			if(subject.size()>0)
			 subjectVal=subject.get(0).getText();
			if(orderRelated.size()>0)
			 orderRelatesVal=orderRelated.get(0).getText();
			inbox.get(j-1).click();
			
			commonMethods.forceWait((long) 3000);
			commonMethods.waitAndGet(driver, wait, By.xpath("//*[@onclick='SlideDownAll()']")).click();
			List<WebElement> listOfMessages=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='messageChat']//ul[@class='mChatList']/li"));
			System.out.println("listOfMessages "+ listOfMessages.size());
			List<Message> msgList=new ArrayList<Message>();
			for (int i = 1; i <= listOfMessages.size(); i++) {
				List<WebElement> messageChatDate=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='messageChat']//ul[@class='mChatList']/li["+i+"]/div/div[@class='mChatDate']"));
				List<WebElement> messageHeader=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='messageChat']//ul[@class='mChatList']/li["+i+"]/div/h3"));
				List<WebElement> messageChatDateTime=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='messageChat']//ul[@class='mChatList']/li["+i+"]/div/div/div[@class='mChatDate']"));
				List<WebElement> messageContent=commonMethods.getAllElements(driver, wait, By.xpath("//*[@id='messageChat']//ul[@class='mChatList']/li["+i+"]/div/div/p"));
				String chatDateVal= null;
				String chatHeaderVal = null;
				String dataTimeVal= null;
				String content= null;
				System.out.println("size"+messageChatDate.get(0).getAttribute("textContent"));
				if(messageChatDate.size()>0) {
					chatDateVal=messageChatDate.get(0).getText();
					if(chatDateVal.isEmpty() || chatDateVal.equals("")) {
						chatDateVal=messageChatDate.get(0).getAttribute("textContent").trim();
					}
				}
					
				if(messageHeader.size()>0) {
					chatHeaderVal=messageHeader.get(0).getText();
					if(chatHeaderVal.isEmpty() || chatHeaderVal.equals("")) {
						chatHeaderVal=messageHeader.get(0).getAttribute("textContent").trim();
					}
				}
					
				if(messageChatDateTime.size()>0) {
					dataTimeVal=messageChatDateTime.get(0).getText();
					if(dataTimeVal.isEmpty() || dataTimeVal.equals("")) {
						dataTimeVal=messageChatDateTime.get(0).getAttribute("textContent").trim();
					}
				}
					
				if(messageContent.size()>0) {
					content=messageContent.get(0).getText();
					if(content.isEmpty() || content.equals("")) {
						content=messageContent.get(0).getAttribute("textContent").trim();
					}
				}
					
				System.out.println("vals"+chatDateVal + chatHeaderVal + dataTimeVal + content);
				Message message=new Message(chatHeaderVal, chatDateVal, dataTimeVal, content);
				msgList.add(message);
			}
			ChatInfo chatInfo=new ChatInfo(orderNumber, subjectVal, nameVal, orderRelatesVal, msgDateVal, msgList);
			chatInfoMap.put(orderNumber, chatInfo);
		}
	}
	public void saveToFirebase() {
		try {
			FileInputStream refreshToken = new FileInputStream("files/refreshToken.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(GoogleCredentials.fromStream(refreshToken))
			    .setDatabaseUrl("https://artificialpermissionsolutions.firebaseio.com")
			    .build();

			FirebaseApp.initializeApp(options);
			Firestore db = FirestoreClient.getFirestore();
			WriteBatch batch = db.batch();
			for (String key : chatInfoMap.keySet()) {
				DocumentReference nycRef = db.collection("messages").document(key);
				batch.set(nycRef, chatInfoMap.get(key));
			}
			ApiFuture<List<WriteResult>> future = batch.commit();
			for (WriteResult result :future.get()) {
			  System.out.println("Update time : " + result.getUpdateTime());
			}
		}catch (Exception e) {
			System.out.println("Exception"+ e);
		}
	}
}
