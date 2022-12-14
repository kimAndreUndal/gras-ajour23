package com.gras.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDto {
    public String customerID;
    private List<LoanTypeDto> loanTypes = new ArrayList<>();

    public CustomerDto(String customerID) {
        this.customerID = customerID;
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
