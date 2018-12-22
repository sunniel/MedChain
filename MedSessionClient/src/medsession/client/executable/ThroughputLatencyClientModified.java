package medsession.client.executable;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONObject;

import bftsmart.tom.ServiceProxy;

public class ThroughputLatencyClientModified {

	public static int initId = 0;

	public static void main(String[] args) throws IOException {

		initId = 0;
		int numThreads = 10;

		Client[] clients = new Client[numThreads];

		for (int i = 0; i < numThreads; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {

				ex.printStackTrace();
			}

			System.out.println("Launching client " + (initId + i));
			clients[i] = new ThroughputLatencyClientModified.Client(initId + i);
		}

		ExecutorService exec = Executors.newFixedThreadPool(clients.length);
		Collection<Future<?>> tasks = new LinkedList<>();

		for (Client c : clients) {
			tasks.add(exec.submit(c));
		}

		// wait for tasks completion
		for (Future<?> currTask : tasks) {
			try {
				currTask.get();
			} catch (InterruptedException | ExecutionException ex) {

				ex.printStackTrace();
			}

		}

		exec.shutdown();

		System.out.println("All clients done.");
	}

	static class Client extends Thread {

		int id;
		ServiceProxy proxy;
		byte[] request;

		public Client(int id) {
			super("Client " + id);

			this.id = id;

			JSONObject event = new JSONObject();
			String enrypted = "b457bb288c6d8818e326655b2dad77c44c14296298fe9da3e5737389bdc0f74f38311"
					+ "79357daa5cd11fc150eabedb9cf4b33b9507ea406b84b9ef23c19d82609123608dafcf66d32e8"
					+ "d70b32897d298995a5ac3fb9ec41ba67e7ceff9a3d7067f879a24b3722d3e8ede73eca02cb515"
					+ "44d9ce6c11223d9560ff24de6d372b722ae6e155294d02e20f5c5d3c2670763b7abe242cf2301"
					+ "f5b134599c8f403d6777868942a10260c1678a92cb8b96785ac2866c940fe598b062b80883f78"
					+ "ba0b7c63811c24586543cdc78bcb95f289dee0a01667fb6b229a9e76e88dd050a56653cd8723b"
					+ "64429465ece3710e6fa0c11506d0efac9ada46015c8b869d27c2d16029";
			event.put("Content", enrypted);
			event.put("Digest", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
			event.put("PKsp",
					"3059301306072a8648ce3d020106082a8648ce3d03010703420004037fe503cce072b189ef03bb6894b3839e372e013e8b602bfa784a21f5e989751bef38f40ba79a7f94bdba140ac9a177dd2e8c7b3fc4213dbae656f75f3fc40b");
			// assign event type
			event.put("Type", 0);
			// sign the message
			event.put("Signature", "3046022100bf160ade6af40695c5f4ae1ac103dd70dd842c42e0e28d341cd19f"
					+ "05bfcf23e8022100b7ac19eb055972d83ed041d3ad03de2220dddb57c91db4b58368419f405110e5");

			JSONObject request = new JSONObject();
			request.put("operation", "addEvent");
			request.put("event", event);
			this.request = request.toString().getBytes();
			this.proxy = new ServiceProxy(id);
		}

		public void run() {

			System.out.println("Warm up...");

			int req = 0;

			JSONObject event = new JSONObject();
			String enrypted = "b457bb288c6d8818e326655b2dad77c44c14296298fe9da3e5737389bdc0f74f38311"
					+ "79357daa5cd11fc150eabedb9cf4b33b9507ea406b84b9ef23c19d82609123608dafcf66d32e8"
					+ "d70b32897d298995a5ac3fb9ec41ba67e7ceff9a3d7067f879a24b3722d3e8ede73eca02cb515"
					+ "44d9ce6c11223d9560ff24de6d372b722ae6e155294d02e20f5c5d3c2670763b7abe242cf2301"
					+ "f5b134599c8f403d6777868942a10260c1678a92cb8b96785ac2866c940fe598b062b80883f78"
					+ "ba0b7c63811c24586543cdc78bcb95f289dee0a01667fb6b229a9e76e88dd050a56653cd8723b"
					+ "64429465ece3710e6fa0c11506d0efac9ada46015c8b869d27c2d16029";
			event.put("Content", enrypted);
			event.put("Digest", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
			event.put("PKsp",
					"3059301306072a8648ce3d020106082a8648ce3d03010703420004037fe503cce072b189ef03bb6894b3839e372e013e8b602bfa784a21f5e989751bef38f40ba79a7f94bdba140ac9a177dd2e8c7b3fc4213dbae656f75f3fc40b");
			// assign event type
			event.put("Type", 0);
			// sign the message
			event.put("Signature", "3046022100bf160ade6af40695c5f4ae1ac103dd70dd842c42e0e28d341cd19f"
					+ "05bfcf23e8022100b7ac19eb055972d83ed041d3ad03de2220dddb57c91db4b58368419f405110e5");

			JSONObject data = new JSONObject();
			data.put("operation", "warmup");
			data.put("event", event);

			for (int i = 0; i < 20; i++, req++) {
				proxy.invokeOrdered(data.toString().getBytes());

				try {
					// sleeps interval ms before sending next request
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
			}

			System.out.println("Executing experiment for " + 10 + " ops");

			for (int i = 0; i < 10; i++, req++) {
				// long last_send_instant = System.nanoTime();
				System.out.print(this.id + " // Sending req " + req + "...");

				proxy.invokeOrdered(request);

				System.out.println(this.id + " // sent!");
				try {
					// sleeps interval ms before sending next request
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}

				if (req % 1000 == 0)
					System.out.println(this.id + " // " + req + " operations sent!");
			}

			// if (id == initId) {
			// System.out.println(this.id + " // Average time for " + numberOfOps / 2 + "
			// executions (-10%) = "
			// + st.getAverage(true) / 1000 + " us ");
			// System.out.println(this.id + " // Standard desviation for " + numberOfOps / 2
			// + " executions (-10%) = "
			// + st.getDP(true) / 1000 + " us ");
			// System.out.println(this.id + " // Average time for " + numberOfOps / 2 + "
			// executions (all samples) = "
			// + st.getAverage(false) / 1000 + " us ");
			// System.out.println(this.id + " // Standard desviation for " + numberOfOps / 2
			// + " executions (all samples) = " + st.getDP(false) / 1000 + " us ");
			// System.out.println(this.id + " // Maximum time for " + numberOfOps / 2 + "
			// executions (all samples) = "
			// + st.getMax(false) / 1000 + " us ");
			// }

			proxy.close();
		}
	}
}
