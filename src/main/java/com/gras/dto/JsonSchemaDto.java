package com.gras.dto;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaDto {
    public String providerID;
    public String postType;
    public List<CustomerDto> customer = new ArrayList<>();
}
