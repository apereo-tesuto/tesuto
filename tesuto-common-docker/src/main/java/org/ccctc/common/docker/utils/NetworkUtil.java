package org.ccctc.common.docker.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;


import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class NetworkUtil {

	private NetworkUtil() {
	}

	public static void waitForPort(String host, Collection<Integer> ports, int timeoutInMillis) {
		if (CollectionUtils.isEmpty(ports)) {
			return;
		}
		ExecutorService executor = Executors.newFixedThreadPool(ports.size());
		CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);

		try {
			for (int port : ports) {
				completionService.submit(new WaitForPortCallable(host, port, timeoutInMillis));
			}
			for (int i = 0; i < ports.size(); i++) {
				completionService.take();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			executor.shutdown();
		}
	}

	static class WaitForPortCallable implements Callable<Void> {

		private static final int DEFAULT_WAIT_IN_MILLIS = 500;

		private final String host;
		private final int port;
		private int timeoutInMillis;

		public WaitForPortCallable(String host, int port, int timeoutInMillis) {
			this.host = host;
			this.port = port;
			this.timeoutInMillis = timeoutInMillis;
		}

		public Void call() {
			long totalWaitInMillis = 0;
			SocketAddress socketAddr = new InetSocketAddress(this.host, this.port);
			while (true) {
				Socket socket = new Socket();
				try {
					socket.connect(socketAddr, this.timeoutInMillis);
					return null;
				} catch (IOException e) {
					log.info("Could not connect to [host={}, port={}]. Might attempt connection again ...", this.host, this.port);
					this.sleep(DEFAULT_WAIT_IN_MILLIS);
					totalWaitInMillis += DEFAULT_WAIT_IN_MILLIS;
					if (totalWaitInMillis >= this.timeoutInMillis) {
						throw new IllegalStateException(String.format("Timeout while waiting for port: %d", this.port));
					}
				} finally {
					IOUtils.closeQuietly(socket);
				}
			}
		}

		private void sleep(long currentWaitInMillis) {
			try {
				Thread.sleep(currentWaitInMillis);
			} catch (InterruptedException ie) {
				throw new IllegalStateException(ie);
			}
		}
	}
}