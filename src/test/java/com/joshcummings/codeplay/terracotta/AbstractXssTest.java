package com.joshcummings.codeplay.terracotta;

public class AbstractXssTest extends AbstractEmbeddedTomcatSeleniumTest {
	protected static final String ALERT = "alert(\"%s is vulnerable on \" + document.domain);";
	
	//*
	protected static final String[] templates = {
			"<script>" + ALERT + "</script>",
	};
	//*/
	
	/*
	protected String[] templates = {
			"<img src=\"http://www.w3schools.com/css/trolltunga.jpg\" onload=\"" + escapeQuotes(ALERT) +"\"/>",
			"<script type=\"text/javascript\">" + ALERT + "</script>",
			"<scr\0ipt>" + ALERT + "</script>", // null byte attack
			"<scr<script>1234</script>ipt>" + ALERT + "</scr<script>1234</script>ipt>", // embedded attack
			"%%253Cscript%%253E" + ALERT + "%%253C/script%%253E", // double-encoding attack
			"+Adw-script+AD4-" + ALERT + "+Adw-/script+AD4-", //UTF-7 attack
			
	};//*/
	
	protected String escapeQuotes(String template) {
		return template.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
