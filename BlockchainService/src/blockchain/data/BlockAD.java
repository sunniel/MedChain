package blockchain.data;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public class BlockAD extends Block {

	private static Logger logger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("blockchain");

	public Set<String> signatureCollection;

	// Block Constructor.
	public BlockAD(String previousHash, List<Event> events, long ts) {
		super(previousHash, events, ts);
		signatureCollection = new TreeSet<String>();
		for (Event event : events) {
			signatureCollection.add(event.PK);
		}
	}

	/**
	 * Making sure we do this after we set the other values, including the Merkle
	 * root
	 */
	public void calculateHash() {
		hash = DigestUtils.sha256Hex(previousHash + Long.toString(timeStamp) + merkleRoot);
		logger.info("...........................");
		logger.info("previousHash: " + previousHash);
		logger.info("timeStamp: " + timeStamp);
		logger.info("merkleRoot: " + merkleRoot);
		logger.info("...........................");
	}
}
