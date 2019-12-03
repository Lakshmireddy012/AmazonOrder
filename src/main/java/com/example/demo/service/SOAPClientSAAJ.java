package com.example.demo.service;

import com.example.demo.model.OrderLine;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.xml.soap.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class SOAPClientSAAJ {

    public static void main(String args[]) {
        /*
            The example below requests from the Web Service at:
             http://www.webservicex.net/uszip.asmx?op=GetInfoByCity


            To call other WS, change the parameters below, which are:
             - the SOAP Endpoint URL (that is, where the service is responding from)
             - the SOAP Action

            Also change the contents of the method createSoapEnvelope() in this class. It constructs
             the inner part of the SOAP envelope that is actually sent.
         */

    }

    private static void createSoapEnvelope(SOAPMessage soapMessage,int type,List<OrderLine> orderline,String [] tracking) throws SOAPException ,IOException{
        //configuration request
        String carrierNameString = "";
        String acceptationStateString = "";
        String productConditionString = "";
        String sellerProductIdString = "";
        String orderNumberString ="";
        String orderStateString = "";
        String trackingNumberString = "";
        String trackingUrlString = "";
        String token = getToken();
        int size=0;
        System.out.println(tracking);
        if(type==2) {
            carrierNameString = tracking[0];
            acceptationStateString = "ShippedBySeller";
            productConditionString = "";
            sellerProductIdString = "";
            orderNumberString = orderline.get(0).getOrderNumber();
            orderStateString = "Shipped";
            trackingNumberString = tracking[1];
            trackingUrlString = "";
            //SOAPClientSAAJ s=new SOAPClientSAAJ();
            int temp = orderline.size();
            size = orderline.size();
            for (int i = 0; i < temp; i++) {
                if (orderline.get(i).getName().equals("Frais de traitement"))
                    size--;
            }
            System.out.println(size);
        }
        if(type==4)
        {
            acceptationStateString = "AcceptedBySeller";
            productConditionString = "";
            sellerProductIdString = "";
            orderNumberString = orderline.get(0).getOrderNumber();
            orderStateString = "AcceptedBySeller";
            size = orderline.size();
        }


        SOAPPart soapPart = soapMessage.getSOAPPart();
        String myNamespace = "a";
        String myNamespace1="i";
        String i_nil="i:nil";
        String myNamespace3="arr";
        String myNamespaceURI = "http://www.cdiscount.com";
        String myNamespaceURI1="http://schemas.datacontract.org/2004/07/Cdiscount.Framework.Core.Communication.Messages";
        String myNamespaceURI2 = "http://www.w3.org/2001/XMLSchema-instance";
        String myNamespaceURI3 = "http://schemas.microsoft.com/2003/10/Serialization/Arrays";

        // SOAP Envelope

        SOAPEnvelope envelope = soapPart.getEnvelope();

        SOAPElement soapBodyElem = null;
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.addNamespaceDeclaration("",myNamespaceURI);
        if(type==1 || type==3)
            soapBodyElem = soapBody.addChildElement("GetOrderList");
        else if(type==2 || type==4)
            soapBodyElem = soapBody.addChildElement("ValidateOrderList");
        else if(type==5)
            soapBodyElem = soapBody.addChildElement("GetProductList");

        // soapBodyElem.addNamespaceDeclaration("",myNamespaceURI);
        SOAPElement headearMessage = soapBodyElem.addChildElement("headerMessage");
        headearMessage.addNamespaceDeclaration(myNamespace, myNamespaceURI1);
        headearMessage.addNamespaceDeclaration(myNamespace1, myNamespaceURI2);
        SOAPElement Context = headearMessage.addChildElement("Context", myNamespace);
        //Context
        SOAPElement CatalogID = Context.addChildElement("CatalogID", myNamespace);
        CatalogID.addTextNode("1");
        SOAPElement CustomerPoolID = Context.addChildElement("CustomerPoolID", myNamespace);
        CustomerPoolID.addTextNode("1");
        SOAPElement SiteID = Context.addChildElement("SiteID", myNamespace);
        SiteID.addTextNode("100");
        //Localization
        SOAPElement Localization = headearMessage.addChildElement("Localization", myNamespace);
        SOAPElement Country = Localization.addChildElement("Country", myNamespace);
        Country.addTextNode("Fr");
        SOAPElement Currency = Localization.addChildElement("Currency", myNamespace);
        Currency.addTextNode("Eur");
        SOAPElement DecimalPosition = Localization.addChildElement("DecimalPosition", myNamespace);
        DecimalPosition.addTextNode("2");
        SOAPElement Language = Localization.addChildElement("Language", myNamespace);
        Language.addTextNode("Fr");
        //Security
        SOAPElement Security = headearMessage.addChildElement("Security", myNamespace);
        SOAPElement DomainRightsList = Security.addChildElement("DomainRightsList", myNamespace);
        DomainRightsList.setAttribute(i_nil, "true");
        SOAPElement IssuerID = Security.addChildElement("IssuerID", myNamespace);
        IssuerID.setAttribute(i_nil, "true");
        SOAPElement SessionID = Security.addChildElement("SessionID", myNamespace);
        SessionID.setAttribute(i_nil, "true");
        SOAPElement SubjectLocality = Security.addChildElement("SubjectLocality", myNamespace);
        SubjectLocality.setAttribute(i_nil, "true");
        SOAPElement TokenID = Security.addChildElement("TokenId", myNamespace);
        TokenID.addTextNode(token);
        SOAPElement UserName = Security.addChildElement("UserName", myNamespace);
        UserName.setAttribute(i_nil, "true");
        SOAPElement Version = headearMessage.addChildElement("Version", myNamespace);
        Version.addTextNode("1.0");
        if(type==1 || type==3) {
            //OrderFilter
            SOAPElement orderFilter = soapBodyElem.addChildElement("orderFilter");
            orderFilter.addNamespaceDeclaration("i", myNamespaceURI2);
            if (type == 3)
                orderFilter.addNamespaceDeclaration("arr", myNamespaceURI3);
            if(type==1) {
                System.out.println(getDate());
                SOAPElement BeginCreationDate = orderFilter.addChildElement("BeginCreationDate");
                BeginCreationDate.addTextNode(getDate());
            }
            SOAPElement FetchOrderLines = orderFilter.addChildElement("FetchOrderLines");
            FetchOrderLines.addTextNode("true");
            if (type == 3) {
                SOAPElement orderReferenceList = orderFilter.addChildElement("OrderReferenceList");

                for(int j = 0; j<tracking.length;j++)
                {
                    SOAPElement ordernumber = orderReferenceList.addChildElement("string",myNamespace3);
                    ordernumber.addTextNode(tracking[j]);

                }
            } else {
                //States
                SOAPElement States = orderFilter.addChildElement("States");
                SOAPElement Status1 = States.addChildElement("OrderStateEnum");
                Status1.addTextNode("CancelledByCustomer");
                SOAPElement Status2 = States.addChildElement("OrderStateEnum");
                Status2.addTextNode("WaitingForSellerAcceptation");
                SOAPElement Status3 = States.addChildElement("OrderStateEnum");
                Status3.addTextNode("AcceptedBySeller");
                SOAPElement Status4 = States.addChildElement("OrderStateEnum");
                Status4.addTextNode("PaymentInProgress");
                SOAPElement Status5 = States.addChildElement("OrderStateEnum");
                Status5.addTextNode("WaitingForShipmentAcceptation");
                SOAPElement Status6 = States.addChildElement("OrderStateEnum");
                Status6.addTextNode("Shipped");
                SOAPElement Status7 = States.addChildElement("OrderStateEnum");
                Status7.addTextNode("RefusedBySeller");
                SOAPElement Status8 = States.addChildElement("OrderStateEnum");
                Status8.addTextNode("AutomaticCancellation");
                SOAPElement Status9 = States.addChildElement("OrderStateEnum");
                Status9.addTextNode("PaymentRefused");
                SOAPElement Status10 = States.addChildElement("OrderStateEnum");
                Status10.addTextNode("ShipmentRefusedBySeller");
                SOAPElement Status11 = States.addChildElement("OrderStateEnum");
                Status11.addTextNode("RefusedNoShipment");
            }
        }
        else if(type==2 || type==4)
        {
            SOAPElement validateOrderListMessage = soapBodyElem.addChildElement("validateOrderListMessage");
            validateOrderListMessage.addNamespaceDeclaration("i", myNamespaceURI2);
            SOAPElement orderList = validateOrderListMessage.addChildElement("OrderList");
            SOAPElement validateOrder = orderList.addChildElement("ValidateOrder");
            if(type==2) {
                SOAPElement carrierName = validateOrder.addChildElement("CarrierName");
                carrierName.addTextNode(carrierNameString);
            }
            SOAPElement orderLineList = validateOrder.addChildElement("OrderLineList");

            for(int i=0; i<size;i++)
            {
                SOAPElement validateOrderLine = orderLineList.addChildElement("ValidateOrderLine");
                SOAPElement acceptationState = validateOrderLine.addChildElement("AcceptationState");
                acceptationState.addTextNode(acceptationStateString);
                SOAPElement productCondition = validateOrderLine.addChildElement("ProductCondition");
                productCondition.addTextNode(orderline.get(i).getProductCondition());
                SOAPElement sellerProductId = validateOrderLine.addChildElement("SellerProductId");
                sellerProductId.addTextNode(orderline.get(i).getSellerProductId());
            }
            SOAPElement orderNumber = validateOrder.addChildElement("OrderNumber");
            orderNumber.addTextNode(orderNumberString);
            SOAPElement orderState = validateOrder.addChildElement("OrderState");
            orderState.addTextNode(orderStateString);
            if(type==2) {
                SOAPElement trackingNumber = validateOrder.addChildElement("TrackingNumber");
                trackingNumber.addTextNode(trackingNumberString);
                SOAPElement trackingUrl = validateOrder.addChildElement("TrackingUrl");
                trackingUrl.setAttribute(i_nil, "true");
            }
        }
        else if(type==5)
        {
            SOAPElement productFilter =soapBodyElem.addChildElement("productFilter");
            productFilter.addNamespaceDeclaration(myNamespace1, myNamespaceURI2);
            SOAPElement categoryCode = productFilter.addChildElement("CategoryCode");
            categoryCode.addTextNode(tracking[0]);
        }





    }

    private static String getDate() {
        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -15);
        Date today30 = cal.getTime();
        System.out.println(today30);
        //2018-08-01T00:00:00.00
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(today30);
        strDate+="T00:00:00.00";
        return strDate;
    }

    public  static String callSoapWebService(String soapEndpointUrl, String soapAction,int type,List<OrderLine> orderline,String[] tracking) {
        String str="";
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction,type,orderline,tracking), soapEndpointUrl);



            // Print the SOAP Response
            //str= String.valueOf(soapResponse);
            OutputStream out = new ByteArrayOutputStream();
            out = new ByteArrayOutputStream();
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(out);
            str=((ByteArrayOutputStream) out).toString();
            System.out.println("str"+str);

            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        return str;
    }

    private static SOAPMessage createSOAPRequest(String soapAction,int type,List<OrderLine> orderline,String[] tracking) throws Exception {
        // String userName = "PetitsPrix-api";
        // String password= "woT@xLhS68#vS";
    	System.out.println("createSOAPRequest");
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.getSOAPHeader().detachNode();
        soapMessage.getSOAPPart().getEnvelope().setPrefix("s");
        soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("SOAP-ENV");
        //soapMessage.getSOAPHeader().setPrefix("s");
        soapMessage.getSOAPBody().setPrefix("s");

        //String authorization = new sun.misc.BASE64Encoder().encode((userName+":"+password).getBytes());


        createSoapEnvelope(soapMessage,type,orderline,tracking);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        //headers.addHeader("Authorization", "Basic " + authorization);
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    public  static String getToken()throws IOException
    {
        String url = "https://sts.cdiscount.com/users/httpIssue.svc/?realm=https://wsvc.cdiscount.com/MarketplaceAPIService.svc";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String userCredentials = "PetitsPrix-api:woT@xLhS68#vS";
        String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
        con.setRequestProperty ("Authorization", basicAuth);
        System.out.println("basicAuth"+basicAuth);
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            String str="";
            Pattern ANCHOR_PATTERN = Pattern.compile(
                    "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">([0-9a-zA-Z]*)</string>"
                    ,
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = ANCHOR_PATTERN.matcher( response.toString() );
            if ( matcher.find() ){
                return matcher.group(1);
            }
        } else {
            System.out.println("GET request not worked");
        }
        return "";
    }

	
}
