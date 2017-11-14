package com.caf.model;

public class ChartStockDto {

	private int stockId;
	private String ticker;
	private String endDate;
	private String portfolioName;
	private int portfolioId;
	private double yield;
	private double percentageChange;
	private double absoluteChange;
	private double initialInvestment;
	private double marketValue;
	private String exchangeName;
	
	public ChartStockDto() {
		
	}
	public ChartStockDto(int stockId, String ticker, String endDate, String portfolioName, int portfolioId,
			double yield, double percentageChange, double absoluteChange, double initialInvestment,
			double marketValue) {
		super();
		this.stockId = stockId;
		this.ticker = ticker;
		this.endDate = endDate;
		this.portfolioName = portfolioName;
		this.portfolioId = portfolioId;
		this.yield = yield;
		this.percentageChange = percentageChange;
		this.absoluteChange = absoluteChange;
		this.initialInvestment = initialInvestment;
		this.marketValue = marketValue;
	}
	public ChartStockDto(int stockId, String ticker, String endDate, String portfolioName, int portfolioId,
			double yield, double percentageChange, double absoluteChange, double initialInvestment,
			double marketValue, String exchangeName) {
		super();
		this.stockId = stockId;
		this.ticker = ticker;
		this.endDate = endDate;
		this.portfolioName = portfolioName;
		this.portfolioId = portfolioId;
		this.yield = yield;
		this.percentageChange = percentageChange;
		this.absoluteChange = absoluteChange;
		this.initialInvestment = initialInvestment;
		this.marketValue = marketValue;
		this.exchangeName = exchangeName;
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
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	public int getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public double getYield() {
		return yield;
	}
	public void setYield(double yield) {
		this.yield = yield;
	}
	public double getPercentageChange() {
		return percentageChange;
	}
	public void setPercentageChange(double percentageChange) {
		this.percentageChange = percentageChange;
	}
	public double getAbsoluteChange() {
		return absoluteChange;
	}
	public void setAbsoluteChange(double absoluteChange) {
		this.absoluteChange = absoluteChange;
	}
	public double getInitialInvestment() {
		return initialInvestment;
	}
	public void setInitialInvestment(double initialInvestment) {
		this.initialInvestment = initialInvestment;
	}
	public double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
}
