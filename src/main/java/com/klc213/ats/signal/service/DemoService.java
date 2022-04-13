package com.klc213.ats.signal.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
	private final static Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

	private String symbol;
	
	private ExecutorService executor = null;
	
	private DemoSignalRunner demoSignalRunner = null;
	
	public void start() throws Exception {
		// trade symbol
		symbol = "SPY";
		
		//
		
		executor = Executors.newFixedThreadPool(1);
		
		demoSignalRunner = new DemoSignalRunner(1, "demogroup");
		
		executor.submit(demoSignalRunner);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				stopRun();

			}
		});
		
	}
	public void stopRun() {
		LOGGER.info(">>>>>>>>>>>> stopRun ");
		if (executor != null && demoSignalRunner != null) {
			demoSignalRunner.shutdown();

//			try {
//				if (sourceConnPool != null) sourceConnPool.close();
//				if (sinkConnPool != null) sinkConnPool.close();
//				if (tglminerConnPool != null) tglminerConnPool.close();
//			} catch (Exception e) {
//				LOG.error(">>>message={}, stack trace={}", e.getMessage(), ExceptionUtils.getStackTrace(e));
//			}

			executor.shutdown();
			if (!executor.isTerminated()) {
				executor.shutdownNow();

				try {
					executor.awaitTermination(3000, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					LOGGER.error(">>> ERROR!!!, msg={}, stacetrace={}",
							ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
				}

			}
			
		}

		LOGGER.info(">>>>>>>>>>>> stopRun done !!!");
	}
}
