package com.example.administrator.monitorcamera;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class MMSSender {
	private static final String TAG = "MMSSender";
	public static String mmscUrl = "http://mmsc.vnet.mobi";
	public static String mmsProxy = "10.0.0.200";
	public static int mmsProt = 80;

	private static String HDR_VALUE_ACCEPT_LANGUAGE = "";
	private static final String HDR_KEY_ACCEPT = "Accept";
	private static final String HDR_KEY_ACCEPT_LANGUAGE = "Accept-Language";
	private static final String HDR_VALUE_ACCEPT = "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic";

	public byte[] sendMMS(Context context, byte[] pdu)
			throws IOException {
		System.out.println("进入sendMMS方法");
		// HDR_VALUE_ACCEPT_LANGUAGE = getHttpAcceptLanguage();
		HDR_VALUE_ACCEPT_LANGUAGE = HTTP.UTF_8;
		if (mmscUrl == null) {
			throw new IllegalArgumentException("URL must not be null.");
		}

		HttpClient client = null;

		try {
			// Make sure to use a proxy which supports CONNECT.
			// client = HttpConnector.buileClient(context);

			HttpHost httpHost = new HttpHost(mmsProxy, mmsProt);
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);

			client = new DefaultHttpClient(httpParams);

			HttpPost post = new HttpPost(mmscUrl);
			// mms PUD START
			ByteArrayEntity entity = new ByteArrayEntity(pdu);
			entity.setContentType("application/vnd.wap.mms-message");
			post.setEntity(entity);
			post.addHeader(HDR_KEY_ACCEPT, HDR_VALUE_ACCEPT);
			post.addHeader(HDR_KEY_ACCEPT_LANGUAGE, HDR_VALUE_ACCEPT_LANGUAGE);
			post.addHeader(
					"user-agent",
					"Mozilla/5.0(Linux;U;Android 2.1-update1;zh-cn;ZTE-C_N600/ZTE-C_N600V1.0.0B02;240*320;CTC/2.0)AppleWebkit/530.17(KHTML,like Gecko) Version/4.0 Mobile Safari/530.17");
			// mms PUD END
			HttpParams params = client.getParams();
			HttpProtocolParams.setContentCharset(params, "UTF-8");

			System.out.println("准备执行发送");

			// PlainSocketFactory localPlainSocketFactory =
			// PlainSocketFactory.getSocketFactory();

			HttpResponse response = client.execute(post);

			System.out.println("执行发送结束， 等回执。。");

			StatusLine status = response.getStatusLine();
			Log.d(TAG, "status " + status.getStatusCode());
			if (status.getStatusCode() != 200) { // HTTP 200 表服务器成功返回网页
				Log.d(TAG, "!200");
				throw new IOException("HTTP error: " + status.getReasonPhrase());
			}
			HttpEntity resentity = response.getEntity();
			byte[] body = null;
			if (resentity != null) {
				try {
					if (resentity.getContentLength() > 0) {
						body = new byte[(int) resentity.getContentLength()];
						DataInputStream dis = new DataInputStream(
								resentity.getContent());
						try {
							dis.readFully(body);
						} finally {
							try {
								dis.close();
							} catch (IOException e) {
								Log.e(TAG,
										"Error closing input stream: "
												+ e.getMessage());
							}
						}
					}
				} finally {
					if (entity != null) {
						entity.consumeContent();
					}
				}
			}
			Log.d(TAG, "result:" + new String(body));

			System.out.println("成功！！" + new String(body));

			return body;
		} catch (IllegalStateException e) {
			Log.e(TAG, "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (SocketException e) {
			Log.e(TAG, "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} catch (Exception e) {
			Log.e(TAG, "", e);
			// handleHttpConnectionException(e, mmscUrl);
		} finally {
			if (client != null) {
				// client.;
			}
		}
		return new byte[0];
	}

	private int CountMoneyCMWAPNEWWAY() {

		String strHead = "";
		try {
			// if(!GameLet._self.isNetworkCMWAPAvailable())
			// {
			// GameLet._self.ActiveNetWorkByMode("wap");
			// Thread.sleep(5000);
			// }

			String urlstr = "http://mmsc.vnet.mobi";

			int splashIndex = urlstr.indexOf("/", 7);

			String hosturl = urlstr.substring(7, splashIndex);
			String hostfile = urlstr.substring(splashIndex);

			HttpHost proxy = new HttpHost("10.0.0.200", 80, "http");
			HttpHost target = new HttpHost(hosturl, 80, "http");

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			HttpClientParams.setRedirecting(httpParams, true);

			// String userAgent = AndroidPlatform.getUAFromProperties();
			// ProductProperties.get(ProductPropertiws.USER_AGENT_KEY,null);
			//
			// HttpProtocolParams.setUserAgent(httpParams, userAgent);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);

			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			HttpGet req = new HttpGet(hostfile);

			HttpResponse rsp = httpclient.execute(target, req);

			HttpEntity entity = rsp.getEntity();

			InputStream inputstream = entity.getContent();
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			byte abyte1[] = new byte[1024];
			for (int k = 0; -1 != (k = inputstream.read(abyte1));)
				bytearrayoutputstream.write(abyte1, 0, k);

			strHead = new String(bytearrayoutputstream.toByteArray(), "UTF-8");

			httpclient.getConnectionManager().shutdown();

		} catch (Exception e) {
			return 2;
		}

		if (strHead.indexOf("status=1301") > -1
				|| strHead.indexOf("status=1300") > -1) {
			return 1;
		} else {
			return 0;
		}
	}

}
