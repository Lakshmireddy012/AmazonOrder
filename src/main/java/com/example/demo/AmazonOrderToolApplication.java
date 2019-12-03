package com.example.demo;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.utils.HomeGUI;

@SpringBootApplication
public class AmazonOrderToolApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AmazonOrderToolApplication.class, args);
		//initGUI();
	}
	
//	@EventListener({ApplicationReadyEvent.class})
//	void applicationReadyEvent() {
//		initGUI();
//	}
	
//	@EventListener({ApplicationReadyEvent.class})
//	void applicationReadyEvent() {
//	    System.out.println("Application started ... launching browser now");
//	    browse("http://localhost:8080/");
//	}
//
//	public static void browse(String url) {
//	    if(Desktop.isDesktopSupported()){
//	        Desktop desktop = Desktop.getDesktop();
//	        try {
//	            desktop.browse(new URI(url));
//	        } catch (IOException | URISyntaxException e) {
//	            e.printStackTrace();
//	        }
//	    }else{
//	        Runtime runtime = Runtime.getRuntime();
//	        try {
//	            runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	}

	private static void initGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeGUI window = new HomeGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
