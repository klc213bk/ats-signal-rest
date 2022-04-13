package com.klc213.ats.signal.service;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DemoSignalRunner implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(DemoSignalRunner.class);

	private final AtomicBoolean closed = new AtomicBoolean(false);
	
	private final KafkaConsumer<String, String> consumer;
	
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
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void shutdown() {
		closed.set(true);
		consumer.wakeup();
	}

}
