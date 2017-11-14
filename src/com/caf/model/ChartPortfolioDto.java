package com.caf.model;

public class ChartPortfolioDto {
	
	private int portfolioId;
	private String portfolioName;
	private String date;
	private double percentageChange;
	private double absoluteChange;
	private double initialInvestment;
	private double marketValue;
	private double yield;
	
	public ChartPortfolioDto(int portfolioId, String portfolioName, String date, double percentageChange, double absoluteChange,
			double initialInvestment, double marketValue, double yield) {
		super();
		this.portfolioId = portfolioId;
		this.setPortfolioName(portfolioName);
		this.date = date;
		this.percentageChange = percentageChange;
		this.absoluteChange = absoluteChange;
		this.initialInvestment = initialInvestment;
		this.marketValue = marketValue;
		this.setYield(yield);
	}
	public int getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public double getYield() {
		return yield;
	}
	public void setYield(double yield) {
		this.yield = yield;
	}
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	
	
}
