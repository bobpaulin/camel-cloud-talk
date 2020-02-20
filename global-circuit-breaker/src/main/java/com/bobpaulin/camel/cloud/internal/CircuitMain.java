package com.bobpaulin.camel.cloud.internal;

import org.apache.camel.main.Main;

import com.bobpaulin.camel.cloud.circuit.FeatureToggleRouteBuilder;
import com.bobpaulin.camel.cloud.circuit.GlobalCircuitRouteBuilder;
import com.bobpaulin.camel.cloud.circuit.RestPathRouteBuilder;

public class CircuitMain {
	
	
	
public static void main(String[] args) throws Exception {
		
		Main main = new Main();
		main.getRoutesBuilders().add(new RestRouteBuilder());
		main.getRoutesBuilders().add(new GlobalCircuitRouteBuilder());
		main.getRoutesBuilders().add(new RestPathRouteBuilder());
		main.getRoutesBuilders().add(new FeatureToggleRouteBuilder());

		
		main.run(args);
		
	}
	
}
