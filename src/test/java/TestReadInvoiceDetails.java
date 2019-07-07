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




public class TestReadInvoiceDetails {
	Connection con = null;
	@Before
	public void before(){
//		Connection con = null;
		
	}
//	@Test(expected = FileNotFoundException.class)
//	public void testReadPdf() throws IOException, SQLException {
//		ReadInvoiceDetails read = new ReadInvoiceDetails();
//		read.readPdf("d://fhj.pdf");
//
//	}
	
	@Test
	public void testStoreTodb() throws SQLException{
		StoreToDB store = new StoreToDB();
		store.StoreToDataBase("Invoice No   90356781", con);
		assertEquals(0, store.invoiceCount );
	}

}
