package com.gras.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.beans.JavaBean;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @Inject
    HandleMessages handleMessages;

    public void readFromQueue(){

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setPort(port);
//        factory.setUsername(user);
//        factory.setPassword(pass);
//        factory.setRequestedHeartbeat(heartbeat);
//        factory.setConnectionTimeout(timeout);
//        factory.setVirtualHost(vhost);
//        factory.setHost(hostname);
//        factory.enableHostnameVerification();
//        factory.setSslContextFactory(s -> {
//            try {
//                return SSLContext.getDefault();
//            } catch (NoSuchAlgorithmException e) {
//                throw new RuntimeException(e);
//            }
//        });

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            DeliverCallback deliverCallback = (consumerTag, message) ->{


//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//                JsonNode node = objectMapper.readTree(body);
//                String providerID = node.get("providerID").asText();
//                String postType = node.get("posttype").asText();
//                DocumentDto document = objectMapper.readValue(body, new TypeReference<>() {});
                handleMessages.handleMessage(message, channel);

            };
            channel.basicConsume("queue", true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            logger.error("readFromQueue() error: " + e.getMessage());
        }
    }
}
