package blockchain.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import blockchain.util.DBUtil;

public class CreateTables {

	/**
	 * 
	 * @param id
	 *            Process ID
	 */
	public static void createTables(int id) {
		// create tables, if not exist
		String sqlCreateEventDG = "CREATE TABLE EVENT_DG (" //
				+ "BLOCK_HASH varchar(64) NOT NULL," //
				+ "EVENT_HASH varchar(64) NOT NULL," //
				+ "DATA BLOB NOT NULL," //
				+ "PKsp varchar(256) NOT NULL," //
				+ "DIGEST varchar(64) NOT NULL," //
				+ "SIGNATURE varchar(256) NOT NULL, " //
				+ "PRIMARY KEY (EVENT_HASH) )"; //

		String sqlCreateEventSC = "CREATE TABLE EVENT_SC (" //
				+ "BLOCK_HASH varchar(64) NOT NULL," //
				+ "EVENT_HASH varchar(64) NOT NULL," //
				+ "SESSION BLOB NOT NULL," //
				+ "PKpat varchar(256) NOT NULL," //
				+ "SIGNATURE varchar(256) NOT NULL, " //
				+ "PRIMARY KEY (EVENT_HASH) )"; //

		String sqlCreateEventSCAD = "CREATE TABLE EVENT_SCAD (" //
				+ "BLOCK_HASH varchar(64) NOT NULL," //
				+ "EVENT_HASH varchar(64) NOT NULL," //
				+ "SESSION BLOB NOT NULL," //
				+ "PKpat varchar(256) NOT NULL," //
				+ "SIGNATURE varchar(256) NOT NULL, " //
				+ "PRIMARY KEY (EVENT_HASH) )"; //

		String sqlCreateEventDGAD = "CREATE TABLE EVENT_DGAD (" //
				+ "BLOCK_HASH varchar(64) NOT NULL," //
				+ "EVENT_HASH varchar(64) NOT NULL," //
				+ "DATA BLOB NOT NULL," //
				+ "PKsp varchar(256) NOT NULL," //
				+ "DIGEST varchar(64) NOT NULL," //
				+ "SIGNATURE varchar(256) NOT NULL, " //
				// + "ENCRYPTED BLOB NOT NULL, " //
				+ "PRIMARY KEY (EVENT_HASH) )"; //

		String sqlCreateEventSRAD = "CREATE TABLE EVENT_SRAD (" //
				+ "BLOCK_HASH varchar(64) NOT NULL," //
				+ "EVENT_HASH varchar(64) NOT NULL," //
				+ "SID varchar(64) NOT NULL," //
				+ "PKpat varchar(256) NOT NULL," //
				+ "SIGNATURE varchar(256) NOT NULL, " //
				+ "PRIMARY KEY (EVENT_HASH) )"; //

		String sqlCreateEVENT_SEQ = "CREATE TABLE EVENT_SEQ (" //
				+ "SEQUENCE int NOT NULL, " //
				+ "PRIMARY KEY (SEQUENCE) )"; //

		String sqlCreateEVENT_SEQ2 = "CREATE TABLE EVENT_SEQ2 (" //
				+ "SEQUENCE int NOT NULL, " //
				+ "PRIMARY KEY (SEQUENCE) )"; //

		// create indexes
		// String sqlIndexExchangeRate1 = "CREATE INDEX index_base_and_quote ON
		// exchange_rate (base_cur, quote_cur)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			Statement statement = conn.createStatement();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, "EVENT_DG", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEventDG);
			}
			rs = dbmd.getTables(null, null, "EVENT_SC", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEventSC);
			}
			// rs = dbmd.getTables(null, null, "EVENT_SR", null);
			// if (!rs.next()) {
			// statement.execute(sqlCreateEventSR);
			// }
			rs = dbmd.getTables(null, null, "EVENT_DGAD", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEventDGAD);
			}
			rs = dbmd.getTables(null, null, "EVENT_SCAD", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEventSCAD);
			}
			rs = dbmd.getTables(null, null, "EVENT_SRAD", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEventSRAD);
			}
			rs = dbmd.getTables(null, null, "EVENT_SEQ", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEVENT_SEQ);
			}
			rs = dbmd.getTables(null, null, "EVENT_SEQ2", null);
			if (!rs.next()) {
				statement.execute(sqlCreateEVENT_SEQ2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnection(conn);
			DBUtil.closeDB();
		}
	}
}
