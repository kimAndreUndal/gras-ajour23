package com.gras.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.gras.dto.CustomerDto;
import com.gras.dto.LoanDto;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FromJsonToObjectsTest {

//    @Test
//    void testMethod() {
//        try{
//            JsonObject root = (JsonObject) JsonParser.parseReader(new FileReader("src/main/resources/json/file1.json"));
//            String json = new Gson().toJson(root);
//            String path = new JsonPath(json).get("providerID");
//
//
//        } catch (FileNotFoundException e){
//            System.out.println(e.getMessage());
//        }
//    }
}