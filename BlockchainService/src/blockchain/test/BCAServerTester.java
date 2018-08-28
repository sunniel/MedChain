/**
 * 
 */
package blockchain.test;

import java.util.Scanner;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blockchain.util.PropertiesLoader;
import ch.qos.logback.classic.LoggerContext;

/**
 * @author user
 *
 */
public class BCAServerTester {

	private static Logger logger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("blockchain");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertiesLoader.loadPropertyFile();

		logger.info("new log!!!");

		// for (int i = 0; i < 4; i++) {
		// String prefix = AppContext.getValue("blockchain.file.prefix");
		// File file = new File(prefix + i + ".dat");
		// if (file.exists()) {
		// System.out.println("file path: " + file.getPath());
		// System.out.println("file name: " + file.getName());
		// file.delete();
		// }
		// }

		System.out.println("Input ID: ");
		Scanner sc = new Scanner(System.in);
		int id = Integer.valueOf(sc.nextLine());

		/*
		 * Test functions start from here
		 */

		Client client = new Client(id);

		boolean run = true;
		while (run) {

			System.out.println("Input: \n" + "0. exit - terminate the program\n"
					+ "1. insertdata - insert data to the blockchain\n"
					+ "2. print - print the entire blockchain on server console\n");

			String cmd = sc.nextLine();
			String reply = null;

			switch (cmd) {
			case "exit":
				run = false;
				System.out.println("Exit from the test");
				break;
			case "insertdata":
				reply = client.insert();
				JSONObject jObject = new JSONObject(reply);
				System.out.println("reply: " + jObject.toString(4));
				break;
			case "print":
				System.out.println("Print the blockchain");
				reply = client.print();
				System.out.println("reply: " + reply);
				break;
			}
			System.out.println();
		}
		client.close();
		sc.close();
	}
	
	

}
