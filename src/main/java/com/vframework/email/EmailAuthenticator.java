package com.vframework.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
	private String username;
	private String password;

	public EmailAuthenticator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}