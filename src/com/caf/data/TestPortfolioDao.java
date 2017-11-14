package com.caf.data;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.caf.model.ChartPortfolioDto;
import com.caf.model.ExchangeDto;
import com.caf.model.PortfolioDto;
import com.caf.model.PortfolioHomeDto;
import com.caf.model.StockDto;
import com.caf.model.StockSearchDto;
import com.caf.model.TradeDto;


	
public class TestPortfolioDao {


	
	@Test
	public void importData(){//Reads file
	String NY22 = "NYSE_20170922.txt";
	String NAS22 = "NASDAQ_20170922.txt";
	String notFile="CK123file";
	int NYSE=1;
	int NASDAQ= 2;
//	int notExch=3;
	PortfolioDao dao =new PortfolioDao();
	//test working
	assertTrue(dao.importData(NY22,NYSE));
	assertTrue(dao.importData(NAS22, NASDAQ));
	//test file error and exch which doesn't exist 
//	assertFalse(dao.importData(NY22, notExch));    //ERROR: allows any number exchanegid to be entered
	assertFalse(dao.importData(notFile, NYSE));
	}
	
	
	//	public int insertStock(List<StockDto> stockDto);//Stock tbl, Adding stock tickers and exchanges to stock tbl from importData()

	
	//	public boolean checkExisitingStock(String ticker);//Check is a stock already exists in the stock tbl
	@Test 
	public void checkExistingStock(){
		PortfolioDao dao = new PortfolioDao();
		
	}
	
	//	public boolean addExchange(String exchangeName);//Exchange tbl, Adding exchanges to tbl
	@Test 
	public void addExchange(){
		PortfolioDao dao = new PortfolioDao();
		String exchangeTest="LIBOR";
		String exchangeName="NASDAQ";
		//unique constraints 
		//false as already exists
		assertTrue(dao.addExchange(exchangeTest));
		assertFalse(dao.addExchange(exchangeName));
	}
	
	
	//	public boolean deleteExchange(String exchangeName);//Exchange tbl, delete exchanges
	@Test 
	public void deleteExchange(){
		PortfolioDao dao = new PortfolioDao();
		String deleteEx ="LIBOR";
		assertTrue(dao.deleteExchange(deleteEx));
		
	}
	

//	public boolean addStock(int portfolioId, int stockId);//INSERT to portfolioTxn tbl
	@Test
	public void addStock(){
		PortfolioDao dao = new PortfolioDao();
		
		assertTrue(dao.addStock(1,301));
		assertTrue(dao.addStock(1,1));
		assertFalse(dao.addStock(3,900000));
		assertFalse(dao.addStock(4,900000));
		assertFalse(dao.addStock(40000,900000));
	}

	
//	public boolean deleteStock(int portfolioId);//DELETE to portfolioTxn tbl
//need to rename pId each time. Deletes transaction but not portfolioId & Name	
	@Test 
	public void deleteStock(){  						//works
		PortfolioDao dao = new PortfolioDao();
		int stockId=78;
		int pId=dao.addPortfolio("caoi"); 		
		dao.addStock(pId, stockId);
		assertTrue(dao.deleteStock(pId));
		assertFalse(dao.deleteStock(pId));
	}
	
		
//	public List<TradeDto> getAllTrades(int portfolioId); //all trades by portfolio everyday -for graph data
//	public List<TradeDto> getAllStockTradeByDate();//In add stock list, display all tickers and % change ONLY
//	public List<TradeDto> getAllStockTradeByExchange(int exchangeId, String date);//Get all stocks for an exchange for a particular date(last day)
//	public List<TradeDto> getStockById(int stockId); //list of all trades per day for that stockId


//	public TradeDto getStockById(int stockId,String date);//list of all trades for a particular date for that stockId
//@Test 
//public void getStockById(){
//	PortfolioDao dao = new PortfolioDao();
//	TradeDto trade =;
//	assertEquals(#, trade.getStockById(3,"20170920"));
//}

	
//	public int getStockIdByTicker (String ticker); //Get a stock's Id by ticker
	@Test 
	public void getStockIdByTicker(){
	PortfolioDao dao = new PortfolioDao();
	
	assertEquals(1, dao.getStockIdByTicker("AaaP"));
	assertEquals(378, dao.getStockIdByTicker("BoLD"));
	assertEquals(1445, dao.getStockIdByTicker("ions"));
	assertNotEquals(7, dao.getStockIdByTicker("test"));
	}


//	public List<StockSearchDto> searchStock(String ticker);//searches the stock by ticker
//	public List<ExchangeDto> getAllExchanges();//Get all exchanges
//	public List<TradeDto> getAllStockByExchange(int exchangeId, String ticker);//Get all exchanges
//	
//	
//	public int checkPortfolioExists(String portfolioName);
	@Test 
	public void checkPortfolioExists(){
		PortfolioDao dao = new PortfolioDao();
		
		assertEquals(3, dao.checkPortfolioExists("TesT3"));
		assertEquals(1, dao.checkPortfolioExists("TEST1"));
		assertNotEquals(1, dao.checkPortfolioExists("Teessst1"));
	}
	
	
//	public int addPortfolio(String portfolioName);//INSERT to Portfolio tbl
	@Test 
	public void addPortfolio(){
		PortfolioDao dao = new PortfolioDao();
		int id= 18;
		++id; 
		//same name twice; checked in DB and adding  
			assertEquals(id, dao.addPortfolio("newCK"));
			//assertNotEquals(1, dao.addPortfolio("TEST1"));
		
	}
	
	

//	public boolean deletePortfolio(int portfolioId);//DELETE to Portfolio tbl
	@Test 
	public void deletePortfolio(){
		PortfolioDao dao = new PortfolioDao();
		//int pId=dao.addPortfolio("caoi"); 		
		//dao.addStock(pId, 18);
		assertTrue(dao.deletePortfolio(16));
		assertTrue(dao.deletePortfolio(11));
	}
	
	
//	public List<PortfolioDto> getAllPortfolios();
//	public PortfolioDto getPortfolioById(int portfolioId);
//	
//	//Analysis
//	public List<PortfolioHomeDto> getAllHomeData(); //Get data for dynamic home page cards
//	public List<ChartPortfolioDto> getAllPortfolioChart();//Get data fro compare portfolio chart
//	
}