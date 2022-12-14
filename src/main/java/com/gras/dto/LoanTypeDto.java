package com.gras.dto;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
@Schema(name = "LoanType", description = "Loans grouped by loantype")
public class LoanTypeDto {
    public String type;
    public List<LoanDto> loans = new ArrayList<>();

    public LoanTypeDto(String type, LoanDto loanDto) {
        this.type = type;
        loans.add(loanDto);
    }

    void addLoan(LoanDto loanDto) {
        loans.add(loanDto);
    }
}
