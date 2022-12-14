package com.gras.dto;

import java.util.ArrayList;
import java.util.List;

public class CustomerDto {
    public String customerID;
    public String financialInstitutionID;

    public List<LoanDto> loans = new ArrayList<>();
}
