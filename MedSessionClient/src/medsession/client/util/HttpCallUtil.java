package medsession.client.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class HttpCallUtil {

	public JSONObject httpCall(String apiEndPoint, JSONObject object) throws IOException {
		JSONObject jsonObject = new JSONObject();

		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream in = null;
		try {
			URL postURL = new URL(apiEndPoint);
			conn = (HttpURLConnection) postURL.openConnection();

			// Set connection parameters. We need to perform input and output,
			// so set both as true.
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			// Set the content type we are POSTing. We impersonate it as
			// encoded form data
			conn.setRequestMethod("POST");

			os = conn.getOutputStream();
			os.write(object.toString().getBytes("UTF-8"));
			os.close();

			// read the response
			in = new BufferedInputStream(conn.getInputStream());
			String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
			jsonObject = new JSONObject(result);

			in.close();
			conn.disconnect();
		} catch (IOException e) {
			throw e;
		} finally {
			// safely close I/O streams here???
		}
		return jsonObject;
	}
}
