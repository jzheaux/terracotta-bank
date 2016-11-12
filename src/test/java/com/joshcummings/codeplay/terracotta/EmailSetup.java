package com.joshcummings.codeplay.terracotta;

import java.util.regex.Pattern;

public class EmailSetup {
	public static void main(String[] args) {
		Pattern lettersNumbers = Pattern.compile("[A-Za-z0-9]+");
		String str = "\u0041";
		System.out.println(lettersNumbers.matcher(str).matches());
	}
}
