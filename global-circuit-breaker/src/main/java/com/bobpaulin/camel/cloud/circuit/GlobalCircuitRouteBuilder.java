package com.bobpaulin.camel.cloud.circuit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.consul.ConsulConstants;
import org.apache.camel.component.consul.endpoint.ConsulKeyValueActions;
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
	    		.metricsRollingPercentileWindowInMilliseconds(10000)
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
		    		.metricsRollingPercentileWindowInMilliseconds(10000)
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
		    		.metricsRollingPercentileWindowInMilliseconds(10000)
		    	.end()
		        .to("direct:test-lb")
		    .onFallback()
		    	.log("fallback")
		        .transform().constant("Fallback message")
		    .end();
		
		from("direct:test-lb")
			.loadBalance().roundRobin()
				.to("http://localhost:8080/camel-cloud-talk/api/slow?bridgeEndpoint=true")
				.to("http://localhost:8080/camel-cloud-talk/api/fast?bridgeEndpoint=true")
			.end();
		
		from("direct:globalKillSwitch")
			.setBody(constant(Boolean.FALSE))
			.setHeader(ConsulConstants.CONSUL_ACTION, constant(ConsulKeyValueActions.PUT))
			.setHeader(ConsulConstants.CONSUL_KEY, constant("featureToggles/featureToggle"))
			.to("consul://kv");
		
		from("direct:slow")
			.process(exchange -> {
				Thread.currentThread().sleep(2000);
			});
		
		from("direct:fast")
		.process(exchange -> {
			Thread.currentThread().sleep(100);
		});
		
	}
}
