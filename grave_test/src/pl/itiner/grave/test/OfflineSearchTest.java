package pl.itiner.grave.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

public class OfflineSearchTest extends SearchTest {

	private static final String GSM_DATA_OFF = "gsm data off\n";
	private static final String GSM_DATA_ON = "gsm data on\n";

	private static boolean isDataDownloaded = false;

	protected void setUp() throws Exception {
		super.setUp();
		if (!isDataDownloaded) {
			helperTestAllBasicCategories(true);
			solo.goBack();
			clearScr();
			isDataDownloaded = true;
		}
		runTelnetCommand(GSM_DATA_OFF);
	}

	private void runTelnetCommand(String cmd) throws SocketException,
			IOException, Exception {
		TelnetClient client = null;
		try {
			client = new TelnetClient();
			client.connect("10.0.2.2", 5554);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String result = in.readLine();
			result = in.readLine();
			if (!result.equals("OK")) {
				throw new Exception("Telnet failed: " + result);
			}
			PrintStream out = new PrintStream(client.getOutputStream());
			out.print(cmd);
			out.flush();
			result = in.readLine();
			if (!result.equals("OK")) {
				throw new Exception("Telnet failed: " + result);
			}
		} finally {
			if (null != client) {
				client.disconnect();
			}
		}
	}

	protected void tearDown() throws Exception {
		runTelnetCommand(GSM_DATA_ON);
		super.tearDown();
	}

	@Override
	protected boolean isOnline() {
		return false;
	}
}
