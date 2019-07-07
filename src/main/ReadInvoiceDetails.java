package com.altimetrik.saasproduct.payable;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
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
	
	public Connection createConnection(Connection connection) throws SQLException{
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
		}
		return listOfInvoiceNumbers;
	}
	
	void readPage(Connection connection, PDDocument document, PDFTextStripper pdfTextStripper,StoreToDB store) throws IOException, SQLException{
		int noOfPages = document.getNumberOfPages();
		
		for(int i = 0 ; i < noOfPages ; i++ ) {
			pdfTextStripper.setStartPage(i);
			pdfTextStripper.setEndPage(i);
			String text = pdfTextStripper.getText(document);
			
			if(text.contains("Invoice No") && text.contains("Net Order Total"))	{							
				store.StoreToDataBase(text, connection);   // call StoreToDatabase in storeToDB class
			}
		}	
		
	}
	
	public void readPdf(String fileLocation) throws IOException, SQLException	{
		
		StoreToDB store = new StoreToDB();
		PDDocument document = null;
//		Scanner sc = new Scanner(System.in);
		Connection connection = null;
		ArrayList<Integer> listOfInvoiceNumbers = new ArrayList<Integer>();
		
		try {
			document = PDDocument.load(new File( fileLocation));			
			PDFTextStripper pdfTextStripper = new PDFTextStripper();
			connection = createConnection(connection);
			Statement statement = connection.createStatement();
			readPage(connection, document, pdfTextStripper, store);
			listOfInvoiceNumbers = getInvoiceNumbers(statement, listOfInvoiceNumbers);
			store.updateStatus(connection, listOfInvoiceNumbers);	//call updateStatus in ReceiveEmailAttachment

		}catch(FileNotFoundException e){
			System.out.println("File not found to read data");
		}catch(IOException e){
			System.out.println("Exception in Input output operation");
		}catch(SQLException e){
			System.out.println("Error occured during database connection....");
		}catch (Exception e) {
			System.out.println("Error Occured " + e);
			System.exit(0);
		} finally {
			if (document != null) {
				document.close();
			}
//			sc.close();
			connection.close();
		}
	
	}

}
