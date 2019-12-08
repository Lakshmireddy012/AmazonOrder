package com.example.demo.controllers.automation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.AWSDBConnection;
import com.utils.FirebaseUtils;
import com.utils.SOAPClientSAAJ;


public class ScrapeTrackingNumber {

	String username;
	String password;
	static String propFilePath = "files/cdiscoundcredentials.properties";
	static String amazonPropFilePath = "files/amazoncredentials.properties";
	String trackingUrl="https://www.amazon.fr/gp/your-account/order-history/ref=ppx_yo_dt_b_search?opt=ab&__mk_fr_FR=%C3%85M%C3%85%C5%BD%C3%95%C3%91&search=";
	static CommonMethods commonMethods = new CommonMethods();
	static String loggerMsg="";
	WebDriverWait wait;
	WebDriver driver;
	static Map<String, String[]> orderNumbersStatusMap=new HashMap<String, String[]>();
	ArrayList<String> cDiscoundOrderNumbers = new ArrayList<String>();
	
	Logger logger = commonMethods.initLogger("","output/ScrapeTrackingNumber"+new SimpleDateFormat("yyyy_MM_yy_HH_mm_ss").format(new Date())+".log");

	public static void main(String[] args) {
		new ScrapeTrackingNumber().startScrapeTrackingNumber();
//		for testing api
//		String[] carrierNameNumber=new String[2];
//		carrierNameNumber[0]="CC729616671DE";
//		carrierNameNumber[1]="DHL";
//		orderNumbersStatusMap.put("19111912289AV84", carrierNameNumber);
//		new ScrapeTrackingNumber().pushValuesToCDiscountApi();
	}

	public void startScrapeTrackingNumber() {
		driver = commonMethods.initDriver();
		wait = new WebDriverWait(driver, 15);
		try {
			initProps();
			login();
			scrapeOrderNumber();
			Map<String,String> orderNumbersMap=FirebaseUtils.getAllOrderNumber(cDiscoundOrderNumbers,"order_shipped");
			// just for log
			for (String orderNumber : orderNumbersMap.keySet()) {
				logger.info(orderNumber+":"+orderNumbersMap.get(orderNumber));
			}
//			amazonInitProps();
//			amazonLogin();
			commonMethods.amazonLogin(driver,wait);
			for (String orderNumber : orderNumbersMap.keySet()) {
				try {
					loggerMsg="";
					String amazonOrder=orderNumbersMap.get(orderNumber);
					loggerMsg=orderNumber+","+amazonOrder;
					if(amazonOrder!=null && !amazonOrder.trim().isEmpty()) {
						String url=trackingUrl+amazonOrder.trim();
						loggerMsg=loggerMsg+","+url;
						driver.get(url);
						commonMethods.getOrderDetails(orderNumber, driver, wait, loggerMsg, logger, orderNumbersStatusMap);
						//getOrderDetails(orderNumber);
					}
				} catch (Exception e) {
					System.out.println("error "+e);
					
					//amazonLogin();
					commonMethods.prompt("Resume after manual operation");
					// repeat if first attempt fails
					driver.get(trackingUrl+orderNumbersMap.get(orderNumber));
					commonMethods.getOrderDetails(orderNumber, driver, wait, loggerMsg, logger, orderNumbersStatusMap);
					//getOrderDetails(orderNumber);
				}
				logger.info("c discount order number :"+orderNumber +" amazon order number : "+orderNumbersMap.get(orderNumber));
			}
			driver.quit();
			// push values to cdiscount api
			pushValuesToCDiscountApi();
		} catch (Exception e) {
			System.out.println("error" + e);
		}

	}

	public void login() {
		driver.get("https://seller.cdiscount.com/login");
		commonMethods.waitAndGet(driver, wait, By.id("Login")).sendKeys(username);
		commonMethods.waitAndGet(driver, wait, By.id("Password")).sendKeys(password);
		commonMethods.waitAndGet(driver, wait, By.id("save")).click();
	}
	
	public void amazonLogin() {
		driver.get("https://www.amazon.fr/login");
		commonMethods.waitAndGet(driver, wait, By.id("ap_email")).sendKeys(username);
		commonMethods.waitAndGet(driver, wait, By.id("continue")).click();
		commonMethods.waitAndGet(driver, wait, By.id("ap_password")).sendKeys(password);
		commonMethods.waitAndGet(driver, wait, By.name("rememberMe")).click();
		commonMethods.waitAndGet(driver, wait, By.id("signInSubmit")).click();
		// verify its home page or not
		List<WebElement> cartCountElements = commonMethods.getAllElements(driver, wait, By.id("nav-cart-count"));
		if (cartCountElements.size() == 0) {
			// check continue button , yes click to proceed
//			List<WebElement> continueElements = commonMethods.getAllElements(driver, wait, By.id("continue"));
//			if (continueElements.size() != 0) {
//				continueElements.get(0).click();
//			}
			// show prompt for manual operation
			commonMethods.prompt("Get the code and push it please and click resume");
		}
	}

	public void initProps() {
		Properties prop = commonMethods.loadProps(propFilePath);
		username = prop.getProperty("username");
		password = prop.getProperty("password");
	}
	
	public void amazonInitProps() {
		Properties prop = commonMethods.loadProps(amazonPropFilePath);
		username = prop.getProperty("amazon_username");
		password = prop.getProperty("amazon_password");
	}

	public void scrapeOrderNumber() {
		driver.get(
				"https://seller.cdiscount.com/order/management/AcceptedToShip/All/All/All/01-01-0001/31-12-9999/1/ScopusId");
		getOrderNumberOfCurrentPage();

	}

	public void getOrderNumberOfCurrentPage() {
		List<WebElement> orderNumberElements = commonMethods.getAllElements(driver, wait,
				By.xpath("//div/a[contains(@href,'/Order/Detail/')]"));
		for (int j = 0; j <orderNumberElements.size(); j++) {
			String orderNumber=orderNumberElements.get(j).getText().trim();
			System.out.println("orderNumber "+orderNumber);
			cDiscoundOrderNumbers.add(orderNumber);
			logger.info(orderNumber);
		}
		List<WebElement> nextPage = commonMethods.getAllElements(driver, wait, By.xpath("//a[@aria-label='Next']"));
		if (nextPage.size() > 0) {
			nextPage.get(0).click();
			getOrderNumberOfCurrentPage();
		}
	}
	
	public void getOrderDetails(String orderNumber) {
		commonMethods.forceWait((long) 2000);
		List<WebElement> trackPackageButton=commonMethods.getAllElements(driver, wait, By.xpath("//a[contains(.,'Suivre votre colis')]"));
		if(trackPackageButton.size()>0) {
			String packageUrl=trackPackageButton.get(0).getAttribute("href");
			System.out.println("packageUrl "+packageUrl);
			boolean isShipped=packageUrl.contains("packageIndex=0");
			System.out.println("isShipped "+isShipped);
			if(isShipped) {
				trackPackageButton.get(0).click();
				commonMethods.forceWait((long) 2000);
				List<WebElement> trackingNUmber=commonMethods.getAllElements(driver, wait, By.xpath("//a[contains(.,'Numéro de suivi')]"));
				List<WebElement> shippedWithElement=commonMethods.getAllElements(driver, wait, By.xpath("//h1[contains(.,'Expédié avec')  or contains(.,'Livraison par')]"));
				if(trackingNUmber.size()>0) {
					String trackingNumberText=trackingNUmber.get(0).getText();
					String trackingNumber=trackingNumberText.split(":")[1].trim();
					System.out.println("trackingNumberText"+trackingNumberText );
					System.out.println("trackingNumber"+trackingNumber );
					loggerMsg=loggerMsg+","+trackingNumber;
					String carrierName=null;
					if(shippedWithElement.size()>0) {
						String shippedText=shippedWithElement.get(0).getText();
						carrierName=shippedText.replace("Expédié avec ", "").trim();
						carrierName=carrierName.replace("Livraison par", "").trim();
						if(carrierName.equalsIgnoreCase("AMAZON")) {
							carrierName="AZ Logistics";
						}
						loggerMsg=loggerMsg+","+carrierName;
						System.out.println("shippedText"+shippedText );
						System.out.println("carrierName"+carrierName );
						// push to map to update orders in Discount
						String[] carrierNameNumber=new String[2];
						carrierNameNumber[0]=trackingNumber;
						carrierNameNumber[1]=carrierName;
						orderNumbersStatusMap.put(orderNumber, carrierNameNumber);
					}
				}
			}
		}
		logger.info(loggerMsg);
	}
	
	public void searchOrder(String amazonOrderID) {
		driver.get("https://www.amazon.fr/gp/css/order-history?ref_=nav_orders_first");
		commonMethods.waitAndGet(driver, wait, By.id("searchOrdersInput")).sendKeys("");
		commonMethods.waitAndGet(driver, wait, By.xpath("//*[@id='a-autoid-0']/span/input")).click();
	}
	
	public void pushValuesToCDiscountApi() {
		try {
			System.out.println("---------**** Started pushing to c discount api ****---------------");
			for (String orderNumber : orderNumbersStatusMap.keySet()) {
				try {
					JSONObject xmlJSONObj = SOAPClientSAAJ.getOrdersByOrderID(orderNumber);
					JSONObject orderInfo = xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body")
							.getJSONObject("GetOrderListResponse").getJSONObject(("GetOrderListResult")).getJSONObject("OrderList")
							.getJSONObject("Order");
					System.out.println("orderInfo" + orderInfo);
					String[] trackingCarrierNameArray=orderNumbersStatusMap.get(orderNumber);
					logger.info(orderNumber+" : "+trackingCarrierNameArray[0]+" : "+ trackingCarrierNameArray[1]);
					SOAPClientSAAJ.setTrackingNumber(orderInfo, trackingCarrierNameArray[0], trackingCarrierNameArray[1]);
				} catch (Exception e) {
					logger.info("Exception "+e);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			System.out.println("---------**** completed pushing to c discount api ****---------------");
		}
	}

}
