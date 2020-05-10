package com.utils;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.controllers.automation.FetchMessages;
import com.example.demo.controllers.automation.FnacTrackingNumber;
import com.example.demo.controllers.automation.PlaceOrder;
import com.example.demo.controllers.automation.ScrapeTrackingNumber;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.SystemColor;
import javax.swing.JTextPane;

public class HomeGUI {

	public JFrame frame;
	private JTextField txtHi;
	private JTextField textField;
	private JLabel lblNewLabel_2;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField EAN_textfield;
	private JLabel blackListStatus;
	static List<String> marketTypes=new ArrayList<String>();
	private JTextField textField_3;
	private JTextField textField_4;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		marketTypes=CommonUtils.readCSVData("files/MarketTypes.csv");
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
		txtHi.setBounds(271, 36, 208, 26);
		frame.getContentPane().add(txtHi);
		txtHi.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Order Number");
		lblNewLabel.setBounds(271, 16, 141, 20);
		frame.getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(271, 95, 208, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Vendor Number");
		lblNewLabel_1.setBounds(271, 69, 141, 20);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnGet = new JButton("Get");
		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> result=FirebaseUtils.getVendorNumberWithOrderNumber(txtHi.getText());
				textField.setText(result.get("vendor_number").toString());
			}
		});
		btnGet.setBounds(484, 35, 79, 29);
		frame.getContentPane().add(btnGet);
		
		JButton btnInsert = new JButton("Insert");
		btnInsert.setBounds(484, 94, 79, 29);
		frame.getContentPane().add(btnInsert);
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean status=FirebaseUtils.insertOrderNumber(txtHi.getText(), textField.getText());
				displayToast(lblNewLabel_2, status);
			}
		});
		
		lblNewLabel_2 = new JLabel("Status");
		lblNewLabel_2.setBounds(425, 69, 69, 20);
		lblNewLabel_2.setVisible(false);
		frame.getContentPane().add(lblNewLabel_2);
		
		JButton btnNewButton_2 = new JButton("View Recent");
		btnNewButton_2.setBounds(402, 176, 128, 29);
		frame.getContentPane().add(btnNewButton_2);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewInTable(FirebaseUtils.getRecentUpdatedRecords("order_shipped", Integer.parseInt(textField_2.getText())));
			}
		});
		
		JButton btnNewButton_3 = new JButton("View By Date");
		btnNewButton_3.setBounds(405, 137, 125, 29);
		frame.getContentPane().add(btnNewButton_3);
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewInTable(FirebaseUtils.getRecordsByDate("order_shipped", textField_1.getText()));
			}
		});
		
		textField_1 = new JTextField();
		textField_1.setBounds(271, 137, 119, 26);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setToolTipText("Enter date in dd/MM/yyyy Format");
		SimpleDateFormat sd=new SimpleDateFormat("dd/MM/yyyy");
		textField_1.setText(sd.format(new Date()));
		
		textField_2 = new JTextField();
		textField_2.setBounds(271, 176, 116, 28);
		frame.getContentPane().add(textField_2);
		textField_2.setToolTipText("Enter number of recent records to fetch");
		textField_2.setColumns(10);
		textField_2.setText("10");
		
		EAN_textfield = new JTextField();
		EAN_textfield.setBounds(26, 467, 193, 26);
		frame.getContentPane().add(EAN_textfield);
		EAN_textfield.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("EAN Code");
		lblNewLabel_3.setBounds(26, 443, 102, 20);
		frame.getContentPane().add(lblNewLabel_3);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("Add new market types in MarketType.csv file ");
		marketTypes.stream().toArray(String[]::new);
		comboBox.setModel(new DefaultComboBoxModel(marketTypes.stream().toArray(String[]::new)));
		comboBox.setBounds(26, 408, 193, 26);
		frame.getContentPane().add(comboBox);
		
		JButton btnNewButton_4 = new JButton("Add to Blacklist");
		btnNewButton_4.setBounds(234, 466, 155, 29);
		frame.getContentPane().add(btnNewButton_4);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filename="files/blacklist/"+comboBox.getSelectedItem()+"_BlackList.csv";
				String EANCode=EAN_textfield.getText();
				boolean status=CommonUtils.addToBlackList(filename, EANCode);
				displayToast(blackListStatus, status);
			}
		});
		
		JLabel lblNewLabel_4 = new JLabel("Market Type");
		lblNewLabel_4.setToolTipText("Add new market types in MarketType.csv file ");
		lblNewLabel_4.setBounds(26, 382, 124, 20);
		frame.getContentPane().add(lblNewLabel_4);
		
		blackListStatus = new JLabel("Status");
		blackListStatus.setBounds(234, 443, 69, 20);
		frame.getContentPane().add(blackListStatus);
		
		textField_3 = new JTextField();
		textField_3.setBounds(234, 408, 156, 26);
		frame.getContentPane().add(textField_3);
		textField_3.setColumns(10);
		
		JLabel lblFilepath = new JLabel("Filepath");
		lblFilepath.setBounds(234, 382, 69, 20);
		frame.getContentPane().add(lblFilepath);
		
		JButton btnGenFiles = new JButton("Gen. Files");
		btnGenFiles.setBounds(425, 407, 115, 29);
		frame.getContentPane().add(btnGenFiles);
		
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setBounds(425, 382, 69, 20);
		frame.getContentPane().add(lblStatus);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(271, 242, 119, 26);
		frame.getContentPane().add(textPane);
		
		JLabel lblOrderState = new JLabel("Order State");
		lblOrderState.setBounds(270, 216, 117, 20);
		frame.getContentPane().add(lblOrderState);
		
		JButton btnViewOrders = new JButton("View Orders");
		btnViewOrders.setBounds(402, 242, 138, 29);
		frame.getContentPane().add(btnViewOrders);
		
		textField_4 = new JTextField();
		textField_4.setBounds(271, 309, 124, 26);
		frame.getContentPane().add(textField_4);
		textField_4.setColumns(10);
		
		JLabel lblOrderNumber = new JLabel("Order Number");
		lblOrderNumber.setBounds(271, 285, 125, 20);
		frame.getContentPane().add(lblOrderNumber);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.setBounds(402, 308, 92, 29);
		frame.getContentPane().add(btnAccept);
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SOAPClientSAAJ.acceptOrRejectOrder(textField_4.getText(),"AcceptedBySeller");
			}
		});
		
		JButton btnReject = new JButton("Reject");
		btnReject.setBounds(495, 308, 97, 29);
		frame.getContentPane().add(btnReject);
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SOAPClientSAAJ.acceptOrRejectOrder(textField_4.getText(),"RefusedBySeller");
			}
		});
		
		btnViewOrders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[][] array =null;
				JSONObject json=SOAPClientSAAJ.getAllOrdersByStatus(textPane.getText());
				JSONArray orderList = json.getJSONObject("s:Envelope").getJSONObject("s:Body")
						.getJSONObject("GetOrderListResponse").getJSONObject(("GetOrderListResult")).getJSONObject("OrderList")
						.getJSONArray("Order");
				array = new String[orderList.length()][4];
				for (int i = 0; i < orderList.length(); i++) {
					JSONObject obj=orderList.getJSONObject(i);
					array[i][0] = String.valueOf(i+1);
					array[i][1] = obj.getString("OrderNumber");
					array[i][2] = obj.getString("CreationDate");
				}
				viewOrders(array);
			}
		});
		
		btnGenFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String marketType=comboBox.getSelectedItem().toString();
				String filepath=textField_3.getText().trim();
				System.out.println("filepath"+filepath);
				if(marketType.equalsIgnoreCase("R")) {
					if(!filepath.isEmpty()) {
						boolean status=CommonUtils.processASINDataFeed(filepath, marketType);
						displayToast(lblStatus, status);
					}else {
						boolean status=CommonUtils.processASINDataFeed("files/asin data feed.csv", marketType);
						displayToast(lblStatus, status);
					}
				}
				if(marketType.equalsIgnoreCase("CDiscount")) {
					if(!filepath.isEmpty()) {
						boolean status=CdiscountDataUtil.processASINDataFeed(filepath, marketType);
						displayToast(lblStatus, status);
					}else {
						boolean status=CdiscountDataUtil.processASINDataFeed("files/input/CdisocuntInputASINs.csv", marketType);
						displayToast(lblStatus, status);
					}
				}
			}
		});
		

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
	    f.getContentPane().add(sp);          
	    f.setSize(700,800);    
	    f.setVisible(true); 
	}
	
	public static void viewOrders(String[][] array) {
		JFrame f;    
	    f=new JFrame();    
	    String data[][]=array;    
	    String column[]={"S.No","Order Number", "Date and Time"};         
	    JTable jt=new JTable(data,column);
	    TableColumnModel tcm = jt.getColumnModel();
	    tcm.getColumn(0).setPreferredWidth(5);
	    jt.setBounds(30,40,500,1000);          
	    JScrollPane sp=new JScrollPane(jt);    
	    f.getContentPane().add(sp);          
	    f.setSize(700,800);    
	    f.setVisible(true); 
	}
	
	public static void displayToast(JLabel label ,boolean status) {
		if(status) {
			label.setText("Success");
			label.setVisible(true);
		}else {
			label.setText("Failed");
			label.setForeground(Color.RED);
			label.setVisible(true);
		}
		label.paintImmediately(label.getVisibleRect());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		label.setVisible(false);
	}
}
