package com.altimetrik.saasproduct.payable.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import com.altimetrik.saasproduct.payable.ReadInvoiceDetails;
import com.altimetrik.saasproduct.payable.StoreToDB;

public class testAttachment {
	ReceiveEmailAttachment attachment = new ReceiveEmailAttachment();
	Connection con = null;

	@Before
	public void doBefore(){
	
	}
	
	@Test
	public void testConfig(){
		Properties Config;
		attachment.readProperty();
	}
	
	@Test
	public void testCreateConnection(){
		ReadInvoiceDetails read = new ReadInvoiceDetails();
		Connection conn = null;
		try {
		assertEquals(true,read.createConnection(conn) instanceof Connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }	
	@Test(expected = FileNotFoundException.class)
	public void testStoreTodb() throws SQLException{
		StoreToDB store = new StoreToDB();
		store.StoreToDataBase("Invoice No \n 90356781", con);
		assertEquals(1, store.invoiceCount );
	}
	
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testReadPdf() throws IOException, SQLException  {
    	thrown.expect(FileNotFoundException.class);
        thrown.expectMessage(("FileNotFoundException"));
        
		ReadInvoiceDetails read = new ReadInvoiceDetails();
		read.readPdf("d://fhj.pdf");

	}	
}


