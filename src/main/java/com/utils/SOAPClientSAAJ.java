package com.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.JsonObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class SOAPClientSAAJ {
	static String soapEndpointUrl = "https://wsvc.cdiscount.com/MarketplaceAPIService.svc?wsdl";
	static String soapAction = "http://www.cdiscount.com/IMarketplaceAPIService/ValidateOrderList";
	static String soapActionOrderList = "http://www.cdiscount.com/IMarketplaceAPIService/GetOrderList";

	// for testing
	public static void main(String[] args) {

//		String str = callSoapWebService(soapEndpointUrl, soapAction,2);// request api to get the new orders
//		System.out.println("FINISH" + str);
//		JSONObject xmlJSONObj = new JSONObject();
//		JSONArray array = new JSONArray();
//
//		try {
//			xmlJSONObj = XML.toJSONObject(str);
//			System.out.println("xmlJSONObj"+xmlJSONObj);
//			array = xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body").getJSONObject("GetOrderListResponse")
//					.getJSONObject(("GetOrderListResult")).getJSONObject("OrderList").getJSONArray("Order");
//			for (int i = 0; i < array.length(); i++) {
//				Object array_element = array.get(i);
//			}

//		} catch (JSONException je) {
//			System.out.println(je.toString());
//		}
//		JSONObject xmlJSONObj = getOrdersByOrderID("2003291151GJE4F");
//		JSONObject orderInfo = xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body")
//				.getJSONObject("GetOrderListResponse").getJSONObject(("GetOrderListResult")).getJSONObject("OrderList")
//				.getJSONObject("Order");
//		System.out.println("orderInfo" + orderInfo);
		// getAllOrdersByStatus("WaitingForShipmentAcceptation");
//		JSONObject orderInfo=getAllOrdersByStatus("WaitingForSellerAcceptation");
//		System.out.println("orderInfo"+orderInfo);
//		changeStatus(orderInfo);
		// getAllOrders();
	}

	/**
	 * The Desciption of the method to explain what the method does
	 * 
	 * @param type=1 for order list with status WaitingForShipmentAcceptation
	 * @return the value returned by the method
	 * @throws what kind of exception does this method throw
	 */
	public static String callSoapWebService(String soapEndpointUrl, String soapAction, int type, String orderId,
			JSONObject orderInfo) {
		String str = "";
		try {
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, type, orderId, orderInfo),
					soapEndpointUrl);

			// Print the SOAP Response
			str = String.valueOf(soapResponse);
			OutputStream out = new ByteArrayOutputStream();
			out = new ByteArrayOutputStream();
			System.out.println("Response SOAP Message:");
			soapResponse.writeTo(out);
			str = ((ByteArrayOutputStream) out).toString();
			System.out.println("str" + str);
			soapConnection.close();
		} catch (Exception e) {
			System.err.println(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
			e.printStackTrace();
		}
		return str;
	}

	private static SOAPMessage createSOAPRequest(String soapAction, int type, String orderId, JSONObject orderInfo)
			throws Exception {
		// String userName = "PetitsPrix-api";
		// String password= "woT@xLhS68#vS";
		System.out.println("createSOAPRequest");
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage.getSOAPHeader().detachNode();
		soapMessage.getSOAPPart().getEnvelope().setPrefix("s");
		soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("SOAP-ENV");
		// soapMessage.getSOAPHeader().setPrefix("s");
		soapMessage.getSOAPBody().setPrefix("s");

		// String authorization = new
		// sun.misc.BASE64Encoder().encode((userName+":"+password).getBytes());

		createSoapEnvelope(soapMessage, type, orderId, orderInfo);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		// headers.addHeader("Authorization", "Basic " + authorization);
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		/* Print the request message, just for debugging purposes */
		System.out.println("Request SOAP Message:");
		soapMessage.writeTo(System.out);
		System.out.println("\n");

		return soapMessage;
	}

	private static void createSoapEnvelope(SOAPMessage soapMessage, int type, String orderId, JSONObject orderInfo)
			throws SOAPException, IOException, TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError {
		// configuration request

		String token = getToken();
		SOAPPart soapPart = soapMessage.getSOAPPart();
		String myNamespace = "a";
		String myNamespace1 = "i";
		String i_nil = "i:nil";
		String myNamespaceURI = "http://www.cdiscount.com";
		String myNamespaceURI1 = "http://schemas.datacontract.org/2004/07/Cdiscount.Framework.Core.Communication.Messages";
		String myNamespaceURI2 = "http://www.w3.org/2001/XMLSchema-instance";
		String myNamespace3 = "arr";
		String myNamespaceURI3 = "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
		// SOAP Envelope

		SOAPEnvelope envelope = soapPart.getEnvelope();

		SOAPElement soapBodyElem = null;
		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		soapBody.addNamespaceDeclaration("", myNamespaceURI);
		if (type == 1 || type == 3 || type == 4) {
			soapBodyElem = soapBody.addChildElement("GetOrderList");
		}
		if (type == 2 || type == 5) {
			soapBodyElem = soapBody.addChildElement("ValidateOrderList");
		}
		// *******************************common header start
		// *******************************//
		SOAPElement headearMessage = soapBodyElem.addChildElement("headerMessage");
		headearMessage.addNamespaceDeclaration(myNamespace, myNamespaceURI1);
		headearMessage.addNamespaceDeclaration(myNamespace1, myNamespaceURI2);
		SOAPElement Context = headearMessage.addChildElement("Context", myNamespace);
		// Context
		SOAPElement CatalogID = Context.addChildElement("CatalogID", myNamespace);
		CatalogID.addTextNode("1");
		SOAPElement CustomerPoolID = Context.addChildElement("CustomerPoolID", myNamespace);
		CustomerPoolID.addTextNode("1");
		SOAPElement SiteID = Context.addChildElement("SiteID", myNamespace);
		SiteID.addTextNode("100");
		// Localization
		SOAPElement Localization = headearMessage.addChildElement("Localization", myNamespace);
		SOAPElement Country = Localization.addChildElement("Country", myNamespace);
		Country.addTextNode("Fr");
		SOAPElement Currency = Localization.addChildElement("Currency", myNamespace);
		Currency.addTextNode("Eur");
		SOAPElement DecimalPosition = Localization.addChildElement("DecimalPosition", myNamespace);
		DecimalPosition.addTextNode("2");
		SOAPElement Language = Localization.addChildElement("Language", myNamespace);
		Language.addTextNode("Fr");
		// Security
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
		// ******************************* header ends *******************************//

		// States
		// OrderFilter
		if (type == 1) {
			SOAPElement orderFilter = soapBodyElem.addChildElement("orderFilter");
			SOAPElement FetchOrderLines = orderFilter.addChildElement("FetchOrderLines");
			FetchOrderLines.addTextNode("true");
			SOAPElement States = orderFilter.addChildElement("States");
			SOAPElement Status5 = States.addChildElement("OrderStateEnum");
			Status5.addTextNode(orderId);
		}
		if (type == 3) {
			SOAPElement orderFilter = soapBodyElem.addChildElement("orderFilter");
			SOAPElement FetchOrderLines = orderFilter.addChildElement("FetchOrderLines");
			FetchOrderLines.addTextNode("true");
			// States
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

		if (type == 4) {
			SOAPElement orderFilter = soapBodyElem.addChildElement("orderFilter");
			SOAPElement FetchOrderLines = orderFilter.addChildElement("FetchOrderLines");
			FetchOrderLines.addTextNode("true");
			orderFilter.addNamespaceDeclaration("i", myNamespaceURI2);
			orderFilter.addNamespaceDeclaration("arr", myNamespaceURI3);
			SOAPElement orderReferenceList = orderFilter.addChildElement("OrderReferenceList");
			SOAPElement ordernumber = orderReferenceList.addChildElement("string", myNamespace3);
			ordernumber.addTextNode(orderId);
		}
		if (type == 2) {
			String orderNumberVal = orderInfo.get("OrderNumber").toString();
			String trackingNumberVal = orderInfo.get("trackingNumber").toString();
			String carrierStringVal = orderInfo.get("carrierString").toString();

			SOAPElement validateOrderListMessage = soapBodyElem.addChildElement("validateOrderListMessage");
			validateOrderListMessage.addNamespaceDeclaration("i", myNamespaceURI2);
			SOAPElement orderList = validateOrderListMessage.addChildElement("OrderList");
			SOAPElement validateOrder = orderList.addChildElement("ValidateOrder");
			SOAPElement carrierName = validateOrder.addChildElement("CarrierName");
			carrierName.addTextNode(carrierStringVal);

			SOAPElement orderLineList = validateOrder.addChildElement("OrderLineList");
			Object orderLineListJavaObject = orderInfo.getJSONObject("OrderLineList").get("OrderLine");
			// if orderline is one object
			if (orderLineListJavaObject instanceof JSONObject) {
				JSONObject orderLineObject = (JSONObject) orderLineListJavaObject;
				String acceptationStateVal = orderLineObject.get("AcceptationState").toString();
				String productConditionVal = orderLineObject.get("ProductCondition").toString();
				String sellerProductIdVal = orderLineObject.get("SellerProductId").toString();
				System.out
						.println("values" + acceptationStateVal + " " + productConditionVal + " " + sellerProductIdVal);
				SOAPElement validateOrderLine = orderLineList.addChildElement("ValidateOrderLine");
				SOAPElement acceptationState = validateOrderLine.addChildElement("AcceptationState");
				acceptationState.addTextNode("ShippedBySeller");
				SOAPElement productCondition = validateOrderLine.addChildElement("ProductCondition");
				productCondition.addTextNode(productConditionVal);
				SOAPElement sellerProductId = validateOrderLine.addChildElement("SellerProductId");
				sellerProductId.addTextNode(sellerProductIdVal);
			} else {
				// if orderline is array of object
				JSONArray orderLineArrayObject = ((JSONArray) orderLineListJavaObject);
				for (int i = 0; i < orderLineArrayObject.length(); i++) {
					JSONObject orderLineObject = orderLineArrayObject.getJSONObject(i);
					String acceptationStateVal = orderLineObject.get("AcceptationState").toString();
					String productConditionVal = orderLineObject.get("ProductCondition").toString();
					String sellerProductIdVal = orderLineObject.get("SellerProductId").toString();
					System.out.println(
							"values" + acceptationStateVal + " " + productConditionVal + " " + sellerProductIdVal);
					
					if(sellerProductIdVal!=null && !sellerProductIdVal.trim().isEmpty()) {
						SOAPElement validateOrderLine = orderLineList.addChildElement("ValidateOrderLine");
						SOAPElement acceptationState = validateOrderLine.addChildElement("AcceptationState");
						acceptationState.addTextNode("ShippedBySeller");
						SOAPElement productCondition = validateOrderLine.addChildElement("ProductCondition");
						productCondition.addTextNode(productConditionVal);
						SOAPElement sellerProductId = validateOrderLine.addChildElement("SellerProductId");
						sellerProductId.addTextNode(sellerProductIdVal);
					}
				}
			}

			SOAPElement orderNumber = validateOrder.addChildElement("OrderNumber");
			orderNumber.addTextNode(orderNumberVal);
			SOAPElement orderState = validateOrder.addChildElement("OrderState");
			orderState.addTextNode("Shipped");
			SOAPElement trackingNumber = validateOrder.addChildElement("TrackingNumber");
			trackingNumber.addTextNode(trackingNumberVal);
			SOAPElement trackingUrl = validateOrder.addChildElement("TrackingUrl");
			trackingUrl.setAttribute(i_nil, "true");
		}
		
		if(type ==5) {

			String orderNumberVal = orderInfo.get("OrderNumber").toString();
			String orderStateVal =orderInfo.get("OrderState").toString();
//			String trackingNumberVal = orderInfo.get("trackingNumber").toString();
//			String carrierStringVal = orderInfo.get("carrierString").toString();

			SOAPElement validateOrderListMessage = soapBodyElem.addChildElement("validateOrderListMessage");
			validateOrderListMessage.addNamespaceDeclaration("i", myNamespaceURI2);
			SOAPElement orderList = validateOrderListMessage.addChildElement("OrderList");
			SOAPElement validateOrder = orderList.addChildElement("ValidateOrder");
			SOAPElement carrierName = validateOrder.addChildElement("CarrierName");
			carrierName.addTextNode("CarrierName");

			SOAPElement orderLineList = validateOrder.addChildElement("OrderLineList");
			Object orderLineListJavaObject = orderInfo.getJSONObject("OrderLineList").get("OrderLine");
			// if orderline is one object
			if (orderLineListJavaObject instanceof JSONObject) {
				JSONObject orderLineObject = (JSONObject) orderLineListJavaObject;
				String acceptationStateVal = orderLineObject.get("AcceptationState").toString();
				String productConditionVal = orderLineObject.get("ProductCondition").toString();
				String sellerProductIdVal = orderLineObject.get("SellerProductId").toString();
				System.out
						.println("values" + acceptationStateVal + " " + productConditionVal + " " + sellerProductIdVal);
				SOAPElement validateOrderLine = orderLineList.addChildElement("ValidateOrderLine");
				SOAPElement acceptationState = validateOrderLine.addChildElement("AcceptationState");
				acceptationState.addTextNode("AcceptedBySeller");
				SOAPElement productCondition = validateOrderLine.addChildElement("ProductCondition");
				productCondition.addTextNode(productConditionVal);
				SOAPElement sellerProductId = validateOrderLine.addChildElement("SellerProductId");
				sellerProductId.addTextNode(sellerProductIdVal);
			} else {
				// if orderline is array of object
				JSONArray orderLineArrayObject = ((JSONArray) orderLineListJavaObject);
				for (int i = 0; i < orderLineArrayObject.length(); i++) {
					JSONObject orderLineObject = orderLineArrayObject.getJSONObject(i);
					String acceptationStateVal = orderLineObject.get("AcceptationState").toString();
					String productConditionVal = orderLineObject.get("ProductCondition").toString();
					String sellerProductIdVal = orderLineObject.get("SellerProductId").toString();
					System.out.println(
							"values" + acceptationStateVal + " " + productConditionVal + " " + sellerProductIdVal);
					
					if(sellerProductIdVal!=null && !sellerProductIdVal.trim().isEmpty()) {
						SOAPElement validateOrderLine = orderLineList.addChildElement("ValidateOrderLine");
						SOAPElement acceptationState = validateOrderLine.addChildElement("AcceptationState");
						acceptationState.addTextNode(orderStateVal);
						SOAPElement productCondition = validateOrderLine.addChildElement("ProductCondition");
						productCondition.addTextNode(productConditionVal);
						SOAPElement sellerProductId = validateOrderLine.addChildElement("SellerProductId");
						sellerProductId.addTextNode(sellerProductIdVal);
					}
				}
			}

			SOAPElement orderNumber = validateOrder.addChildElement("OrderNumber");
			orderNumber.addTextNode(orderNumberVal);
			SOAPElement orderState = validateOrder.addChildElement("OrderState");
			orderState.addTextNode(orderStateVal);
			SOAPElement trackingNumber = validateOrder.addChildElement("TrackingNumber");
			trackingNumber.addTextNode("TrackingNumber");
			SOAPElement trackingUrl = validateOrder.addChildElement("TrackingUrl");
			trackingUrl.setAttribute(i_nil, "true");
		
		}

		// to print
//		DOMSource source = new DOMSource(soapBody);
//		StringWriter stringResult = new StringWriter();
//		TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
//		String message = stringResult.toString();
//		System.out.println("soapBody"+message);
	}

	public static String getToken() throws IOException {
		String url = "https://sts.cdiscount.com/users/httpIssue.svc/?realm=https://wsvc.cdiscount.com/MarketplaceAPIService.svc";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		String userCredentials = "PetitsPrix-api:woT@xLhS68#vS";
		String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
		con.setRequestProperty("Authorization", basicAuth);
		System.out.println("basicAuth" + basicAuth);
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			String str = "";
			Pattern ANCHOR_PATTERN = Pattern.compile(
					"<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">([0-9a-zA-Z]*)</string>",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = ANCHOR_PATTERN.matcher(response.toString());
			if (matcher.find()) {
				return matcher.group(1);
			}
		} else {
			System.out.println("GET request not worked");
		}
		return "";
	}

	public static JSONObject getOrdersByOrderID(String orderId) {
		String str = callSoapWebService(soapEndpointUrl, soapActionOrderList, 4, orderId, null);// request api to get
																								// the new orders
		System.out.println("FINISH" + str);
		JSONObject xmlJSONObj = new JSONObject();
		try {
			xmlJSONObj = XML.toJSONObject(str);
			System.out.println("xmlJSONObj " + xmlJSONObj);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return xmlJSONObj;
	}

	public static JSONObject getAllOrders() {
		String str = callSoapWebService(soapEndpointUrl, soapActionOrderList, 3, null, null);// request api to get the
																								// new orders
		System.out.println("FINISH" + str);
		JSONObject xmlJSONObj = new JSONObject();
		try {
			xmlJSONObj = XML.toJSONObject(str);
			System.out.println("xmlJSONObj " + xmlJSONObj);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return xmlJSONObj;
	}

	public static JSONObject getAllOrdersByStatus(String status) {
		String str = callSoapWebService(soapEndpointUrl, soapActionOrderList, 1, status, null);// request api to get the
																								// new orders
		System.out.println("FINISH" + str);
		JSONObject xmlJSONObj = new JSONObject();
		try {
			xmlJSONObj = XML.toJSONObject(str);
			System.out.println("xmlJSONObj " + xmlJSONObj);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return xmlJSONObj;
	}

	public static JSONObject setTrackingNumber(JSONObject orderInfo, String trackingNumber, String carrierString) {
		orderInfo.put("trackingNumber", trackingNumber);
		orderInfo.put("carrierString", carrierString);
		String str = callSoapWebService(soapEndpointUrl, soapAction, 2, null, orderInfo);// request api to get the new
																							// orders
		System.out.println("FINISH" + str);
		JSONObject xmlJSONObj = new JSONObject();
		try {
			xmlJSONObj = XML.toJSONObject(str);
			System.out.println("xmlJSONObj " + xmlJSONObj);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return xmlJSONObj;
	}
	
	public static JSONObject changeStatus(JSONObject orderInfo) {
		String str = callSoapWebService(soapEndpointUrl, soapAction, 5, null, orderInfo);// request api to get the new																					// orders
		System.out.println("FINISH" + str);
		JSONObject xmlJSONObj = new JSONObject();
		try {
			xmlJSONObj = XML.toJSONObject(str);
			System.out.println("xmlJSONObj " + xmlJSONObj);
		} catch (JSONException je) {
			System.out.println(je.toString());
		}
		return xmlJSONObj;
	}
	
	public static void acceptOrRejectOrder(String orderNumber,String state) {
		
		JSONObject xmlJSONObj = getOrdersByOrderID(orderNumber);
		JSONObject orderInfo = xmlJSONObj.getJSONObject("s:Envelope").getJSONObject("s:Body")
				.getJSONObject("GetOrderListResponse").getJSONObject(("GetOrderListResult")).getJSONObject("OrderList")
				.getJSONObject("Order");
		System.out.println("orderInfo" + orderInfo);
		orderInfo.put("OrderState", state);
		changeStatus(orderInfo);
	}


}
