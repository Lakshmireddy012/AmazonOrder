package com.example.demo.controllers.automation;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RakutenSalesList {
	
	static CommonMethods commonMethods = new CommonMethods();
	WebDriver driver;
	WebDriverWait wait;
	
	public static void main(String[] args) {
		RakutenSalesList s=new RakutenSalesList();
		s.start();

	}
	public void start() {
		try {
			init();
			try {
				driver.get("https://fr.shopping.rakuten.com/user");
				commonMethods.rakutenLogin(driver, wait);
				driver.get("https://fr.shopping.rakuten.com/purchase?action=salelist");
				downLoadSalesList();
			} catch (Exception e) {
				commonMethods.prompt("Detected as BOT , Enter manually");
				commonMethods.rakutenLogin(driver, wait);
				driver.get("https://fr.shopping.rakuten.com/purchase?action=salelist");
				downLoadSalesList();
			}
			
		} catch (Exception e) {
			driver.close();
		} finally {
			driver.close();
		}
	}
	
	public void init() {
		driver = commonMethods.initDriver();
		wait = new WebDriverWait(driver, 15);
	}
	public void downLoadSalesList() {
		try {
			List<WebElement> salesListLink= commonMethods.getAllElements(driver, wait, By.linkText("Export comptabilité"));
			if(salesListLink.size()>0) {
				salesListLink.get(0).click();
			}
			commonMethods.forceWait((long) 60000);
		} catch (Exception e) {
			commonMethods.prompt("Detected as BOT , Enter manually");
			downLoadSalesList();
		}
	}
}
