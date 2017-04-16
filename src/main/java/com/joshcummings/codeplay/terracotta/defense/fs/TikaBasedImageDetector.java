package com.joshcummings.codeplay.terracotta.defense.fs;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.tika.Tika;

public class TikaBasedImageDetector implements ImageDetector {
	private final Tika tika = new Tika();
	
	@Override
	public boolean isAnImage(String name, InputStream is) throws IOException {
		try {
			MimeType type = new MimeType(tika.detect(is));
			return "image".equals(type.getPrimaryType());
		} catch (MimeTypeParseException e) {
			throw new IOException(e);
		}
		
	}

}
