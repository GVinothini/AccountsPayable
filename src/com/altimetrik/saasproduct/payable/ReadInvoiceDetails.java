package com.altimetrik.saasproduct.payable;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class ReadInvoiceDetails {
	
	
	Connection createConnection(Connection connection) throws SQLException{
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String username ="hr";
		String password = "hr";
	
		Properties properties = new Properties();
		properties.put("user", username);
		properties.put("password", password);
		connection = DriverManager.getConnection(url, properties);
		System.out.println("Connection created Successfully....");
		return connection;
		
	}
	
	ArrayList<Integer> getInvoiceNumbers(Statement statement, ArrayList<Integer> listOfInvoiceNumbers) throws SQLException{
	
		String query = "select * from INVOICE_DETAILS";
		ResultSet rs =  statement.executeQuery(query);
	
		while(rs.next())	{
			listOfInvoiceNumbers.add(rs.getInt(1));
			System.out.println("Invoice_No :    "+ rs.getInt(1));
		}
		return listOfInvoiceNumbers;
	}
	
	public void readPdf(String fileLocation)
			throws IOException, SQLException	{
		
		StoreToDB store = new StoreToDB();
		PDDocument document = null;
		Scanner sc = new Scanner(System.in);
		Connection connection = null;
		ArrayList<Integer> listOfInvoiceNumbers = new ArrayList<>();
		
		try {
			connection = createConnection(connection);
			Statement statement = connection.createStatement();
			document = PDDocument.load(new File( fileLocation));
//			System.out.println("Document  "+ document);
			
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			int noOfPages = document.getNumberOfPages();
//			System.out.println("Total No of Pages  " + noOfPages);
			
			for(int i = 0 ; i < noOfPages ; i++ ) {
				pdfTextStripper.setStartPage(i);
				pdfTextStripper.setEndPage(i);
				String text = pdfTextStripper.getText(document);
				
				if(text.contains("Invoice No") && text.contains("Net Order Total"))	{							
					store.StoreToDataBase(text, connection);   // call StoreToDatabase in storeToDB class
				}
			}	
			
			listOfInvoiceNumbers = getInvoiceNumbers(statement, listOfInvoiceNumbers);
			store.updateStatus(connection, listOfInvoiceNumbers);	//call updateStatus in ReceiveEmailAttachment

		} 
		catch(SQLException e){
			System.out.println("Error occured during database connection....");
			e.printStackTrace();

		}
		catch (IOException e) {
			System.out.println("Error occured during I/O processing...");
			e.printStackTrace();

		}
		catch (Exception e) {
			System.out.println("Error Occured " + e);
			e.printStackTrace();
			System.exit(0);

		} finally {
			if (document != null) {
				document.close();
			}
			sc.close();
			connection.close();
		}
	
	}

}
