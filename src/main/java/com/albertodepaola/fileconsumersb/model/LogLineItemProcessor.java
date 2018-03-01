package com.albertodepaola.fileconsumersb.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class LogLineItemProcessor implements ItemProcessor<LogLine, LogLine> {
	private static final Logger log = LoggerFactory.getLogger(LogLineItemProcessor.class);

	@Override
	public LogLine process(final LogLine logLine) throws Exception {
		
		final String type = logLine.getType().toUpperCase();

		final LogLine transformedLogLine = new LogLine(type);

		log.info("Converting (" + logLine + ") into (" + transformedLogLine + ")");

		return transformedLogLine;
	}
}
