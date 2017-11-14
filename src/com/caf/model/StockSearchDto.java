package com.caf.model;

public class StockSearchDto {
	
	private int stockID;
	private String ticker;
	private double open;
	public int getStockID() {
		return stockID;
	}
	public void setStockID(int stockID) {
		this.stockID = stockID;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	
	public StockSearchDto(){
		
	}
	public StockSearchDto(int stockID, String ticker, double open) {
		super();
		this.stockID = stockID;
		this.ticker = ticker;
		this.open = open;
	}
	
	

}