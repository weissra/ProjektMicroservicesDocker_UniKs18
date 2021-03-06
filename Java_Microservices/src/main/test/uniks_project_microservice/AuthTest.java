package uniks_project_microservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;
import org.junit.Test;

import uniks_project_microservice.util.MicroserviceConstants;
import uniks_project_microservice.util.MicroserviceUtil;

public class AuthTest{

	@Test
	public void testAuthLocal() {
		JSONObject nameCheck = new JSONObject();
		nameCheck.put("name", "test123_localhost");
		System.out.println("testAuthLocal trying to send msg");

		MicroserviceUtil.doPost("http://localhost"+MicroserviceConstants.AUTH_PORT,nameCheck);
	}

//	@Test
//	public void testAuthDocker() {
//		JSONObject nameCheck = new JSONObject();
//		nameCheck.put("name", "test123_docker");
//		JSONObject responseJson = null;
//
//		System.out.println("testAuthDocker trying to send msg");
//
//		// docker 192.168.99.100
//
//		doPost("172.17.0.2", nameCheck);
//	}
	
	@Test
	public void testGoogleAuth() {

		try {
			// s = new Socket("smtp.gmail.com", 25);

			SSLSocket s = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory
					.getDefault()).createSocket(
							InetAddress.getByName("smtp.gmail.com"), 465);
			PrintWriter out = new PrintWriter(
					new OutputStreamWriter(s.getOutputStream()), true);
			BufferedReader in = new BufferedReader(
					new InputStreamReader(s.getInputStream()));
			System.out.println(in.readLine());

			System.out.println("HELO:");
			out.println("helo example.com");

			System.out.println(in.readLine());

			System.out.println("mail from:");
			out.println("mail from: <p00n187@gmail.com>");

			System.out.println(in.readLine());

			System.out.println("rcpt from:");
			out.println("rcpt to: <p00n187@gmail.com>");

			System.out.println(in.readLine());
			out.println("quit");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	

}
