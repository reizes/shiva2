package com.reizes.shiva2.http;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.reizes.shiva2.core.ExceptionListener;

import lombok.Getter;
import lombok.Setter;

public class ThreadedRestClientPool implements Closeable {
	@Getter
	@Setter
	private int maxTotal = 100;
	@Getter
	@Setter
	private int maxPerRoute = 20;
	@Getter
	@Setter
	private int timeout = 5000;
	private static ThreadedRestClientPool instance = null;
	private AtomicBoolean shutdown = new AtomicBoolean(true);
	
	@Getter
	private CloseableHttpClient httpclient;
	//private ExecutorService pool = Executors.newFixedThreadPool(100);

	@Getter
	@Setter
	private ExceptionListener exceptionListener;
	PoolingHttpClientConnectionManager connManager;
	private IdleConnectionMonitorThread monitor; 
	
	private ThreadedRestClientPool() {
		httpclient = initHttpClient();
	}
	
	public synchronized static ThreadedRestClientPool getInstance() {
		if (instance==null) {
			instance = new ThreadedRestClientPool();
		}
		
		return instance;
	}

	private CloseableHttpClient initHttpClient() {
		connManager = new PoolingHttpClientConnectionManager();
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(maxPerRoute);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.build();
		
		ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
	        @Override
	        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
	            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
	            while (it.hasNext()) {
	                HeaderElement he = it.nextElement();
	                String param = he.getName();
	                String value = he.getValue();
	                if (value != null && param.equalsIgnoreCase("timeout")) {
	                    return Long.parseLong(value) * 1000;
	                }
	            }
	            return 10 * 1000;
	        }
	    };
	    
		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(connManager)
				.setDefaultRequestConfig(requestConfig)
				.setKeepAliveStrategy(keepAliveStrategy)
				.build();
		
        monitor = new IdleConnectionMonitorThread(connManager); 
        monitor.setDaemon(true); 
        monitor.start(); 

		shutdown.set(false);
		return client;
	}

	@Override
	public synchronized void close() throws IOException {
		if (!shutdown.get()) {
			shutdown.set(true);
			try {
				monitor.shutdown();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//httpclient.close();
			//connManager.shutdown();
			/*pool.shutdown();
			try {
				if (!pool.awaitTermination(30000, TimeUnit.MILLISECONDS)) {
					pool.shutdownNow();
				}
			} catch (InterruptedException e) {
				pool.shutdownNow();
				Thread.currentThread().interrupt();
			}*/
		}
	}
	
	public boolean isShutdown() {
		return shutdown.get();
	}
	
    // Watches for stale connections and evicts them. 
    private class IdleConnectionMonitorThread extends Thread { 
      // The manager to watch. 
      private final PoolingHttpClientConnectionManager cm; 
      // Use a BlockingQueue to stop everything. 
      private final BlockingQueue<Stop> stopSignal = new ArrayBlockingQueue<Stop>(1); 
 
      // Pushed up the queue. 
      private class Stop { 
        // The return queue. 
        private final BlockingQueue<Stop> stop = new ArrayBlockingQueue<Stop>(1); 
 
        // Called by the process that is being told to stop. 
        public void stopped() { 
          // Push me back up the queue to indicate we are now stopped. 
          stop.add(this); 
        } 
 
        // Called by the process requesting the stop. 
        public void waitForStopped() throws InterruptedException { 
          // Wait until the callee acknowledges that it has stopped. 
          stop.poll(30, TimeUnit.SECONDS); 
        } 
 
      } 
 
      IdleConnectionMonitorThread(PoolingHttpClientConnectionManager cm) { 
        super(); 
        this.cm = cm; 
      } 
 
      @Override 
      public void run() { 
        try { 
          // Holds the stop request that stopped the process. 
          Stop stopRequest; 
          // Every 5 seconds. 
          while ((stopRequest = stopSignal.poll(5, TimeUnit.SECONDS)) == null) { 
            // Close expired connections 
            cm.closeExpiredConnections(); 
            // Optionally, close connections that have been idle too long. 
            cm.closeIdleConnections(60, TimeUnit.SECONDS); 
          } 
          // Acknowledge the stop request. 
          stopRequest.stopped(); 
        } catch (InterruptedException ex) { 
          // terminate 
        } 
      } 
 
      public void shutdown() throws InterruptedException { 
        // Signal the stop to the thread. 
        Stop stop = new Stop(); 
        stopSignal.add(stop); 
        // Wait for the stop to complete. 
        stop.waitForStopped(); 
        // Close the pool - Added 
        try { 
        	httpclient.close(); 
        } catch (IOException ioe) { 
            System.out.println("IO Exception while closing HttpClient connecntions.");
            ioe.printStackTrace();
        } 
        // Close the connection manager. 
        cm.close(); 
      } 
 
    } 
}
