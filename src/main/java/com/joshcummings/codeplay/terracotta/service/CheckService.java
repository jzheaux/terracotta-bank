package com.joshcummings.codeplay.terracotta.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.joshcummings.codeplay.terracotta.model.Check;

public class CheckService extends ServiceSupport {
	private static final String CHECK_IMAGE_LOCATION = "images/checks";
	static {
		new File(CHECK_IMAGE_LOCATION).mkdirs();
	}
	
	public void addCheck(Check check) {
		runUpdate("INSERT INTO check (id, number, amount, account_id)"
				+ " VALUES ('" + check.getId() + "','" + check.getNumber() + 
				"','" + check.getAmount() + "','" + check.getAccountId() + "')");
	}
	
	public void updateCheckImage(String checkNumber, InputStream is) {
		try {
			String location = new URI(CHECK_IMAGE_LOCATION + "/" + checkNumber).normalize().toString();
			try ( FileOutputStream fos = new FileOutputStream(location) ) {
				byte[] b = new byte[1024];
				int read;
				while ( ( read = is.read(b) ) != -1 ) {
					fos.write(b, 0, read);
				}
			} catch ( IOException e ) {
				throw new IllegalArgumentException(e);
			}
		} catch ( URISyntaxException e ) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void findCheckImage(String checkNumber, OutputStream os) {
		try ( FileInputStream fis = new FileInputStream(CHECK_IMAGE_LOCATION + "/" + checkNumber) ) {
			byte[] b = new byte[1024];
			int read;
			while ( ( read = fis.read(b) ) != -1 ) {
				os.write(b, 0, read);
			}
		} catch ( IOException e ) {
			throw new IllegalArgumentException(e);
		}
	}
}
