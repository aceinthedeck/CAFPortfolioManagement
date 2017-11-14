package com.caf.model;

import java.util.List;

public class PortfolioDto {
	private String portfolioName;
	private List<String> stockNames;
	
	public PortfolioDto() {
		
	}
	public PortfolioDto(String portfolioName, List<String> stockNames) {
		super();
		this.portfolioName = portfolioName;
		this.stockNames = stockNames;
	}
	
	public String getPortfolioName() {
		return portfolioName;
	}
	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}
	public List<String> getStockNames() {
		return stockNames;
	}
	public void setStockNames(List<String> stockNames) {
		this.stockNames = stockNames;
	}
}
