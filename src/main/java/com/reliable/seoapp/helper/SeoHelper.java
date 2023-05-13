package com.reliable.seoapp.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.reliable.seoapp.commans.Constants;
import com.reliable.seoapp.view.CrawleDataView;
import com.reliable.seoapp.view.CrawleRequetView;

@Component
public class SeoHelper {

	static List<String> traverseList = new ArrayList<>();

	private static String rootDir;

	@Autowired
	public SeoHelper(@Value("${cloud.files.path}") String rootDir) {
		this.rootDir = rootDir;
		System.out.println(rootDir);
	}

	public static void beginCrawle(CrawleRequetView requetView) {
		byte[] response = null;
		try {
			crawleUrl(requetView.getUrlName(), requetView.getTagName());
			ZipCompress.compress(rootDir);
		} catch (Exception exp) {
			exp.printStackTrace();
		}

	}

	private static void crawleUrl(String url1, String tag) throws Exception {
		trustAllHosts();
		if (!traverseList.contains(url1)) {
			System.out.println(url1);
			traverseList.add(url1);
			CrawleDataView crawleDataView = getData(url1);
			writeDataToFile(crawleDataView.getDataArray(), url1, tag);
			// data = data.replaceAll("\\<(.*?)\\>","");
			crawleDataView.getDataList().forEach(data -> {
				String[] data1 = data.split("\"");
				for (String str : data1) {
					if ((str.startsWith("http") || str.startsWith("'/'")) && str.contains(tag)) {
						try {
							crawleUrl(str, tag);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			});
		}
	}

	private static CrawleDataView getData(String url1) throws Exception {
		trustAllHosts();
		URL url = new URL(url1);
		URLConnection conn = url.openConnection();
		URL url2 = new URL(url1);
		URLConnection conn1 = url.openConnection();

		conn.setRequestProperty("User-Agent", "Mozilla/5.0");
		conn1.setRequestProperty("User-Agent", "Mozilla/5.0");

		List<String> list = new ArrayList<>();

		Scanner scanner = new Scanner(conn.getInputStream());
		StringBuilder br = new StringBuilder();
		String dat;
		while (scanner.hasNext()) {
			dat = scanner.nextLine();
			if (dat.contains("href") && dat.contains("http")) {
				list.add(dat);
			}
			br.append(dat);
		}

		CrawleDataView crawleDataView = new CrawleDataView(br, list, getBytesFromInputStream(conn1.getInputStream()));

		return crawleDataView;
	}

	private static void writeDataToFile(byte[] data, String fileName, String tag) throws IOException {
		trustAllHosts();
		StringBuilder stringBuilder = new StringBuilder();
		File file;
		String folderName = fileName.split("\\.")[1];
		fileName = fileName.replace(tag, "");
		if (fileName.equals("")) {
			fileName = rootDir + "/" + folderName + "/index.html";
			file = new File(fileName);
		} else {
			file = new File(rootDir + "/" + folderName + "/" + fileName);
		}
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(data);

	}

	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[0xFFFF];
		for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
			os.write(buffer, 0, len);
		}
		return os.toByteArray();
	}

	public static void rightMessageTofile(String name, String contactNo, String emailId, String message) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File("/home/ec2-user/messages.txt"), true);
			PrintWriter printWriter = new PrintWriter(fileOutputStream, true);
			printWriter.println(getEcodedData(name, contactNo, emailId, message));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getEcodedData(String name, String contactNo, String emailId, String message) {
		StringBuilder builder = new StringBuilder();

		appendChar(name.toCharArray(), builder);
		builder.append(" ");
		appendChar(contactNo.toCharArray(), builder);
		builder.append(" ");
		appendChar(emailId.toCharArray(), builder);
		builder.append(" ");
		appendChar(message.toCharArray(), builder);

		return builder.toString();
	}

	private static void appendChar(char[] array, StringBuilder builder) {
		for (char c : array) {
			int index = c;
			builder.append(String.format("%s", index));
			builder.append(" ");
		}
	}

	private static void trustAllHosts() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) {

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) {

				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string,
						SSLEngine ssle) {

				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string,
						SSLEngine ssle) {

				}

			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
