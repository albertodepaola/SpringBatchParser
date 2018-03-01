package com.albertodepaola.fileconsumersb.model;

public class SellerLine extends LogLine {

	private String name;
	private String cpf;
	private String sallary;
	
	public SellerLine() {
	}
	
	public SellerLine(String name, String cpf, String sallary) {
		super();
		this.name = name;
		this.cpf = cpf;
		this.sallary = sallary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
