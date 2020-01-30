package com.bobpaulin.camel.cloud.servicecall.consumer.internal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class RestRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		getContext().setStreamCaching(true);

        restConfiguration().component("netty-http").port(8080)
            .contextPath("/camel-cloud-talk")
            .bindingMode(RestBindingMode.off);

	}

}
