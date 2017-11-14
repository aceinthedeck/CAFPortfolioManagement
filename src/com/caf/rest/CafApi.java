package com.caf.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.caf.data.PortfolioDao;
import com.caf.model.ChartPortfolioDto;
import com.caf.model.ChartStockDto;
import com.caf.model.ExchangeDto;
import com.caf.model.PortfolioDto;
import com.caf.model.PortfolioHomeDto;
import com.caf.model.StockSearchDto;
import com.caf.model.TradeDto;

@Path("/caf")
public class CafApi {

	// Test Rest
	@GET
	@Produces("text/plain")
	@Path("status")
	public String getStatus() {
		return "Hello from REST!";
	}

	// Rest for DB
	@GET
	@Produces("text/plain")
	@Path("file/{filename}/{exchangeId}")
	public boolean getInsertData(@PathParam("filename") String filename, @PathParam("exchangeId") int exchangeId) {
		return new PortfolioDao().importData(filename, exchangeId);
	}

	@GET
	@Produces("text/plain")
	@Path("stockexists/{ticker}")
	public boolean checkExisitingStock(@PathParam("ticker") String ticker) {
		return new PortfolioDao().checkExisitingStock(ticker);
	}

	@GET
	@Produces("text/plain")
	@Path("addexchange/{exchangeName}")
	public boolean addExchange(@PathParam("exchangeName") String exchangeName) {
		return new PortfolioDao().addExchange(exchangeName);
	}

	@GET
	@Produces("text/plain")
	@Path("deleteexchange/{exchangeName}")
	public boolean deleteExchange(@PathParam("exchangeName") String exchangeName) {
		return new PortfolioDao().deleteExchange(exchangeName);
	}

	// Get all exchanges - public List<String> getAllExchanges()
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("allexchanges")
	public List<ExchangeDto> getAllExchanges() {
		return new PortfolioDao().getAllExchanges();
	}

	// Rest for Portfolios
	@GET
	@Produces("text/plain")
	@Path("addportfolio/{portfolioName}")
	public int addPortfolio(@PathParam("portfolioName") String portfolioName) {
		return new PortfolioDao().addPortfolio(portfolioName);
	}

	@GET
	@Produces("text/plain")
	@Path("deleteportfolio/{portfolioId}")
	public boolean deletePortfolio(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().deletePortfolio(portfolioId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("allportfolios")
	public List<PortfolioDto> getAll() {
		return new PortfolioDao().getAllPortfolios();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getportfolio/{portfolioId}")
	public PortfolioDto getPortfolio(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().getPortfolioById(portfolioId);
	}

	@GET
	@Produces("text/plain")
	@Path("portfolioexists/{portfolioName}")
	public int getPortfolio(@PathParam("portfolioName") String portfolioName) {
		// Portfolio Id is returned
		return new PortfolioDao().checkPortfolioExists(portfolioName);
	}

	// Rest for Stocks

	// Add stock to portfolio - main portfolioTxn table
	@GET
	@Produces("text/plain")
	@Path("addstock/{portfolioId}/{stockId}")
	public boolean addStock(@PathParam("portfolioId") int portfolioId, @PathParam("stockId") int stockId) {
		return new PortfolioDao().addStock(portfolioId, stockId);
	}

	// Delete stock to portfolio - main affect portfolioTxn table
	@GET
	@Produces("text/plain")
	@Path("deletestock/{portfolioId}")
	public boolean deleteStock(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().deleteStock(portfolioId);
	}

	// Get All trades for a portfolio
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getalltrades/{portfolioId}")
	public List<TradeDto> getAllTrades(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().getAllTrades(portfolioId);
	}

	// public int getStockIdByTicker (String ticker)
	@GET
	@Produces("text/plain")
	@Path("stockidbyticker/{ticker}")
	public int getStockIdByTicker(@PathParam("ticker") String ticker) {
		return new PortfolioDao().getStockIdByTicker(ticker);
	}

	// public TradeDto getStockById(int stockId,String date);//list of all trades
	// for a particular date for that stockId
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tradestockbyid/{stockId}/{date}")
	public TradeDto getStockById(@PathParam("stockId") int stockId, @PathParam("date") String date) {
		return new PortfolioDao().getStockById(stockId, date);
	}

	// public List<TradeDto> getStockById(int stockId);
	// Returns all trades for that stock
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stockbyid/{stockId}")
	public List<TradeDto> getStockById(@PathParam("stockId") int stockId) {
		return new PortfolioDao().getStockById(stockId);
	}

	// public List<TradeDto> getAllStockTradeByExchange(int exchangeId, String
	// date);
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tradesbyexchange/{exchangeid}/{date}")
	public List<TradeDto> getAllTradeByEx(@PathParam("exchangeid") int exchangeId, @PathParam("date") String date) {
		return new PortfolioDao().getAllStockTradeByExchange(exchangeId, date);
	}

	// public List<TradeDto> getAllStockByExchange(int exchangeId)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stocksbyexchange/{exchangeid}/{ticker}")
	public List<TradeDto> getAllStockByExchange(@PathParam("exchangeid") int exchangeId,
			@PathParam("ticker") String ticker) {
		return new PortfolioDao().getAllStockByExchange(exchangeId, ticker);
	}

	// public List<TradeDto> getAllStockTradeByDate();
	// Will be used for calculations
	// Open price is from very first date
	// All other prices are from last date
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("alltradesbydate")
	public List<TradeDto> getAllStockTradeByDate() {
		return new PortfolioDao().getAllStockTradeByDate();
	}

	// for search
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("searchStock/{ticker}")
	public List<StockSearchDto> searchTicker(@PathParam("ticker") String ticker) {
		return new PortfolioDao().searchStock(ticker);
	}

	// Get DAta for dynamic home page
	// public List<PortfolioHomeDto> getAllHomeData()
	// for search
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("homepage")
	public List<PortfolioHomeDto> getAllHomeData() {
		return new PortfolioDao().getAllHomeData();
	}

	// Get data for the compare portfolio chart
	// public List<ChartPortfolioDto> getAllPortfolioChart()
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("portfoliocompare")
	public List<ChartPortfolioDto> getAllPortfolioChart() {
		return new PortfolioDao().getAllPortfolioChart();
	}

	// Get data for the compare stocks in a portfolio chart
	// public List<ChartPortfolioDto> getAllPortfolioChart()
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stockcompare/{portfolioId}")
	public List<ChartStockDto> getAllStockChart(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().getAllStockChart(portfolioId);
	}

	// public List<ChartPortfolioDto> getAllPortfolioPie()
	// GEt data for portfolio stock Pie Chart
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stockcomparepie/{portfolioId}")
	public List<ChartStockDto> getAllPortfolioPie(@PathParam("portfolioId") int portfolioId) {
		return new PortfolioDao().getAllPortfolioPie(portfolioId);
	}
}
