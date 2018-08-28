package blockchain.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blockchain.util.StringUtil;
import ch.qos.logback.classic.LoggerContext;

public class Block {

	private static Logger logger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("blockchain");

	// basic elements of a block
	public String hash;
	public String previousHash;
	public long timeStamp; // as number of milliseconds since 1/1/1970.
	public List<Event> events;
	public String merkleRoot;

	// Block Constructor.
	public Block(String previousHash, List<Event> events, long ts) {
		this.events = new ArrayList<Event>(events);
		this.previousHash = previousHash;
		this.timeStamp = ts;
	}

	public void computeMerkleRoot() {
		logger.info("...........................");
		List<String> hashes = new ArrayList<String>();
		for (Event event : this.events) {
			hashes.add(event.id);
			logger.info("event id: " + event.id);
		}
		merkleRoot = StringUtil.getMerkleRoot(hashes);
		logger.info("...........................");
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
