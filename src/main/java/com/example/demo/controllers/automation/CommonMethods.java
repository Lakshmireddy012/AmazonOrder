package com.example.demo.controllers.automation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap ;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.service.SOAPClientSAAJ;
import com.utils.MyFormatter;


public class CommonMethods {
	
	@Autowired
    private SOAPClientSAAJ soapClientSAAJ;
	
	public WebDriver initDriver() {
		System.setProperty("webdriver.chrome.driver", "browsers/chromedriver.exe");
		WebDriver driver;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }	
	
	public LinkedHashMap <String,String> readPlaceOrderCSVData(String filepath) {
		LinkedHashMap <String, String> placeOrderData=new LinkedHashMap <String, String>();
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		try { 
			  
			br = new BufferedReader(new FileReader(filepath));
            while ((line = br.readLine()) != null) {
                String[] ordersList = line.split(cvsSplitBy);
                if(ordersList.length==2) {
                	placeOrderData.put(ordersList[0], ordersList[1]);
                }
                
            }
	    } 
	    catch (Exception e) { 
	    	System.out.println("error"+e);
	        e.printStackTrace(); 
	    } 
		return placeOrderData;
	}
	
    public WebElement waitAndGet(WebDriver driver, WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }
    
    public WebElement waitUntilClickable(WebDriver driver, WebDriverWait wait, By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public List<WebElement> waitAndGetAll(WebDriver driver, WebDriverWait wait,By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }
    public List<WebElement> getAllElements(WebDriver driver, WebDriverWait wait,By by) {
        return driver.findElements(by);
    }
    
    public void prompt(String message) {
    	JFrame frmOpt= new JFrame();
    	frmOpt.setVisible(true);
        frmOpt.setLocation(500, 500);
        //frmOpt.setAlwaysOnTop(true);
        String[] options = {"RESUME"};
        int response = JOptionPane.showOptionDialog(frmOpt, message, "Resume Button", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "RESUME");
        if (response == JOptionPane.YES_OPTION) {
            
        }
        frmOpt.dispose();
    }
    
    
    public void generateDB(boolean add) {//generate the db is : if add=false so get the new orders via api if add=true check if orders status changed
    	
        String soapEndpointUrl = "https://wsvc.cdiscount.com/MarketplaceAPIService.svc?wsdl";
        String soapAction = "http://www.cdiscount.com/IMarketplaceAPIService/GetOrderList";
        String str="";
        System.out.println(add);
        if(add==false) {
            str = soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction, 1, null, null);//request api to get the new orders
        }
            else
            //request api who get all the orders with status waiting for seller acceptation and return the new status
            //str= soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction,3,null,(orderInfoRepository.findListOrderWichNotShipped()).stream().toArray(String[]::new));

        System.out.println("FINISH"+str);
        JSONObject xmlJSONObj=new JSONObject();
        JSONArray array = new JSONArray();

        try {
            xmlJSONObj = XML.toJSONObject(str);
            array=xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("GetOrderListResponse")
                    .getJSONObject(("GetOrderListResult")).getJSONObject("OrderList").getJSONArray("Order");
            System.out.println("JSON respones"+xmlJSONObj.toString());
            for (int i = 0; i < array.length(); i++) {
				Object array_element = array.get(i);
				
			}

        } catch (JSONException je) {
            System.out.println(je.toString());
        }
    } 
    
    public Properties loadProps(String path) {
    	Properties prop = new Properties();
    	try (InputStream input = new FileInputStream(path)) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    	return prop;
    }
    
    public void forceWait(Long millis) {
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Logger initLogger(String name,String path) {
    	Logger logger = Logger.getLogger(name);  
        FileHandler fh;
    	try {  
            // This block configure the logger with handler and formatter  
            fh = new FileHandler(path);  
            logger.addHandler(fh);
            //SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(new MyFormatter()); 

        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    	return logger;
    }
    
    public void actionClick(WebDriver driver, WebElement element) {
    	Actions ob = new Actions(driver);
    	ob.click(element);
    	ob.build().perform();
    }
    
    public void amazonLogin(WebDriver driver, WebDriverWait wait) {
    	String amazonPropFilePath = "files/amazoncredentials.properties";
    	Properties prop = loadProps(amazonPropFilePath);
		String username = prop.getProperty("amazon_username");
		String password = prop.getProperty("amazon_password");
		driver.get("https://www.amazon.fr/login");
		waitAndGet(driver, wait, By.id("ap_email")).sendKeys(username);
		waitAndGet(driver, wait, By.id("continue")).click();
		waitAndGet(driver, wait, By.id("ap_password")).sendKeys(password);
		waitAndGet(driver, wait, By.name("rememberMe")).click();
		waitAndGet(driver, wait, By.id("signInSubmit")).click();
		// verify its home page or not
		List<WebElement> cartCountElements = getAllElements(driver, wait, By.id("nav-cart-count"));
		if (cartCountElements.size() == 0) {
			// check continue button , yes click to proceed
//			List<WebElement> continueElements = commonMethods.getAllElements(driver, wait, By.id("continue"));
//			if (continueElements.size() != 0) {
//				continueElements.get(0).click();
//			}
			// show prompt for manual operation
			prompt("Get the code and push it please and click resume");
		}
	}
    
    public void getOrderDetails(String orderNumber,WebDriver driver, WebDriverWait wait , String loggerMsg , Logger logger , Map<String, String[]> orderNumbersStatusMap) {
		forceWait((long) 2000);
		List<WebElement> trackPackageButton=getAllElements(driver, wait, By.xpath("//a[contains(.,'Suivre votre colis')]"));
		if(trackPackageButton.size()>0) {
			String packageUrl=trackPackageButton.get(0).getAttribute("href");
			System.out.println("packageUrl "+packageUrl);
			boolean isShipped=packageUrl.contains("packageIndex=0");
			System.out.println("isShipped "+isShipped);
			if(isShipped) {
				trackPackageButton.get(0).click();
				forceWait((long) 2000);
				List<WebElement> trackingNUmber=getAllElements(driver, wait, By.xpath("//div[@class='a-fixed-right-grid-col'][contains(.,'Numéro de suivi')]"));
				List<WebElement> shippedWithElement=getAllElements(driver, wait, By.xpath("//h1[contains(.,'Expédié avec')  or contains(.,'Livraison par')]"));
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
    
    public void rakutenLogin(WebDriver driver, WebDriverWait wait) {
    	String amazonPropFilePath = "files/rakutencredentials.properties";
    	Properties prop = loadProps(amazonPropFilePath);
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");
		try {
			waitAndGet(driver, wait, By.xpath("//*[@id='cnilBanner']//span[contains(.,'OK')]")).click();;
			waitAndGet(driver, wait, By.id("auth_user_identifier")).sendKeys(username);
			waitAndGet(driver, wait, By.id("userpassword")).sendKeys(password);
			waitAndGet(driver, wait, By.name("sbtn_login")).click();
		} catch (Exception e) {
			rakutenLogin(driver, wait);
		}
	}
}