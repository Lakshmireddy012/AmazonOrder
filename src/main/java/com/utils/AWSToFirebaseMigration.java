package com.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;

import com.example.demo.model.OrderShipped;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class AWSToFirebaseMigration {
	static Map<String , OrderShipped> orderInfoMap=new HashMap<String, OrderShipped>();
	public static void main(String[] args) {
		// for fnac migration
//		orderInfoMap=AWSDBConnection.getAllOrderNumber("fnac_shipping_address");
//		System.out.println("size"+orderInfoMap.size());
//		saveToFirebase("fnac_shipping_address");
		
		// for cdiscount remaining
		orderInfoMap=AWSDBConnection.getAllOrderNumber("order_shipped");
		System.out.println("size"+orderInfoMap.size());
		//saveToFirebase("order_shipped");
		LinkedHashMap <String, String> placeOrderData=readPlaceOrderCSVData("C:\\Users\\1023610\\Documents\\CSV_latest.csv");
		for (String string : placeOrderData.keySet()) {
			System.out.println("string"+string);
			orderInfoMap.remove(string);
		}
		System.out.println("orderInfoMap remaining"+orderInfoMap.size());
		saveToFirebase("order_shipped");
		
	}
	
	public static void saveToFirebase(String collectionCategory) {
		try {
			FileInputStream refreshToken = new FileInputStream("files/ordersShipped.json");

			FirebaseOptions options = new FirebaseOptions.Builder()
			    .setCredentials(GoogleCredentials.fromStream(refreshToken))
			    .setDatabaseUrl("https://orders-a0441.firebaseio.com")
			    .build();

			FirebaseApp.initializeApp(options);
			Firestore db = FirestoreClient.getFirestore();
			int i=1;
			for (String key : orderInfoMap.keySet()) {
				System.out.println("*******"+i+key);
//				if(!FirebaseUtils.isOrderNumberExist(key)) {
				//if(i>9075) {
					ApiFuture<WriteResult> future = db.collection(collectionCategory).document(key).set(orderInfoMap.get(key));
					System.out.println("Update time : " + future.get().getUpdateTime());

				//}
					//				}else {
//					System.out.println("skipped");
//				}
				i++;
			}
		}catch (Exception e) {
			System.out.println("Exception"+ e);
		}
	}
	
	public static LinkedHashMap <String,String> readPlaceOrderCSVData(String filepath) {
		LinkedHashMap <String, String> placeOrderData=new LinkedHashMap <String, String>();
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		try { 
			  
			br = new BufferedReader(new FileReader(filepath));
            while ((line = br.readLine()) != null) {
                String[] ordersList = line.split(cvsSplitBy);
              //  if(ordersList.length==2) {
                	System.out.println("ordersList"+ordersList.length);
                	System.out.println(ordersList[0] +" "+ ordersList[5]);
                	placeOrderData.put(ordersList[0], ordersList[5]);
              //  }
                
            }
	    } 
	    catch (Exception e) { 
	    	System.out.println("error"+e);
	        e.printStackTrace(); 
	    } 
		return placeOrderData;
	}
}
