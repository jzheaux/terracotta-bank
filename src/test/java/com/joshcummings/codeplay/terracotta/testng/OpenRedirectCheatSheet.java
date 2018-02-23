package com.joshcummings.codeplay.terracotta.testng;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class OpenRedirectCheatSheet implements Iterable<String> {
	List<String> exploits = Arrays.asList(
		"dummy" + (char)22029 + (char)22026 + "X-Evil-Header: %s is vulnerable to CRLF injection",
		"dummy" + (char)13 + (char)10 + "X-Evil-Header: %s is vulnerable to CRLF injection"
	);

	@Override
	public Iterator<String> iterator() {
		return exploits.iterator();
	}
}
