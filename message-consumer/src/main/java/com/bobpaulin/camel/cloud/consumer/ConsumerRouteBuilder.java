package com.bobpaulin.camel.cloud.consumer;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;

@Component(service = RouteBuilder.class)
public class ConsumerRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("{{env:QUEUE_COMPONENT:seda}}:testAsynch?{{env:QUEUE_CONFIG:}}")
		.log("Got it!");

	}

}
