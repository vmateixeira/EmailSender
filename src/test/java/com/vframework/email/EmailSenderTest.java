package com.vframework.email;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmailSenderTest {
	
	private Properties systemProperties = System.getProperties();
	
	@BeforeClass
	public static void beforeClass() {
		//Not needed
	}
	
	@Before
	public void init() {
		systemProperties.setProperty("mail.smtp.user", "");
		systemProperties.setProperty("mail.smtp.password", "");
		systemProperties.setProperty("mail.smtp.host", "");
		systemProperties.setProperty("mail.smtp.port", "");
		
		systemProperties.setProperty("vframework.email.sender", "");
		systemProperties.setProperty("vframework.email.toRecipients", "");
		systemProperties.setProperty("vframework.email.ccRecipients", "");
		systemProperties.setProperty("vframework.email.bccRecipients", "");
		systemProperties.setProperty("vframework.email.subject", "");
		systemProperties.setProperty("vframework.email.message", "");
		systemProperties.setProperty("vframework.email.attachments", "");
		systemProperties.setProperty("vframework.email.attachmentsPath", "");
		systemProperties.setProperty("vframework.email.type", "");
	}
	
	@Test
	public void sendEmailTest() {
		String sender = "";
		String[] toRecipients = {};
		String[] ccRecipients = {};
		String[] bccRecipients = {};
		String subject = "";
		String message = "";
		String[] attachments = {};
		String attachmentsPath = "";
		
		String type = systemProperties.getProperty("vframework.email.type");
		EmailType emailType = null;
		
		if(type.equals("HTML")) {
			emailType = EmailType.HTML;
		} else if(type.equals("TEXT")){
			emailType = EmailType.TEXT;
		}
		
		try {
			boolean result = EmailSender.sendEmail(
					sender,
					toRecipients,
					ccRecipients,
					bccRecipients,
					subject,
					message,
					attachments,
					attachmentsPath,
					emailType);
			assertTrue(result);
			
		} catch (Exception exception) {
			assertFalse(true);
		}
	}
	
	@After
	public void tearDown() {
		//Not needed
	}
	
	@AfterClass
	public static void afterClass() {
		//Not needed
	}
}
