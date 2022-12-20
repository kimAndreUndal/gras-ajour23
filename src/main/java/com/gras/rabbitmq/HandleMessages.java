package com.gras.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gras.database.DatabaseHandler;
import com.gras.dto.CustomerDto;
import com.gras.dto.DocumentDto;
import com.gras.dto.LoanDto;
import com.gras.util.MapFromJson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

@ApplicationScoped
public class HandleMessages {
    private static final Logger logger = LoggerFactory.getLogger(HandleMessages.class);
    @Inject
    DatabaseHandler databaseHandler;

    @Inject
    MapFromJson mapFromJson;

    boolean shutdown;

    public boolean handleMessage(Delivery delivery, Channel channel)  {
        try{
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
            //get providerID
            //get postType

            //get customerDTO
            //get loanDto
            ObjectMapper objectMapper = new ObjectMapper();

            DocumentDto document = objectMapper.readValue(json, new TypeReference<>() {});
            LoanDto loanDto = mapFromJson.getLoan(document);
            String financialInstitutionID = mapFromJson.getCustomerDto(document).get("financialInstitutionID");
            String customerID = mapFromJson.getCustomerDto(document).get("customerID");
            String postType = mapFromJson.getpostType(document);
            String providerID = mapFromJson.getProviderID(document);
            String loanType = loanDto.loanType;
            String accountID = loanDto.accountID;
            boolean rowsAffected = false;
            boolean deleted = false;
            while(true){
                if(shutdown){
                    logger.info("I" + " GRAS" + " Terminerer gras-ajour programmet.");
                    break;
                }

                boolean genAccountID = false;
                if (postType.equals("batch") || postType.equals("push") && (loanType == null)){
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    continue;
                }
                if(postType.equals("push") && (accountID == null || accountID.isEmpty())){
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    continue;
                }
                if(postType.equals("batch") && (accountID==null || accountID.isEmpty())){
                    rowsAffected = databaseHandler.deleteCustomersForFi(customerID, providerID, financialInstitutionID);
                    genAccountID = true;
                }
                if(postType.equals("batch") || postType.equals("push")){
                    if(postType.equals("batch")){
                        databaseHandler.insertOppdateringsLogg(providerID, financialInstitutionID);
                    }
                    String ownAccountID = "";

                    if(loanType.equals("creditFacility") &&
                            (loanDto.creditLimit == null || Integer.parseInt(loanDto.creditLimit)==0) &&
                            (loanDto.interestBearingBalance == null || Integer.parseInt(loanDto.interestBearingBalance) == 0) &&
                            (loanDto.nonInterestBearingBalance == null) || Integer.parseInt(loanDto.nonInterestBearingBalance) == 0){
                                deleted = true;
                                rowsAffected = databaseHandler.
                                deleteLoan(customerID, providerID, financialInstitutionID, loanType, accountID, Timestamp.valueOf(loanDto.receivedTime));
                                logger.info(String.format("TYPE: DELETE loanType: %s, accountId %s", loanType, accountID));
                    }
                    else if(loanType.equals("repaymentLoan") && (loanDto.balance == null || Integer.parseInt(loanDto.balance) == 0)){
                        deleted = true;
                        rowsAffected = databaseHandler.deleteLoan(customerID, providerID, financialInstitutionID, loanType, accountID, Timestamp.valueOf(loanDto.receivedTime));
                        logger.info(String.format("TYPE: DELETE loanType: %s, accountID %s", loanType, accountID));
                    }
                    else {
                        if(!genAccountID){
                            ownAccountID = accountID;
                        }
                        rowsAffected = databaseHandler.upsertLoan(customerID, providerID, financialInstitutionID, loanType, ownAccountID, loanDto.accountName, Float.parseFloat(loanDto.originalBalance), Float.parseFloat(loanDto.balance), loanDto.terms, Float.parseFloat(loanDto.interestBearingBalance), loanDto.nonInterestBearingBalance, Integer.parseInt(loanDto.effectiveInterestRate), Integer.parseInt(loanDto.nominalInterestRate), Float.parseFloat(loanDto.installmentCharges), loanDto.installmentChargePeriod, Integer.parseInt(loanDto.coBorrower), Float.parseFloat(loanDto.creditLimit), Timestamp.valueOf(loanDto.processedTime), Timestamp.valueOf(loanDto.processedTime));                  }
                        logger.info(String.format("TYPE: UPSERT loanType: %s, accountID: %s", loanType, accountID));
                }
                if (postType.equals("push")){
                    rowsAffected = databaseHandler.upsertLoanPush(providerID, financialInstitutionID, accountID, loanDto.accountName, Timestamp.valueOf(loanDto.receivedTime), deleted);
                }
            }

        }catch (IOException e){
            logger.error("handleMessage: " + e.getMessage());
        }


        boolean queried = false;

        return queried;
    }
}
