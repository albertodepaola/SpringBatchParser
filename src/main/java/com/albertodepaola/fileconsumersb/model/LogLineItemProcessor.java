package com.albertodepaola.fileconsumersb.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class LogLineItemProcessor implements ItemProcessor<LogLine, LogLine> {
	private static final Logger log = LoggerFactory.getLogger(LogLineItemProcessor.class);

	@Override
	public LogLine process(final LogLine logLine) throws Exception {
		

		if("003".equals(logLine.getType())) {
			MathContext mc = MathContext.DECIMAL64;
			
			String[] salesList = logLine.getSaleItems().replaceAll("(\\[|\\])", "").split(",");
			
			BigDecimal saleAmount = BigDecimal.ZERO;
			List<SaleDetail> saleDetails = new ArrayList<>();
			for (String string : salesList) {
				String[] saleDetail = string.split("-");
				
				Long id = Long.valueOf(saleDetail[0]);
				// TODO colocar em parametros
				
				BigDecimal quantity = new BigDecimal(saleDetail[1], mc);
				BigDecimal price = new BigDecimal(saleDetail[2], mc);
				
				SaleDetail sd = new SaleDetail(id, quantity, price);
				saleDetails.add(sd);
				
				saleAmount = saleAmount.add(quantity.multiply(price));
				
			}
			saleAmount = saleAmount.setScale(4, mc.getRoundingMode());
			
			logLine.setSaleAmount(saleAmount);
			logLine.setSaleDetails(saleDetails);

			log.info("Processed (" + saleDetails.size() + ") into (" + logLine.getSaleId() + " by " + logLine.getSellerName() + ")");
			log.info("Processed: " + logLine.getSellerName() + " saleSellerName: " + logLine.getSaleSellerName() +  " clazz: " + logLine.getClass());
		} else if("002".equals(logLine.getType())) {
			log.info("Processed: " + logLine.getClientName());
		} 

		return logLine;
	}
}
