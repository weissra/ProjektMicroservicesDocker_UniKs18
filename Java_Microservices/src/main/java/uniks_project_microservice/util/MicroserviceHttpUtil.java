package uniks_project_microservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MicroserviceHttpUtil {
	public static String readAll(BufferedReader in) {
		StringBuffer buf = new StringBuffer();
		try {
			while (true) {

				String line;

				line = in.readLine();

				if (line == null) {
					break;
				}

				buf.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public static String readAll(InputStream in) {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in));
		StringBuffer buf = new StringBuffer();
		try {
			while (true) {

				String line;

				line = br.readLine();

				if (line == null) {
					break;
				}

				buf.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
}
