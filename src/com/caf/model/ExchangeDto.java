package com.caf.model;

public class ExchangeDto {
	private int exchangeId;
	private String exchangeName;
	
	public ExchangeDto() {
		
	}
	public ExchangeDto(int exchangeId, String exchangeName) {
		super();
		this.exchangeId = exchangeId;
		this.exchangeName = exchangeName;
	}
	public int getExchangeId() {
		return exchangeId;
	}
	public void setExchangeId(int exchangeId) {
		this.exchangeId = exchangeId;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
}
