package com.utils;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.example.demo.controllers.automation.FetchMessages;
import com.example.demo.controllers.automation.FnacTrackingNumber;
import com.example.demo.controllers.automation.PlaceOrder;
import com.example.demo.controllers.automation.ScrapeTrackingNumber;

import javax.swing.JButton;
import java.awt.SystemColor;

public class HomeGUI {

	public JFrame frame;
	private JTextField txtHi;
	private JTextField textField;
	private JLabel lblNewLabel_2;
	private JTextField textField_1;
	private JTextField textField_2;
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
		frame.setBounds(100, 100, 614, 565);
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
		
		txtHi = new JTextField();
		txtHi.setToolTipText("Enter Order Number");
		txtHi.setBounds(271, 50, 208, 26);
		frame.getContentPane().add(txtHi);
		txtHi.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Order Number");
		lblNewLabel.setBounds(271, 26, 141, 20);
		frame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(271, 118, 208, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Vendor Number");
		lblNewLabel_1.setBounds(271, 86, 141, 20);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnGet = new JButton("Get");
		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> result=FirebaseUtils.getVendorNumberWithOrderNumber(txtHi.getText());
				textField.setText(result.get("vendor_number").toString());
			}
		});
		btnGet.setBounds(484, 49, 79, 29);
		frame.getContentPane().add(btnGet);
		
		JButton btnInsert = new JButton("Insert");
		btnInsert.setBounds(484, 118, 79, 29);
		frame.getContentPane().add(btnInsert);
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean status=FirebaseUtils.insertOrderNumber(txtHi.getText(), textField.getText());
				if(status) {
					lblNewLabel_2.setText("Success");
					lblNewLabel_2.setVisible(true);
				}else {
					lblNewLabel_2.setText("Failed");
					lblNewLabel_2.setForeground(Color.RED);
					lblNewLabel_2.setVisible(true);
				}
				lblNewLabel_2.paintImmediately(lblNewLabel_2.getVisibleRect());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				lblNewLabel_2.setVisible(false);
			}
		});
		
		lblNewLabel_2 = new JLabel("Status");
		lblNewLabel_2.setBounds(425, 86, 69, 20);
		lblNewLabel_2.setVisible(false);
		frame.getContentPane().add(lblNewLabel_2);
		
		JButton btnNewButton_2 = new JButton("View Recent");
		btnNewButton_2.setBounds(157, 464, 128, 29);
		frame.getContentPane().add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewInTable(FirebaseUtils.getRecentUpdatedRecords("order_shipped", Integer.parseInt(textField_2.getText())));
			}
		});
		
		JButton btnNewButton_3 = new JButton("View By Date");
		btnNewButton_3.setBounds(160, 421, 125, 29);
		frame.getContentPane().add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewInTable(FirebaseUtils.getRecordsByDate("order_shipped", textField_1.getText()));
			}
		});
		
		textField_1 = new JTextField();
		textField_1.setBounds(26, 422, 119, 26);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setToolTipText("Enter date in dd/MM/yyyy Format");
		SimpleDateFormat sd=new SimpleDateFormat("dd/MM/yyyy");
		textField_1.setText(sd.format(new Date()));
		
		textField_2 = new JTextField();
		textField_2.setBounds(26, 465, 116, 28);
		frame.getContentPane().add(textField_2);
		textField_2.setToolTipText("Enter number of recent records to fetch");
		textField_2.setColumns(10);
		textField_2.setText("10");
	}
	
	public static void viewInTable(String[][] array) {
		JFrame f;    
	    f=new JFrame();    
	    String data[][]=array;    
	    String column[]={"S.No","Order Number","Amazon Order Number", "Date and Time"};         
	    JTable jt=new JTable(data,column);
	    TableColumnModel tcm = jt.getColumnModel();
	    tcm.getColumn(0).setPreferredWidth(5);
	    jt.setBounds(30,40,500,1000);          
	    JScrollPane sp=new JScrollPane(jt);    
	    f.add(sp);          
	    f.setSize(700,800);    
	    f.setVisible(true); 
	}
}
