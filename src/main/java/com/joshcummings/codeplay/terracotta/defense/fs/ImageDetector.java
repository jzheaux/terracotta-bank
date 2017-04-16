package com.joshcummings.codeplay.terracotta.defense.fs;

import java.io.IOException;
import java.io.InputStream;

public interface ImageDetector {
	boolean isAnImage(String name, InputStream is) throws IOException;
}
