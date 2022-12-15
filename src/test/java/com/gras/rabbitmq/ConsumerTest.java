package com.gras.rabbitmq;

import com.rabbitmq.client.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerTest {

    @Inject
    @ConfigProperty(name = "rabbitmq.hostname")
    String hostname;
    @Inject
    @ConfigProperty(name = "rabbitmq.port")
    int port;
    @Inject
    @ConfigProperty(name = "rabbitmq.user")
    String user;
    @Inject
    @ConfigProperty(name = "rabbitmq.pass")
    String pass;
    @Inject
    @ConfigProperty(name = "rabbitmq.vhost")
    String vhost;
    @Inject
    @ConfigProperty(name = "rabbitmq.timeout")
    int timeout;
    @Inject
    @ConfigProperty(name = "rabbitmq.ssl")
    int ssl;
    @Inject
    @ConfigProperty(name = "rabbitmq.ssl_verify_host")
    int verifyHost;
    @Inject
    @ConfigProperty(name = "rabbitmq.ssl_cacert")
    String cacert;
    @Inject
    @ConfigProperty(name = "rabbitmq.init")
    int init;
    @Inject
    @ConfigProperty(name = "rabbitmq.queuename")
    String queuename;

    @Inject
    @ConfigProperty(name = "rabbitmq.heartbeat")
    int heartbeat;

    @Test
    @Order(1)
    void sendToQueue(){
        ConnectionFactory factory = new ConnectionFactory();
        try(Connection conn = factory.newConnection()){
            Channel channel = conn.createChannel();
            channel.queueDeclare("queue", false, false, false, null);
            String message = "did it work?";

            channel.basicPublish("", "queue", false, null, message.getBytes());
            System.out.println("message sent: " + message);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(2)
    void readFromQueue() throws NoSuchAlgorithmException, KeyManagementException, IOException {

        ConnectionFactory factory = new ConnectionFactory();
        SslContextFactory context =
        factory.setUsername(user);
        factory.setPort(port);
        factory.setPassword(pass);
        factory.setRequestedHeartbeat(heartbeat);
        factory.setConnectionTimeout(timeout);
        factory.setVirtualHost(vhost);
        factory.setHost(hostname);
        factory.setSslContextFactory();

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            DeliverCallback deliverCallback = (consumerTag, message) ->{
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.println("message received: " + body);
            };
            channel.basicConsume(queuename, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}