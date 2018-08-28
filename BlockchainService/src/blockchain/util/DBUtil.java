package blockchain.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;

public class DBUtil {
	private static String DB_PREFIX = "blockchain.db.prefix";
	private static String DB_SCHEMA = "blockchain.db.schema";
	private static Properties prop = new Properties();
	
	public static String schema;

	/**
	 * @param id
	 *            Process ID
	 * @param autoCommit
	 *            true for auto-commit; false for manually commit
	 * @return
	 */
	public static Connection getConnection(int id, boolean autoCommit) {
		Connection conn = null;
		prop.setProperty("create", "true");
		prop.setProperty("useUnicode", "true");
		prop.setProperty("characterEncoding", "utf8");
		String jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";
		DbUtils.loadDriver(jdbcDriver);
		try {
			String prefix = AppContext.getValue(DB_PREFIX);
			schema = AppContext.getValue(DB_SCHEMA);
			conn = DriverManager.getConnection(prefix + schema + id, prop);
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static Connection startTransaction(int id) {
		Connection conn = getConnection(id, false);
		// try {
		// conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		// }
		// catch (SQLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return conn;
	}

	public static void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			DbUtils.close(conn);
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public static void closeDB() {
		try {
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		} catch (SQLException e) {
			;
		}
	}
}
