package blockchain.dao;

import java.sql.Blob;
import java.sql.Connection;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import blockchain.data.EventDGADDomain;
import blockchain.data.EventDGDomain;
import blockchain.data.EventSCDomain;
import blockchain.util.DBUtil;

public class EventDAO {
	public void addEventDG(int id, String blockHash, String eventHash, byte[] data, String PKsp, String digest,
			String signature) throws Exception {
		String sqlInsertEvent = "INSERT INTO EVENT_DG (BLOCK_HASH, EVENT_HASH, DATA, PKsp, DIGEST, SIGNATURE) VALUES(?, ?, ?, ?, ?, ?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			Blob blob = new SerialBlob(data);
			runner.update(conn, sqlInsertEvent, blockHash, eventHash, blob, PKsp, digest, signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new data generation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	// public void addEventDGAD(int id, String blockHash, String eventHash, byte[]
	// data, String PKsp, String digest,
	// String signature. String encrypted) throws Exception {
	public void addEventDGAD(int id, String blockHash, String eventHash, byte[] data, String PKsp, String digest,
			String signature) throws Exception {
		// String sqlInsertEvent = "INSERT INTO EVENT_DGAD (BLOCK_HASH, EVENT_HASH,
		// DATA, PKsp, DIGEST, SIGNATURE, ENCRYPTED) VALUES(?, ?, ?, ?, ?, ?, ?)";
		String sqlInsertEvent = "INSERT INTO EVENT_DGAD (BLOCK_HASH, EVENT_HASH, DATA, PKsp, DIGEST, SIGNATURE) VALUES(?, ?, ?, ?, ?, ?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			Blob blob = new SerialBlob(data);
			// Blob blob2 = new SerialBlob(encrypted);
			runner.update(conn, sqlInsertEvent, blockHash, eventHash, blob, PKsp, digest, signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new data generation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public void addEventSC(int id, String blockHash, String eventHash, byte[] session, String PKpat, String signature)
			throws Exception {
		String sqlInsertEvent = "INSERT INTO EVENT_SC (BLOCK_HASH, EVENT_HASH, SESSION, PKpat, SIGNATURE) VALUES(?, ?, ?, ?, ?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			Blob blob = new SerialBlob(session);
			runner.update(conn, sqlInsertEvent, blockHash, eventHash, blob, PKpat, signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new session creation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public void addEventSCAD(int id, String blockHash, String eventHash, byte[] session, String PKpat, String signature)
			throws Exception {
		String sqlInsertEvent = "INSERT INTO EVENT_SCAD (BLOCK_HASH, EVENT_HASH, SESSION, PKpat, SIGNATURE) VALUES(?, ?, ?, ?, ?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			Blob blob = new SerialBlob(session);
			runner.update(conn, sqlInsertEvent, blockHash, eventHash, blob, PKpat, signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new session creation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public void addEventSRAD(int id, String blockHash, String eventHash, String sid, String PKpat, String signature)
			throws Exception {
		String sqlInsertEvent = "INSERT INTO EVENT_SRAD (BLOCK_HASH, EVENT_HASH, SID, PKpat, SIGNATURE) VALUES(?, ?, ?, ?, ?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			runner.update(conn, sqlInsertEvent, blockHash, eventHash, sid, PKpat, signature);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new session creation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public EventDGDomain getEventDG(int id, String eventHash) throws Exception {

		String sql = "SELECT * FROM EVENT_DG WHERE EVENT_HASH = ?";

		Connection conn = null;
		EventDGDomain event = null;
		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			ResultSetHandler<EventDGDomain> resultSet = new BeanHandler<EventDGDomain>(EventDGDomain.class);
			event = runner.query(conn, sql, resultSet, eventHash);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to retrieve data geneartion event");
		} finally {
			DBUtil.closeConnection(conn);
		}
		return event;
	}

	public EventDGADDomain getEventDGAD(int id, String eventHash) throws Exception {

		String sql = "SELECT * FROM EVENT_DGAD WHERE EVENT_HASH = ?";

		Connection conn = null;
		EventDGADDomain event = null;
		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			ResultSetHandler<EventDGADDomain> resultSet = new BeanHandler<EventDGADDomain>(EventDGADDomain.class);
			event = runner.query(conn, sql, resultSet, eventHash);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to retrieve data geneartion event");
		} finally {
			DBUtil.closeConnection(conn);
		}
		return event;
	}

	public EventSCDomain getEventSC(int id, String eventHash) throws Exception {

		String sql = "SELECT * FROM EVENT_SC WHERE EVENT_HASH = ?";

		Connection conn = null;
		EventSCDomain event = null;
		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			ResultSetHandler<EventSCDomain> resultSet = new BeanHandler<EventSCDomain>(EventSCDomain.class);
			event = runner.query(conn, sql, resultSet, eventHash);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to retrieve session creation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
		return event;
	}

	public void empty(int id) throws Exception {
		String sql1 = "DELETE FROM EVENT_DG";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			runner.update(conn, sql1);
			updateSeq(id, 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to add a new data generation event");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	// public void addEventSR(int id, String blockHash, String eventHash, String
	// sid, byte[] sessionEnd, String PKpat, String digest, String signature)
	// throws Exception {
	// String sqlInsertEvent = "INSERT INTO EVENT_SG (BLOCK_HASH, EVENT_HASH, SID,
	// SESSION_END, PKpat, SIGNATURE) VALUES(?, ?, ?, ?, ?, ?)";
	//
	// Connection conn = null;
	//
	// try {
	// conn = DBUtil.getConnection(id, true);
	// QueryRunner runner = new QueryRunner();
	// Blob blob = new SerialBlob(sessionEnd);
	// runner.update(conn, sqlInsertEvent, blockHash, eventHash, sid, blob, PKpat,
	// signature);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new Exception("Faild to add a new session removal event");
	// } finally {
	// DBUtil.closeConnection(conn);
	// }
	// }

	public int getSeq(int id) throws Exception {

		String seqlCount = "select count(*) FROM EVENT_SEQ";
		String sqlUpdateEventSeq = "SELECT SEQUENCE FROM EVENT_SEQ";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			ScalarHandler<Integer> resultSet = new ScalarHandler<Integer>();
			int count = runner.query(conn, seqlCount, resultSet);
			int seq = 0;
			if (count > 0) {
				seq = runner.query(conn, sqlUpdateEventSeq, resultSet);
			} else {
				updateSeq(id, 0);
			}
			return seq;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to retrieve event sequence number");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public void updateSeq(int id, int seq) throws Exception {
		String sqlUpdateEventSeq = "UPDATE EVENT_SEQ SET SEQUENCE = ?";
		String sqlInsertEventSeq = "INSERT INTO EVENT_SEQ (SEQUENCE) VALUES(?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			int row = runner.update(conn, sqlUpdateEventSeq, seq);
			if (row == 0) {
				runner.update(conn, sqlInsertEventSeq, seq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to update event sequence number");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public int getSeq2(int id) throws Exception {

		String seqlCount = "select count(*) FROM EVENT_SEQ2";
		String sqlUpdateEventSeq2 = "SELECT SEQUENCE FROM EVENT_SEQ2";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			ScalarHandler<Integer> resultSet = new ScalarHandler<Integer>();
			int count = runner.query(conn, seqlCount, resultSet);
			int seq = 0;
			if (count > 0) {
				seq = runner.query(conn, sqlUpdateEventSeq2, resultSet);
			} else {
				updateSeq(id, 0);
			}
			return seq;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to retrieve event sequence number");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}

	public void updateSeq2(int id, int seq) throws Exception {
		String sqlUpdateEventSeq2 = "UPDATE EVENT_SEQ2 SET SEQUENCE = ?";
		String sqlInsertEventSeq2 = "INSERT INTO EVENT_SEQ2 (SEQUENCE) VALUES(?)";

		Connection conn = null;

		try {
			conn = DBUtil.getConnection(id, true);
			QueryRunner runner = new QueryRunner();
			int row = runner.update(conn, sqlUpdateEventSeq2, seq);
			if (row == 0) {
				runner.update(conn, sqlInsertEventSeq2, seq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Faild to update event sequence number");
		} finally {
			DBUtil.closeConnection(conn);
		}
	}
}
