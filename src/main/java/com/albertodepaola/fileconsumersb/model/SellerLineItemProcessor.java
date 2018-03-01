package com.albertodepaola.fileconsumersb.model;

import org.springframework.batch.item.ItemProcessor;

public class SellerLineItemProcessor implements ItemProcessor<LogLine, SellerLine> {

	@Override
	public SellerLine process(LogLine logLine) throws Exception {
		final String type = logLine.getType();
		SellerLine sl = null;
		if("001".equals(type)) {
			sl = (SellerLine)logLine;
		}
		return sl;
	}

}
