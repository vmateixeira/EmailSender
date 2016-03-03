package com.vframework.email;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);
	
	public static void main(String[] args) {
		
		try {
			//Load properties from file
			Properties systemProperties = System.getProperties();
			systemProperties.load(new FileReader("conf/email.properties"));
			
			String sender = systemProperties.getProperty("vframework.email.sender").trim();
			String[] toRecipients = systemProperties.getProperty("vframework.email.toRecipients").trim().split(",");
			String[] ccRecipients = systemProperties.getProperty("vframework.email.ccRecipients").trim().split(",");
			String[] bccRecipients = systemProperties.getProperty("vframework.email.bccRecipients").trim().split(",");
			String subject = systemProperties.getProperty("vframework.email.subject").trim();
			String message = systemProperties.getProperty("vframework.email.message").trim();
			
			//If message is null or empty, use templates
			if(null == message || message.isEmpty()) {
				
				Calendar calendar = Calendar.getInstance();
				
				//Morning
				if(calendar.get(Calendar.HOUR_OF_DAY) <= 11) {
					
					message = new String(Files.readAllBytes(Paths.get("templates/templateManha.txt")));
					
					//Afternoon
				} else {
					message = new String(Files.readAllBytes(Paths.get("templates/templateTarde.txt")));
				}
			}
			
			String[] attachments = systemProperties.getProperty("vframework.email.attachments").trim().split(",");
			String attachmentsPath = systemProperties.getProperty("vframework.email.attachmentsPath").trim();
			
			String emailType = systemProperties.getProperty("vframework.email.type").trim();
			
			EmailType emailTypeEnum = null;
			
			if(emailType.equals("HTML")) {
				emailTypeEnum = EmailType.HTML;
				
			} else if(emailType.equals("TEXT")) {
				emailTypeEnum = EmailType.TEXT;
			}
			
			EmailSender.sendEmail(sender, toRecipients, ccRecipients, bccRecipients, subject, message, attachments, attachmentsPath, emailTypeEnum);
			
		} catch (Exception exception) {
			LOGGER.error("Something wrong happened: ", exception);
		}
	}
	
	public static synchronized boolean sendEmail(String sender, String toRecipients[], String ccRecipients[], String bccRecipients[], String subject, String message, String[] attachments, String attachmentsPath, EmailType emailType) throws Exception {
		LOGGER.debug("Trying to send email...");
		
		Properties systemProperties = System.getProperties();
		
		EmailAuthenticator authentication = new EmailAuthenticator(
				systemProperties.getProperty("mail.smtp.user"),
				systemProperties.getProperty("mail.smtp.password"));
		
		Session session = Session.getDefaultInstance(systemProperties, authentication);
		session.setDebug(true);
		
		MimeMessage mimeMessage = new MimeMessage(session);
		BodyPart messageBodyPart = new MimeBodyPart();
		
		try {
			messageBodyPart.setText(message);
			messageBodyPart.setHeader("Content-Type", emailType.getValue());
			
			Multipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(messageBodyPart);
			
			DataSource dataSource = null;
			DataHandler dataHandler = null;
			
			//Multipart (with attachments)
			if(null != attachments && attachments.length > 0) {	
				for(String attachment : attachments) {
					if(attachment.isEmpty())
						continue;
					
					messageBodyPart = new MimeBodyPart();
					dataSource = new FileDataSource(attachmentsPath + attachment);
					dataHandler = new DataHandler(dataSource);
					messageBodyPart.setDataHandler(dataHandler);
					messageBodyPart.setFileName(attachment);
					multiPart.addBodyPart(messageBodyPart);
				}
			}
			
			mimeMessage.setContent(multiPart);
			mimeMessage.setSubject(subject);
			mimeMessage.setFrom(new InternetAddress(sender));
			
			if(null != toRecipients) {
				for(String recipient : toRecipients) {
					if(recipient.isEmpty())
						continue;
					mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				}
			}
			
			if(null != ccRecipients) {
				for(String recipient : ccRecipients) {
					if(recipient.isEmpty())
						continue;
					mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient));
				}
			}
			
			if(null != bccRecipients) {
				for(String recipient : bccRecipients) {
					if(recipient.isEmpty())
						continue;
					mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
				}
			}
			
			Transport transport = session.getTransport("smtp");
			transport.connect(
					systemProperties.getProperty("mail.smtp.host"),
					Integer.parseInt(systemProperties.getProperty("mail.smtp.port")),
					systemProperties.getProperty("mail.smtp.user"),
					systemProperties.getProperty("mail.smtp.password"));
			transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			transport.close();
			
		} catch (MessagingException messagingException) {
			LOGGER.error("Error sending email: ", messagingException);
			LOGGER.debug("Email send failed!");
			return false;
		}
		
		LOGGER.debug("Email sent successfully!");
		return true;
	}
}	
