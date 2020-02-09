package com.example.demo.controllers.automation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.AWSDBConnection;
import com.utils.SOAPClientSAAJ;


public class FnacTrackingNumber {

	String username;
	String password;
	String phoneNumber;
	static String propFilePath = "files/fnaccredentials.properties";
	Map<String, String> AmazonFnacTransporterMap=new HashMap<String, String>();
	String trackingUrl="https://www.amazon.fr/gp/your-account/order-history/ref=ppx_yo_dt_b_search?opt=ab&__mk_fr_FR=%C3%85M%C3%85%C5%BD%C3%95%C3%91&search=";
	static CommonMethods commonMethods = new CommonMethods();
	
	static String loggerMsg="";
	static String fnacBaseUrl="https://mp.fnac.com/compte/vendeur/commande/";
	WebDriverWait wait;
	WebDriver driver;
	static Map<String, String[]> orderNumbersStatusMap=new HashMap<String, String[]>();
	ArrayList<String> fnacOrderNumbers = new ArrayList<String>();
	
	Logger logger = commonMethods.initLogger("","output/FnacScrapeTrackingNumber"+new SimpleDateFormat("yyyy_MM_yy_HH_mm_ss").format(new Date())+".log");

	public static void main(String[] args) {
		
//		for testing api
//		String[] carrierNameNumber=new String[2];
//		carrierNameNumber[0]="55830012042674140";
//		carrierNameNumber[1]="COLIS PRIVE";
//		orderNumbersStatusMap.put("GDEOMDBGULNII", carrierNameNumber);
		new FnacTrackingNumber().startScrapeTrackingNumber();	
	}

	public void startScrapeTrackingNumber() {
		driver = commonMethods.initDriver();
		wait = new WebDriverWait(driver, 15);
		try {
			initProps();
			initAmazonFnacTransporterMap();
			login();
			scrapeOrderNumber();
			Map<String,String> orderNumbersMap=AWSDBConnection.getAllOrderNumber(fnacOrderNumbers,"fnac_shipping_address");
			// just for log
			for (String orderNumber : orderNumbersMap.keySet()) {
				logger.info(orderNumber+":"+orderNumbersMap.get(orderNumber));
			}
			commonMethods.amazonLogin(driver,wait);
			for (String orderNumber : orderNumbersMap.keySet()) {
				try {
					loggerMsg="";
					String amazonOrder=orderNumbersMap.get(orderNumber);
					loggerMsg=orderNumber+","+amazonOrder;
					if(amazonOrder!=null && !amazonOrder.trim().isEmpty() && !amazonOrder.trim().equalsIgnoreCase("")) {
						String url=trackingUrl+amazonOrder.trim();
						loggerMsg=loggerMsg+","+url;
						driver.get(url);
						commonMethods.getOrderDetails(orderNumber, driver, wait, loggerMsg, logger, orderNumbersStatusMap);
					}
				} catch (Exception e) {
					System.out.println("error "+e);
					
					isLoginRequested();
					// repeat if first attempt fails
					driver.get(trackingUrl+orderNumbersMap.get(orderNumber).trim());
					commonMethods.getOrderDetails(orderNumber, driver, wait, loggerMsg, logger, orderNumbersStatusMap);
				}
				logger.info("Fnac order number :"+orderNumber +" amazon order number : "+orderNumbersMap.get(orderNumber));
			}
			pushValuesToFnac();
			commonMethods.forceWait((long) 5000);
			driver.quit();
		} catch (Exception e) {
			System.out.println("error" + e);
		}

	}

	public void login() {
		driver.get("https://mp.fnac.com/vendre/pro");
		commonMethods.waitAndGet(driver, wait, By.id("_username")).sendKeys(username);
		commonMethods.waitAndGet(driver, wait, By.id("_password")).sendKeys(password);
		commonMethods.waitAndGet(driver, wait, By.id("customer_login_signin")).click();
		commonMethods.forceWait((long) 2000);
		WebElement telephoneNumber=commonMethods.waitAndGet(driver, wait, By.id("seller_auth_call_call_phone"));
		Select phoneNumberDropdown=new Select(telephoneNumber);
		phoneNumberDropdown.selectByVisibleText(phoneNumber);
		
		WebElement phoneRadio=commonMethods.waitAndGet(driver, wait, By.id("seller_auth_call_call_type_1"));
		commonMethods.actionClick(driver, phoneRadio);
		commonMethods.waitAndGet(driver, wait, By.id("client_seller_auth")).click();
		commonMethods.prompt("Get the code and push it please and click resume");
	}
	
	
	public void initProps() {
		Properties prop = commonMethods.loadProps(propFilePath);
		username = prop.getProperty("username");
		password = prop.getProperty("password");
		phoneNumber = prop.getProperty("phoneNumber");
	}
	
	public void initAmazonFnacTransporterMap() {
		AmazonFnacTransporterMap.put("AZ Logistics", "Amazon Logistics");
		AmazonFnacTransporterMap.put("COLIS PRIVE", "COLISPRIVE");
		AmazonFnacTransporterMap.put("La Poste", "La Poste");
		AmazonFnacTransporterMap.put("DHL", "DHL");
		AmazonFnacTransporterMap.put("UPS", "UPS");
		AmazonFnacTransporterMap.put("TNT", "TNT");
		AmazonFnacTransporterMap.put("Chronopost", "Chronopost");
	}

	
	
	public void scrapeOrderNumber() {
		driver.get(
				"https://mp.fnac.com/compte/vendeur/commandes/date_desc/1/30");
		getOrderNumberOfCurrentPage();
	}

	public void getOrderNumberOfCurrentPage() {
		List<WebElement> orderNumberElements = commonMethods.getAllElements(driver, wait,
				By.xpath("//div/a[contains(@href,'/compte/vendeur/commande/')]"));
		for (int j = 0; j <orderNumberElements.size(); j++) {
			String orderNumber=orderNumberElements.get(j).getText().trim();
			System.out.println("orderNumber "+orderNumber);
			fnacOrderNumbers.add(orderNumber);
			logger.info(orderNumber);
		}
		List<WebElement> nextPage = commonMethods.getAllElements(driver, wait, By.xpath("//a[contains(@class,'next page-next')]"));
		if (nextPage.size() > 0) {
			nextPage.get(0).click();
			getOrderNumberOfCurrentPage();
		}
	}
	
	
	
	public void searchOrder(String amazonOrderID) {
		driver.get("https://www.amazon.fr/gp/css/order-history?ref_=nav_orders_first");
		commonMethods.waitAndGet(driver, wait, By.id("searchOrdersInput")).sendKeys("");
		commonMethods.waitAndGet(driver, wait, By.xpath("//*[@id='a-autoid-0']/span/input")).click();
	}
	
	public void pushValuesToFnac() {
		try {
			System.out.println("---------**** Started pushing to fnac ****---------------");
			for (String orderNumber : orderNumbersStatusMap.keySet()) {
				try {
					commonMethods.forceWait((long) 1500);
					driver.get(fnacBaseUrl+orderNumber);
					String[] trackingCarrierNameArray=orderNumbersStatusMap.get(orderNumber);
					String carrierName=trackingCarrierNameArray[1];
					String trackingNumber=trackingCarrierNameArray[0];
					WebElement carrierElement=commonMethods.waitAndGet(driver, wait, By.id("tracking_transporter1"));
					Select phoneNumberDropdown=new Select(carrierElement);
					try {
						if(AmazonFnacTransporterMap.containsKey(carrierName)) {
							phoneNumberDropdown.selectByVisibleText(AmazonFnacTransporterMap.get(carrierName));
						}else {
							if(carrierName.contains("DHL")) {
								phoneNumberDropdown.selectByVisibleText("DHL");
							}else {
								phoneNumberDropdown.selectByVisibleText(carrierName);
							}
						}
					} catch (Exception e) {
						System.out.println("Failed orderNumber: "+orderNumber+"carrierName: "+carrierName +"trackingNumber "+trackingNumber +e);
						logger.info("Failed orderNumber: "+orderNumber+"carrierName: "+carrierName +"trackingNumber "+trackingNumber);
					}
					WebElement trackingElement=commonMethods.waitAndGet(driver, wait, By.id("shipping_transporters_order_details_0_tracking_number"));
					trackingElement.sendKeys(trackingNumber);
					logger.info(orderNumber+" : "+trackingNumber+" : "+ carrierName);
					commonMethods.waitAndGet(driver, wait, By.id("accept_submit")).click();
					
				} catch (Exception e) {
					logger.info("Exception "+e);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			System.out.println("---------**** completed pushing to fnac ****---------------");
		}
	}
	
	public void isLoginRequested() {
		List<WebElement> passwordElements=commonMethods.getAllElements(driver, wait, By.id("ap_password"));
		if(passwordElements.size()>0) {
			passwordElements.get(0).sendKeys(password);
		}
		List<WebElement> signInSubmitElements=commonMethods.getAllElements(driver, wait, By.id("signInSubmit"));
		if(signInSubmitElements.size()>0) {
			signInSubmitElements.get(0).click();
		}
		commonMethods.forceWait((long) 2000);
		// verification requested
		List<WebElement> cartCountElements = commonMethods.getAllElements(driver, wait, By.xpath("//h1[contains(.,'Vérification')]"));
		List<WebElement> continueButton = commonMethods.getAllElements(driver, wait, By.id("continue"));
		if (continueButton.size() != 0 || cartCountElements.size()!=0) {
			commonMethods.prompt("Get the code and push it please and click resume");
		}
	}


}
