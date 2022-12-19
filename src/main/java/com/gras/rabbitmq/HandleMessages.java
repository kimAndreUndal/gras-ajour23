package com.gras.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gras.database.DatabaseHandler;
import com.gras.dto.DocumentDto;
import com.gras.dto.LoanDto;
import com.gras.util.MapFromJson;
import com.rabbitmq.client.Channel;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

public class HandleMessages {
    @Inject
    DatabaseHandler databaseHandler;

    @Inject
    MapFromJson mapFromJson;

    public boolean handleMessage(String jsonBody, Channel channel){
        ObjectMapper objectMapper = new ObjectMapper();

        //get providerID
        //get postType

        //get customerDTO
        //get loanDto
        boolean queried = false;

        return queried;
    }
}
