package com.utils;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import com.example.demo.controllers.automation.FetchMessages;
import com.example.demo.controllers.automation.FnacTrackingNumber;
import com.example.demo.controllers.automation.PlaceOrder;
import com.example.demo.controllers.automation.ScrapeTrackingNumber;

import javax.swing.JButton;
import java.awt.SystemColor;

public class HomeGUI {

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/**
	 * Create the application.
	 */
	public HomeGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnPlaceOrder = new JButton("Place Order");
		btnPlaceOrder.setBounds(26, 16, 193, 40);
		btnPlaceOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PlaceOrder().startPlaceOrder();
			}
		});
		frame.getContentPane().add(btnPlaceOrder);
		
		JButton btnCdiscountScrap = new JButton("Cdiscount Scrap");
		btnCdiscountScrap.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				new ScrapeTrackingNumber().startScrapeTrackingNumber();
			}
		});
		btnCdiscountScrap.setBounds(26, 66, 193, 40);
		frame.getContentPane().add(btnCdiscountScrap);
		
		JButton btnNewButton = new JButton("Fnac Scrap");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FnacTrackingNumber().startScrapeTrackingNumber();	
			}
		});
		btnNewButton.setBounds(26, 118, 193, 36);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Scrap Messages");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FetchMessages().startFetchMessages();
			}
		});
		btnNewButton_1.setBounds(26, 170, 193, 40);
		frame.getContentPane().add(btnNewButton_1);
	}
}
