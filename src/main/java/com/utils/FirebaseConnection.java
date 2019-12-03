package com.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseConnection {

	public static void main(String[] args) throws IOException, Exception, ExecutionException {
		FileInputStream refreshToken = new FileInputStream("files/refreshToken.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(GoogleCredentials.fromStream(refreshToken))
		    .setDatabaseUrl("https://artificialpermissionsolutions.firebaseio.com")
		    .build();

		FirebaseApp.initializeApp(options);
		
		Firestore db = FirestoreClient.getFirestore();
		// Create a Map to store the data we want to set
//		Map<String, Object> docData = new HashMap<>();
//		docData.put("name", "Los Angeles");
//		docData.put("state", "CA");
//		docData.put("country", "USA");
//		docData.put("regions", Arrays.asList("west_coast", "socal"));
//		// Add a new document (asynchronously) in collection "cities" with id "LA"
//		ApiFuture<WriteResult> future = db.collection("cities").document("LA").set(docData);
//		// ...
//		// future.get() blocks on response
//		System.out.println("Update time : " + future.get().getUpdateTime());
		
		
		System.out.println("users"+db.collection("users"));
	}
	
	
}
