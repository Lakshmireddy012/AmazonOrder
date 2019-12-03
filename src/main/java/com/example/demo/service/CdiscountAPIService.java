package com.example.demo.service;
//package service;
//
//import CloudDataSource.model.*;
//import CloudDataSource.repository.*;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.XML;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.remote.DesiredCapabilities;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.awt.*;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import static java.lang.Thread.sleep;
//
//@Service
//public class CdiscountOrdersService {
//    private static final String REFUND_FILE = "C:\\Users\\Public\\Cdiscount\\refund.txt";
//    private boolean add=false;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private AmountRepository amountRepository;
//
//    @Autowired
//    private OrderLineRepository orderLineRepository;
//
//    @Autowired
//    private SOAPClientSAAJ soapClientSAAJ;
//
//    @Autowired
//    private OrderInfoRepository orderInfoRepository;
//
//    @Autowired
//    private ShippingAddressRepository shippingAddressRepository;
//
//    @Autowired
//    private PushDataService pushDataService;
//
//    @Autowired
//    private RakutenInfosService rakutenInfosService;
//
//    @Autowired
//    private FactureService factureService;
//
//    @Autowired
//    private PassOrderService passOrderService;
//
//    @Autowired
//    private AmazonSellerService amazonSellerService;
//
//    @Autowired
//    private OrderShippedRepository orderShippedRepository;
//
//    @Autowired
//    private CheatTrackingNumberRepository cheatTrackingNumberRepository;
//
//    @Autowired
//    private ShippingOrderAndCreateAddress shippingOrderAndCreateAddress;
//
//
//    @Autowired
//    private OrderShippedService orderShippedService;
//
//
//    List<OrderEntity> orderList = new ArrayList<OrderEntity>();
//    List<Customer> customerList = new ArrayList<Customer>();
//    List<Amount> amountList = new ArrayList<Amount>();
//    List<OrderLine> orderLineList = new ArrayList<OrderLine>();
//    List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
//    List<ShippingAddress> shippingAddresseList = new ArrayList<ShippingAddress>();
//    final static String CDISCOUNT_PATH="https://seller.cdiscount.com/order/management/AcceptedToShip/All/All/All/01-01-0001/31-12-9999/1/ScopusId";
//    final static String AMAZON_SELLER_FILE_PATH="C:\\Users\\Public\\AmazonSeller\\AmazonSeller.csv";
//    final static String CDISCOUNT_ORDERS_URL="https://seller.cdiscount.com/order/management";
//    final static int MAX_COMMISSION=20;
//    int count=0;
//
//    public void getAllInfosFromApiCdiscount(JSONArray array)//array is the response from api transleted from xml to json and get all the infos we need
//    {
//        add=false;
//        System.out.println(orderInfoRepository.findListOrderWichNotShipped().size());
//        System.out.println(orderInfoRepository.findListOrderWichNotShipped());
//        String orderNumber;
//        count=0;
//        for(int i=0; i<array.length();i++) {//each place in array is order
//            orderNumber = array.getJSONObject(i).get("OrderNumber").toString();//get the orderNumber(id)
//            System.out.println(orderNumber);
//            /*check if this order is in the database
//            *the order contains more than 60 fields so we divide this field in 6 tables
//            * check each table if the infos in the database
//            * each if if different table to check
//            */
//            if (customerRepository.findByOrderNumber(orderNumber) == null)
//                customerList.add(pushDataService.setCustomerList(array.getJSONObject(i).getJSONObject("Customer"), orderNumber));
//            if (orderRepository.findByOrderNumber(orderNumber) == null)
//                orderList.add(pushDataService.setOrderList(array.getJSONObject(i), orderNumber));
//            if (amountRepository.findByOrderNumber(orderNumber) == null)
//                amountList.add(pushDataService.setAmountList(array.getJSONObject(i), orderNumber));
//            if (orderLineRepository.findQuantityByOrderNumber(orderNumber).size()==0) {
//                if (array.getJSONObject(i).getJSONObject("OrderLineList").get("OrderLine") instanceof JSONArray) {
//                    //if array that say there are minimum 2 different products
//                    for (int j = 0; j < array.getJSONObject(i).getJSONObject("OrderLineList").getJSONArray("OrderLine").length(); j++)
//                        orderLineList.add(pushDataService.setOrderLineList(array.getJSONObject(i).getJSONObject("OrderLineList").getJSONArray("OrderLine").getJSONObject(j), orderNumber));
//                } else
//                    orderLineList.add(pushDataService.setOrderLineList(array.getJSONObject(i).getJSONObject("OrderLineList").getJSONObject("OrderLine"), orderNumber));
//            }
//            if(orderInfoRepository.findOrder(orderNumber)==null)
//                orderInfoList.add(pushDataService.setOrderInfo(array.getJSONObject(i)));
//            if(shippingAddressRepository.findByOrderNumber(orderNumber)==null)
//                shippingAddresseList.add(pushDataService.setShippingAddress(array.getJSONObject(i), orderNumber));
//            }
//            saveAll();//save all the new orders in the database
//            generateDB(true);//generate database with true is to check the changement in the old olders
//            acceptOrder();//accept the new orders
//        }
//    //===============================================================================
//    private void acceptOrder() {
//        List<String> orderlist= orderInfoRepository.findWaitingForSellerAcceptation();//get all the orders with status:WaitingForSellerAcceptation
//        String soapEndpointUrl = "https://wsvc.cdiscount.com/MarketplaceAPIService.svc?wsdl";
//        String soapAction = "http://www.cdiscount.com/IMarketplaceAPIService/ValidateOrderList";
//        for (int i = 0; i <orderlist.size() ; i++) {
//            List<OrderLine> orderLines = orderLineRepository.findQuantityByOrderNumber(orderlist.get(i));//take all the products in this order
//            //if the commission < Max commission
//            if(shippingAddressRepository.findByOrderNumber(orderlist.get(i))!=null) {
//                String commission = shippingAddressRepository.findByOrderNumber(orderlist.get(i)).getSiteCommissionPromisedAmount();
//                if (commission != null && Double.parseDouble(commission) < MAX_COMMISSION)
//                    soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction, 4, orderLines, null);
//            }
//        }
//    }
//
//    //===============================================================================
//    public void changementState(JSONArray array)//check all the orders in status wainting for seller acceptation
//    // and save infos if the status changed
//    {
//        for(int i=0;i<array.length();i++)
//        {
//            String orderNumber=array.getJSONObject(i).get("OrderNumber").toString();
//            System.out.println(array.getJSONObject(i).get("OrderNumber").toString() + " - " + orderInfoRepository.findByOrderState(array.getJSONObject(i).get("OrderNumber").toString()));
//            //  System.out.println(array.getJSONObject(place).get("OrderState"));
//            if (!orderInfoRepository.findByOrderState(orderNumber).equals(array.getJSONObject(i).get("OrderState").toString())) {
//                //if the status changed
//                System.out.println("STATE CAHNGED");
//                OrderInfo orderToUpdate = orderInfoRepository.findOrder(orderNumber);
//                orderToUpdate.setOrderState(array.getJSONObject(i).get("OrderState").toString());//update the status in the db
//                orderInfoRepository.save(orderToUpdate);
//
//                if (orderInfoRepository.findByOrderState(orderNumber).equals("WaitingForShipmentAcceptation")) {
//                    //if the status is WaitingForShipmentAcceptation save the address in the concernate table
//                    System.out.println("SHIPPMENT STATE");
//                    updateAddressInfos(orderNumber,array,i);
//                }
//            }
//            if (orderInfoRepository.findByOrderState(orderNumber).equals("WaitingForShipmentAcceptation") && shippingAddressRepository.findByOrderNumber(orderNumber).getStreet().equals("true")) {
//                    System.out.println("HERE");
//                updateAddressInfos(orderNumber,array,i);
//            }
//        }
//    }
//
//    private void updateAddressInfos(String orderNumber, JSONArray array, int i) {
//        ShippingAddress shipping = shippingAddressRepository.findByOrderNumber(orderNumber);//get the order from the db
//        OrderEntity order = orderRepository.findByOrderNumber(orderNumber);//get the order from the db
//        ShippingAddress shippingUpdate = pushDataService.updateShippingAddress(array, i, shipping);//update the address
//        shippingAddressRepository.save(shippingUpdate);
//        OrderEntity orderUpdate = pushDataService.updateBillingAddress(array, i, order);//update the address
//        orderRepository.save(orderUpdate);
//    }
//
//    //===============================================================================
//    public void saveAll()//save all the new orders in the database
//    {
//        orderRepository.saveAll(orderList);
//        customerRepository.saveAll(customerList);
//        amountRepository.saveAll(amountList);
//        orderLineRepository.saveAll(orderLineList);
//        orderInfoRepository.saveAll(orderInfoList);
//        shippingAddressRepository.saveAll(shippingAddresseList);
//    }
//    //===============================================================================
//    public void generateDB(boolean add) {//generate the db is : if add=false so get the new orders via api if add=true check if orders status changed
//
//        String soapEndpointUrl = "https://wsvc.cdiscount.com/MarketplaceAPIService.svc?wsdl";
//        String soapAction = "http://www.cdiscount.com/IMarketplaceAPIService/GetOrderList";
//        String str="";
//        System.out.println(add);
//        if(add==false) {
//            cleanThearray();
//            str = soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction, 1, null, null);//request api to get the new orders
//        }
//            else
//            //request api who get all the orders with status waiting for seller acceptation and return the new status
//            str= soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction,3,null,(orderInfoRepository.findListOrderWichNotShipped()).stream().toArray(String[]::new));
//
//        System.out.println("FINISH");
//        JSONObject xmlJSONObj=new JSONObject();
//        JSONArray array = new JSONArray();
//
//        try {
//            xmlJSONObj = XML.toJSONObject(str);
//            array=xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("GetOrderListResponse")
//                    .getJSONObject(("GetOrderListResult")).getJSONObject("OrderList").getJSONArray("Order");
//            //  System.out.println(array.get(0));
//            if(add==false)
//                getAllInfosFromApiCdiscount(array);
//            else {
//                changementState(array);
//                add=false;
//            }
//
//
//        } catch (JSONException je) {
//            System.out.println(je.toString());
//        }
//    }
//
//    private void cleanThearray() {
//
//         orderList.clear();
//        customerList.clear();
//        amountList.clear();
//        orderLineList.clear();
//        orderInfoList.clear();
//    }
//
//    public void catchLateOrdersCdiscount() throws IOException, InterruptedException, AWTException, ParseException {
//        //catch all the orders with not shipped and this is the last day to shipped or refund the order
//        WebDriver driver;
//        DesiredCapabilities cap = rakutenInfosService.getOptions("");//get all the options for the chrome driver
//        driver = new ChromeDriver(cap);
//        factureService.firstconnectionCdiscount(CDISCOUNT_PATH,driver,0);// first connection to cdiscount site
//        driver.get(CDISCOUNT_PATH);
//        getDate(driver);//check if is the last day or less
//    }
//
//    private void getDate(WebDriver driver) throws IOException, InterruptedException, AWTException, ParseException {
//    //check if the date of each order not exceed yesterday to shipped or refund the order
//        int i=1;
//        List<String> ordersWithoutVendor=new ArrayList<>();
//        factureService.deleteFile(AMAZON_SELLER_FILE_PATH);//delete the file which all the ambiguous orders
//        BufferedWriter writer= new BufferedWriter(new FileWriter(AMAZON_SELLER_FILE_PATH, true));
//        while(true) {
//            if (!passOrderService.checkifThisPathisEmpty("//tr[" + i + "]", driver)) {//
//                String order_number=driver.findElement(By.xpath("//tr[" + i + "]//td[5]/div/div/a")).getText();
//                //check the difference between today and the date
//                String difference=amazonSellerService.getBeginAndEndShippingDate(driver.findElement(By.xpath("//tr[" + i + "]//td[4]")).getText().replace("/"," "));
//                System.out.println(difference);
//                if(difference.indexOf("-")!=-1)//if is late and need to shipp or refund
//                {
//                    if(orderShippedRepository.findTheLine(order_number)==null) {// if vendor number doesn't exist
//                        ordersWithoutVendor.add(order_number);
//                    }
//                    else
//                    {
//                        String [] cheatTracking= shippingOrderAndCreateAddress.createCheatTrackingNumber();//get cheat  tracking number
//                        ShippingOrder(cheatTracking,order_number);//shipe the orders
//                    }
//                }
//                } else {
//                //if the end of the page and need to push on the chevron right
//                if(!passOrderService.checkifThisPathisEmpty("//*[@class='glyphicon glyphicon-chevron-right']//parent::span//parent::a",driver))
//                    driver.findElement(By.xpath("//*[@class='glyphicon glyphicon-chevron-right']")).click();
//                else
//                    break;
//                i=0;
//            }
//            i++;
//        }
//        if(ordersWithoutVendor.size()!=0)
//        {
//            refundTheClient(driver,ordersWithoutVendor);
//        }
//        writer.close();
//        //amazonSellerService.findTheBestSeller(1);
//    }
//
//    private void refundTheClient(WebDriver driver, List<String> ordersWithoutVendor) throws InterruptedException, IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(REFUND_FILE,true));
//        Calendar today = Calendar.getInstance();
//        writer.write("======================================" +String.valueOf(today.getTime())+"==================================\n");
//        writer.close();
//
//        for (String order:ordersWithoutVendor) {
//            writer = new BufferedWriter(new FileWriter(REFUND_FILE,true));
//            driver.get(CDISCOUNT_ORDERS_URL);
//            driver.findElement(By.xpath("//input[@name='SearchTerm']")).sendKeys(order);
//            driver.findElement(By.xpath("//button[contains(.,'Recherche')]")).click();
//            if(!factureService.checkifThisPathisEmpty("//span[contains(.,'annulation est en cours')]",driver))
//            {
//                driver.findElement(By.xpath("//*[@value='AcceptCancellation']/following-sibling::a")).click();
//                continue;
//            }
//            //JOptionPane.showMessageDialog(null, "CHECK IF EXSIT VENDOR NUMBER", "InfoBox: ", JOptionPane.ERROR_MESSAGE);
//            String [] cheatTracking= shippingOrderAndCreateAddress.createCheatTrackingNumber();//get cheat  tracking number
//            driver.findElement(By.xpath("//option[@value='SelectedOtherCarrierId' and contains(.,'Autre')]")).click();
//            driver.findElement(By.xpath("//input[@id='carrierNameTextId']")).sendKeys(cheatTracking[0]);
//            driver.findElement(By.xpath("//input[@id='trackingNumberTextId']")).sendKeys(cheatTracking[1]);
//            sleep(3000);
//            driver.findElement(By.xpath("//a[contains(.,'Expédier')]")).click();
//            sleep(3000);
//            driver.findElement(By.xpath("//button[contains(.,'Expédier')]")).click();
//            sleep(2000);
//            driver.findElement(By.xpath("//a[contains(.,'Rembourser')]")).click();
//            sleep(3000);
//            driver.findElement(By.xpath("//button[contains(.,'Confirmer le remboursement')]")).click();
//            writer.write(order+"\n");
//            writer.close();
//        }
//    }
//
//    private void ShippingOrder(String[] cheatTracking,String orderNumber) {//shippe order cdicsount
//        String soapEndpointUrl = "https://wsvc.cdiscount.com/MarketplaceAPIService.svc?wsdl";
//        String soapAction = "http://www.cdiscount.com/IMarketplaceAPIService/ValidateOrderList";
//        //request api to shipped the order
//        String str = soapClientSAAJ.callSoapWebService(soapEndpointUrl, soapAction, 2, orderLineRepository.findAccount(orderNumber), cheatTracking);
//        System.out.println(str);
//        //save the response api
//        JSONObject obj = orderShippedService.convertToJSON(str);
//        OrderShipped order = orderShippedRepository.findTheLine(orderNumber);//find the order to update
//        OrderShipped orderUpdate = orderShippedService.updateorder(order, cheatTracking);//upadte the order
//        if (obj.get("Validated").toString().equals("true"))//if shipping  success
//            order.setShipped("true");
//        orderShippedRepository.save(orderUpdate);//save the update order in the database
//    }
//}
