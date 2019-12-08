package com.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.model.OrderShipped;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseUtils {

	public static void main(String[] args) {
		//System.out.println("val"+isOrderNumberExist("HI"));
//		ArrayList<String> cDiscoundOrderNumbers=new ArrayList<String>();
//		cDiscoundOrderNumbers.add("18082915122ELKA");
//		cDiscoundOrderNumbers.add("18082910052IHS6");
//		getAllOrderNumber(cDiscoundOrderNumbers, "order_shipped");
//		isOrderNumberExist("18082910052IHS6");
//		insertOrderNumber("test", "test11");
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

	public static void insertOrderNumber(String order_number, String vendorNumber) {
		try {
			Firestore db = getConnectionToFirestore();
			Map<String, OrderShipped> data = new HashMap<>();
			OrderShipped orderShipped = new OrderShipped(order_number, null, null, null, null, vendorNumber);
			data.put(order_number, orderShipped);
			ApiFuture<WriteResult> future = db.collection("order_shipped").document(order_number)
					.set(data.get(order_number));
			System.out.println("Update time : " + future.get().getUpdateTime());
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
	}

	// returns selected records
	public static Map<String, String> getAllOrderNumber(ArrayList<String> cDiscoundOrderNumbers, String collectionCategory) {
		Map<String , String> totalRecords=new HashMap<String, String>();
		try {
			Firestore db = getConnectionToFirestore();
			CollectionReference citiesRef = db.collection(collectionCategory);

			Query query = citiesRef.whereIn("order_number", cDiscoundOrderNumbers);

			ApiFuture<QuerySnapshot> q=query.get();
			QuerySnapshot querySnapshot = q.get();
			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {
				totalRecords.put(document.getString("order_number"), document.getString("vendor_number"));
			}
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return totalRecords;
	}
}
