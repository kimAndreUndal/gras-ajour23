package com.gras.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DocumentDto {
    @JsonProperty("providerID")
    public String providerID;
    @JsonProperty("posttype")
    public String posttype;

    @JsonProperty("customers")
    public  List<CustomerDto> customers;
}
