package com.bobpaulin.camel.cloud.internal;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.main.Main;

import com.bobpaulin.camel.cloud.producer.ProducerRouteBuilder;

public class ProducerMain {
	public static void main(String[] args) throws Exception {
		
		Main main = new Main();
		main.getRoutesBuilders().add(new ProducerRouteBuilder());

		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
		connectionFactory.setUserName("karaf");
		connectionFactory.setPassword("karaf");
		//AmazonSQS client = AmazonSQSClientBuilder.defaultClient();
		main.bind("connectionFactory", connectionFactory);
		//main.bind("client", client);
		main.start();
		
		System.in.read();
		main.close();
		
	}
}
