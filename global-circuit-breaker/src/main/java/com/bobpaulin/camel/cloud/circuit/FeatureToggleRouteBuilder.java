package com.bobpaulin.camel.cloud.circuit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.consul.ConsulConstants;
import org.apache.camel.component.consul.endpoint.ConsulKeyValueActions;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.component.hazelcast.HazelcastOperation;
import org.osgi.service.component.annotations.Component;

@Component(service=RouteBuilder.class)
public class FeatureToggleRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("timer:checkFeatureToggles?fixedRate=true&period=1000")
			.setHeader(ConsulConstants.CONSUL_ACTION, constant(ConsulKeyValueActions.GET_KEYS))
			.setHeader(ConsulConstants.CONSUL_KEY, constant("featureToggles"))
			.to("consul:kv")
			.split(body())
				.setHeader(ConsulConstants.CONSUL_ACTION, constant(ConsulKeyValueActions.GET_VALUE))
				.setHeader(ConsulConstants.CONSUL_VALUE_AS_STRING, constant(Boolean.TRUE))
				.setHeader(ConsulConstants.CONSUL_KEY, body())
				.to("consul:kv")
				.setHeader(HazelcastConstants.OBJECT_ID, header(ConsulConstants.CONSUL_KEY))
				.setHeader(HazelcastConstants.OPERATION, constant(HazelcastOperation.PUT))
				.to("hazelcast-map:featureToggles")
			.end();

	}

}
