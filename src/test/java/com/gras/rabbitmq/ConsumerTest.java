package com.gras.rabbitmq;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gras.dto.CustomerDto;
import com.gras.dto.LoanDto;
import com.gras.dto.LoanTypeDto;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.impl.AMQImpl;
import com.rabbitmq.client.impl.nio.NioParams;
import io.restassured.path.json.JsonPath;
import io.vertx.core.json.Json;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ConsumerTest{

    private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);
    @ConfigProperty(name = "rabbitmq.hostname")
    String hostname;

    @ConfigProperty(name = "rabbitmq.port")
    int port;

    @ConfigProperty(name = "rabbitmq.user")
    String user;

    @ConfigProperty(name = "rabbitmq.pass")
    String pass;

    @ConfigProperty(name = "rabbitmq.vhost")
    String vhost;

    @ConfigProperty(name = "rabbitmq.timeout")
    int timeout;

    @ConfigProperty(name = "rabbitmq.ssl")
    int ssl;

    @ConfigProperty(name = "rabbitmq.ssl_verify_host")
    int verifyHost;

    @ConfigProperty(name = "rabbitmq.ssl_cacert")
    String cacert;

    @ConfigProperty(name = "rabbitmq.init")
    int init;

    @ConfigProperty(name = "rabbitmq.queuename")
    String queuename;


    @ConfigProperty(name = "rabbitmq.heartbeat")
    int heartbeat;




    @Test
    @Order(1)
    void sendToQueue(){
        ConnectionFactory factory = new ConnectionFactory();
        try(Connection conn = factory.newConnection()){
            JsonElement root = new JsonParser().parse(new FileReader("src/main/resources/json/file1.json"));
            Channel channel = conn.createChannel();
            channel.queueDeclare("queue", false, false, false, null);

            JsonObject object = root.getAsJsonObject();
            String message = object.toString();
            channel.basicPublish("", "queue", false, null, message.getBytes());
            logger.info("sendToQueue() sent: " + message);
        } catch (IOException | TimeoutException e) {
            logger.error("sendToQueue() error: " + e.getMessage());
        }
    }
    @Test
    @Order(2)
    void readFromQueue() throws NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {

        ConnectionFactory factory = new ConnectionFactory();
//
//        factory.setPort(5671);
//        factory.setUsername("gras");
//        factory.setPassword("7XEaERKJGAzEDwFz5KMt");
//        factory.setRequestedHeartbeat(60);
//        factory.setConnectionTimeout(30);
//        factory.setVirtualHost("/ajour");
//        factory.setHost("egi-mq-vip.egi.osl.basefarm.net");
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
                String body = new String(message.getBody(), StandardCharsets.UTF_8);
                String s = JsonPath.from(body).get("providerID");

                List<Map<String, List<Map<String,Object>>>> customers = JsonPath.from(body).get("customers");

                for ( Map<String, List<Map<String, Object>>> customer : customers)
                {
                    CustomerDto customerDto = new CustomerDto(JsonPath.from(body).get("customerID"));
                    System.out.println("1: "+(String) JsonPath.from(body).get("customerID"));
                    for (Map<String, Object> loans: customer.get("loans")
                         ) {

                        LoanDto loanDto1 = publicLoanFields.apply(loans);
                        Objects.requireNonNull(customerDto).addLoanToLoanType((String)loans.get("loanType"), loanDto1);
                        System.out.println(customerDto.getLoanTypes());
                        for (LoanTypeDto loan: customerDto.getLoanTypes()
                             ) {
                            System.out.println("loanDto: " + loan);
                            for (LoanDto dto: loan.loans
                                 ) {
                                System.out.println("FI: " + dto.financialInstitutionID);
                            }
                        }
                    }
                }

            };
            channel.basicConsume("queue", true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            logger.error("readFromQueue() error: " + e.getMessage());
        }
    }
    private LoanDto loanDto(Map<String, Object> resultSet){
        LoanDto loanDto = new LoanDto();
        loanDto.balance = mapToZeroIfNegative((String) resultSet.get("balance"));
        loanDto.terms = (String) resultSet.get("terms");
        loanDto.originalBalance = mapToZeroIfNegative((String) resultSet.get("originalBalance"));
        loanDto.interestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("interestBearingBalance")));
        loanDto.nonInterestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("nonInterestBearingBalance")));
        loanDto.coBorrower = String.valueOf(resultSet.get("coBorrower"));
        loanDto.nominalInterestRate = (String) resultSet.get("nominalInterestRate");
        loanDto.installmentCharges = (String) resultSet.get("installmentCharges");
        loanDto.installmentChargePeriod = (String) resultSet.get("installmentChargesPeriod");
        loanDto.creditLimit = (String) resultSet.get("creditLimit");
//            loanDto.processedTime = (Timestamp) resultSet.get("processed_time").toInstant().toString();
//            loanDto.receivedTime = (Timestamp) resultSet.get("received_time").toInstant().toString();
        loanDto.processedTime = (String) resultSet.get("processed_time");
        loanDto.receivedTime = (String) resultSet.get("received_time");
        System.out.println("lll: "+loanDto.creditLimit);
        return loanDto;

    }
    private Function<Map<String, Object>, LoanDto> publicLoanFields = resultSet -> {
        LoanDto loanDto = new LoanDto();

        loanDto.balance = mapToZeroIfNegative((String) resultSet.get("balance"));
        loanDto.terms = (String) resultSet.get("terms");
        loanDto.originalBalance = mapToZeroIfNegative((String) resultSet.get("originalBalance"));
        loanDto.interestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("interestBearingBalance")));
        loanDto.nonInterestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("nonInterestBearingBalance")));
        loanDto.coBorrower = String.valueOf(resultSet.get("coBorrower"));
        loanDto.nominalInterestRate = (String) resultSet.get("nominalInterestRate");
        loanDto.installmentCharges = (String) resultSet.get("installmentCharges");
        loanDto.installmentChargePeriod = (String) resultSet.get("installmentChargesPeriod");
        loanDto.creditLimit = (String) resultSet.get("creditLimit");
//            loanDto.processedTime = (Timestamp) resultSet.get("processed_time").toInstant().toString();
//            loanDto.receivedTime = (Timestamp) resultSet.get("received_time").toInstant().toString();
        loanDto.processedTime = (String) resultSet.get("processed_time");
        loanDto.receivedTime = (String) resultSet.get("received_time");
        System.out.println("lll: "+loanDto.creditLimit);
        return loanDto;
    };

    static class FailedToMapFields extends RuntimeException {
        FailedToMapFields(Exception e) {
            super(e);
        }
    }
    String mapToZeroIfNegative(String value) {
        if (value == null) {
            return value;
        }

        try {
            if (Long.parseLong(value) < 0) {
                return "0";
            }

            return String.valueOf(Long.parseLong(value));
        }
        catch (NumberFormatException e) {
            logger.error("Value should be numeric", e);
            return "0";
        }
    }
}