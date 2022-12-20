package com.gras.rabbitmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gras.database.DatabaseHandler;
import com.gras.dto.DocumentDto;
import com.gras.dto.LoanDto;
import com.gras.util.Utils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;

@ApplicationScoped
public class HandleMessages {
    private static final Logger logger = LoggerFactory.getLogger(HandleMessages.class);
//    @Inject
//    DatabaseHandler databaseHandler;

//    @Inject
//    MapFromJson mapFromJson;

    boolean shutdown;

    public boolean handleMessage(Delivery delivery, Channel channel)  {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        try{
            String json = new String(delivery.getBody(), StandardCharsets.UTF_8);
            //get providerID
            //get postType

            //get customerDTO
            //get loanDto
            ObjectMapper objectMapper = new ObjectMapper();
            Utils utils = new Utils();
            DocumentDto document = objectMapper.readValue(json, new TypeReference<>() {});
            LoanDto loanDto = utils.getLoan(document);
            String financialInstitutionID = utils.getCustomerDto(document).get("financialInstitutionID");
            String customerID = utils.getCustomerDto(document).get("customerID");
            String postType = utils.getpostType(document);
            String providerID = utils.getProviderID(document);
            String loanType = loanDto.loanType;
            String accountID = loanDto.accountID;
            boolean rowsAffected = false;
            System.out.println("org: "+loanDto.originalBalance);
            int deleted = 0;


                boolean genAccountID = false;
                if (postType.equals("batch") || postType.equals("push") && loanType == null){
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
                if(postType.equals("push") && (accountID == null || accountID.isEmpty())){
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
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

                    assert loanType != null;
                    if(loanType.equals("creditFacility") &&
                            (loanDto.creditLimit == null || Float.parseFloat(loanDto.creditLimit) == 0) &&
                            (loanDto.interestBearingBalance == null || Float.parseFloat(loanDto.interestBearingBalance) == 0) &&
                            (loanDto.nonInterestBearingBalance == null|| Float.parseFloat(loanDto.nonInterestBearingBalance) == 0) ){
                                deleted = 1;
                                rowsAffected = databaseHandler.
                                deleteLoan(customerID, providerID, financialInstitutionID, loanType, accountID,  loanDto.processedTime);
                                logger.info(String.format("TYPE: DELETE loanType: %s, accountId %s", loanType, accountID));
                    }
                    else if(loanDto.loanType.equals("repaymentLoan") && (loanDto.balance == null || loanDto.balance.equals("0"))){
                        deleted = 1;
                        rowsAffected = databaseHandler.deleteLoan(customerID, providerID, financialInstitutionID, loanType, accountID,  loanDto.processedTime);
                        logger.info(String.format("TYPE: DELETE loanType: %s, accountID %s", loanType, accountID));
                    }
                    else {
                        if(!genAccountID){
                            ownAccountID = accountID;
                        }
                        rowsAffected = databaseHandler.upsertLoan(
                                customerID,
                                providerID,
                                financialInstitutionID,
                                loanType,
                                ownAccountID,
                                loanDto);
                    }
                        logger.info(String.format("TYPE: UPSERT loanType: %s, accountID: %s", loanType, accountID));
                }
                if (postType.equals("push")){
                    rowsAffected = databaseHandler.upsertLoanPush(providerID, financialInstitutionID, accountID, loanDto.accountName, (loanDto.processedTime), deleted);
                }

            return rowsAffected;
        }catch (IOException e){
            logger.error("handleMessage: " + e.getMessage());
        }
        return false;
    }
}
