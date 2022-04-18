package com.klc213.ats.signal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.klc213.ats.common.TopicEnum;
import com.klc213.ats.common.util.HttpUtils;
import com.klc213.ats.common.util.KafkaUtils;

@Service
public class DemoService {
	private final static Logger LOGGER = LoggerFactory.getLogger(DemoService.class);

	@Value("${ats.ib.rest.url}")
	private String atsIbRestUrl;
	
	@Value("${kafka.client.id}")
	private String kafkaClientId;

	@Value("${kafka.bootstrap.server}")
	private String kafkaBootstrapServer;
	
	private String symbol;
	
	private ExecutorService executor = null;
	
	private DemoSignalRunner demoSignalRunner = null;
	
	private Producer<String, String> producer;
	
	public void start() throws Exception {
		// trade symbol
		symbol = "SPY";
		
//		producer = KafkaUtils.createProducer(kafkaBootstrapServer, kafkaClientId);
			

		// run realtimemktdata consumer
		executor = Executors.newFixedThreadPool(1);
		demoSignalRunner = new DemoSignalRunner(1, "demogroup");
		
		executor.submit(demoSignalRunner);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

				try {
					stopRun();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// request ib RealtimeMktData
		String url = atsIbRestUrl + "/mktdata/reqRealTimeBars/" + symbol;
		String resp = HttpUtils.restService(url, "POST");
		LOGGER.info(">>>>>>>>>>>> resp={} ", resp);
		
		
	}
	public void stopRun() throws Exception {
		LOGGER.info(">>>>>>>>>>>> stopRun ");
		if (executor != null && demoSignalRunner != null) {
			demoSignalRunner.shutdown();

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
			
			if (producer != null) {
				producer.close();
			}
			
			String url = atsIbRestUrl + "/mktdata/cancelRealTimeBars/" + symbol;
			String resp = HttpUtils.restService(url, "POST");
			LOGGER.info(">>>>>>>>>>>> resp={} ", resp);
			
		}

		LOGGER.info(">>>>>>>>>>>> stopRun done !!!");
	}
}
