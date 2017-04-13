package com.joshcummings.codeplay.terracotta;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CharsetTests {
	private static void printArray(byte[] b) {
		System.out.println(Arrays.toString(b));
	}
	
	private static void printArray(char[] b) {
		System.out.println(Arrays.toString(b));
	}
	
	public static void main(String[] args) {
		String s = "\r\n\0\0\0嘍嘊";
		printArray(s.getBytes(StandardCharsets.UTF_16));
		printArray(s.getBytes(StandardCharsets.UTF_8));
		printArray(s.getBytes(StandardCharsets.ISO_8859_1));
		char[] c = new char[s.length()];
		s.getChars(0, s.length(), c, 0);
		printArray(c);
		// and then cast each element of c to byte
		byte[] b = new byte[c.length];
		for ( int i = 0; i < c.length; i++ ) {
			b[i] = (byte)c[i];
		}
		printArray(b);
	}
}
