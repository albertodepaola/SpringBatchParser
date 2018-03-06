package com.albertodepaola.fileconsumersb.model;

import java.math.BigDecimal;
import java.util.List;


public class LogLine {

	private String type;
	private String sellerName;
	private String cpf;
	private String sallary;
	
	private String cnpj;
	private String clientName;
	private String businessType;
	
	private String saleId;
	private String saleItems;
	private String saleSellerName;
	
	private BigDecimal saleAmount;
	private List<SaleDetail> saleDetails;

	public LogLine() {

	}

	public LogLine(String type) {
		this.setType(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSallary() {
		return sallary;
	}

	public void setSallary(String sallary) {
		this.sallary = sallary;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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

	public String getSaleSellerName() {
		return saleSellerName;
	}

	public void setSaleSellerName(String saleSellerName) {
		this.saleSellerName = saleSellerName;
	}

	public BigDecimal getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}

	public List<SaleDetail> getSaleDetails() {
		return saleDetails;
	}

	public void setSaleDetails(List<SaleDetail> saleDetails) {
		this.saleDetails = saleDetails;
	}

	@Override
	public String toString() {
		return "Type: " + getType()  + " sellerName: " + getSellerName() + " clientName: " + getClientName() + " saleItems: " + getSaleItems();
	}

}
