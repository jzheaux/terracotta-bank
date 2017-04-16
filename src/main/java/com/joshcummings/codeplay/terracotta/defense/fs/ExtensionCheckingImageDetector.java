package com.joshcummings.codeplay.terracotta.defense.fs;

import java.io.InputStream;
import java.util.regex.Pattern;

public class ExtensionCheckingImageDetector implements ImageDetector {
	private static final Pattern ENDS_WITH_IMG_EXTENSION =
			Pattern.compile("[A-Za-z0-9_\\-]+(.jpg|.png|.gif)");
	
	@Override
	public boolean isAnImage(String name, InputStream is) {
		return ENDS_WITH_IMG_EXTENSION.matcher(name).matches();
	}
}
