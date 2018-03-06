package com.albertodepaola.fileconsumersb.model;

import java.math.BigDecimal;

public class SaleDetail {
	
	private Long itemId;
	private BigDecimal quantity;
	private BigDecimal price;

	public SaleDetail() {
		
	}

	public SaleDetail(Long itemId, BigDecimal quantity, BigDecimal price) {
		super();
		this.itemId = itemId;
		this.quantity = quantity;
		this.price = price;
	}
	
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}