package org.snripa.netty;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

/**
 * Created by sergeyr on 04.08.16.
 */
public class NettyEchoTest {

	public static final int PORT = 8888;
	NettyEchoServer server;

	@Before
	public void setUp() throws Exception {
		server = new NettyEchoServer(PORT);
		server.start();
	}

	@Test
	public void testEcho() throws Exception {
		CloseableHttpClient httpClient = HttpClients.createMinimal();
		String echoRequest = "hello";
		URI uri = new URIBuilder().setScheme("http").setHost("localhost").setPort(PORT).setPath("/" + echoRequest).build();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse response = httpClient.execute(httpGet);
		String echoResponse = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("Request and response are not equal", echoRequest, echoResponse);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

}
