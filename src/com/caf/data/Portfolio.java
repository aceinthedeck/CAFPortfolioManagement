package com.caf.data;
import java.util.List;

import com.caf.model.ChartPortfolioDto;
import com.caf.model.ChartStockDto;
import com.caf.model.ExchangeDto;
import com.caf.model.PortfolioDto;
import com.caf.model.PortfolioHomeDto;
import com.caf.model.StockDto;
import com.caf.model.StockSearchDto;
import com.caf.model.TradeDto;

public interface Portfolio {
	//DB
	public boolean importData(String fileName, int exchangeId);//Reads file
	public boolean insertTradeData(List<TradeDto> tradeDto);//TradeData tbl
	public int insertStock(List<StockDto> stockDto);//Stock tbl, Adding stock tickers and exchanges to stock tbl from importData()
	public boolean checkExisitingStock(String ticker);//Check is a stock already exists in the stock tbl
	public boolean addExchange(String exchangeName);//Exchange tbl, Adding exchanges to tbl
	public boolean deleteExchange(String exchangeName);//Exchange tbl, delete exchanges
	
	//stocks
	public boolean addStock(int portfolioId, int stockId);//INSERT to portfolioTxn tbl
	public boolean deleteStock(int portfolioId);//DELETE to portfolioTxn tbl
	public List<TradeDto> getAllTrades(int portfolioId); //all trades by portfolio everyday -for graph data 
	public List<TradeDto> getAllStockTradeByDate();//In add stock list, display all tickers and % change ONLY
	public List<TradeDto> getAllStockTradeByExchange(int exchangeId, String date);//Get all stocks for an exchange for a particular date(last day)
	public List<TradeDto> getStockById(int stockId); //list of all trades per day for that stockId
	public TradeDto getStockById(int stockId,String date);//list of all trades for a particular date for that stockId
	public int getStockIdByTicker (String ticker); //Get a stock's Id by ticker
	public List<StockSearchDto> searchStock(String ticker);//searches the stock by ticker
	public List<ExchangeDto> getAllExchanges();//Get all exchanges
	public List<TradeDto> getAllStockByExchange(int exchangeId, String ticker);//Get all exchanges
	
	//portfolio - All in rest
	public int checkPortfolioExists(String portfolioName);
	public int addPortfolio(String portfolioName);//INSERT to Portfolio tbl
	public boolean deletePortfolio(int portfolioId);//DELETE to Portfolio tbl
	public List<PortfolioDto> getAllPortfolios();
	public PortfolioDto getPortfolioById(int portfolioId);
	
	//Analysis
	public List<PortfolioHomeDto> getAllHomeData(); //Get data for dynamic home page cards
	public List<ChartPortfolioDto> getAllPortfolioChart();//Get data for compare portfolio chart
	public List<ChartStockDto> getAllPortfolioPie(int portfolioId);//Get data for compare stocks of portfolio in piechart
	public List<ChartStockDto> getAllStockChart(int portfolioId); //Get data for compare stocks within a portfolio
}
