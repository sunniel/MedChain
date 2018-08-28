package blockchain.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;
import blockchain.dao.EventDAO;
import blockchain.data.Block;
import blockchain.data.CreateTables;
import blockchain.data.Event;
import blockchain.data.EventDG;
import blockchain.data.EventSC;
import blockchain.util.AppContext;
import blockchain.util.PropertiesLoader;

public class BCAuditServer extends DefaultRecoverable {

	private static final int EVENT_DG = 0;
	private static final int EVENT_SC = 1;

	private Logger logger = LoggerFactory.getLogger("blockchain");

	private List<Block> blockchain;
	private int id;

	// the events haven't been load into a block
	private List<String> eventsToProcess;

	public BCAuditServer(int id) {
		new ServiceReplica(id, this, this);

		// add security provider
		Security.addProvider(new BouncyCastleProvider());

		this.id = id;
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();
		blockchain = new ArrayList<Block>();
		eventsToProcess = new LinkedList<String>();

		// init local database
		CreateTables.createTables(this.id);
		// load and parse blockchain data file
		BufferedReader reader = null;
		try {
			String prefix = AppContext.getValue("blockchain.file.prefix");
			if (new File(prefix + id + ".dat").exists()) {
				reader = new BufferedReader(new FileReader(prefix + id + ".dat"));
				String line = reader.readLine();
				while (line != null) {
					// TODO block and event verification
					System.out.println("line: " + line);
					JSONObject jsBlock = new JSONObject(line);
					JSONArray array = jsBlock.getJSONArray("Events");
					List<Event> events = new ArrayList<Event>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject jsEvent = array.getJSONObject(i);
						Event event = new EventDG();
						event.content = jsEvent.getString("Content");
						event.type = jsEvent.getInt("Type");
						if (event.type == EVENT_DG) {
							event.digest = jsEvent.getString("Digest");
						}
						event.PK = jsEvent.getString("PK");
						event.id = jsEvent.getString("EventHash");
						event.signature = jsEvent.getString("Signature");
						events.add(event);
					}
					long ts = jsBlock.getLong("Timestamp");
					Block block = new Block(jsBlock.getString("PreviousHash"), events, ts);
					block.merkleRoot = jsBlock.getString("MerkleRoot");
					block.hash = jsBlock.getString("BlockHash");
					blockchain.add(block);
					line = reader.readLine();
				}
				reader.close();
			}

			// load event sequence number
			EventDAO dao = new EventDAO();
			Event.sequence = dao.getSeq(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: blockchain.service.BlockchainServer <server id>");
			System.exit(0);
		}

		new BCAuditServer(Integer.parseInt(args[0]));
	}

	@Override
	public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs, boolean fromConsensus) {
		byte[][] replies = new byte[commands.length][];
		List<JSONObject> results = new ArrayList<JSONObject>();
		long ts = 0;
		List<Event> events = new ArrayList<Event>();
		PrintWriter writer = null;
		try {
			// create new events
			for (int i = 0; i < commands.length; i++) {
				replies[i] = new String("Invalid request").getBytes();
				JSONObject result = new JSONObject();
				result.put("Result", "Error");
				result.put("index", i);

				String input = new String(commands[i], "UTF-8");
				JSONObject jObject = new JSONObject(input);
				System.out.println("jObject: " + jObject.toString(4));
				if (jObject.has("operation") && jObject.getString("operation").equals("addEvent")
						&& jObject.has("event")) {
					JSONObject value = jObject.getJSONObject("event");
					if (validateEvent(value)) {
						// msgCtxs[i].getTimestamp() actually all return the timestamp of the first
						// request in the batch
						ts = msgCtxs[i].getTimestamp();
						System.out.println("msgCtxs[i].getTimestamp(): " + msgCtxs[i].getTimestamp());
						int type = value.getInt("Type");
						if (type == EVENT_DG) {
							EventDG event = new EventDG();
							event.content = value.getString("Content");
							event.digest = value.getString("Digest");
							event.PK = value.getString("PKsp");
							event.type = value.getInt("Type");
							event.signature = value.getString("Signature");
							if (event.processTransaction()) {
								events.add(event);
								result.put("Result", "OK");
								result.put("EventHash", event.id);
								results.add(result);
							}
						} else if (type == EVENT_SC) {
							EventSC event = new EventSC();
							event.content = value.getString("Content");
							event.PK = value.getString("PKsp");
							event.type = value.getInt("Type");
							event.signature = value.getString("Signature");
							if (event.processTransaction()) {
								events.add(event);
								result.put("Result", "OK");
								result.put("EventHash", event.id);
								results.add(result);
							}
						}
					}
				} else if ("warmup".equals(jObject.getString("operation"))) {
					replies[i] = new String("warm up").getBytes();
				}
			}
			// create a new block
			if (events.size() > 0) {

				String previousHash;
				if (blockchain.isEmpty()) {
					previousHash = DigestUtils.sha256Hex(AppContext.getValue("blockchain.service.block.gensis.seed"));
				} else {
					previousHash = blockchain.get(blockchain.size() - 1).hash;
				}
				Block block = new Block(previousHash, events, ts);
				block.computeMerkleRoot();
				block.calculateHash();
				blockchain.add(block);

				JSONObject jsBlock = new JSONObject();
				jsBlock.put("BlockHash", block.hash).put("PreviousHash", block.previousHash)
						.put("Timestamp", block.timeStamp).put("MerkleRoot", block.merkleRoot)
						.put("Size", block.events.size());
				for (Event event : events) {
					JSONObject jsEvent = new JSONObject();
					// store events into database
					EventDAO dao = new EventDAO();
					if (event.type == EVENT_DG) {
						dao.addEventDG(id, block.hash, event.id, event.content.getBytes(), event.PK, event.digest,
								event.signature);
						Event.sequence++;
					} else if (event.type == EVENT_SC) {
						dao.addEventSC(id, block.hash, event.id, event.content.getBytes(), event.PK, event.signature);
						Event.sequence++;
					}
					dao.updateSeq(id, Event.sequence);
					jsEvent.put("EventHash", event.id);
					jsEvent.put("Content", event.content);
					jsEvent.put("PK", event.PK);
					if (event.type == EVENT_DG) {
						jsEvent.put("Digest", event.digest);
					}
					jsEvent.put("Type", event.type);
					jsEvent.put("Signature", event.signature);
					jsBlock.append("Events", jsEvent);
				}

				// store the new block to the blockchain data file
				String prefix = AppContext.getValue("blockchain.file.prefix");
				writer = new PrintWriter(new FileWriter(prefix + id + ".dat", true));
				writer.println(jsBlock.toString());

				// generate replies
				for (JSONObject result : results) {
					if (result.getString("Result").equals("OK")) {
						result.put("BlockHash", block.hash);
					}
					int index = (int) result.remove("index");
					// For one-to-one matching the requests to the replies
					replies[index] = result.toString().getBytes();
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

		return replies;
	}

	@Override
	public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
		JSONObject jsReply = new JSONObject();
		jsReply.put("Result", "Record no found");
		try {
			String input = new String(command, "UTF-8");
			JSONObject jObject = new JSONObject(input);
			System.out.println("jObject: " + jObject.toString(4));
			if (jObject.getString("operation").equals("getEvent") && jObject.has("value")) {
				String blockHash = jObject.getJSONObject("value").getString("BlockHash");
				String eventHash = jObject.getJSONObject("value").getString("EventHash");
				for (Block block : blockchain) {
					if (block.hash.equals(blockHash)) {
						List<Event> events = block.events;
						for (Event event : events) {
							if (event.id.equals(eventHash)) {
								JSONObject js = new JSONObject();
								js.put("BlockHash", block.hash);
								js.put("EventHash", event.id);
								js.put("Content", event.content);
								js.put("Signature", event.signature);
								js.put("Type", event.type);
								if (event.type == EVENT_DG) {
									js.put("Digest", event.digest);
								}
								jsReply.put("Result", "OK");
								jsReply.put("Content", js);
								break;
							}
						}
					}
				}
			} else {
				jsReply.put("Result", "Invalid Request");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			jsReply.put("Result", "Error");
		}
		System.out.println("jsReply: " + jsReply);
		return jsReply.toString().getBytes();
	}

	@Override
	public void installSnapshot(byte[] state) {
		ByteArrayInputStream bis = new ByteArrayInputStream(state);
		try {
			ObjectInput in = new ObjectInputStream(bis);
			blockchain = (List<Block>) in.readObject();
			in.close();
			bis.close();
		} catch (ClassNotFoundException e) {
			System.out.print("Coudn't find Map: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("Exception installing the application state: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getSnapshot() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(blockchain);
			out.flush();
			out.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			System.out.println(
					"Exception when trying to take a + " + "snapshot of the application state" + e.getMessage());
			e.printStackTrace();
			return new byte[0];
		}
	}

	private boolean validateEvent(JSONObject value) {
		// TODO
		return true;
	}
}
