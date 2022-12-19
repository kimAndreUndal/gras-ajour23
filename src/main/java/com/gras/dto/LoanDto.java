package com.gras.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.sql.Timestamp;
@Schema(name = "Loan", description = "Loan representing a customer loan as defined in the specification")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanDto {
    @JsonProperty("accountID")
    public String accountID;

    @JsonProperty("loanType")
    public String loanType;
    @JsonProperty("timeStamp")
    public String receivedTime;
    @JsonProperty("originalBalance")
    public String originalBalance;
    @JsonProperty("interestBearingBalance")
    public String interestBearingBalance;

    @JsonProperty("nonInterestBearingBalance")
    public String nonInterestBearingBalance;

    @JsonProperty("creditLimit")
    public String creditLimit;
    @JsonProperty("balance")
    public String balance;

    @JsonProperty("terms")
    public String terms;
    @JsonProperty("coBorrower")
    public String coBorrower;
    @JsonProperty("nominalInterestRate")
    public String nominalInterestRate;
    @JsonProperty("installmentCharges")
    public String installmentCharges;
    @JsonProperty("installmentChargePeriod")
    public String installmentChargePeriod;
    @JsonProperty("processedTime")
    public String processedTime;
    @JsonProperty("accountName")

    public String accountName;

    @Override
    public String toString() {
        return "LoanDto{" +
                "accountID='" + accountID + '\'' +
                ", loanType='" + loanType + '\'' +
                ", receivedTime='" + receivedTime + '\'' +
                ", originalBalance='" + originalBalance + '\'' +
                ", interestBearingBalance='" + interestBearingBalance + '\'' +
                ", nonInterestBearingBalance='" + nonInterestBearingBalance + '\'' +
                ", creditLimit='" + creditLimit + '\'' +
                ", balance='" + balance + '\'' +
                ", terms='" + terms + '\'' +
                ", coBorrower='" + coBorrower + '\'' +
                ", nominalInterestRate='" + nominalInterestRate + '\'' +
                ", installmentCharges='" + installmentCharges + '\'' +
                ", installmentChargePeriod='" + installmentChargePeriod + '\'' +
                ", processedTime='" + processedTime + '\'' +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
