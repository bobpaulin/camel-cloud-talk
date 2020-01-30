package com.bobpaulin.camel.cloud.servicecall.consumer.internal;

import org.apache.camel.main.Main;

import com.bobpaulin.camel.cloud.servicecall.consumer.ServiceCallRouteBuilder;

public class ServiceCallMain {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		
		main.getRoutesBuilders().add(new RestRouteBuilder());
		main.getRoutesBuilders().add(new ServiceCallRouteBuilder());
		
		main.run(args);
	}
}
