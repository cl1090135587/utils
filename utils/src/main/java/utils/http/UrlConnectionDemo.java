package utils.http;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
public class UrlConnectionDemo {

	@Test
	public void test_url_connection() {
		String url = "http://php.weather.sina.com.cn/iframe/index/w_cl.php";
		Map<String, String> map = new HashMap<String, String>();
		map.put("code", "js");
		map.put("day", "0");
		map.put("city", "上海");
		map.put("dfc", "1");
		map.put("charset", "utf-8");
		send(url, map, "utf-8");
	}

	private void send(String urlStr, Map<String, String> map, String encoding) {
		String body = "";
		StringBuffer sbuf = new StringBuffer();

		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				sbuf.append(entry.getKey()).append("=").append(entry.getValue())
						.append("&");
			}
			if (sbuf.length() > 0) {
				sbuf.deleteCharAt(sbuf.length() - 1);
			}
		}
		byte[] postData = null;

		try {
			postData = sbuf.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 2、发送 HTTP(S) 请求
		OutputStream reqStream = null;
		InputStream resStream = null;
		URLConnection request = null;

		try {
			System.out.println("交易请求地址：" + urlStr);
			System.out.println("参数：" + sbuf.toString());
			URL url = null;
			Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP,
					new InetSocketAddress("127.0.0.1", 8087));
			url = new URL(urlStr);
			// 使用代理
			// request = url.openConnection(proxy);
			request = url.openConnection();
			request.setDoInput(true);
			request.setDoOutput(true);

			// B、指定报文头【Content-type】、【Content-length】 与 【Keep-alive】
			request.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			request.setRequestProperty("Content-length",
					String.valueOf(postData.length));
			request.setRequestProperty("Keep-alive", "false");
			request.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			// C、发送报文至服务器
			reqStream = request.getOutputStream();
			reqStream.write(postData);
			reqStream.close();

			// D、接收服务器返回结果
			ByteArrayOutputStream ms = null;
			resStream = request.getInputStream();
			ms = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int count;
			while ((count = resStream.read(buf, 0, buf.length)) > 0) {
				ms.write(buf, 0, count);
			}

			resStream.close();
			body = new String(ms.toByteArray(), encoding);
			System.out.println(body);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}