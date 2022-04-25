package com.klc213.ats.signal.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klc213.ats.common.AccountInfo;
import com.klc213.ats.common.AtsBar;
import com.klc213.ats.common.TopicEnum;
import com.klc213.ats.common.util.KafkaUtils;
import com.klc213.ats.signal.bean.PivotPoint;
import com.klc213.ats.signal.bean.PriceSignal;
import com.klc213.ats.signal.utils.StatisticsUtils;


public class DemoSignalRunner implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(DemoSignalRunner.class);
	
	static final String SYMBOL_SPY = "SPY";

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final KafkaConsumer<String, String> consumer;

	private List<String> topicList;

	private String realtimeTopicSPY;

	private AccountInfo accountInfo;

	private Map<String, PriceSignal> realTimeBarsMap;

	private ExecutorService executor;

	private final AtomicBoolean signalRunning = new AtomicBoolean(false);

	public DemoSignalRunner(int id, String groupId)  {

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", groupId);
		props.put("client.id", groupId + "-" + id);
		props.put("group.instance.id", groupId + "-mygid" );
		props.put("enable.auto.commit", "false");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", StringDeserializer.class.getName());
		props.put("session.timeout.ms", 120000 ); // 120 seconds
		props.put("heartbeat.interval.ms", 40000 ); // 40 seconds
		props.put("max.poll.interval.ms", 300000 ); 
		props.put("max.poll.records", 50 );
		props.put("auto.offset.reset", "earliest" );
		this.consumer = new KafkaConsumer<>(props);

		topicList = new ArrayList<>();

		this.realtimeTopicSPY = KafkaUtils.getTwsMktDataRealtimeTopic("SPY");

		topicList.add(TopicEnum.TWS_ACCOUNT.getTopic());
		topicList.add(realtimeTopicSPY);

		realTimeBarsMap = new HashMap<>();
		realTimeBarsMap.put(SYMBOL_SPY, new PriceSignal());

		executor = Executors.newFixedThreadPool(1);
	}

	@Override
	public void run() {
		consumer.subscribe(topicList);

		List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
		while (!closed.get()) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			for (ConsumerRecord<String, String> record : records) {
				buffer.add(record);
			}

			if (buffer.size() > 0) {
				process(buffer);

				consumer.commitSync();

				buffer.clear();
			}
		}

	}

	public void shutdown() {
		closed.set(true);
		consumer.wakeup();
	}
	public void process(List<ConsumerRecord<String, String>> buffer) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		for (ConsumerRecord<String, String> record : buffer) {
			LOGGER.info(">>>record topic={}, key={},value={},offset={}", record.topic(), record.key(), record.value(), record.offset());
			if (StringUtils.equals(TopicEnum.TWS_ACCOUNT.getTopic(), record.topic())) {

				try {
					accountInfo = objectMapper.readValue(record.value(), AccountInfo.class);
					LOGGER.debug(">>>record received accountInfo={}", ToStringBuilder.reflectionToString(accountInfo));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} else if (StringUtils.equals(realtimeTopicSPY, record.topic())) {
				try {
					AtsBar atsBar = objectMapper.readValue(record.value(), AtsBar.class);
					LOGGER.debug(">>>record received atsBar={}", ToStringBuilder.reflectionToString(atsBar));
					if (realTimeBarsMap.containsKey(atsBar.getSymbol())) {
						realTimeBarsMap.get(atsBar.getSymbol()).add(atsBar);
						
						// trigger signal generator
						if (!signalRunning.get()) {
							signalRunning.set(true);
							
							executor.execute(new Runnable() {

								@Override
								public void run() {
									runSignal();
									signalRunning.set(false);
								}

							});
						} else {
							LOGGER.debug(">>>signalRunning is running. skip ");
						}

					} else {
						LOGGER.error("invalid symbol:{}", atsBar.getSymbol());
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}

		}
	}
	private void runSignal() {
		LOGGER.debug(">>>accountInfo={}", ToStringBuilder.reflectionToString(accountInfo));
		LOGGER.debug(">>>spy atsBar list size={}", realTimeBarsMap.get(SYMBOL_SPY).getBarSize());

		PriceSignal priceSignal = realTimeBarsMap.get(SYMBOL_SPY);
		Double sma = priceSignal.getSMA(5);
	
		LOGGER.debug(">>>sma={}", sma);
		
		AtsBar lastBar = priceSignal.getLastBar() ;
		if (lastBar.getClose().doubleValue() > sma) {
			// buy if no order or position
		} else {
			// sell  
		}
		
		
	}
}
