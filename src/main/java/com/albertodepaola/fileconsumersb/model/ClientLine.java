package com.albertodepaola.fileconsumersb.model;

public class ClientLine extends LogLine {

	private String cnpj;
	private String name;
	private String businessType;
	
	public ClientLine() {
		super();
	}

	public ClientLine(String type, String cnpj, String name, String businessType) {
		super(type);
		this.cnpj = cnpj;
		this.name = name;
		this.businessType = businessType;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

}
