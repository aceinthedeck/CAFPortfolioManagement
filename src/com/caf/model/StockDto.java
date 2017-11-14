package com.caf.model;

public class StockDto {
	private int stockId; //Auto generated in DB
	private String ticker;
	private int exchangeId; //FK to exchange table
	public StockDto(int stockId, String ticker, int exchangeId) {
		super();
		this.stockId = stockId;
		this.ticker = ticker;
		this.exchangeId = exchangeId;
	}
	public StockDto(String ticker, int exchangeId) {
		super();
		this.ticker = ticker;
		this.exchangeId = exchangeId;
	}
	public StockDto() {
		
	}
	
	public int getStockId() {
		return stockId;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public int getExchangeId() {
		return exchangeId;
	}
	public void setExchangeId(int exchangeId) {
		this.exchangeId = exchangeId;
	}
	
}
