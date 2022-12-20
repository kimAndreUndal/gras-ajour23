package com.gras.rabbitmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gras.dto.CustomerDto;
import com.gras.dto.DocumentDto;
import com.gras.dto.LoanDto;
import com.rabbitmq.client.*;
import org.codehaus.groovy.transform.SourceURIASTTransformation;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

class ConsumerTest{

    private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);
    @ConfigProperty(name = "hostname")
    String hostname;

    @ConfigProperty(name = "port")
    int port;

    @ConfigProperty(name = "user")
    String user;

    @ConfigProperty(name = "pass")
    String pass;

    @ConfigProperty(name = "vhost")
    String vhost;

    @ConfigProperty(name = "timeout")
    int timeout;

    @ConfigProperty(name = "ssl")
    int ssl;

    @ConfigProperty(name = "ssl_verify_host")
    int verifyHost;

    @ConfigProperty(name = "ssl_cacert")
    String cacert;

    @ConfigProperty(name = "init")
    int init;

    @ConfigProperty(name = "queuename")
    String queuename;


    @ConfigProperty(name = "heartbeat")
    int heartbeat;



    @Test
    @Order(1)
    void sendToQueue(){
        ConnectionFactory factory = new ConnectionFactory();
        File directory = new File("src/main/resources/json");
        String[] files = new String[(int) directory.length()];
        String message = "";
        try(Connection conn = factory.newConnection()){
            for (String file : Objects.requireNonNull(directory.list())) {

                JsonElement root = new JsonParser().parse(new FileReader("src/main/resources/json/" + file));
                JsonObject object = root.getAsJsonObject();
                message = object.toString();
                Channel channel = conn.createChannel();
                channel.queueDeclare("queue", false, false, false, null);
                channel.basicPublish("", "queue", false, null, message.getBytes());
            }

            System.out.println();
        } catch (IOException | TimeoutException e) {
            logger.error("sendToQueue() error: " + e.getMessage());
        }
    }
    @Test
    @Order(2)
    void readFromQueue() throws NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException, KeyStoreException, UnrecoverableKeyException {

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
                String body = new String(message.getBody(), StandardCharsets.UTF_8);

                ObjectMapper objectMapper = new ObjectMapper();
                DocumentDto document = objectMapper.readValue(body, new TypeReference<>() {});


                for (CustomerDto customerDto: document.customers
                     ) {
                    LoanDto loanDto = publicLoanFields.apply(customerDto);
                    System.out.println(loanDto.toString());
                }
            };
            channel.basicConsume("queue", true, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            logger.error("readFromQueue() error: " + e.getMessage());
        }
    }
//private Function<Map<String, Object>, LoanDto> publicLoanFields = resultSet -> {
//    LoanDto loanDto = new LoanDto();
//
//    loanDto.balance = mapToZeroIfNegative(String.valueOf(resultSet.get("balance")));
//    loanDto.terms = (String) resultSet.get("terms");
//    loanDto.originalBalance = mapToZeroIfNegative((String) resultSet.get("originalBalance"));
//    loanDto.interestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("interestBearingBalance")));
//    loanDto.nonInterestBearingBalance = mapToZeroIfNegative(String.valueOf(resultSet.get("nonInterestBearingBalance")));
//    loanDto.coBorrower = String.valueOf(resultSet.get("coBorrower"));
//    loanDto.nominalInterestRate = (String) resultSet.get("nominalInterestRate");
//    loanDto.installmentCharges = (String) resultSet.get("installmentCharges");
//    loanDto.installmentChargePeriod = (String) resultSet.get("installmentChargesPeriod");
//    loanDto.creditLimit = mapToZeroIfNegative(String.valueOf(resultSet.get("creditLimit")));
//    loanDto.accountID = (String) resultSet.get("accountID");
//    loanDto.loanType = (String) resultSet.get("loanType");
//    loanDto.accountName = (String) resultSet.get("accountName");
//    loanDto.processedTime = (String) resultSet.get("timeStamp");
//    loanDto.receivedTime = String.valueOf(new java.sql.Timestamp(System.nanoTime()));
//    System.out.println("l: " + loanDto.creditLimit);
//    return loanDto;
//};
private final Function<CustomerDto, LoanDto> publicLoanFields = loans ->{
    LoanDto loanDto = new LoanDto();
    for (LoanDto loan: loans.getLoan()
    ) {
        loanDto.balance = mapToZeroIfNegative(loan.balance);
        loanDto.terms = loan.terms;
        loanDto.originalBalance = mapToZeroIfNegative(loan.originalBalance);
        loanDto.interestBearingBalance = mapToZeroIfNegative(loan.interestBearingBalance);
        loanDto.nonInterestBearingBalance = mapToZeroIfNegative(loan.nonInterestBearingBalance);
        loanDto.coBorrower = loan.coBorrower;
        loanDto.nominalInterestRate = loan.nominalInterestRate;
        loanDto.installmentCharges = loan.installmentCharges;
        loanDto.installmentChargePeriod = loan.installmentChargePeriod;
        loanDto.creditLimit = loan.creditLimit;
        loanDto.accountID = loan.accountID;
        loanDto.loanType = loan.loanType;
        loanDto.accountName = loan.accountName;
        loanDto.processedTime = loan.processedTime;
        loanDto.receivedTime = String.valueOf(new java.sql.Timestamp(System.nanoTime()));
    }
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