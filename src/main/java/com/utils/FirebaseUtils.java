package com.utils;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.example.demo.model.OrderShipped;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseUtils {

	public static void main(String[] args) {
		// System.out.println("val"+isOrderNumberExist("HI"));
//		ArrayList<String> cDiscoundOrderNumbers=new ArrayList<String>();
//		cDiscoundOrderNumbers.add("191205195221T5D");
//		cDiscoundOrderNumbers.add("191205195021UX3");
//		Map<String , String> totalRecords=getAllOrderNumber(cDiscoundOrderNumbers, "order_shipped");
//		for (String string : totalRecords.keySet()) {
//			System.out.println("key "+string +"value "+totalRecords.get(string));
//		}
//		isOrderNumberExist("18082910052IHS6");
//		insertOrderNumber("test", "test11");
//		getRecentUpdatedRecords("order_shipped",1);
//		System.out.println("total doc count"+count);
		getRecordsByDate("order_shipped", "09/12/2019");
	}

	public static Firestore getConnectionToFirestore() {
		Firestore db = null;
		try {
			FirebaseApp firebaseApp = null;
			List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
			if (firebaseApps != null && !firebaseApps.isEmpty()) {
				for (FirebaseApp app : firebaseApps) {
					if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
						firebaseApp = app;
				}
			} else {
				FileInputStream refreshToken = new FileInputStream("files/ordersShipped.json");

				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(refreshToken))
						.setDatabaseUrl("https://orders-a0441.firebaseio.com").build();

				FirebaseApp.initializeApp(options);
			}
			
			db = FirestoreClient.getFirestore();
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return db;
	}

	public static boolean isOrderNumberExist(String order_number) {
		boolean status = true;
		try {
			System.out.println("before");
			Firestore db = getConnectionToFirestore();
			System.out.println("after");
			DocumentReference d = db.collection("order_shipped").document(order_number);
			DocumentSnapshot doc = d.get().get();
			System.out.println("doc val" + doc);
			if (doc.getData() == null) {
				status = false;
			} else {
				System.out.println("order number exist" + doc.getData());
			}
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return status;
	}

	public static boolean insertOrderNumber(String order_number, String vendorNumber) {
		try {
			Firestore db = getConnectionToFirestore();
			
			Map<String, OrderShipped> data = new HashMap<>();
			OrderShipped orderShipped = new OrderShipped(order_number, null, null, null, null, vendorNumber,new Date());
			data.put(order_number, orderShipped);
			ApiFuture<WriteResult> future = db.collection("order_shipped").document(order_number)
					.set(data.get(order_number));
			System.out.println("Update time : " + future.get().getUpdateTime());
			return true;
			
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return false;
	}

	// returns selected records
	public static Map<String, String> getAllOrderNumber(ArrayList<String> cDiscoundOrderNumbers,
			String collectionCategory) {
		Map<String, String> totalRecords = new HashMap<String, String>();
		System.out.println("cDiscoundOrderNumbers size" + cDiscoundOrderNumbers.size());
		try {
			Firestore db = getConnectionToFirestore();
			CollectionReference citiesRef = db.collection(collectionCategory);
			// split array into equal parts
			List<List<String>> allRecords = new ArrayList<List<String>>();
			int size = cDiscoundOrderNumbers.size();
			for (int i = 0; i <= size / 10; i++) {
				if ((i * 10) + 10 > size) {
					allRecords.add(cDiscoundOrderNumbers.subList(i * 10, size));
				} else {
					allRecords.add(cDiscoundOrderNumbers.subList(i * 10, (i * 10) + 10));
				}
			}
			// fetch orders
			for (int i = 0; i < allRecords.size(); i++) {
				List<String> subList = allRecords.get(i);
//				System.out.println("subList size"+subList.size());
//				for (int j = 0; j < subList.size(); j++) {
//					System.out.println("val"+subList.get(j));
//				}
				Query query = citiesRef.whereIn("order_number", subList);
				ApiFuture<QuerySnapshot> q = query.get();
				QuerySnapshot querySnapshot = q.get();
				List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
				for (QueryDocumentSnapshot document : documents) {
					totalRecords.put(document.getString("order_number"), document.getString("vendor_number"));
				}
			}
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		System.out.println("totalRecords size" + totalRecords.size());
		return totalRecords;
	}

	public static Map<String, Object> getVendorNumberWithOrderNumber(String order_number) {
		DocumentSnapshot doc = null;
		try {
			Firestore db = getConnectionToFirestore();
			DocumentReference d = db.collection("order_shipped").document(order_number);
			doc = d.get().get();
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return doc.getData();
	}
	
	public static int getCountOfDocuments(String collectionCategory) {
		try {
			Firestore db = getConnectionToFirestore();
			ApiFuture<QuerySnapshot> query = db.collection(collectionCategory).get();
			QuerySnapshot querySnapshot = query.get();
			return querySnapshot.size();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
	
	public static String[][] getRecentUpdatedRecords(String collectionCategory,int count) {
		String[][] array =null;
		try {
			Firestore db = getConnectionToFirestore();
			Query query = db.collection(collectionCategory).orderBy("updateDate", Direction.DESCENDING).limit(count);
			ApiFuture<QuerySnapshot> q = query.get();
			QuerySnapshot querySnapshot = q.get();
			System.out.println("querySnapshot size"+querySnapshot.size());
			array = new String[querySnapshot.size()][4];
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			int i=0;
			for (QueryDocumentSnapshot document : documents) {
				array[i][0] = String.valueOf(i+1);
				array[i][1] = document.getString("order_number");
				array[i][2] = document.getString("vendor_number");
				array[i][3] = document.getTimestamp("updateDate").toString();
				i++;
				System.out.println(" val "+document.getTimestamp("updateDate")+" "+document.getString("order_number"));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return array;
	}
	
	public static String[][] getRecordsByDate(String collectionCategory,String date) {
		String[][] array =null;
		try {
			Firestore db = getConnectionToFirestore();
			// set time zone and JVM level
			TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
			Date startDate = df.parse(date+" 00:00:00");
			Calendar c = Calendar.getInstance(); 
			c.setTime(startDate); 
			c.add(Calendar.DATE, 1);
			Date startDate1 = c.getTime();
			System.out.println("date"+startDate+startDate1);
			Query query = db.collection(collectionCategory).whereGreaterThanOrEqualTo("updateDate", startDate).whereLessThan("updateDate", startDate1);
			ApiFuture<QuerySnapshot> q = query.get();
			QuerySnapshot querySnapshot = q.get();
			System.out.println("querySnapshot size"+querySnapshot.size());
			array = new String[querySnapshot.size()][4];
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			int i=0;
			for (QueryDocumentSnapshot document : documents) {
				array[i][0] = String.valueOf(i+1);
				array[i][1] = document.getString("order_number");
				array[i][2] = document.getString("vendor_number");
				array[i][3] = df.format(document.getDate("updateDate"));
				i++;
				System.out.println(" val "+document.getTimestamp("updateDate")+" "+document.getString("order_number"));
			}
			
		} catch (Exception e) {
			System.out.println("e"+e);
		}
		return array;
	}
}
