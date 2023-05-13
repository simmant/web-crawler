package com.reliable.seoapp.view;

public class ContactView {

	private String name;
	private String emailId;
	private String contactNo;
	private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ContactModel [name=" + name + ", emailId=" + emailId + ", contactNo=" + contactNo + ", message="
				+ message + "]";
	}

}
