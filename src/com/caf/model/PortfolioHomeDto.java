package com.caf.model;

public class PortfolioHomeDto {
    
    private int portfolioId;
	private String portfolioName;
	private double percentageChange;
	private double absoluteValue;
	private double currentValue;
	private double initialInvestment;
	private double yield;
	
	public int getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	public double getPercentageChange() {
		return percentageChange;
	}
	public void setPercentageChange(double percentageChange) {
		this.percentageChange = percentageChange;
	}
	public double getAbsoluteValue() {
		return absoluteValue;
	}
	public void setAbsoluteValue(double absoluteValue) {
		this.absoluteValue = absoluteValue;
	}
	public double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}
	public double getInitialInvestment() {
		return initialInvestment;
	}
	public void setInitialInvestment(double initialInvestment) {
		this.initialInvestment = initialInvestment;
	}
	public double getYield() {
		return yield;
	}
	public void setYield(double yield) {
		this.yield = yield;
	}
	public PortfolioHomeDto(int portfolioId, String portfolioName, double percentageChange, double absoluteValue,
			double currentValue, double initialInvestment, double yield) {
		super();
		this.portfolioId = portfolioId;
		this.portfolioName = portfolioName;
		this.percentageChange = percentageChange;
		this.absoluteValue = absoluteValue;
		this.currentValue = currentValue;
		this.initialInvestment = initialInvestment;
		this.yield = yield;
	}
}
