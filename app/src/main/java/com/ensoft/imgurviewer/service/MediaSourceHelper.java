package com.ensoft.imgurviewer.service;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.ensoft.imgurviewer.model.MediaType;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Predicate;
import com.google.android.exoplayer2.util.Util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MediaSourceHelper
{
	private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
	private static class TrustedHttpDataSource extends BaseDataSource implements HttpDataSource {
		public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 8 * 1000;
		public static final int DEFAULT_READ_TIMEOUT_MILLIS = 8 * 1000;
		private static final String TAG = "TrustedHttpDataSource";
		private static final int MAX_REDIRECTS = 20; // Same limit as okhttp.
		private static final int HTTP_STATUS_TEMPORARY_REDIRECT = 307;
		private static final int HTTP_STATUS_PERMANENT_REDIRECT = 308;
		private static final long MAX_BYTES_TO_DRAIN = 2048;
		private static final Pattern CONTENT_RANGE_HEADER =
			Pattern.compile("^bytes (\\d+)-(\\d+)/(\\d+)$");
		private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference<>();
		private final boolean allowCrossProtocolRedirects;
		private final int connectTimeoutMillis;
		private final int readTimeoutMillis;
		private final String userAgent;
		private final @Nullable Predicate<String> contentTypePredicate;
		private final @Nullable
		HttpDataSource.RequestProperties defaultRequestProperties;
		private final HttpDataSource.RequestProperties requestProperties;
		
		private @Nullable
		DataSpec dataSpec;
		private @Nullable
		HttpURLConnection connection;
		private @Nullable
		InputStream inputStream;
		private boolean opened;
		
		private long bytesToSkip;
		private long bytesToRead;
		
		private long bytesSkipped;
		private long bytesRead;
		
		public TrustedHttpDataSource( String userAgent, @Nullable Predicate<String> contentTypePredicate) {
			this(
				userAgent,
				contentTypePredicate,
				DEFAULT_CONNECT_TIMEOUT_MILLIS,
				DEFAULT_READ_TIMEOUT_MILLIS);
		}
		
		public TrustedHttpDataSource(
			String userAgent,
			@Nullable Predicate<String> contentTypePredicate,
			int connectTimeoutMillis,
			int readTimeoutMillis) {
			this(
				userAgent,
				contentTypePredicate,
				connectTimeoutMillis,
				readTimeoutMillis,
				/* allowCrossProtocolRedirects= */ false,
				/* defaultRequestProperties= */ null);
		}
		
		public TrustedHttpDataSource(
			String userAgent,
			@Nullable Predicate<String> contentTypePredicate,
			int connectTimeoutMillis,
			int readTimeoutMillis,
			boolean allowCrossProtocolRedirects,
			@Nullable HttpDataSource.RequestProperties defaultRequestProperties) {
			super(/* isNetwork= */ true);
			this.userAgent = Assertions.checkNotEmpty(userAgent);
			this.contentTypePredicate = contentTypePredicate;
			this.requestProperties = new HttpDataSource.RequestProperties();
			this.connectTimeoutMillis = connectTimeoutMillis;
			this.readTimeoutMillis = readTimeoutMillis;
			this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
			this.defaultRequestProperties = defaultRequestProperties;
		}
		
		@Deprecated
		@SuppressWarnings("deprecation")
		public TrustedHttpDataSource(
			String userAgent,
			@Nullable Predicate<String> contentTypePredicate,
			@Nullable TransferListener listener) {
			this(userAgent, contentTypePredicate, listener, DEFAULT_CONNECT_TIMEOUT_MILLIS,
				DEFAULT_READ_TIMEOUT_MILLIS);
		}
		
		@Deprecated
		@SuppressWarnings("deprecation")
		public TrustedHttpDataSource(
			String userAgent,
			@Nullable Predicate<String> contentTypePredicate,
			@Nullable TransferListener listener,
			int connectTimeoutMillis,
			int readTimeoutMillis) {
			this(userAgent, contentTypePredicate, listener, connectTimeoutMillis, readTimeoutMillis, false,
				null);
		}
		
		@Deprecated
		public TrustedHttpDataSource(
			String userAgent,
			@Nullable Predicate<String> contentTypePredicate,
			@Nullable TransferListener listener,
			int connectTimeoutMillis,
			int readTimeoutMillis,
			boolean allowCrossProtocolRedirects,
			@Nullable HttpDataSource.RequestProperties defaultRequestProperties) {
			this(
				userAgent,
				contentTypePredicate,
				connectTimeoutMillis,
				readTimeoutMillis,
				allowCrossProtocolRedirects,
				defaultRequestProperties);
			if (listener != null) {
				addTransferListener(listener);
			}
		}
		
		@Override
		public @Nullable Uri getUri() {
			return connection == null ? null : Uri.parse(connection.getURL().toString());
		}
		
		@Override
		public Map<String, List<String>> getResponseHeaders() {
			return connection == null ? Collections.emptyMap() : connection.getHeaderFields();
		}
		
		@Override
		public void setRequestProperty(String name, String value) {
			Assertions.checkNotNull(name);
			Assertions.checkNotNull(value);
			requestProperties.set(name, value);
		}
		
		@Override
		public void clearRequestProperty(String name) {
			Assertions.checkNotNull(name);
			requestProperties.remove(name);
		}
		
		@Override
		public void clearAllRequestProperties() {
			requestProperties.clear();
		}
		
		@Override
		public long open(DataSpec dataSpec) throws HttpDataSource.HttpDataSourceException
		{
			this.dataSpec = dataSpec;
			this.bytesRead = 0;
			this.bytesSkipped = 0;
			transferInitializing(dataSpec);
			try {
				connection = makeConnection(dataSpec);
			} catch ( IOException e) {
				throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e,
					dataSpec, HttpDataSource.HttpDataSourceException.TYPE_OPEN);
			}
			
			int responseCode;
			String responseMessage;
			try {
				responseCode = connection.getResponseCode();
				responseMessage = connection.getResponseMessage();
			} catch (IOException e) {
				closeConnectionQuietly();
				throw new HttpDataSource.HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e,
					dataSpec, HttpDataSource.HttpDataSourceException.TYPE_OPEN);
			}
			
			// Check for a valid response code.
			if (responseCode < 200 || responseCode > 299) {
				Map<String, List<String>> headers = connection.getHeaderFields();
				closeConnectionQuietly();
				HttpDataSource.InvalidResponseCodeException exception =
					new HttpDataSource.InvalidResponseCodeException(responseCode, responseMessage, headers, dataSpec);
				if (responseCode == 416) {
					exception.initCause(new DataSourceException(DataSourceException.POSITION_OUT_OF_RANGE));
				}
				throw exception;
			}
			
			// Check for a valid content type.
			String contentType = connection.getContentType();
			if (contentTypePredicate != null && !contentTypePredicate.evaluate(contentType)) {
				closeConnectionQuietly();
				throw new HttpDataSource.InvalidContentTypeException(contentType, dataSpec);
			}
			
			// If we requested a range starting from a non-zero position and received a 200 rather than a
			// 206, then the server does not support partial requests. We'll need to manually skip to the
			// requested position.
			bytesToSkip = responseCode == 200 && dataSpec.position != 0 ? dataSpec.position : 0;
			
			// Determine the length of the data to be read, after skipping.
			if (!dataSpec.isFlagSet(DataSpec.FLAG_ALLOW_GZIP)) {
				if (dataSpec.length != C.LENGTH_UNSET) {
					bytesToRead = dataSpec.length;
				} else {
					long contentLength = getContentLength(connection);
					bytesToRead = contentLength != C.LENGTH_UNSET ? (contentLength - bytesToSkip)
						: C.LENGTH_UNSET;
				}
			} else {
				// Gzip is enabled. If the server opts to use gzip then the content length in the response
				// will be that of the compressed data, which isn't what we want. Furthermore, there isn't a
				// reliable way to determine whether the gzip was used or not. Always use the dataSpec length
				// in this case.
				bytesToRead = dataSpec.length;
			}
			
			try {
				inputStream = connection.getInputStream();
			} catch (IOException e) {
				closeConnectionQuietly();
				throw new HttpDataSource.HttpDataSourceException(e, dataSpec, HttpDataSource.HttpDataSourceException.TYPE_OPEN);
			}
			
			opened = true;
			transferStarted(dataSpec);
			
			return bytesToRead;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int readLength) throws HttpDataSource.HttpDataSourceException
		{
			try {
				skipInternal();
				return readInternal(buffer, offset, readLength);
			} catch (IOException e) {
				throw new HttpDataSource.HttpDataSourceException(e, dataSpec, HttpDataSource.HttpDataSourceException.TYPE_READ);
			}
		}
		
		@Override
		public void close() throws HttpDataSource.HttpDataSourceException
		{
			try {
				if (inputStream != null) {
					maybeTerminateInputStream(connection, bytesRemaining());
					try {
						inputStream.close();
					} catch (IOException e) {
						throw new HttpDataSource.HttpDataSourceException(e, dataSpec, HttpDataSource.HttpDataSourceException.TYPE_CLOSE);
					}
				}
			} finally {
				inputStream = null;
				closeConnectionQuietly();
				if (opened) {
					opened = false;
					transferEnded();
				}
			}
		}
		
		protected final @Nullable HttpURLConnection getConnection() {
			return connection;
		}
		
		protected final long bytesSkipped() {
			return bytesSkipped;
		}
		
		protected final long bytesRead() {
			return bytesRead;
		}
		
		protected final long bytesRemaining() {
			return bytesToRead == C.LENGTH_UNSET ? bytesToRead : bytesToRead - bytesRead;
		}
		
		private HttpURLConnection makeConnection(DataSpec dataSpec) throws IOException {
			URL url = new URL(dataSpec.uri.toString());
			@DataSpec.HttpMethod int httpMethod = dataSpec.httpMethod;
			byte[] httpBody = dataSpec.httpBody;
			long position = dataSpec.position;
			long length = dataSpec.length;
			boolean allowGzip = dataSpec.isFlagSet(DataSpec.FLAG_ALLOW_GZIP);
			
			if (!allowCrossProtocolRedirects) {
				// HttpURLConnection disallows cross-protocol redirects, but otherwise performs redirection
				// automatically. This is the behavior we want, so use it.
				return makeConnection(
					url, httpMethod, httpBody, position, length, allowGzip, true /* followRedirects */);
			}
			
			// We need to handle redirects ourselves to allow cross-protocol redirects.
			int redirectCount = 0;
			while (redirectCount++ <= MAX_REDIRECTS) {
				HttpURLConnection connection =
					makeConnection(
						url, httpMethod, httpBody, position, length, allowGzip, false /* followRedirects */);
				int responseCode = connection.getResponseCode();
				String location = connection.getHeaderField("Location");
				if ((httpMethod == DataSpec.HTTP_METHOD_GET || httpMethod == DataSpec.HTTP_METHOD_HEAD)
					&& (responseCode == HttpURLConnection.HTTP_MULT_CHOICE
					|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_MOVED_TEMP
					|| responseCode == HttpURLConnection.HTTP_SEE_OTHER
					|| responseCode == HTTP_STATUS_TEMPORARY_REDIRECT
					|| responseCode == HTTP_STATUS_PERMANENT_REDIRECT)) {
					connection.disconnect();
					url = handleRedirect(url, location);
				} else if (httpMethod == DataSpec.HTTP_METHOD_POST
					&& (responseCode == HttpURLConnection.HTTP_MULT_CHOICE
					|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
					|| responseCode == HttpURLConnection.HTTP_MOVED_TEMP
					|| responseCode == HttpURLConnection.HTTP_SEE_OTHER)) {
					// POST request follows the redirect and is transformed into a GET request.
					connection.disconnect();
					httpMethod = DataSpec.HTTP_METHOD_GET;
					httpBody = null;
					url = handleRedirect(url, location);
				} else {
					return connection;
				}
			}
			
			// If we get here we've been redirected more times than are permitted.
			throw new NoRouteToHostException("Too many redirects: " + redirectCount);
		}
		
		static TrustManager[] getTrustAllCertsManager()
		{
			return new TrustManager[] {
				new X509TrustManager()
				{
					public X509Certificate[] getAcceptedIssuers()
					{
						return null;
					}
					
					@Override
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					
					@Override
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
			};
		}
		
		protected static void setTrustAllCerts( HttpsURLConnection httpsURLConnection )
		{
			try
			{
				SSLContext sc = SSLContext.getInstance( "TLS" );
				sc.init( null, getTrustAllCertsManager(), new SecureRandom() );
				httpsURLConnection.setSSLSocketFactory( sc.getSocketFactory() );
				httpsURLConnection.setHostnameVerifier( ( hostname, session ) -> true );
			}
			catch( Exception exception )
			{
				android.util.Log.e( TAG, exception.toString() );
			}
		}
		
		private HttpURLConnection makeConnection(
			URL url,
			@DataSpec.HttpMethod int httpMethod,
			byte[] httpBody,
			long position,
			long length,
			boolean allowGzip,
			boolean followRedirects)
			throws IOException {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			setTrustAllCerts( (HttpsURLConnection)connection );
			connection.setConnectTimeout(connectTimeoutMillis);
			connection.setReadTimeout(readTimeoutMillis);
			if (defaultRequestProperties != null) {
				for (Map.Entry<String, String> property : defaultRequestProperties.getSnapshot().entrySet()) {
					connection.setRequestProperty(property.getKey(), property.getValue());
				}
			}
			for (Map.Entry<String, String> property : requestProperties.getSnapshot().entrySet()) {
				connection.setRequestProperty(property.getKey(), property.getValue());
			}
			if (!(position == 0 && length == C.LENGTH_UNSET)) {
				String rangeRequest = "bytes=" + position + "-";
				if (length != C.LENGTH_UNSET) {
					rangeRequest += (position + length - 1);
				}
				connection.setRequestProperty("Range", rangeRequest);
			}
			connection.setRequestProperty("User-Agent", userAgent);
			if (!allowGzip) {
				connection.setRequestProperty("Accept-Encoding", "identity");
			}
			connection.setInstanceFollowRedirects(followRedirects);
			connection.setDoOutput(httpBody != null);
			connection.setRequestMethod(DataSpec.getStringForHttpMethod(httpMethod));
			if (httpBody != null) {
				connection.setFixedLengthStreamingMode(httpBody.length);
				connection.connect();
				OutputStream os = connection.getOutputStream();
				os.write(httpBody);
				os.close();
			} else {
				connection.connect();
			}
			return connection;
		}
		
		private static URL handleRedirect(URL originalUrl, String location) throws IOException {
			if (location == null) {
				throw new ProtocolException("Null location redirect");
			}
			// Form the new url.
			URL url = new URL(originalUrl, location);
			// Check that the protocol of the new url is supported.
			String protocol = url.getProtocol();
			if (!"https".equals(protocol) && !"http".equals(protocol)) {
				throw new ProtocolException("Unsupported protocol redirect: " + protocol);
			}
			// Currently this method is only called if allowCrossProtocolRedirects is true, and so the code
			// below isn't required. If we ever decide to handle redirects ourselves when cross-protocol
			// redirects are disabled, we'll need to uncomment this block of code.
			// if (!allowCrossProtocolRedirects && !protocol.equals(originalUrl.getProtocol())) {
			//   throw new ProtocolException("Disallowed cross-protocol redirect ("
			//       + originalUrl.getProtocol() + " to " + protocol + ")");
			// }
			return url;
		}
		
		private static long getContentLength(HttpURLConnection connection) {
			long contentLength = C.LENGTH_UNSET;
			String contentLengthHeader = connection.getHeaderField("Content-Length");
			if (!TextUtils.isEmpty(contentLengthHeader)) {
				try {
					contentLength = Long.parseLong(contentLengthHeader);
				} catch (NumberFormatException e) {
					Log.e(TAG, "Unexpected Content-Length [" + contentLengthHeader + "]");
				}
			}
			String contentRangeHeader = connection.getHeaderField("Content-Range");
			if (!TextUtils.isEmpty(contentRangeHeader)) {
				Matcher matcher = CONTENT_RANGE_HEADER.matcher(contentRangeHeader);
				if (matcher.find()) {
					try {
						long contentLengthFromRange =
							Long.parseLong(matcher.group(2)) - Long.parseLong(matcher.group(1)) + 1;
						if (contentLength < 0) {
							// Some proxy servers strip the Content-Length header. Fall back to the length
							// calculated here in this case.
							contentLength = contentLengthFromRange;
						} else if (contentLength != contentLengthFromRange) {
							// If there is a discrepancy between the Content-Length and Content-Range headers,
							// assume the one with the larger value is correct. We have seen cases where carrier
							// change one of them to reduce the size of a request, but it is unlikely anybody would
							// increase it.
							Log.w(TAG, "Inconsistent headers [" + contentLengthHeader + "] [" + contentRangeHeader
								+ "]");
							contentLength = Math.max(contentLength, contentLengthFromRange);
						}
					} catch (NumberFormatException e) {
						Log.e(TAG, "Unexpected Content-Range [" + contentRangeHeader + "]");
					}
				}
			}
			return contentLength;
		}
		
		private void skipInternal() throws IOException {
			if (bytesSkipped == bytesToSkip) {
				return;
			}
			
			// Acquire the shared skip buffer.
			byte[] skipBuffer = skipBufferReference.getAndSet(null);
			if (skipBuffer == null) {
				skipBuffer = new byte[4096];
			}
			
			while (bytesSkipped != bytesToSkip) {
				int readLength = (int) Math.min(bytesToSkip - bytesSkipped, skipBuffer.length);
				int read = inputStream.read(skipBuffer, 0, readLength);
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedIOException();
				}
				if (read == -1) {
					throw new EOFException();
				}
				bytesSkipped += read;
				bytesTransferred(read);
			}
			
			// Release the shared skip buffer.
			skipBufferReference.set(skipBuffer);
		}
		
		private int readInternal(byte[] buffer, int offset, int readLength) throws IOException {
			if (readLength == 0) {
				return 0;
			}
			if (bytesToRead != C.LENGTH_UNSET) {
				long bytesRemaining = bytesToRead - bytesRead;
				if (bytesRemaining == 0) {
					return C.RESULT_END_OF_INPUT;
				}
				readLength = (int) Math.min(readLength, bytesRemaining);
			}
			
			int read = inputStream.read(buffer, offset, readLength);
			if (read == -1) {
				if (bytesToRead != C.LENGTH_UNSET) {
					// End of stream reached having not read sufficient data.
					throw new EOFException();
				}
				return C.RESULT_END_OF_INPUT;
			}
			
			bytesRead += read;
			bytesTransferred(read);
			return read;
		}
		
		private static void maybeTerminateInputStream(HttpURLConnection connection, long bytesRemaining) {
			if ( Util.SDK_INT != 19 && Util.SDK_INT != 20) {
				return;
			}
			
			try {
				InputStream inputStream = connection.getInputStream();
				if (bytesRemaining == C.LENGTH_UNSET) {
					// If the input stream has already ended, do nothing. The socket may be re-used.
					if (inputStream.read() == -1) {
						return;
					}
				} else if (bytesRemaining <= MAX_BYTES_TO_DRAIN) {
					// There isn't much data left. Prefer to allow it to drain, which may allow the socket to be
					// re-used.
					return;
				}
				String className = inputStream.getClass().getName();
				if ("com.android.okhttp.internal.http.HttpTransport$ChunkedInputStream".equals(className)
					|| "com.android.okhttp.internal.http.HttpTransport$FixedLengthInputStream"
					.equals(className)) {
					Class<?> superclass = inputStream.getClass().getSuperclass();
					Method unexpectedEndOfInput = superclass.getDeclaredMethod("unexpectedEndOfInput");
					unexpectedEndOfInput.setAccessible(true);
					unexpectedEndOfInput.invoke(inputStream);
				}
			} catch (Exception e) {
				// If an IOException then the connection didn't ever have an input stream, or it was closed
				// already. If another type of exception then something went wrong, most likely the device
				// isn't using okhttp.
			}
		}
		
		private void closeConnectionQuietly() {
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {
					Log.e(TAG, "Unexpected error while disconnecting", e);
				}
				connection = null;
			}
		}
		
	}
	
	private static class AppHttpDataSourceFactory extends HttpDataSource.BaseFactory
	{
		private final String userAgent;
		private final @Nullable TransferListener listener;
		private final int connectTimeoutMillis;
		private final int readTimeoutMillis;
		private final boolean allowCrossProtocolRedirects;
		
		public AppHttpDataSourceFactory( String userAgent) {
			this(userAgent, null);
		}
		
		public AppHttpDataSourceFactory( String userAgent, @Nullable TransferListener listener) {
			this(userAgent, listener, TrustedHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
				TrustedHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, false);
		}
		
		public AppHttpDataSourceFactory(
			String userAgent,
			int connectTimeoutMillis,
			int readTimeoutMillis,
			boolean allowCrossProtocolRedirects) {
			this(
				userAgent,
				/* listener= */ null,
				connectTimeoutMillis,
				readTimeoutMillis,
				allowCrossProtocolRedirects);
		}
		
		public AppHttpDataSourceFactory(
			String userAgent,
			@Nullable TransferListener listener,
			int connectTimeoutMillis,
			int readTimeoutMillis,
			boolean allowCrossProtocolRedirects) {
			this.userAgent = userAgent;
			this.listener = listener;
			this.connectTimeoutMillis = connectTimeoutMillis;
			this.readTimeoutMillis = readTimeoutMillis;
			this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
		}
		
		@Override
		protected HttpDataSource createDataSourceInternal(
			HttpDataSource.RequestProperties defaultRequestProperties) {
			HttpDataSource dataSource =
				new TrustedHttpDataSource(
					userAgent,
					/* contentTypePredicate= */ null,
					connectTimeoutMillis,
					readTimeoutMillis,
					allowCrossProtocolRedirects,
					defaultRequestProperties);
			if (listener != null) {
				dataSource.addTransferListener(listener);
			}
			return dataSource;
		}
	}
	private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0 (Chrome)";
	private static final HttpDataSource.BaseFactory TRUSTED_DATA_SOURCE_FACTORY = new AppHttpDataSourceFactory( USER_AGENT, BANDWIDTH_METER );
	private static final DefaultHttpDataSourceFactory DATA_SOURCE_FACTORY = new DefaultHttpDataSourceFactory( USER_AGENT, BANDWIDTH_METER );
	
	private static boolean trustedDomain( Uri uri )
	{
		if ( uri.getHost() != null )
		{
			return uri.getHost().endsWith( "cdnja.co" );
		}
		
		return false;
	}
	
	public static MediaSource buildMediaSource( Context context, Uri uri, MediaType mediaType )
	{
		HttpDataSource.BaseFactory baseFactory = trustedDomain( uri ) ? TRUSTED_DATA_SOURCE_FACTORY : DATA_SOURCE_FACTORY;
		if ( MediaType.STREAM_SS == mediaType )
			return new SsMediaSource.Factory( new DefaultSsChunkSource.Factory( baseFactory ), baseFactory ).createMediaSource(uri);
		
		if ( MediaType.STREAM_DASH == mediaType )
			return new DashMediaSource.Factory( new DefaultDashChunkSource.Factory( baseFactory ), baseFactory ).createMediaSource(uri);
		
		if ( MediaType.STREAM_HLS == mediaType )
			return new HlsMediaSource.Factory( baseFactory ).createMediaSource(uri);
		
		if ( UriUtils.isFileUri( uri ) )
			return new ExtractorMediaSource.Factory( new DefaultDataSourceFactory( context, "ua" ) ).setExtractorsFactory( new DefaultExtractorsFactory() ).createMediaSource( uri );
		
		return new ExtractorMediaSource.Factory( baseFactory ).createMediaSource(uri);
	}
	
	public static MediaSource buildLoopingMediaSource( Context context, Uri uri, MediaType mediaType )
	{
		return new LoopingMediaSource( MediaSourceHelper.buildMediaSource( context, uri, mediaType ) );
	}
}
