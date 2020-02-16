import org.apache.activemq.ActiveMQConnectionFactory;

//kamel.exe run -e BROKER_USERNAME=karaf -e BROKER_PASSWORD=karaf -e BROKER_URL=tcp://192.168.99.1:61616 amqconsumer.groovy 

beans {
    connectionFactory = new ActiveMQConnectionFactory(System.getenv('BROKER_USERNAME'), System.getenv('BROKER_PASSWORD'), System.getenv('BROKER_URL'))
}

from('activemq:testAsynch?connectionFactory=#connectionFactory')
    .log('Hello Camel K')