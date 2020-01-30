package com.bobpaulin.camel.cloud.servicecall.consumer;

import org.apache.camel.builder.RouteBuilder;
import org.osgi.service.component.annotations.Component;

@Component(service = RouteBuilder.class)
public class ServiceCallRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		rest("/serviceCall")
	    	.get()
	    		.to("direct:service-call");
	    
	    from("direct:service-call")
	        .setBody().constant(null)
	        .removeHeaders("CamelHttp*")
	        .serviceCall()
	        	.name("my-service")
	        	.consulServiceDiscovery()
	        .end()
	        .convertBodyTo(String.class)
	        .log("answer: ${body}");

	}

}
