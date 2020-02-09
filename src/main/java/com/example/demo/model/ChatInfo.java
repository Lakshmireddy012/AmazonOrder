package com.example.demo.model;

import java.util.Date;
import java.util.List;

public class ChatInfo {
	String orderNumber;
	String subject;
	String name;
	String relatedOrder;
	String date;
	Date instertedDate;
	public String getOrderNumber() {
		return orderNumber;
	}
	public ChatInfo(String orderNumber, String subject, String name, String relatedOrder, String date,
			List<Message> messages,Date instertedDate) {
		super();
		this.orderNumber = orderNumber;
		this.subject = subject;
		this.name = name;
		this.relatedOrder = relatedOrder;
		this.date = date;
		this.messages = messages;
		this.instertedDate = instertedDate;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelatedOrder() {
		return relatedOrder;
	}
	public void setRelatedOrder(String relatedOrder) {
		this.relatedOrder = relatedOrder;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	List<Message> messages;
}
