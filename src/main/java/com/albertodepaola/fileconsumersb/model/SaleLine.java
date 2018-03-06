package com.albertodepaola.fileconsumersb.model;

public class SaleLine extends LogLine {

	private Long sale_id;
	private String saleId;
	private String saleItems;
	private String sellerName;

	public SaleLine() {
		super();
	}

	public SaleLine(String type, String saleId, String saleItems, String sellerName) {
		super(type);
		this.saleId = saleId;
		this.saleItems = saleItems;
		this.sellerName = sellerName;
	}

	public Long getSale_id() {
		return sale_id;
	}

	public void setSale_id(Long sale_id) {
		this.sale_id = sale_id;
	}

	public String getSaleId() {
		return saleId;
	}

	public void setSaleId(String saleId) {
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
