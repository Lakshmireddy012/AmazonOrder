package com.example.demo.controllers.automation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.AWSDBConnection;
import com.utils.SOAPClientSAAJ;

public class PlaceOrder implements Runnable {

	static String placeOrderFilePath = "files/PlaceOrders.csv";
	static String propFilePath = "files/amazoncredentials.properties";
	String username;
	String password;
	// for address
	String name;
	String addressLine1;
	String addressLine2 = "";
	String city;
	String zipCode;
	String phoneNumber;

	static CommonMethods commonMethods = new CommonMethods();
	WebDriverWait wait;
	WebDriver driver;
	JSONObject currentOrderInfo = new JSONObject();
	String orderNumber;
	
	Logger logger = commonMethods.initLogger(PlaceOrder.class.getName(),"output/PlaceOrder"+new SimpleDateFormat("yyyy_MM_yy_HH_mm_ss").format(new Date())+".log");


	public void startPlaceOrder() {
		// fetch order from cdiscount api
		JSONArray ordersArray = getAllOrder();
		driver = commonMethods.initDriver();
		initProps();
		wait = new WebDriverWait(driver, 15);
		login();
		LinkedHashMap<String, String> placeOrderData = commonMethods.readPlaceOrderCSVData(placeOrderFilePath);
		for (String key : placeOrderData.keySet()) {
			// verify cdiscount api have order in the list
			for (int i = 0; i < ordersArray.length(); i++) {
				currentOrderInfo = ordersArray.getJSONObject(i);
				orderNumber = key;
				if (currentOrderInfo.has("OrderNumber") && currentOrderInfo.get("OrderNumber").equals(key)) {
					String url="";
					// check order present in db
					if (AWSDBConnection.isOrderNumberExist(orderNumber)) {
						System.out.println("Order already exist in Database " + orderNumber);
						logger.info(orderNumber+", Order already exist in Database");
					} else {
						try {
							url = placeOrderData.get(key);
							startPlaceOrderSteps(url);
							
						} catch (Exception e) {
							logger.info(orderNumber+", Error: "+e.toString());
							System.out.println(e);
							// if login is requested by amazon
							isLoginRequested();
						}
					}
				}
			}
		}
		driver.close();
	}
	
	public void startPlaceOrderSteps(String url) {
		removeExistingCartItems();
		intilizeOrderInfo();
		if (addToCart(url)) {
			System.out.println("After adding to cart");
			chooseAddress();
			choosePayment();
			giftOptions();
			chooseDeliveryOptionsBuy();
			getOrderNumberSaveToDB();
		}
	}

	public static void main(String[] args) {

		new PlaceOrder().startPlaceOrder();
		// commonMethods.generateDB(false);

	}

	public void initProps() {
		Properties prop = commonMethods.loadProps(propFilePath);
		username = prop.getProperty("amazon_username");
		password = prop.getProperty("amazon_password");
	}

	// to get all orders with state WaitingForShipmentAcceptation
	public JSONArray getAllOrder() {
		JSONObject xmlJSONObj =  SOAPClientSAAJ.getAllOrdersByStatus("WaitingForShipmentAcceptation");// request api to get the new orders
		JSONArray array = new JSONArray();

		try {
			array = xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("GetOrderListResponse")
					.getJSONObject(("GetOrderListResult")).getJSONObject("OrderList").getJSONArray("Order");
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return array;
	}

	public void login() {
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
	
	public void isLoginRequested() {
		List<WebElement> passwordElements=commonMethods.getAllElements(driver, wait, By.id("ap_password"));
		if(passwordElements.size()>0) {
			passwordElements.get(0).sendKeys(password);
		}
		List<WebElement> signInSubmitElements=commonMethods.getAllElements(driver, wait, By.id("signInSubmit"));
		if(signInSubmitElements.size()>0) {
			signInSubmitElements.get(0).click();
		}
		if(passwordElements.size()>0 || signInSubmitElements.size()>0)  {
			commonMethods.prompt("Get the code and push it please and click resume");
		}
	}

	public void removeExistingCartItems() {
		int cartCount = 0;
		driver.get("https://www.amazon.fr");
		List<WebElement> cartCountElements = commonMethods.getAllElements(driver, wait, By.id("nav-cart-count"));

		if (cartCountElements.size() != 0) {
			cartCount = Integer.parseInt(cartCountElements.get(0).getText());
		}
		System.out.println("remove cart" + cartCount);
		if (cartCount != 0) {
			cartCountElements.get(0).click();
			List<WebElement> removeFromCartElemets = commonMethods.getAllElements(driver, wait,
					By.xpath("(//*[contains(@id, 'sc-active-cart')]//input[contains(@value, 'Supprimer')])[1]"));
			if (removeFromCartElemets.size() != 0) {
				removeFromCartElemets.get(0).click();
			}
			removeExistingCartItems();
		}
	}

	public boolean addToCart(String url) {
		driver.get(url);
		// amazon full filled elements
		boolean isItemAvailable = false;
		List<WebElement> addToCartAmazonElements = commonMethods.getAllElements(driver, wait, By.xpath(
				"(((//a[contains(., 'EXPÉDIÉ PAR AMAZON')])//ancestor::div[@role='gridcell'])//following-sibling::div//*[contains(@name, 'submit.addToCart')])[1]"));
		if (addToCartAmazonElements.size() == 0) {
			// if item not available in amazon full filled
			List<WebElement> addToCartElements = commonMethods.getAllElements(driver, wait,
					By.xpath("//*[contains(@name, 'submit.addToCart')]"));
			if (addToCartElements.size() != 0) {
				isItemAvailable = true;
				addToCartElements.get(0).click();
			}
		} else {
			isItemAvailable = true;
			addToCartAmazonElements.get(0).click();
		}
		if (isItemAvailable) {
			List<WebElement> checkoutElements = commonMethods.getAllElements(driver, wait,
					By.xpath("(//*[contains(., 'Passer la commande') and @role='button'])[1]"));
			if (checkoutElements.size() != 0) {
				checkoutElements.get(0).click();
			}
			// click on no thanks if warranty is available
			List<WebElement> warranty = commonMethods.getAllElements(driver, wait,
					By.xpath("//button[contains(.,' Non, merci.')]"));
			if (warranty.size() != 0) {
				warranty.get(0).click();
				commonMethods.forceWait((long) 3000);
				try {
					if(warranty.get(0).isDisplayed()) {
						warranty.get(0).click();
					}
				} catch (Exception e) {
					System.out.println("Exception while clicking second time"+e);
				}
			}

		}
		return isItemAvailable;
	}

	public void chooseAddress() {
		// old screen list view
		List<WebElement> existingAddressElements = commonMethods.getAllElements(driver, wait,
				By.xpath("(//*[@class='a-label a-radio-label' and contains(.,'" + name + "')])[1]"));
		System.out.println("step" + existingAddressElements.size());
		if (existingAddressElements.size() != 0) {
			existingAddressElements.get(0).click();
			// send to this address click
			commonMethods.waitAndGet(driver, wait, By.xpath("//*[@id='orderSummaryPrimaryActionBtn']//input")).click();
			commonMethods.forceWait((long) 3000);
		} else {
			existingAddressElements = commonMethods.getAllElements(driver, wait, By.xpath(
					"(//*[contains(@id, 'address-book-entry')]//b[contains(.,'\"+name+\"')])//ancestor::div[contains(@id, 'address-book-entry')]/div[contains(@class,'ship-to-this-address')]"));
			if (existingAddressElements.size() != 0) {
				existingAddressElements.get(0).click();
			} else {
				List<WebElement> addAddress = commonMethods.getAllElements(driver, wait,
						By.xpath("//*[@id='add-new-address-popover-link']"));
				if (addAddress.size() != 0) {
					addAddress.get(0).click();
					addAddress();
				} else {
					addAddress();
				}
			}
		}
	}

	public void addAddress() {
		WebElement fullNameElement = commonMethods.waitAndGet(driver, wait,
				By.xpath("//*[@id='enterAddressFullName']"));
		WebElement AddressLine1Element = commonMethods.waitAndGet(driver, wait,
				By.xpath("//*[@id='enterAddressAddressLine1']"));
		WebElement AddressLine2Element = commonMethods.waitAndGet(driver, wait,
				By.xpath("//*[@id='enterAddressAddressLine2']"));
		WebElement cityElement = commonMethods.waitAndGet(driver, wait, By.xpath("//*[@id='enterAddressCity']"));
		WebElement postalCodeElement = commonMethods.waitAndGet(driver, wait,
				By.xpath("//*[@id='enterAddressPostalCode']"));
		WebElement phoneNumberElement = commonMethods.waitAndGet(driver, wait,
				By.xpath("//*[@id='enterAddressPhoneNumber']"));
		WebElement useAddressButtonElement = commonMethods.waitAndGet(driver, wait, By.xpath(
				"//span[@data-action='add-address-popover-submit']//span[contains(.,'Envoyer à cette adresse')]//preceding-sibling::input"));

		fullNameElement.sendKeys(name);
		AddressLine1Element.sendKeys(addressLine1);
		if(!addressLine2.isEmpty()) {
			AddressLine2Element.sendKeys(addressLine2);
		}
		cityElement.sendKeys(city);
		postalCodeElement.sendKeys(zipCode);
		phoneNumberElement.sendKeys(phoneNumber);
		useAddressButtonElement.click();
		List<WebElement> errorElement=commonMethods.getAllElements(driver, wait, By.xpath("//h1[contains(.,'Veuillez corriger les champs ')]"));
		if(errorElement.size()>0 && errorElement.get(0).isDisplayed()) {
			for (int i = 0; i < 3; i++) {
				useAddressButtonElement.click();
				if(errorElement.size()>0 && errorElement.get(0).isDisplayed()) {
					errorElement=commonMethods.getAllElements(driver, wait, By.xpath("//h1[contains(.,'Veuillez corriger les champs ')]"));
				}else {
					break;
				}
			}
		}

		// Check your shipping address prompt case
		List<WebElement> exsitingAddress = commonMethods.getAllElements(driver, wait,
				By.xpath("//input[@value='addr_0']"));
		if (exsitingAddress.size() != 0) {
			exsitingAddress.get(0).click();
			commonMethods.waitAndGet(driver, wait, By.xpath("//input[@name='useSelectedAddress']")).click();
		}
		// end Check your shipping address case
	}

	public void choosePayment() {
		// choose default payment and continue click on use this payment method
		// make action click to avoid error
		commonMethods.forceWait((long) 4000);
		commonMethods.waitUntilClickable(driver, wait, By.xpath("//*[@id='orderSummaryPrimaryActionBtn']//input"))
				.click();
	}

	public void giftOptions() {
		commonMethods.forceWait((long) 2000);
		List<WebElement> giftOptionsButton = commonMethods.getAllElements(driver, wait,
				By.xpath("//a[contains(.,'Ajouter des options cadeau')]"));
		if (giftOptionsButton.size() > 0) {
			giftOptionsButton.get(0).click();
			try {
				commonMethods.forceWait((long) 3000);
				List<WebElement> IncludeMessage = commonMethods.getAllElements(driver, wait, By.xpath(
						"//span[contains(.,'Inclure un message de cadeau personnalisé gratuit')]/preceding-sibling::input[@type='checkbox']"));
				List<WebElement> giftWrap = commonMethods.getAllElements(driver, wait,
						By.xpath("//span[contains(.,'Emballage cadeau avec')]/preceding-sibling::input[@type='checkbox']"));
				List<WebElement> hidePrices = commonMethods.getAllElements(driver, wait, By.xpath(
						"//span[contains(.,'Cacher les prix sur le bordereau d')]/preceding-sibling::input[@type='checkbox']"));
				// remove gift message check box if more than one product is selected
				for (WebElement webElement : IncludeMessage) {
					commonMethods.forceWait((long) 3000);
					if (webElement.isSelected() && webElement.isDisplayed()) {
						webElement.click();
					}
				}

				// hide prices , select if not
				if (hidePrices.size() > 0 && hidePrices.get(0).isDisplayed() && !hidePrices.get(0).isSelected()) {
					hidePrices.get(0).click();
				} else {
					hidePrices = commonMethods.getAllElements(driver, wait, By.xpath(
							"//span[contains(.,'Si possible, masquer les prix sur le bor')]/preceding-sibling::input[@type='checkbox']"));
					if (hidePrices.size() > 0 && hidePrices.get(0).isDisplayed() && !hidePrices.get(0).isSelected()) {
						hidePrices.get(0).click();
					}
				}
				if (giftWrap.size() > 0 && giftWrap.get(0).isDisplayed() && giftWrap.get(0).isSelected()) {
					giftWrap.get(0).click();
				}
			} catch (Exception e) {
				System.out.println("error occured"+e);
			}
			List<WebElement> closeGiftOptions = commonMethods.getAllElements(driver, wait, By.xpath(
					"(//span[contains(.,'Enregistrer vos options et continuer')]//preceding-sibling::input)[1]"));
			if (closeGiftOptions.size() > 0) {
				closeGiftOptions.get(0).click();
			}
		}

	}

	public void chooseDeliveryOptionsBuy() {

		commonMethods.forceWait((long) 4000);
		List<WebElement> standardDelivery = commonMethods.getAllElements(driver, wait,
				By.xpath("//*[@value='std-fr']"));
		if (standardDelivery.size() != 0) {
			standardDelivery.get(0).click();
		}
		commonMethods.forceWait((long) 2000);
		try {
			List<WebElement> buyButtonBottom = commonMethods.waitAndGetAll(driver, wait,
					By.xpath("//*[@id='bottomSubmitOrderButtonId']//input"));
			List<WebElement> submitOrderButton = commonMethods.waitAndGetAll(driver, wait,
					By.xpath("//*[@id='submitOrderButtonId']//input"));
			commonMethods.forceWait((long) 1000);
			if (submitOrderButton.size() != 0) {
				System.out.println("submitOrderButton");
				commonMethods.actionClick(driver, submitOrderButton.get(0));
				commonMethods.forceWait((long) 1000);
				if( submitOrderButton.get(0).isDisplayed()) {
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", submitOrderButton.get(0));
				}
			} else {
				System.out.println("buyButtonBottom");
				if (buyButtonBottom.size() != 0) {
					commonMethods.actionClick(driver, buyButtonBottom.get(0));
					commonMethods.forceWait((long) 1000);
					if( buyButtonBottom.get(0).isDisplayed()) {
						JavascriptExecutor executor = (JavascriptExecutor) driver;
						executor.executeScript("arguments[0].click();", buyButtonBottom.get(0));
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error "+e);
		}
		

	}

	public void getOrderNumberSaveToDB() {
		// choose default payment and continue click on use this payment method
		String vendorNumber = commonMethods
				.waitAndGet(driver, wait, By.xpath("//h5[contains(.,'Numéro de commande')]//child::span")).getText();
		System.out.println("order and venodr number" + orderNumber + vendorNumber);
		logger.info(orderNumber+","+vendorNumber);
		AWSDBConnection.insertOrderNumber(orderNumber, vendorNumber);
	}

	public void intilizeOrderInfo() {
		orderNumber=currentOrderInfo.getString("OrderNumber");
		name="";
		addressLine1="";
		addressLine2="";
		city="";
		zipCode="";
		phoneNumber="";
		JSONObject shippingAddress = currentOrderInfo.getJSONObject("ShippingAddress");
		name = "(" + orderNumber.substring(orderNumber.length() - 4, orderNumber.length()) + ") "
				+ shippingAddress.getString("FirstName")+" "+ shippingAddress.getString("LastName");
		addressLine1 = shippingAddress.getString("Street");
		String aptNumber=shippingAddress.get("ApartmentNumber").toString();
		String building=shippingAddress.get("Building").toString();
		String instructions=shippingAddress.get("Instructions").toString();
		String placeName=shippingAddress.get("PlaceName").toString();
		if (aptNumber != null && !aptNumber.isEmpty()) {
			addressLine2 = "APT "+aptNumber + " - ";
		}
		if (building != null &&  !building.trim().isEmpty()) {
			addressLine2 = addressLine2+"BAT "+building+ " - ";
		}
		if (instructions != null && !instructions.trim().isEmpty()) {
			addressLine2 = addressLine2+instructions+ " - ";
		}
		if (placeName != null && !placeName.trim().isEmpty()) {
			addressLine2 = addressLine2+placeName+ " - ";
		}
		
		city = shippingAddress.getString("City");
		zipCode = shippingAddress.get("ZipCode").toString();
		phoneNumber = currentOrderInfo.getJSONObject("Customer").getString("MobilePhone");
		if (phoneNumber == null || phoneNumber.equals("") || phoneNumber.isEmpty()) {
			phoneNumber = currentOrderInfo.getJSONObject("Customer").getString("Phone");
		}
		System.out.println("*********** Address *****");
		System.out.println("Name: " + name);
		System.out.println("addressLine1: " + addressLine1);
		System.out.println("addressLine2: " + addressLine2);
		System.out.println("city" + city);
		System.out.println("zipCode: " + zipCode);
		System.out.println("phoneNumber: " + phoneNumber);
		System.out.println("***********");
	}

	@Override
	public void run() {
		startPlaceOrder();
	}
}
