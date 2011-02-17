package org.openmrs.module.chirdlutil.util;

import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to send email messages.
 *
 * @author Steve McKee
 */
public class MailSender {
	
	protected final Log log = LogFactory.getLog(getClass());
	private Properties props = new Properties();
	
	/**
	 * Constructor method
	 * 
	 * @param props Properties containing the mail host.  mail.smtp.host or mail.pop3.host need to be specified.
	 */
	public MailSender(Properties props) {
		this.props = props;
	}

	/**
	 * Sends the mail message.
	 * 
	 * @param sender The sender address (user@host.domain)
	 * @param recipients Array of recipient addresses (user@host.domain)
	 * @param subject The email subject.
	 * @param body The body of the email.
	 * 
	 * @return true if the email was sent successfully, false otherwise.
	 */
	public boolean sendMail(String sender, String[] recipients, String subject, String body) {
		Session session = Session.getInstance(props);
		MimeMessage message = new MimeMessage(session);
		try {
	        message.setFrom(new InternetAddress(sender));
        }
        catch (AddressException e) {
	        log.error("Error creating an internet address from: " + sender, e);
	        return false;
        }
        catch (MessagingException e) {
	        log.error("Error setting the email from field to " + sender, e);
	        return false;
        }
		
        for (String recipient : recipients) {
        	try {
	            message.addRecipient(RecipientType.TO, new InternetAddress(recipient));
            }
            catch (AddressException e) {
	            log.error("Error creating an internet address from: " + recipient, e);
	            return false;
            }
            catch (MessagingException e) {
	            log.error("Error adding the provided recipient: " + recipient, e);
	            return false;
            }
        }
        
        try {
	        message.setSubject(subject);
        }
        catch (MessagingException e) {
	        log.error("Error setting the subject.", e);
	        return false;
        }
        
        try {
	        message.setText(body);
        }
        catch (MessagingException e) {
	        log.error("Error setting the body.", e);
	        return false;
        }
        
        try {
	        Transport.send(message);
        }
        catch (MessagingException e) {
	        log.error("Error sending the email message.", e);
	        return false;
        }
        
        return true;
	}
}
