package com.bobpaulin.camel.cloud.circuit;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.component.hazelcast.HazelcastOperation;
import org.osgi.service.component.annotations.Component;

@Component(service=RouteBuilder.class)
public class RestPathRouteBuilder extends RouteBuilder {
	@Override
	public void configure() throws Exception {
		
		interceptFrom("rest:*")
			.setProperty("tempBody", body())
			.setHeader(HazelcastConstants.OPERATION, constant(HazelcastOperation.GET_KEYS))
			.to("hazelcast-map:featureToggles")
			.to("log:featureToggle?showHeaders=true")
			.split(body(), AggregationStrategies.groupedBody())
				.setHeader(HazelcastConstants.OBJECT_ID, body())
				.setHeader("FEATURE-TOGGLE-KEY", body())
				.setHeader(HazelcastConstants.OPERATION, constant(HazelcastOperation.GET))
				.to("hazelcast-map:featureToggles")
				.to("log:featureUpdate?showHeaders=true")
				.process(exchange -> {
					String key = exchange.getIn().getHeader("FEATURE-TOGGLE-KEY", String.class);
					String value = exchange.getIn().getBody(String.class);
					exchange.getIn().setBody(Collections.singletonMap(key, value));
				})
			.end()
			.process(exchange -> {
				List<Map<String, Object>> featureToggleMaps = exchange.getIn().getBody(List.class);
				
				if(featureToggleMaps != null)
				{
					featureToggleMaps.stream().forEach(map -> 
						{
							map.forEach((key, value) -> {
								exchange.setProperty((String)key, value);
							});
						});
				}
			})
			.setBody(exchangeProperty("tempBody"));
		
		rest("api/circuit")
			.get("path1")
				.to("direct:circuitBreaker1")
			.get("path2")
				.to("direct:circuitBreaker2")
			.get("path3")
				.to("direct:circuitBreaker3");
		
		rest("api/toggle")
			.get("path1")
				.to("direct:killSwitchPath1")
			.get("path2")
				.to("direct:killSwitchPath2")
			.get("path3")
				.to("direct:killSwitchPath3");
		
		rest("api")
			.get("slow")
				.to("direct:slow")
			.get("fast")
				.to("direct:fast");
	}
}
