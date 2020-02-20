package com.bobpaulin.camel.cloud.circuit;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.consul.ConsulConstants;
import org.apache.camel.component.consul.endpoint.ConsulKeyValueActions;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.apache.camel.component.hazelcast.HazelcastOperation;
import org.apache.camel.spi.CircuitBreakerConstants;
import org.osgi.service.component.annotations.Component;


@Component(service = RouteBuilder.class)
public class GlobalCircuitRouteBuilder extends RouteBuilder {

	
	@Override
	public void configure() throws Exception {
		
		
		
		
			
		
		
		from("direct:killSwitchPath1")
			.filter(exchangeProperty("featureToggles/featureToggle").isEqualTo("true"))
			    .to("direct:circuitBreaker1")
			    .filter(header(CircuitBreakerConstants.RESPONSE_SHORT_CIRCUITED).isEqualTo(Boolean.TRUE))
			    	.log("Short Circuit")
			    	.to("direct:globalKillSwitch")
			    .end()
			.end();
		
		from("direct:killSwitchPath2")
			.filter(exchangeProperty("featureToggles/featureToggle").isEqualTo("true"))
			    .to("direct:circuitBreaker2")
			    .filter(header(CircuitBreakerConstants.RESPONSE_SHORT_CIRCUITED).isEqualTo(Boolean.TRUE))
			    	.log("Short Circuit")
			    	.to("direct:globalKillSwitch")
			    .end()
			.end();
		
		from("direct:killSwitchPath3")
			.filter(exchangeProperty("featureToggles/featureToggle").isEqualTo("true"))
			    .to("direct:circuitBreaker3")
			    .filter(header(CircuitBreakerConstants.RESPONSE_SHORT_CIRCUITED).isEqualTo(Boolean.TRUE))
			    	.log("Short Circuit")
			    	.to("direct:globalKillSwitch")
			    .end()
			.end();
		
		from("direct:circuitBreaker1")
			.circuitBreaker()
	    	.hystrixConfiguration()
	    		.circuitBreakerErrorThresholdPercentage(20)
	    		.metricsRollingPercentileWindowInMilliseconds(60000)
	    	.end()
	        .to("direct:test-lb")
	    .onFallback()
	    	.log("fallback")
	        .transform().constant("Fallback message")
	    .end();
		
		from("direct:circuitBreaker2")
			.circuitBreaker()
		    	.hystrixConfiguration()
		    		.circuitBreakerErrorThresholdPercentage(20)
		    		.metricsRollingPercentileWindowInMilliseconds(60000)
		    	.end()
		        .to("direct:test-lb")
		    .onFallback()
		    	.log("fallback")
		        .transform().constant("Fallback message")
		    .end();
		
		from("direct:circuitBreaker3")
			.circuitBreaker()
		    	.hystrixConfiguration()
		    		.circuitBreakerErrorThresholdPercentage(20)
		    		.metricsRollingPercentileWindowInMilliseconds(60000)
		    	.end()
		        .to("direct:test-lb")
		    .onFallback()
		    	.log("fallback")
		        .transform().constant("Fallback message")
		    .end();
		
		from("direct:test-lb")
			.loadBalance().roundRobin()
				.to("https://bobpaulin.com?bridgeEndpoint=true")
				.to("https://bobpaulin.com/test?bridgeEndpoint=true")
			.end();
		
		from("direct:globalKillSwitch")
			.setBody(constant(Boolean.FALSE))
			.setHeader(ConsulConstants.CONSUL_ACTION, constant(ConsulKeyValueActions.PUT))
			.setHeader(ConsulConstants.CONSUL_KEY, constant("featureToggles/featureToggle"))
			.to("consul://kv");
		
	}
}
