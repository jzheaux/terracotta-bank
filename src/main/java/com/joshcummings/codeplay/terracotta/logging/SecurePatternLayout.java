package com.joshcummings.codeplay.terracotta.logging;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SecurePatternLayout extends PatternLayout {
	public SecurePatternLayout() {
		defaultConverterMap.put("guid", GuidConverter.class.getName());
		defaultConverterMap.put("msg", SecureMessageConverter.class.getName());
	}
	
	public static class SecureMessageConverter extends ClassicConverter {

		@Override
		public String convert(ILoggingEvent event) {
			return event.getFormattedMessage()
					.replaceAll("\r", "_")
					.replaceAll("\n", "_");
		}
	}
	
	public static class GuidConverter extends ClassicConverter {
		private TimeBasedGenerator gen = Generators.timeBasedGenerator();
		
		@Override
		public String convert(ILoggingEvent event) {
			return gen.generate().toString();
		}
	}
}
