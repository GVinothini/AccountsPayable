package com.altimetrik.saasproduct.payable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;

public class ReceiveEmailAttachment {
	
	   static String userName = null;
	   static String password = null;
	   static Address to;
	
	
	public void sendApprovalMail(String invoice_number)	{
		String  msg  = "your invoice "+invoice_number+" got approved.";
    	String subject = "Accounts Payable";

		  Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			
			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			try {
				MimeMessage message = new MimeMessage(session);
				String toAddress = to.toString();
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
				message.setSubject(subject);
				message.setText(msg);
				// send message
				Transport.send(message);
				System.out.println("Approved Successfully.... Check your Mail inbox for acknowledgement");
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	  }
	  
	  
	public static void receiveEmail(String pop3Host, 
			String mailStoreType, String userName, String password){
				
			 ReadInvoiceDetails readPDF = new ReadInvoiceDetails();
			 String fileLocation = new String();
			 
			 Properties props = new Properties();
			 	props.put("mail.store.protocol", "pop3");
			    props.put("mail.pop3.host", pop3Host);
			    props.put("mail.pop3.port", "995");
			    props.put("mail.pop3.starttls.enable", "true");
			 
			    // Get the Session object.
			    Session session = Session.getInstance(props);
		    
			    try {
		        //Create the POP3 store object and connect to the pop store.
					Store store = session.getStore("pop3s");
					store.connect(pop3Host, userName, password);
				 
					//Create the folder object and open it in your mailbox.
					Folder emailFolder = store.getFolder("INBOX");
					emailFolder.open(Folder.READ_ONLY);
				 
					//Retrieve the messages from the folder object.
					Message[] messages = emailFolder.getMessages();
					System.out.println("Total Message" + messages.length);
				 
					//Iterate the messages
					for (int i = 0; i < messages.length; i++) {
					   Message message = messages[i];
					   Address[] toAddress = 
				             message.getRecipients(Message.RecipientType.TO);
					     System.out.println("---------------------------------");  
					     System.out.println("Details of Email Message " 
				                                                   + (i + 1) + " :");  
					     System.out.println("Subject: " + message.getSubject());  
					     if(message.getSubject().equalsIgnoreCase("Accounts Payable")){
					    	 to = message.getFrom()[0];
					    	 System.out.println(to);
					     }
					     System.out.println("From: " + message.getFrom()[0]);  
				 
					     //Iterate recipients 
					     System.out.println("To: "); 
					     for(int j = 0; j < toAddress.length; j++){
					       System.out.println(toAddress[j].toString());
					     }
				 
					     if(message.getContent() instanceof Multipart)  {  //check instance of multipart
					     //Iterate multiparts
						     Multipart multipart = (Multipart) message.getContent();
						    
						     for(int k = 0; k < multipart.getCount(); k++){
						       BodyPart bodyPart = multipart.getBodyPart(k);  
						       if(bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
						    	   System.out.println("file name " +bodyPart.getFileName());
							       System.out.println("size " +bodyPart.getSize());
							       System.out.println("content type " +bodyPart.getContentType());
							       InputStream stream = (InputStream) bodyPart.getInputStream(); 
							       File targetFile = new File("d:\\vino\\"+bodyPart.getFileName());
							       fileLocation =targetFile.toString();
							       java.nio.file.Files.copy(
							    		   	  stream, 
							    		      targetFile.toPath(), 
							    		      StandardCopyOption.REPLACE_EXISTING);
							    		    //  IOUtils.closeQuietly(stream);
							       
						       }
						     }  
			     
					     }
					}
		 
			   //close the folder and store objects
			   emailFolder.close(false);
			   store.close();	
			   
			   readPDF.readPdf(fileLocation);	//call readPDf in ReadInvoiceDetails
			   
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (MessagingException e){
				e.printStackTrace();
			} catch (Exception e) {
			       e.printStackTrace();
			}
		 			
	   }
		 
		public static void main(String[] args) {
			Scanner scanner = new Scanner(System.in);
			  ReceiveEmailAttachment attachment = new ReceiveEmailAttachment();
			  String pop3Host = "pop.gmail.com";//change accordingly
			  String mailStoreType = "pop3";	
			  System.out.println("Enter your email id:   ");
			  userName = scanner.next();
			  System.out.println("Enter your password:   ");
			  password = scanner.next();
//			  userName = "vinogv13@gmail.com";
//			  password = "9942259055";
	  //call receiveEmail
			  receiveEmail(pop3Host, mailStoreType, userName, password);
		 }
		}


