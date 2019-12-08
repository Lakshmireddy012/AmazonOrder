package com.example.demo.model;

public class OrderShipped {
	public String order_number;
	public String shipped;
	public String tracking_number;
	public String tracking_url;
	public String transporter_name;
	public String vendor_number;
	public OrderShipped(String order_number, String shipped, String tracking_number, String tracking_url,
			String transporter_name, String vendor_number) {
		this.order_number = order_number;
		this.shipped = shipped;
		this.tracking_number = tracking_number;
		this.tracking_url = tracking_url;
		this.transporter_name = transporter_name;
		this.vendor_number = vendor_number;
	}

	
}
