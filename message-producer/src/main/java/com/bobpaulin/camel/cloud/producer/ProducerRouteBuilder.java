package com.bobpaulin.camel.cloud.producer;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;

@Component(service = RouteBuilder.class)
public class ProducerRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("timer://test?fixedRate=true&period=1000")
			.setBody(constant("test"))
			.to("{{env:QUEUE_COMPONENT:seda}}:testAsynch?{{env:QUEUE_CONFIG:}}");

	}

}
