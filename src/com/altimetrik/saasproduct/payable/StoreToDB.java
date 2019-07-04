package com.altimetrik.saasproduct.payable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class StoreToDB {
	int invoiceCount = 0;
	
	void updateStatus(Connection connection, ArrayList<Integer> list ){
		
		ReceiveEmailAttachment attachment = new ReceiveEmailAttachment();
		Scanner scanner = new Scanner(System.in);
		
		for(Integer invoices : list){
			System.out.println(invoices);
		}
		
		System.out.println("1. Enter 1 to approve Or\n 2. Enter exit to exit from application....");
		
		while(!(scanner.next().equalsIgnoreCase("exit"))){
					System.out.println("Enter invoice_number which you want to approve:  ");
			String invoice_number = scanner.next();
			
			try {
				String query = "update invoice_details set status = ? where invoice_number = ?";
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, "Approved");
				preparedStmt.setString(2, invoice_number);
				preparedStmt.executeUpdate();
				System.out.println(invoice_number + " got approved.");
				attachment.sendApprovalMail(invoice_number);   // call sendApprovalMail in ReceiveEmailAttachment
			
			} catch (Exception e) {
				System.out.println("Error Occured during approving invoice "+invoice_number);
				e.printStackTrace();
				System.exit(0);
			}
			System.out.println("1.Enter 1 to approve Or\n Enter exit to exit from application....");
		}
		
		System.out.println("Exited successfully......");
		
		
	}
	
	void StoreToDataBase(String text, Connection connection ) throws SQLException	{
			String pageDetails[] = text.split("\r\n|\r|\n");
			ArrayList<String>  list = new ArrayList<>();
			String querytoInsert = " insert into INVOICE_DETAILS ( invoice_number, order_number,"
					+ " customer_po, invoice_date, sold_to, total_invoice, status)"
					+ " values (?, ?, ?, ?, ?, ?, ?)";
									
			invoiceCount++;
			System.out.println("INVOICE "+ invoiceCount);
			
			for(int i =0 ; i < pageDetails.length ; i++ )	{
				
				switch(pageDetails[i])
		    	{
		    	    case "Invoice No":
		    	    	list.add(pageDetails[++i]);
		    	    	break;
		    	    case "Invoice Date":
		    	    	list.add( pageDetails[++i]);
			    	    break;
		    	    case "Order No":
		    	    	list.add(pageDetails[++i]);
		    	    	break;
		    	    case "Customer P.O.":
		    	    	list.add(pageDetails[++i]);
			    	    break;
		    	    case "Sold To":
		    	    	list.add(pageDetails[++i]+" "+pageDetails[++i]+" "+pageDetails[++i]);
			    	     break;
		    	    case "Total Invoice":
		    	    	  if(invoiceCount == 3)
		    	    	    i= i+4;
		    	    	  else
		    	    	    i= i+3;
		    	    	 list.add(pageDetails[i]);
			    	     break;
		    	    case "CREDIT":
		    	    	list.add(pageDetails[++i]);
			    	      break;
		    	}			
			}
			
			ArrayList<Integer> invoice_numbers = new ArrayList<>();
			invoice_numbers =	new ReadInvoiceDetails().getInvoiceNumbers(connection.createStatement(), invoice_numbers);
			
			if( !(invoice_numbers.contains( list.get(0) ) ) ) {
				System.out.println(list.get(1));
				PreparedStatement preparedStmt = connection.prepareStatement(querytoInsert);
				preparedStmt.setObject(1, list.get(0));
				preparedStmt.setObject(2, list.get(2));
				preparedStmt.setObject(3, list.get(1));
				preparedStmt.setObject(4, list.get(3));
				preparedStmt.setObject(5, list.get(4));
				preparedStmt.setObject(6, list.get(5));
				preparedStmt.setString(7, "Unapproved");
				preparedStmt.execute();
				
			} else{
				System.out.println("your invoice inserted already...");
			}
		
	}

}
