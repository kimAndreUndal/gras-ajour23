package com.gras.dto;

import java.sql.Timestamp;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanDto {
    public String receivedTime;
    public String originalBalance;
    public String interestBearingBalance;
    public String nonInterestBearingBalance;
    public String creditLimit;
    public String balance;
    public String terms;
    public String coBorrower;
    public String financialInstitutionID;
    public String financialInstitutionEmail;
    public String financialInstitutionPhone;
    public String financialInstitutionUrl;
    public String financialInstitutionName;
    public String providerID;
    public String nominalInterestRate;
    public String installmentCharges;
    public String installmentChargePeriod;
    public String processedTime;
    public Integer selfProviderOfTheLoan;
}