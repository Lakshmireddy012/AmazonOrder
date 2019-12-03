package com.example.demo.model;

public class Message {
	String header;
	String date;
	String dateTime;
	String content;
	public String getHeader() {
		return header;
	}
	public Message(String header, String date, String dateTime, String content) {
		super();
		this.header = header;
		this.date = date;
		this.dateTime = dateTime;
		this.content = content;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
