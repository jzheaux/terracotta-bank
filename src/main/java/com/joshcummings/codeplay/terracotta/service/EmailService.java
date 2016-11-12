package com.joshcummings.codeplay.terracotta.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
	private String from = "no-reply-terracotta-bank@mailinator.com";
	private String host = "in-v3.mailjet.com";
    private Properties properties = System.getProperties();
    
    {
    	properties.setProperty("mail.smtp.host", host);
    	properties.setProperty("mail.smtp.auth", "true");
    }
    
    
    public void sendMessage(String to, String subject, String content) {
	    Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("49ef5e4854ccd94d532dd275b77135b8", "296e1e0085b8aa90e06da635c357ecf1");
				}
			  });

        MimeMessage message = new MimeMessage(session);

        try {
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        message.setSubject(subject);
	        message.setText(content);
	        Transport.send(message);
        } catch ( MessagingException mex ) {
        	throw new IllegalStateException(mex);
        }
    }
}
