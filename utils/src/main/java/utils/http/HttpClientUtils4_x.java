package utils.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClientUtils4_x {

	@Test
	public void test_get() throws Exception {
		String url = "http://php.weather.sina.com.cn/iframe/index/w_cl.php";
		Map<String, String> map = new HashMap<String, String>();
		map.put("code", "js");
		map.put("day", "0");
		map.put("city", "上海");
		map.put("dfc", "1");
		map.put("charset", "utf-8");
		String body = send(url, map, "utf-8");
		System.out.println("交易响应结果：");
		System.out.println(body);
	}

	// 原来如果网站的证书已经被ca机构认证通过了，那么用HttpClient来调用的话，会直接成功的。
	// 不用再单独配置htts链接了。不过如果是自生成的证书，还是需要配置https的，下篇就来配置一下吧，敬请期待。
	@Test
	public void test_https() throws ParseException, IOException {
		String url = "https://www.qingyidai.com/investmanagement/invest.shtml";
		String body = send(url, null, "utf-8");
		System.out.println("交易响应结果：");
		System.out.println(body);
	}

	private String send(String url, Map<String, String> map, String encoding)
			throws ParseException, IOException {
		// 创建httpclient对象
		CloseableHttpClient client = HttpClients.createDefault();
		// 创建post方式请求对象
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(),
						entry.getValue()));
			}
		}

		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
		System.out.println("请求地址：" + url);
		System.out.println("请求参数：" + nvps.toString());

		// 设置header信息
		// 指定报文头【Content-type】、【User-Agent】
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
		httpPost.setHeader("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		// 执行请求操作，并拿到结果（同步阻塞）
		CloseableHttpResponse response = client.execute(httpPost);
		// 获取结果实体
		HttpEntity entity = response.getEntity();
		Header[] allHeaders = response.getAllHeaders();
		System.out.println("response head:\n" + Arrays.toString(allHeaders));
		String body = "";
		byte[] byteArray = new byte[0];
		if (entity != null) {
			// 按指定编码转换结果实体为String类型
			// body = EntityUtils.toString(entity, encoding);
			byteArray = EntityUtils.toByteArray(entity);
		}

		body = new String(byteArray, encoding);
		System.out.println("response body:\n" + body);
		EntityUtils.consume(entity);
		// 释放链接
		response.close();
		return body;
	}
}
