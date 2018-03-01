package com.albertodepaola.fileconsumersb.model;

public class SalesLine extends LogLine {

	private Long saleId;
	private String saleItems;
	private String sellerName;

	public SalesLine() {
		super();
	}

	public SalesLine(String type, Long saleId, String saleItems, String sellerName) {
		super(type);
		this.saleId = saleId;
		this.saleItems = saleItems;
		this.sellerName = sellerName;
	}

	public Long getSaleId() {
		return saleId;
	}

	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}

	public String getSaleItems() {
		return saleItems;
	}

	public void setSaleItems(String saleItems) {
		this.saleItems = saleItems;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

}
