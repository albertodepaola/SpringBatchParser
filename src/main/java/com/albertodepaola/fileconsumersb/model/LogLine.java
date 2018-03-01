package com.albertodepaola.fileconsumersb.model;

public class LogLine {

	private String type;
	private String sellerName;
	private String cpf;
	private String sallary;
	
	private String cnpj;
	private String clientName;
	private String businessType;
	
	private Long saleId;
	private String saleItems;
	private String saleSellerName;
	

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

	public String getSaleSellerName() {
		return saleSellerName;
	}

	public void setSaleSellerName(String saleSellerName) {
		this.saleSellerName = saleSellerName;
	}

	@Override
	public String toString() {
		return "Type: " + getType()  + " sellerName: " + getSellerName() + " clientName: " + getClientName() + " saleItems: " + getSaleItems();
	}

}
