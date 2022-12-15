package com.gras.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gras.dto.CustomerDto;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class FromJsonToObjects {

    public void testMethod(){

        try{
            JsonElement root = new JsonParser().parse(new FileReader("src/main/resources/json/file1.json"));

            JsonObject object = root.getAsJsonObject().get("customers").getAsJsonObject();
            Gson gson = new Gson();
            for (Map.Entry<String, JsonElement> entry: object.entrySet()
                 ) {
               CustomerDto customerDto = gson.fromJson(entry.getValue(), CustomerDto.class);
                System.out.println(customerDto.getLoanTypes());
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }
}
