package com.klc213.ats.signal.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klc213.ats.common.TopicEnum;
import com.klc213.ats.common.util.KafkaUtils;


public class DemoSignalRunner implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(DemoSignalRunner.class);

	private final AtomicBoolean closed = new AtomicBoolean(false);
	
	private final KafkaConsumer<String, String> consumer;
	
	private List<String> topicList;
	
	private String realtimeTopicSPY;
	
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
		
		for (ConsumerRecord record : buffer) {
			LOGGER.info(">>>record topic={}, key={},value={},offset={}", record.topic(), record.key(), record.value(), record.offset());
			if (StringUtils.equals(realtimeTopicSPY, record.topic())) {
				
			}
			
		}
	}
}
