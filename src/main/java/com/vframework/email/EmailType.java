package com.vframework.email;

public enum EmailType {
	TEXT("text/plain"), HTML("text/html");

	private String type;

	private EmailType(final String type) {
		this.type = type;
	}

	public String getValue() {
		return type;
	}
}
