package ds.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.AppContext;
import de.uniba.wiai.lspi.chord.service.AsynChord;
import de.uniba.wiai.lspi.chord.service.ChordFuture;
import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
import de.uniba.wiai.lspi.chord.service.Key;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.Report;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/**
 * Servlet implementation class HelloWorld
 */
@WebServlet(description = "A directory service", urlPatterns = { "/directory" }, loadOnStartup = 1)
public class DirectoryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ArrayList<AsynChord> nodes;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DirectoryServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// read property file by invoking static method on ChordImpl
		PropertiesLoader.loadPropertyFile();

		// create peers
		int size = Integer.valueOf(AppContext.getValue("ds.web.DirectoryServlet.node")).intValue();
		String ip = AppContext.getValue("ds.web.DirectoryServlet.ip");
		int basePort = Integer.valueOf(AppContext.getValue("ds.web.DirectoryServlet.baseport")).intValue();
		nodes = new ArrayList<AsynChord>();

		try {
			for (int i = 0; i < size; i++) {
				int port = basePort + i;
				String endpoint = ip + ":" + port;
				String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
				URL url = new URL(protocol + "://" + endpoint + "/");
				AsynChord chord = new ChordImpl();
				if (i > 0) {
					URL bootstrap = nodes.get(i - 1).getURL();
					chord.join(url, bootstrap);
				} else {
					chord.create(url);
				}
				nodes.add(chord);
			}
			System.out.println("P2P network initialization completes");
			// Thread.sleep(1000);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			closeConnections();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeConnections();
		}
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject reply = new JSONObject();

		StringBuilder sb = new StringBuilder();
		BufferedReader br = request.getReader();
		String str;
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}
		JSONObject jReq = new JSONObject(sb.toString());

		String operation = jReq.getString("operation");
		if (operation.equals("check")) {
			for (AsynChord node : nodes) {
				System.out.println("Print the entries of node " + node.getID());
				System.out.println(((Report) node).printEntries());
				System.out.println();
			}
		} else if (operation.equals("exit")) {
			for (AsynChord chord : nodes) {
				System.out.println("node " + chord.getID() + " exit");
				try {
					chord.leave();
				} catch (ServiceException e) {
					e.printStackTrace();
					System.out.println("Failed to close the service for node " + chord.getID());
				}
			}
		} else {
			Random r = new Random(System.currentTimeMillis());
			int index = r.nextInt(nodes.size());
			AsynChord node = nodes.get(index);

			JSONObject content = jReq.getJSONObject("content");
			if (operation.equals("insert")) {
				String key = content.getString("InvID");
				String value = content.getString("InvContent");
				insert(key, value, node);
			} else if (operation.equals("get")) {
				String key = content.getString("InvID");
				String value = retrieve(key, node);
				reply.put("key", key).put("value", value);
			} else if (operation.equals("remove")) {
				String key = content.getString("InvID");
				remove(key, node);
			}
		}
		reply.put("state", "OK").put("operation", operation);

		// Set response content type
		response.setContentType("application/json");
		// response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(reply.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private void insert(String strkey, String data, AsynChord chord) {
		System.out.println("On node " + chord.getID());
		try {
			Key key = new CryptoHashKey(Hex.decodeHex(strkey.trim()));
			System.out.println("Key of the inserted data: " + key.toString());
			ChordFuture future = chord.insertAsync(key, data);
			boolean finished = future.isDone();
			while (!finished) {
				try {
					future.waitForBeingDone();
					finished = true;
				} catch (InterruptedException e) {
					finished = false;
				}
			}
			System.out.println("Finish the insertion of data on " + chord.getID());
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println("Failed to insert for node " + chord.getID());
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Key decoding error on node " + chord.getID());
		}
	}

	// private String insertData(String data, AsynChord chord) {
	// System.out.println("On node " + chord.getID());
	// String strkey = "";
	// try {
	// Key key = CryptoHashKey.encode(data.getBytes());
	// strkey = key.toString();
	// System.out.println("Key of the inserted data: " + strkey);
	// ChordFuture future = chord.insertAsync(key, data);
	// boolean finished = future.isDone();
	// while (!finished) {
	// try {
	// future.waitForBeingDone();
	// finished = true;
	// } catch (InterruptedException e) {
	// finished = false;
	// }
	// }
	// System.out.println("Finish the insertion of data on " + chord.getID());
	// } catch (ServiceException e) {
	// e.printStackTrace();
	// System.out.println("Failed to insert data for node " + chord.getID());
	// }
	// return strkey;
	// }

	private String retrieve(String strkey, AsynChord chord) {
		System.out.println("On node " + chord.getID());
		String data = "";
		try {
			System.out.println("strkey: " + strkey);
			Key key = new CryptoHashKey(Hex.decodeHex(strkey.trim()));
			// Set<Serializable> rawDataSet = chord.retrieve(key);
			ChordRetrievalFuture futureRetrieval = chord.retrieveAsync(key);
			boolean finished = futureRetrieval.isDone();
			while (!finished) {
				try {
					futureRetrieval.waitForBeingDone();
					finished = true;
				} catch (InterruptedException e) {
					finished = false;
				}
			}
			Set<Serializable> rawDataSet = futureRetrieval.getResult();
			System.out.println("Size of the retrieved data: " + rawDataSet.size());
			for (Serializable rawData : rawDataSet) {
				data = (String) rawData;
				System.out.println("Data: " + data);
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println("Failed to retrieve data for node " + chord.getID());
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Key decoding error on node " + chord.getID());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	private void remove(String strkey, AsynChord chord) {
		System.out.println("On node " + chord.getID());
		try {
			System.out.println("strkey: " + strkey);
			Key key = new CryptoHashKey(Hex.decodeHex(strkey.trim()));
			ChordFuture future = chord.removeAsync(key, "");
			boolean finished = future.isDone();
			while (!finished) {
				try {
					future.waitForBeingDone();
					finished = true;
				} catch (InterruptedException e) {
					finished = false;
				}
			}
			System.out.println("Finish the removal of data on " + chord.getID());
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println("Failed to retrieve data for node " + chord.getID());
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Key decoding error on node " + chord.getID());
		}
	}

	private void closeConnections() {
		for (AsynChord chord : nodes) {
			System.out.println("node " + chord.getID() + " exit");
			try {
				chord.leave();
			} catch (ServiceException e) {
				e.printStackTrace();
				System.out.println("Failed to close the service for node " + chord.getID());
			}
		}
	}

}
