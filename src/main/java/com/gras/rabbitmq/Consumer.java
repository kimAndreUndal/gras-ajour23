package com.gras.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    @ConfigProperty(name = "rabbitmq.user")
    String USERNAME;

    @ConfigProperty(name = "rabbitmq.pass")
    String PASSWORD;

    @ConfigProperty(name = "rabbitmq.vhost")
    String VHOST;

    @ConfigProperty(name = "rabbitmq.hostname")
    String HOST_NAME;

    @ConfigProperty(name = "rabbitmq.port")
    int PORT;
    @ConfigProperty(name = "rabbitmq.timeout")
    int TIMEOUT;
    @ConfigProperty(name = "rabbitmq.ssl")
    String SSL;
    @ConfigProperty(name = "rabbitmq.ssl_verify_host")
    String SSL_VERIFY_HOST;
    @ConfigProperty(name = "rabbitmq.ssl_cacert")
    String CACERT;
    @ConfigProperty(name = "rabbitmq.ssl_init")
    String SSL_INIT;
    @ConfigProperty(name = "rabbitmq.queuename")
    String QUEUENAME;
    @ConfigProperty(name = "rabbitmq.heartbeat")
    int HEARTBEAT;
    public void readFromQueue(){

    }
}
