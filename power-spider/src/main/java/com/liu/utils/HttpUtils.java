package com.liu.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by liucheng on 2015/3/31.
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.151 Safari/535.19";

    public static final int TIMEOUT = 1000 * 30;

    private static PoolingHttpClientConnectionManager connManager = null;

    private static CloseableHttpClient httpClient = null;

    public static final long _1M = 1024 * 1024l;

    static {

        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().build();
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } }, null);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext)).build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200)
                    .setMaxLineLength(2000).build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setMessageConstraints(messageConstraints)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(512);
            connManager.setDefaultMaxPerRoute(150);

            httpClient = HttpClients.custom().setUserAgent(USER_AGENT).setConnectionManager(connManager)
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).setRetryHandler(new RetryHandler())
                    .build();
        } catch (KeyManagementException e) {
            logger.error("httpUtils exception:", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("httpUtils exception:", e);
        }

    }

    public static String post(String url, String referer, Map<String, String> data, String encoding, int socketTimeout,
            int connectTimeout) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpPost httpPost = new HttpPost(url);
        if (!StringUtils.isBlank(referer))
            httpPost.setHeader("referer", referer);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, encoding));
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        result = EntityUtils.toString(entity, encoding);
                    }
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpPost.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String postByType(String url, String data, String referer, String contentType, String encoding,
            int socketTimeout, int connectTimeout) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpPost httpPost = new HttpPost(url);
        if (!StringUtils.isBlank(referer))
            httpPost.setHeader("referer", referer);
        httpPost.setHeader("Content-Type", contentType);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            StringEntity params = new StringEntity(data, Charset.forName(encoding));
            httpPost.setEntity(params);
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                try {
                    if (entity != null) {
                        result = EntityUtils.toString(entity, encoding);
                    }
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpPost.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String post(String url) {
        return post(url, null, null, "UTF-8", TIMEOUT, TIMEOUT);
    }

    public static String post(String url, Map<String, String> data) {
        return post(url, null, data, "UTF-8", TIMEOUT, TIMEOUT);
    }

    public static String postByStr(String url, String data, String contentType) {
        return postByType(url, data, null, contentType, "UTF-8", TIMEOUT, TIMEOUT);
    }
    
    public static String postByStr(String url, String data,String referer, String contentType) {
        return postByType(url, data, referer, contentType, "UTF-8", TIMEOUT, TIMEOUT);
    }

    public static String post(String url, String referer, Map<String, String> data) {
        return post(url, referer, data, "UTF-8", TIMEOUT, TIMEOUT);
    }
    
    /**
     *  爬取[http://www.yxdown.com/az/game/sheji/]出现[HttpClient] java.io.EOFException
     *  基本确定是由于请求默认返回gzip压缩过的内容, 而HttpClient的GZIPInputStream在处理流的时候意外到达末尾.
     *  Solution:尝试添加请求头 “Accept-Encoding: /“ 改变返回内容压缩方式
     * @param url
     * @param encoding
     * @param socketTimeout
     * @param connectTimeout
     * @return
     */
    public static String getByEncoding(String url, String encoding, int socketTimeout, int connectTimeout) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Accept-Encoding", "/");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // int status = response.getStatusLine().getStatusCode();
            // if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            try {
                if (entity != null) {
                    result = EntityUtils.toString(entity, encoding);
                }
            } finally {
                EntityUtils.consumeQuietly(entity);
            }
            // }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String get(String url, String encoding, int socketTimeout, int connectTimeout) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // int status = response.getStatusLine().getStatusCode();
            // if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            try {
                if (entity != null) {
                    result = EntityUtils.toString(entity, encoding);
                }
            } finally {
                EntityUtils.consumeQuietly(entity);
            }
            // }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }
    
    public static CookieStore getCookies(String url, String encoding, int socketTimeout, int connectTimeout) {
        CookieStore  result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        HttpClientContext httpClientContext = new HttpClientContext();
        try {
            response = httpClient.execute(httpGet,httpClientContext);
            CookieStore cooikeStore = httpClientContext.getCookieStore();
            result = cooikeStore;
        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String getByCooike(String url, String encoding, int socketTimeout, int connectTimeout, String cooike) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("referer", "https://android.byfen.com/app/10505");
        httpGet.setHeader("Cookie", cooike);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // int status = response.getStatusLine().getStatusCode();
            // if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            try {
                if (entity != null) {
                    result = EntityUtils.toString(entity, encoding);
                }
            } finally {
                EntityUtils.consumeQuietly(entity);
            }
            // }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String getDefaultEncoding(String url) {
        String result = null;

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                try {
                    if (entity != null) {
                        result = EntityUtils.toString(entity, charset);
                    }
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }
    
    public static CookieStore getCooikes(String url) {
        return getCookies(url, "UTF-8", TIMEOUT, TIMEOUT);
    }

    public static String get(String url, String encoding) {
        return get(url, encoding, TIMEOUT, TIMEOUT);
    }

    public static String get(String url) {
        return get(url, "UTF-8", TIMEOUT, TIMEOUT);
    }
    
    public static String getByEncoding(String url){
        return getByEncoding(url, "UTF-8", TIMEOUT, TIMEOUT);
    }
    
    public static String getByCooike(String url, String cooike) {
        return getByCooike(url, "UTF-8", TIMEOUT, TIMEOUT, cooike);
    }

    public static String getByCooike(String url, String encoding, String cooike) {
        return getByCooike(url, encoding, TIMEOUT, TIMEOUT, cooike);
    }

    // proxy ip
    public static String get(String url, String encoding, String ip, int port, int socketTimeout, int connectTimeout)
            throws ClientProtocolException, IOException {
        String result = null;

        HttpHost proxy = new HttpHost(ip, port);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).setProxy(proxy).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            // int status = response.getStatusLine().getStatusCode();
            // if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            try {
                if (entity != null) {
                    result = EntityUtils.toString(entity, encoding);
                }
            } finally {
                EntityUtils.consumeQuietly(entity);
            }
            // }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return result;
    }

    public static String get(String url, String ip, int port) throws ClientProtocolException, IOException {
        return get(url, "UTF-8", ip, port, TIMEOUT, TIMEOUT);
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * 获取重定向之后的网址信息
     * 
     * @see   HttpClient缺省会自动处理客户端重定向
     * @see 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
     * @see 若想取得B网页的地址
     *      ,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
     * @see 我们自己也可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
     */
    public static String getRedirectInfo(String url) {
        HttpContext httpContext = new BasicHttpContext();
        String result = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            // 将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
            httpClient.execute(httpGet, httpContext);
            // 获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
            HttpHost targetHost = (HttpHost) httpContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            // 获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
            HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);

            result = targetHost.toString() + realRequest.getURI();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }

        return result;
    }
    
    /**
     * 获取重定向之后的网址信息
     * 
     * @see HttpClient缺省会自动处理客户端重定向
     * @see 即访问网页A后,假设被重定向到了B网页,那么HttpClient将自动返回B网页的内容
     * @see 若想取得B网页的地址
     *      ,就需要借助HttpContext对象,HttpContext实际上是客户端用来在多次请求响应的交互中,保持状态信息的
     * @see 我们自己也可以利用HttpContext来存放一些我们需要的信息,以便下次请求的时候能够取出这些信息来使用
     */
    public static String getRedirectInfo(String url,String referer,CookieStore cookieStore) {
        HttpContext httpContext = new BasicHttpContext();
        String result = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            
            if(!StringUtils.isBlank(referer))
                httpGet.setHeader("referer",referer);
            if(null != cookieStore)
                httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
            // 将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
            httpClient.execute(httpGet, httpContext);
            // 获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
            HttpHost targetHost = (HttpHost) httpContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
            // 获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
            HttpUriRequest realRequest = (HttpUriRequest) httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST);

            result = targetHost.toString() + realRequest.getURI();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpGet.releaseConnection();
        }

        return result;
    }

    public static long multipleDownloadFile(String url, File file, int socketTimeout, int connectTimeout) {
        try {
            final long contentLength = getFileContentLength(url);
            if (contentLength == 0)
                return 0;
            file.createNewFile();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.setLength(contentLength);
            raf.close();

            List<Range> ranges = generateRanges(url, contentLength);
            ExecutorService executor = Executors.newFixedThreadPool(ranges.size());
            for (Range range : ranges) {
                executor.submit(new DownloadRange(file, range, socketTimeout, connectTimeout, url, httpClient));
            }
            executor.shutdown();

            while (!executor.isTerminated()) {
            }

            return contentLength;
        } catch (IOException e) {
            logger.error("多线程下载异常", e);
            return 0;
        }
    }

    private static List<Range> generateRanges(String url, long contentLength) {
        List<Range> ranges = new ArrayList<Range>();
        HttpHead httpHead = null;
        CloseableHttpResponse response = null;
        try {
            httpHead = new HttpHead(url);
            httpHead.addHeader("Range", "bytes=0-" + (contentLength - 1));
            response = httpClient.execute(httpHead);

            if (response.getStatusLine().getStatusCode() == 206) {
                logger.debug(String.format("url:%s support multiple thread download.", url));
                ranges = calcRanges(ranges, contentLength);
            } else {
                ranges.add(new Range(0, contentLength - 1));
            }
        } catch (IOException e) {
            ranges.add(new Range(0, contentLength - 1));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {

            }

        }
        httpHead.abort();
        return ranges;
    }

    private static List<Range> calcRanges(List<Range> ranges, final long contentLength) {
        int threadNum = 0;
        if (contentLength <= _1M * 50) {
            threadNum = 10;
        } else {
            threadNum = 20;
        }

        // 每个请求的大小
        long perThreadLength = contentLength / threadNum + 1;
        long startPosition = 0;
        long endPosition = perThreadLength;
        do {
            if (endPosition >= contentLength)
                endPosition = contentLength - 1;

            ranges.add(new Range(startPosition, endPosition));

            startPosition = endPosition + 1;// 此处加 1,从结束位置的下一个地方开始请求
            endPosition += perThreadLength;
        } while (startPosition < contentLength);
        return ranges;
    }

    private static long getFileContentLength(String url) {
        long contentLength = 0;
        CloseableHttpResponse response = null;
        try {
            HttpHead httpHead = new HttpHead(url);
            // logger.debug("download url:" + url);
            response = httpClient.execute(httpHead);
            // 获取HTTP状态码
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                httpHead.abort();
                return 0l;
            }
            // Content-Length
            final Header[] headers = response.getHeaders("Content-Length");
            if (headers.length > 0) {
                contentLength = Long.valueOf(headers[0].getValue());
                // logger.debug("length :" + contentLength);
            }
            httpHead.abort();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
            }
        }
        return contentLength;
    }

}

class RetryHandler implements HttpRequestRetryHandler {

    private static final Logger logger = LoggerFactory.getLogger(RetryHandler.class);

    public static final int RETRY_TIME_OUT = 3000;
    public static final int MAX_RETRY_TIME = 5;

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
        final HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        final HttpRequest request = clientContext.getRequest();
        final String url = request.getRequestLine().getUri();
        try {
            logger.warn("连接超时，重试等待中，url=" + url);
            Thread.sleep(RETRY_TIME_OUT);
        } catch (InterruptedException ex) {
            logger.error("连接超时等待中出错", ex);
        }
        logger.warn("重试次数=" + executionCount + "，url=" + url);
        if (executionCount >= MAX_RETRY_TIME) {
            // Do not retry if over max retry count
            return false;
        }
        if (e instanceof NoHttpResponseException || e instanceof ConnectException) {
            // Retry if the server dropped connection on us
            try {
                Thread.sleep(randInt(2000, 5000));
            } catch (InterruptedException e1) {
                logger.error("连接超时等待中出错", e1);
            }
            return true;
        }
        if (e instanceof SSLHandshakeException) {
            // Do not retry on SSL handshake exception
            return false;
        }

        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        if (idempotent) {
            // Retry if the request is considered idempotent
            return true;
        }
        return false;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }
}

class DownloadRange implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DownloadRange.class);

    File file;
    Range range;
    int socketTimeout;
    int connectTimeout;
    String url;
    CloseableHttpClient httpClient;

    public DownloadRange(File file, Range range, int socketTimeout, int connectTimeout, String url,
            CloseableHttpClient httpClient) {
        this.file = file;
        this.range = range;
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
        this.url = url;
        this.httpClient = httpClient;
    }

    @Override
    public void run() {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Range", "bytes=" + range.getStart() + "-" + range.getEnd());
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        // logger.debug(String.format("download:%s,from range:%s to %s", url,
        // range.getStart(), range.getEnd()));
        try {
            response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                InputStream inputStream = response.getEntity().getContent();
                RandomAccessFile outputStream = new RandomAccessFile(file, "rw");
                outputStream.seek(range.getStart());
                int count = 0;
                byte[] buffer = new byte[10240];
                while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
            }

        } catch (ClientProtocolException e) {
            logger.error(String.format("exception for url %s", url), e);
        } catch (IOException e) {
            logger.error(String.format("exception for url %s", url), e);
        } finally {
            httpGet.releaseConnection();
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                ;
            }
        }
    }
}

class Range {
    private long start;
    private long end;

    public Range(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
