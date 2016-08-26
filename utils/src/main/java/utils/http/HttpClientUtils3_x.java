package utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// org.apache.commons.httpclient是httpclient3.x版本
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
// httpclient 4.3.x
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.junit.Test;

public class HttpClientUtils3_x {

	@Test
	public void test_get_request() {
		HttpClient client = new HttpClient();
		// 需要添加http
		GetMethod getMethod = new GetMethod("http://www.baidu.com");
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		InputStream ins = null;
		try {
			int statusCode = client.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				ins = getMethod.getResponseBodyAsStream();
				byte[] b = new byte[1024];
				StringBuffer sb = new StringBuffer();
				int r_len = 0;
				while ((r_len = ins.read(b)) > 0) {
					sb.append(new String(b, 0, r_len,
							getMethod.getResponseCharSet()));
				}
				System.out.println(sb.toString());
			} else {
				System.err.println(getMethod.getStatusLine());
				System.err.println("Response Code: " + statusCode);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Test
	public void test_post_method() {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod("http://www.baidu.com/getValue");
		method.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=utf-8");
		NameValuePair[] param = new NameValuePair[]{
				new NameValuePair("age", "11"),
				new NameValuePair("name", "jay")};
		method.setRequestBody(param);
		try {
			int statusCode = client.executeMethod(method);
			System.out.println(statusCode);
			System.out.println("====================");
			System.out.println(method.getResponseBodyAsString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_https()
			throws NoSuchAlgorithmException, KeyManagementException, ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		SSLContext ctx = SSLContext.getInstance("SSL");
		X509TrustManager tm = new X509TrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
		};

		ctx.init(null, new TrustManager[]{tm}, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ClientConnectionManager ccm = client.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", 443, ssf));
		HttpGet httpget = new HttpGet("https://www.baidu.com");
		HttpParams params = client.getParams();

		params.setParameter("param1", "paramValue1");
		System.out.println("REQUEST:" + httpget.getURI());  
        ResponseHandler responseHandler = new BasicResponseHandler();  
        String responseBody;  

        responseBody = client.execute(httpget, responseHandler);  
        

        System.out.println(responseBody);  
	}
}
