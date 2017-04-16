package com.joshcummings.codeplay.terracotta.testng;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CrlfCheatSheet implements Iterable<String> {
	List<String> exploits = Arrays.asList(
		"dummy" + (char)22029 + (char)22026 + "X-Evil-Header:+%s+is+vulnerable+to+CRLF+injection",
		"dummy%%E5%%98%%8D%%E5%%98%%8AX-Evil-Header:+%s+is+vulnerable+to+CRLF+injection",
		"dummy%%0D%%0AX-Evil-Header:+%s+is+vulnerable+to+CRLF+injection"
	);

	@Override
	public Iterator<String> iterator() {
		return exploits.iterator();
	}
}
