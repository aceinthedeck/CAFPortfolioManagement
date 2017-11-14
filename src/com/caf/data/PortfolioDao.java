package com.caf.data;

import java.io.BufferedReader;
import java.util.logging.Logger;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.caf.model.ChartPortfolioDto;
import com.caf.model.ChartStockDto;
import com.caf.model.ExchangeDto;
import com.caf.model.PortfolioDto;
import com.caf.model.PortfolioHomeDto;
import com.caf.model.StockDto;
import com.caf.model.StockSearchDto;
import com.caf.model.TradeDto;

public class PortfolioDao implements Portfolio {
	private final static Logger LOGGER = Logger.getLogger(PortfolioDao.class.getName());
	// done
	@Override
	public boolean importData(String fileName, int exchangeId) {
		// TODO Auto-generated method stub
		boolean inserted = false;
		fileName = "C:\\JavaExamples\\Temp\\" + fileName;
		List<StockDto> stockDtoList = new ArrayList<StockDto>();
		List<TradeDto> tradeDtoList = new ArrayList<TradeDto>();
		String line;

		try (BufferedReader in = new BufferedReader(new FileReader(fileName));) {
			// To read and exclude the header in the file
			in.readLine();
			while ((line = in.readLine()) != null) {

				// Split the line
				String[] columns = line.split(",");

				StockDto stockDto = new StockDto(columns[0], exchangeId);
				stockDtoList.add(stockDto);

				TradeDto tradeDto = new TradeDto(columns[0], columns[1], Double.parseDouble(columns[2]),
						Double.parseDouble(columns[3]), Double.parseDouble(columns[4]), Double.parseDouble(columns[5]),
						Integer.parseInt(columns[6]), exchangeId, 0); // Will overwrite id 0 later
				tradeDtoList.add(tradeDto);
			}

			insertStock(stockDtoList);
			insertTradeData(tradeDtoList);

			inserted = true;

		} // Resources declared in the try are closed automatically
		catch (IOException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
			inserted = false;
		}

		return inserted;
	}

	// done
	@Override
	public boolean insertTradeData(List<TradeDto> tradeDto) {
		// TODO Auto-generated method stub
		boolean inserted = false;
		String line;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			Statement stClear = cn.createStatement();
			int stockId = 0;
			// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
			PreparedStatement st = cn.prepareStatement(
					"INSERT INTO tradeData (stockId, tradeDate, open, high, low, close, volume) VALUES (?,?,?,?,?,?,?)");
			for (int i = 0; i < tradeDto.size(); i++) {

				// int stockId=getStockIdByTicker(tradeDto.get(i).getTicker());
				PreparedStatement ps = cn.prepareStatement("SELECT stockId FROM stock WHERE ticker = ? ");
				// Outlines what should be passed in in place of the ?
				ps.setString(1, tradeDto.get(i).getTicker());
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					stockId = rs.getInt("stockId");
				}
				rs.close();
				ps.close();
				// First ? in sql will be set as values from column[x] from String[] columns
				// array
				st.setInt(1, stockId); // stock id
				st.setString(2, tradeDto.get(i).getDate()); // datestring
				st.setDouble(3, tradeDto.get(i).getOpen()); // open
				st.setDouble(4, tradeDto.get(i).getHigh()); // high
				st.setDouble(5, tradeDto.get(i).getLow()); // low
				st.setDouble(6, tradeDto.get(i).getClose()); // close
				st.setDouble(7, tradeDto.get(i).getVolume()); // volume

				st.executeUpdate();
				cn.commit();

			}
			inserted = true;

		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			inserted = false;
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}
		return inserted;

	}

	// done - delete?
	@Override
	public boolean checkExisitingStock(String ticker) {
		// Connect to Oracle, run SELECT, populate List
		boolean exists = false;
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn
					.prepareStatement("SELECT COUNT(*) AS totalStocks FROM stock WHERE UPPER(ticker) = UPPER(?)");
			// Outlines what should be passed in in place of the ?
			ps.setString(1, ticker);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("totalStocks") == 0) {
					exists = false;
				} else {
					exists = true;
				}
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return exists;
	}

	// done
	@Override
	public int insertStock(List<StockDto> stockDto) {
		// TODO Auto-generated method stub
		int stockId = 0;
		String line;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			// Statement stClear = cn.createStatement();
			// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
			PreparedStatement st = cn.prepareStatement("MERGE " + "    INTO stock s "
					+ "    USING (SELECT  ? AS TICKER FROM SYS.DUAL) e " + "    ON (e.ticker = s.ticker) "
					+ "WHEN NOT MATCHED " + "THEN " + "INSERT (s.ticker, s.exchangeId) " + "VALUES (?, ?) ");

			// First ? in sql will be set as values from column[x] from String[] columns
			// array
			for (int i = 0; i < stockDto.size(); i++) {

				st.setString(1, stockDto.get(i).getTicker());// ticker
				st.setString(2, stockDto.get(i).getTicker());// ticker
				st.setInt(3, stockDto.get(i).getExchangeId());

				st.executeUpdate();
				cn.commit();
			}
		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}

		return stockId;

	}

	// done
	@Override
	public int getStockIdByTicker(String ticker) {
		int stockId = 0;
		Connection cn = null;
		ResultSet rs = null;

		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try {
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement("SELECT stockId FROM stock WHERE UPPER(ticker) = UPPER(?) ");
			// Outlines what should be passed in in place of the ?
			ps.setString(1, ticker);
			rs = ps.executeQuery();

			if (rs.next()) {
				stockId = rs.getInt("stockId");
			}

			rs.close();
			cn.close();
		} // Resources declared in the try are closed automatically
		catch (	SQLException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return stockId;

	}

	// done
	@Override
	public boolean addExchange(String exchangeName) {
		boolean inserted = false;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {

			PreparedStatement st = cn.prepareStatement("INSERT INTO exchange (exchangeName) VALUES (?)");
			st.setString(1, exchangeName.toUpperCase());
			st.executeUpdate();

			cn.commit();
			st.close();

			inserted = true;

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			inserted = false;
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}
		return inserted;
	}

	// done
	@Override
	public boolean deleteExchange(String exchangeName) {
		boolean deleted = false;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {

			PreparedStatement st = cn.prepareStatement("DELETE FROM exchange WHERE UPPER(exchangeName) = UPPER(?)");
			st.setString(1, exchangeName.toUpperCase());
			st.executeUpdate();

			cn.commit();
			st.close();

			deleted = true;

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			deleted = false;
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}
		return deleted;
	}

	// done
	@Override
	public boolean addStock(int portfolioId, int stockId) {
		// TODO Auto-generated method stub
		boolean added = false;

		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			// Statement stClear = cn.createStatement();
			// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
			PreparedStatement st = cn.prepareStatement("INSERT INTO portfolioTxn (portfolioId, stockId) VALUES (?,?) ");

			st.setInt(1, portfolioId);
			st.setInt(2, stockId);

			if (st.executeUpdate() > 0) {

				cn.commit();

				st.close();
				added = true;
			}
		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			added = false;
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}

		return added;
	}

	// done
	@Override
	public boolean deleteStock(int portfolioId) {
		// TODO Auto-generated method stub
		boolean deleted = false;

		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			// Statement stClear = cn.createStatement();
			// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
			PreparedStatement st = cn.prepareStatement("DELETE FROM portfolioTxn WHERE portfolioId = ?");

			st.setInt(1, portfolioId);

			if (st.executeUpdate() > 0) {

				cn.commit();

				st.close();
				deleted = true;
			}

		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}

		return deleted;
	}

	// done
	@Override
	public List<TradeDto> getAllTrades(int portfolioId) {
		// Connect to Oracle, run SELECT, populate List
		List<TradeDto> trades = new ArrayList<TradeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"    SELECT s.ticker AS ticker, tradedate, open, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
							+ "    FROM stock s " + "    JOIN tradeData t " + "    ON s.stockId = t.stockId "
							+ "    JOIN portfolioTxn pt " + "    ON s.stockId = pt.stockId "
							+ "    WHERE pt.portfolioId = ?");
			ps.setInt(1, portfolioId);
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				trades.add(new TradeDto(rs.getString("ticker"), rs.getString("tradedate"), rs.getDouble("open"),
						rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
						rs.getInt("exchangeId"), rs.getInt("stockId")));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return trades;
	}

	// done
	@Override
	public List<TradeDto> getAllStockTradeByDate() {
		// Connect to Oracle, run SELECT, populate List
		List<TradeDto> trades = new ArrayList<TradeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"SELECT l.ticker AS ticker, l.tradedate AS currentDate, f.open AS open, l.high AS high, l.low AS low, "
							+ "    l.close AS close, l.volume AS volume, l.exchangeId AS exchangeId, l.stockId AS stockId "
							+ "    FROM ( SELECT s.ticker AS ticker, tradedate, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
							+ "        FROM stock s " + "        JOIN tradeData t "
							+ "        ON s.stockId = t.stockId " + "        WHERE tradedate = (SELECT MAX(tradedate) "
							+ "            FROM tradeData)) l " + "    JOIN (SELECT open, stockId "
							+ "        FROM tradeData t " + "        WHERE tradedate = (SELECT MIN(tradedate) "
							+ "            FROM tradeData)) f " + "    ON l.stockId = f.stockId ");

			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				trades.add(new TradeDto(rs.getString("ticker"), rs.getString("currentDate"), rs.getDouble("open"),
						rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
						rs.getInt("exchangeId"), rs.getInt("stockId")));
			}
			rs.close();
			ps.close();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return trades;
	}

	// done
	@Override
	public List<TradeDto> getAllStockTradeByExchange(int exchangeId, String date) {
		// date in format yyyymmdd
		List<TradeDto> trades = new ArrayList<TradeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"    SELECT s.ticker AS ticker, tradedate, open, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
							+ "    FROM stock s " + "    JOIN tradeData t " + "    ON s.stockId = t.stockId "
							+ "    WHERE s.exchangeId = ? AND tradedate = ? ");
			ps.setInt(1, exchangeId);
			ps.setString(2, date);
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				trades.add(new TradeDto(rs.getString("ticker"), rs.getString("tradedate"), rs.getDouble("open"),
						rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
						rs.getInt("exchangeId"), rs.getInt("stockId")));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return trades;
	}

	// done
	@Override
	public List<TradeDto> getStockById(int stockId) {
		List<TradeDto> trades = new ArrayList<TradeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"    SELECT s.ticker AS ticker, tradedate, open, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
							+ "    FROM stock s " + "    JOIN tradeData t " + "    ON s.stockId = t.stockId "
							+ "    WHERE s.stockId = ?");
			ps.setInt(1, stockId);
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				trades.add(new TradeDto(rs.getString("ticker"), rs.getString("tradedate"), rs.getDouble("open"),
						rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
						rs.getInt("exchangeId"), rs.getInt("stockId")));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return trades;
	}

	// done
	@Override
	public TradeDto getStockById(int stockId, String date) {
		// date in format yyyymmdd
		// Connect to Oracle, run SELECT, populate List
		TradeDto trade = null;
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"    SELECT s.ticker AS ticker, tradedate, open, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
							+ "    FROM stock s " + "    JOIN tradeData t " + "    ON s.stockId = t.stockId "
							+ "    WHERE s.stockId = ? AND tradedate = ?");
			ps.setInt(1, stockId);
			ps.setString(2, date);
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			if (rs.next()) {
				trade = new TradeDto(rs.getString("ticker"), rs.getString("tradedate"), rs.getDouble("open"),
						rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
						rs.getInt("exchangeId"), rs.getInt("stockId"));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return trade;
	}

	// done
	@Override
	public int checkPortfolioExists(String portfolioName) {
		int portfolioId = 0;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			PreparedStatement ps = cn
					.prepareStatement("SELECT portfolioId FROM portfolio WHERE UPPER(portfolioName) = UPPER(?) ");
			// Outlines what should be passed in in place of the ?
			ps.setString(1, portfolioName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				portfolioId = rs.getInt("portfolioId");
			}

			rs.close();
			ps.close();
		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}

		return portfolioId;
	}

	// done
	@Override
	public int addPortfolio(String portfolioName) {
		// TODO Auto-generated method stub
		int portfolioId = 0;
		if (checkPortfolioExists(portfolioName) == 0) {
			Driver d = new oracle.jdbc.driver.OracleDriver();
			try {
				DriverManager.registerDriver(d);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.warning("LOGGED: IO Error " + e.getMessage());
			}

			try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
					"Pa$$w0rd");) {
				// Statement stClear = cn.createStatement();
				// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
				PreparedStatement st = cn.prepareStatement("INSERT INTO portfolio (portfolioName) VALUES (?) ");

				st.setString(1, portfolioName.toUpperCase());// ticker

				st.executeUpdate();
				cn.commit();

				PreparedStatement ps = cn
						.prepareStatement("SELECT portfolioId FROM portfolio WHERE portfolioName = UPPER(?) ");
				// Outlines what should be passed in in place of the ?
				ps.setString(1, portfolioName.toUpperCase());
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					portfolioId = rs.getInt("portfolioId");
				}
				rs.close();
				ps.close();

			} // Resources declared in the try are closed automatically
			catch (SQLException ex) {
				System.err.println(ex.getMessage());
				LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
			}
		}
		return portfolioId;

	}

	// done
	@Override
	public boolean deletePortfolio(int portfolioId) {
		boolean deleted = false;
		Driver d = new oracle.jdbc.driver.OracleDriver();
		try {
			DriverManager.registerDriver(d);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.warning("LOGGED: IO Error " + e.getMessage());
		}

		try (Connection cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS",
				"Pa$$w0rd");) {
			// Statement stClear = cn.createStatement();
			// Order for sql is Ticker,Datestring,Open,High,Low,Close,Volume
			PreparedStatement st = cn.prepareStatement("DELETE FROM portfolio WHERE portfolioId = ? ");

			st.setInt(1, portfolioId);// ticker

			st.executeUpdate();
			cn.commit();

			st.close();

			deleted = true;
		} // Resources declared in the try are closed automatically
		catch (SQLException ex) {
			System.err.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		}
		return deleted;

	}

	// done
	@Override
	public List<PortfolioDto> getAllPortfolios() {
		// select portf name and id create & group by portfolioId in Sql
		int id = 0;
		String portfolioName = "";
		List<PortfolioDto> portfolios = new ArrayList<PortfolioDto>();
		Connection cn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName

			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement("SELECT portfolioId, portfolioName  FROM portfolio");
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time

			while (rs.next()) {

				id = rs.getInt("portfolioId");
				portfolioName = rs.getString("portfolioName");

				PreparedStatement ps2 = cn.prepareStatement("SELECT ticker " + "FROM portfolioTxn t  "
						+ "JOIN portfolio p " + "ON p.portfolioId = t.portfolioId " + "JOIN  stock s "
						+ "ON s.stockId = t.stockId " + "WHERE p.portfolioId = ? ");

				ps2.setInt(1, id);

				rs2 = ps2.executeQuery();
				List<String> stocks = new ArrayList<String>();

				while (rs2.next()) {

					stocks.add(rs2.getString("ticker"));
				}

				portfolios.add(new PortfolioDto(portfolioName, stocks));
				rs2.close();
				ps2.close();
			}
			rs.close();
			ps.close();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return portfolios;
	}

	// done
	@Override
	public PortfolioDto getPortfolioById(int portfolioId) {
		// select portf name and id create & group by portfolioId in Sql
		PortfolioDto portfolio = null;
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");

			PreparedStatement ps = cn.prepareStatement("SELECT portfolioName, ticker " + "FROM portfolioTxn t  "
					+ "JOIN portfolio p " + "ON p.portfolioId = t.portfolioId " + "JOIN  stock s "
					+ "ON s.stockId = t.stockId " + "WHERE p.portfolioId = ? ");

			ps.setInt(1, portfolioId);

			rs = ps.executeQuery();
			List<String> stocks = new ArrayList<String>();
			String portfolioName = "";

			while (rs.next()) {
				stocks.add(rs.getString("ticker"));
				portfolioName = rs.getString("portfolioName");
			}

			portfolio = new PortfolioDto(portfolioName, stocks);

			rs.close();
			ps.close();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return portfolio;
	}
	
	//done
	@Override
	public List<StockSearchDto> searchStock(String ticker) {

		List<StockSearchDto> stocksList = new ArrayList<StockSearchDto>();

		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn
					.prepareStatement("select s.stockid as stockId,s.ticker as ticker,td.open as open from stock s "
							+ "inner join tradedata td on s.stockid=td.stockid "
							+ "where td.TRADEDATE =(select min(tradedate) from TRADEDATA) and UPPER(s.ticker) like UPPER(?)");
			ps.setString(1, (ticker + "%"));
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {

				stocksList.add(new StockSearchDto(rs.getInt("stockId"), rs.getString("ticker"), rs.getDouble("open")));

			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return stocksList;

	}

	
	//done
	@Override
	public List<ExchangeDto> getAllExchanges() {
		// select portf name and id create & group by portfolioId in Sql

		List<ExchangeDto> exchanges = new ArrayList<ExchangeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName

			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement("SELECT exchangeId, exchangeName  FROM exchange");
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time

			while (rs.next()) {

				exchanges.add(new ExchangeDto(rs.getInt("exchangeId"), rs.getString("exchangeName")));
				
				}

			rs.close();
			ps.close();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return exchanges;
	}

	@Override
	public List<TradeDto> getAllStockByExchange(int exchangeId, String ticker) {
		// date in format yyyymmdd
				List<TradeDto> stocks = new ArrayList<TradeDto>();
				Connection cn = null;
				ResultSet rs = null;
				try {
					// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
					// Class.forName
					Driver d = new oracle.jdbc.driver.OracleDriver();
					DriverManager.registerDriver(d);
					cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
					PreparedStatement ps = cn.prepareStatement(
							"    SELECT s.ticker AS ticker, tradedate, open, high, low, close, volume, s.exchangeId AS exchangeId, s.stockId AS stockId "
									+ "    FROM stock s " + "    JOIN tradeData t " + "    ON s.stockId = t.stockId "
									+ "    WHERE s.exchangeId = ? AND UPPER(s.ticker) like UPPER(?)"); 
									
					ps.setInt(1, exchangeId);
					ps.setString(2, (ticker + "%"));
					rs = ps.executeQuery();
					// Process the results of the query, one row at a time
					while (rs.next()) {
						stocks.add(new TradeDto(rs.getString("ticker"), rs.getString("tradedate"), rs.getDouble("open"),
								rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getInt("volume"),
								rs.getInt("exchangeId"), rs.getInt("stockId")));
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				} finally {
					if (cn != null) {
						try {
							if (!cn.isClosed()) {
								cn.close();
							}
						} catch (SQLException ex) {
							System.out.println(ex.getMessage());
							LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
						}
					}
				}
				return stocks;
	}

	@Override
	public List<PortfolioHomeDto> getAllHomeData() {
		// date in format yyyymmdd
		List<PortfolioHomeDto> homeData = new ArrayList<PortfolioHomeDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"SELECT (SUM(marketValue) - SUM(costValue)) AS absoluteChange,     + " + 
					"ROUND(TO_CHAR((SUM(marketValue) - SUM(costValue)) / SUM(costValue)*100),2) AS percentageChange,   " + 
					"portfolioName, portfolioId,   " + 
					"SUM(marketValue) AS currentValue, SUM(costValue) AS initialInvestment, " + 
					"ROUND(TO_CHAR((SUM(marketValue) - SUM(costValue)) / SUM(marketValue)*100),2) AS yield " + 
					"FROM ( SELECT (l.close*pt.numberOfStocks) AS marketValue, (f.open*pt.numberOfStocks) AS costValue,       \r\n" + 
					"p.portfolioName As portfolioName, p.portfolioId As portfolioId    " + 
					"FROM portfolio p   " + 
					"JOIN portfolioTxn pt   " + 
					"ON p.portfolioId = pt.portfolioId   " + 
					"JOIN ( SELECT close, stockId   " + 
					"FROM  tradeData    " + 
					"WHERE tradedate = (SELECT MAX(tradedate)  " + 
					"FROM tradeData)) l   " + 
					"ON l.stockId = pt.stockId   " + 
					"JOIN (SELECT open, stockId   " + 
					"FROM tradeData    " + 
					"WHERE tradedate = (SELECT MIN(tradedate)    " + 
					"FROM tradeData)) f  " + 
					"ON l.stockId = f.stockId)  " + 
					"GROUP BY portfolioId, portfolioName " + 
					"ORDER BY portfolioName"); 
							
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				homeData.add(new PortfolioHomeDto(rs.getInt(4), rs.getString(3), rs.getDouble(2),
						rs.getDouble(1), rs.getDouble(5), rs.getDouble(6), rs.getDouble(7)));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return homeData;
	}

	@Override
	public List<ChartPortfolioDto> getAllPortfolioChart() {
		// date in format yyyymmdd
		List<ChartPortfolioDto> chartData = new ArrayList<ChartPortfolioDto>();
		Connection cn = null;
		ResultSet rs = null;
		try {
			// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
			// Class.forName
			Driver d = new oracle.jdbc.driver.OracleDriver();
			DriverManager.registerDriver(d);
			cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
			PreparedStatement ps = cn.prepareStatement(
					"SELECT (e.close * e.numberOfStocks) AS marketValue, (f.open * e.numberOfStocks) AS initialInvestment, (e.close  - f.open) AS absoluteChange,  " + 
					"    ROUND(TO_CHAR((e.close  - f.open)/f.open)*100,2) AS percentageChange, " + 
					"    ROUND(TO_CHAR((e.close  - f.open)/e.close)*100,2) AS yield, " + 
					"    e.portfolioId, e.endDate, e.portfolioName " + 
					"FROM " + 
					" (SELECT t.tradeDate AS endDate, SUM(t.close) AS close, pt.portfolioId, p.portfolioName, pt.numberOfStocks AS numberofStocks " + 
					" FROM tradeData t " + 
					" JOIN stock s " + 
					" ON s.stockId = t.stockId " + 
					" JOIN portfoliotxn pt " + 
					" ON pt.stockId = s.stockId " + 
					" JOIN portfolio p " + 
					" ON p.portfolioId = pt.portfolioId " +
					" GROUP BY pt.portfolioId, p.portfolioName, t.tradeDate, pt.numberOfStocks)e " + 
					" JOIN " + 
					"(SELECT tradeDate, portfolioId, SUM(open) AS open " + 
					" FROM tradeData t " + 
					" JOIN stock s " + 
					" ON s.stockId = t.stockId " + 
					" JOIN portfoliotxn pt " + 
					" ON pt.stockId = s.stockId " + 
					" WHERE tradedate = (SELECT MIN(tradedate) " + 
					"        FROM tradeData) " + 
					" GROUP BY pt.portfolioId, tradeDate) f " + 
					" ON  f.portfolioId =  e.portfolioId " + 
					" ORDER BY e.portfolioName, e.portfolioId, e.endDate "); 
							
			rs = ps.executeQuery();
			// Process the results of the query, one row at a time
			while (rs.next()) {
				chartData.add(new ChartPortfolioDto(rs.getInt(6), rs.getString(8), rs.getString(7), rs.getDouble(4),
						rs.getDouble(3), rs.getDouble(2), rs.getDouble(1), rs.getDouble(5)));
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
		} finally {
			if (cn != null) {
				try {
					if (!cn.isClosed()) {
						cn.close();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				}
			}
		}
		return chartData;
	}

	@Override
	public List<ChartStockDto> getAllStockChart(int portfolioId) {
		// date in format yyyymmdd
				List<ChartStockDto> chartData = new ArrayList<ChartStockDto>();
				Connection cn = null;
				ResultSet rs = null;
				try {
					// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
					// Class.forName
					Driver d = new oracle.jdbc.driver.OracleDriver();
					DriverManager.registerDriver(d);
					cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
					PreparedStatement ps = cn.prepareStatement(
							"SELECT (e.close * e.numberOfStocks) AS marketValue, (f.open * e.numberOfStocks) AS initialInvestment, (e.close  - f.open) AS absoluteChange, " + 
							"ROUND(TO_CHAR((e.close  - f.open)/f.open)*100,2) AS percentageChange,  " + 
							"ROUND(TO_CHAR((e.close  - f.open)/e.close)*100,2) AS yield,   " + 
							"e.portfolioId, e.portfolioName, e.endDate, e.ticker, e.stockId    " + 
							"FROM    " + 
							"(SELECT t.tradeDate AS endDate, SUM(t.close) AS close, pt.portfolioId, s.ticker, s.stockId, p.portfolioName, pt.numberOfStocks AS numberofStocks  " + 
							"FROM tradeData t " + 
							"JOIN stock s  " + 
							"ON s.stockId = t.stockId  " + 
							"JOIN portfoliotxn pt  " + 
							"ON pt.stockId = s.stockId  " + 
							"JOIN portfolio p " + 
							"ON p.portfolioId = pt.portfolioId " + 
							"WHERE pt.portfolioId = ? " + 
							"GROUP BY s.stockId, s.ticker, t.tradeDate, pt.numberOfStocks, pt.portfolioId, p.portfolioName " + 
							"ORDER BY s.ticker)e  " + 
							"JOIN   " + 
							"(SELECT tradeDate, s.stockId, pt.portfolioId, SUM(open) AS open  " + 
							"FROM tradeData t  " + 
							"JOIN stock s  " + 
							"ON s.stockId = t.stockId  " + 
							"JOIN portfoliotxn pt  " + 
							"ON pt.stockId = s.stockId   " + 
							"WHERE tradedate = (SELECT MIN(tradedate)  " + 
							"FROM tradeData)   " + 
							"AND pt.portfolioId = ?  " + 
							"GROUP BY s.stockId, pt.portfolioId, tradeDate " + 
							" ) f  " + 
							"ON  f.stockId =  e.stockId     " + 
							"WHERE e.portfolioId = ? " + 
							"ORDER BY e.ticker, e.endDate ASC  "); 
					
					ps.setInt(1, portfolioId);	
					ps.setInt(2, portfolioId);	
					ps.setInt(3, portfolioId);	
					rs = ps.executeQuery();
					// Process the results of the query, one row at a time
					// stockId; ticker; endDate; portfolioName; portfolioId; yield; percentageChange; absoluteChange; initialInvestment; marketValue;
					while (rs.next()) {
						chartData.add(new ChartStockDto(rs.getInt(10), rs.getString(9), rs.getString(8), rs.getString(7), rs.getInt(6),
								rs.getDouble(5), rs.getDouble(4), rs.getDouble(3), rs.getDouble(2), rs.getDouble(1)));
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				} finally {
					if (cn != null) {
						try {
							if (!cn.isClosed()) {
								cn.close();
							}
						} catch (SQLException ex) {
							System.out.println(ex.getMessage());
							LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
						}
					}
				}
				return chartData;
	}

	
	@Override
	public List<ChartStockDto> getAllPortfolioPie(int portfolioId) {
		// date in format yyyymmdd
				List<ChartStockDto> chartData = new ArrayList<ChartStockDto>();
				Connection cn = null;
				ResultSet rs = null;
				try {
					// Class.forName("oracle.jdbc.driver.OracleDriver");ClassNotFoundException for
					// Class.forName
					Driver d = new oracle.jdbc.driver.OracleDriver();
					DriverManager.registerDriver(d);
					cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "INOAPPS", "Pa$$w0rd");
					PreparedStatement ps = cn.prepareStatement(
							"SELECT (e.close * e.numberOfStocks) AS marketValue, (f.open * e.numberOfStocks) AS initialInvestment, (e.close  - f.open) AS absoluteChange,  \r\n" + 
							"ROUND(TO_CHAR((e.close  - f.open)/f.open)*100,2) AS percentageChange, \r\n" + 
							"ROUND(TO_CHAR((e.close  - f.open)/e.close)*100,2) AS yield,  \r\n" + 
							"e.portfolioId, e.portfolioName, e.endDate, e.ticker, e.stockId,  e.exchangeName   \r\n" + 
							"FROM    \r\n" + 
							"(SELECT t.tradeDate AS endDate, SUM(t.close) AS close, pt.portfolioId, s.ticker, s.stockId, p.portfolioName, pt.numberOfStocks AS numberofStocks, ex.exchangeName  " + 
							"FROM tradeData t  " + 
							"JOIN stock s   " + 
							"ON s.stockId = t.stockId   " + 
							"JOIN portfoliotxn pt  " + 
							"ON pt.stockId = s.stockId  " + 
							"JOIN portfolio p   " + 
							"ON p.portfolioId = pt.portfolioId  " + 
							"JOIN exchange ex " + 
							"ON ex.exchangeId = s.exchangeId " +
							"WHERE pt.portfolioId = ?  " + 
							"AND tradeDate = (SELECT MAX(tradeDate)  " + 
							"FROM tradeData)   " + 
							"GROUP BY s.stockId, s.ticker, t.tradeDate, pt.numberOfStocks, pt.portfolioId, p.portfolioName, ex.exchangeName   " + 
							"ORDER BY s.ticker)e  " + 
							"JOIN  " + 
							"(SELECT tradeDate, s.stockId, pt.portfolioId, SUM(open) AS open  " + 
							"FROM tradeData t " + 
							"JOIN stock s " + 
							"ON s.stockId = t.stockId  " + 
							"JOIN portfoliotxn pt " + 
							"ON pt.stockId = s.stockId  " + 
							"WHERE tradedate = (SELECT MIN(tradedate) " + 
							"FROM tradeData)  " + 
							"AND pt.portfolioId = ?  " + 
							"GROUP BY s.stockId, pt.portfolioId, tradeDate  " + 
							") f  " + 
							"ON  f.stockId =  e.stockId   " + 
							"WHERE e.portfolioId = ?      " + 
							"ORDER BY e.ticker, e.portfolioId, e.endDate "); 
									
					ps.setInt(1, portfolioId);
					ps.setInt(2, portfolioId);
					ps.setInt(3, portfolioId);
					rs = ps.executeQuery();
					// Process the results of the query, one row at a time
					//int stockId, String ticker, String endDate, String portfolioName, int portfolioId,
					//double yield, double percentageChange, double absoluteChange, double initialInvestment,
					//double marketValue
					while (rs.next()) {
						chartData.add(new ChartStockDto(rs.getInt(10), rs.getString(9), rs.getString(8), rs.getString(7),
								rs.getInt(6), rs.getDouble(5), rs.getDouble(4), rs.getDouble(3),
								rs.getDouble(2), rs.getDouble(1), rs.getString(11)));
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
				} finally {
					if (cn != null) {
						try {
							if (!cn.isClosed()) {
								cn.close();
							}
						} catch (SQLException ex) {
							System.out.println(ex.getMessage());
							LOGGER.warning("LOGGED: IO Error " + ex.getMessage());
						}
					}
				}
				return chartData;
	}

}
