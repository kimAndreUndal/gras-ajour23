package com.gras.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Schema(name = "Customer", description = "Customer representation of a customer as defined in the specification")
public class CustomerDto {

    @JsonProperty("customerID")
    public String customerID;
    @JsonProperty("financialInstitutionID")
    public String financialInstitutionID;
    @JsonProperty("loans")
    private List<LoanDto> loans = new ArrayList<>();
    public CustomerDto() {
    }

//    public void addLoanToLoanType(String type, LoanDto loan) {
//        List<LoanTypeDto> types = getLoanTypesOf(type);
//
//        if (types.size() == 0) {
//            loanTypes.add(new LoanTypeDto(type, loan));
//        } else {
//            types.forEach(loanType -> loanType.addLoan(loan));
//        }
//    }
//
//    private List<LoanTypeDto> getLoanTypesOf(String type) {
//        return loanTypes.stream()
//                .filter(loanType -> loanType.type.equals(type))
//                .collect(Collectors.toList());
//    }

    public List<LoanDto>  getLoan() {
        return loans;
    }

    @Override
    public String toString() {
        return "CustomerDto{" +
                "customerID='" + customerID + '\'' +
                ",financialInstitutionID='" + financialInstitutionID + '\'' +
                '}';
    }
}
