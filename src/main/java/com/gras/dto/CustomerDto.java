package com.gras.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Schema(name = "Customer", description = "Customer representation of a customer as defined in the specification")
public class CustomerDto {
    public String customerID;
    private List<LoanTypeDto> loanTypes = new ArrayList<>();

    public CustomerDto(String customerID) {
        this.customerID = customerID;
    }

    public CustomerDto() {
    }

    public void addLoanToLoanType(String type, LoanDto loan) {
        List<LoanTypeDto> types = getLoanTypesOf(type);

        if (types.size() == 0) {
            loanTypes.add(new LoanTypeDto(type, loan));
        } else {
            types.forEach(loanType -> loanType.addLoan(loan));
        }
    }

    private List<LoanTypeDto> getLoanTypesOf(String type) {
        return loanTypes.stream()
                .filter(loanType -> loanType.type.equals(type))
                .collect(Collectors.toList());
    }

    public List<LoanTypeDto> getLoanTypes() {
        return loanTypes;
    }
}
