package com.gras.database;

import com.gras.dto.LoanDto;
import io.agroal.api.AgroalDataSource;
import org.postgresql.util.internal.Nullness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@ApplicationScoped
public class DatabaseHandler {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);

//    @Inject
//    AgroalDataSource dataSource;
    private Connection connection(){
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username ="postgres";
        String pass = "pourrainet43";

        Connection connection = null;
        try{
            connection =DriverManager.getConnection(url, username, pass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;

    }

//    public int insertIntoGrasLoan(String customerId,
//                                      String providerId,
//                                      String financialInstitutionId,
//                                      String loanType,
//                                      String accountId,
//                                      String accountName,
//                                      Long originalBalance,
//                                      Long balance,
//                                      String terms,
//                                      Long interestBearingBalance,
//                                      Long nonInterestBearingBalance,
//                                      int effectiveInterestRate,
//                                      int nominalInterestRate,
//                                      Long installmentCharges,
//                                      String installmentChargesPeriod,
//                                      int coBorrower,
//                                      Long creditLimit,
//                                      Timestamp processedTime,
//                                      String processedTimeText
//    ){
//        int ok = 1;
//        String sql ="INSERT INTO gras.loan(\n" +
//                "customer_id, provider_id, financial_institution_id, loan_type, received_time, account_id, account_name, original_balance, balance, terms,\n" +
//                "\tinterest_bearing_balance, non_interest_bearing_balance, effective_interest_rate, nominal_interest_rate, installment_charges,\n" +
//                "\tinstallment_charges_period, co_borrower, credit_limit, processed_time,processed_time_text, delete_mark)\n" +
//                "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'false')\n" +
//                "ON CONFLICT ON CONSTRAINT pk_loan\n" +
//                "DO UPDATE SET (received_time, account_name, original_balance, balance, terms, interest_bearing_balance, non_interest_bearing_balance,\n" +
//                "\t\t\t   effective_interest_rate, nominal_interest_rate, installment_charges, installment_charges_period, co_borrower, credit_limit,\n" +
//                "\t\t\t   processed_time, processed_time_text, delete_mark) =(now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'false')\n" +
//                "WHERE (gras.loan.customer_id =? and gras.loan.provider_id =? and gras.loan.financial_institution_id = ? and gras.loan.loan_type = ?\n" +
//                "\tand gras.loan.account_id=? and gras.loan.processed_time <= ?)";
//        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)){
//            pstm.setString(1, customerId);
//            pstm.setString(2, providerId);
//            pstm.setString(3, financialInstitutionId);
//            pstm.setString(4, loanType);
//            pstm.setString(6, accountId);
//            pstm.setString(7, accountName);
//            pstm.setLong(8, originalBalance);
//            pstm.setLong(9, balance);
//            pstm.setString(10, terms);
//            pstm.setLong(11, interestBearingBalance);
//            pstm.setLong(12, nonInterestBearingBalance);
//            pstm.setLong(13, effectiveInterestRate);
//            pstm.setLong(14, nominalInterestRate);
//            pstm.setLong(15, installmentCharges);
//            pstm.setString(16, installmentChargesPeriod);
//            pstm.setInt(17, coBorrower);
//            pstm.setLong(18, creditLimit);
//            pstm.setTimestamp(19, processedTime);
//            pstm.setTimestamp(20, stringToTimestamp(processedTimeText));
//            pstm.setString(21, accountName);
//            pstm.setLong(22, originalBalance);
//            pstm.setLong(23, balance);
//            pstm.setString(24, terms);
//            pstm.setLong(25, interestBearingBalance);
//            pstm.setLong(26, nonInterestBearingBalance);
//            pstm.setLong(27, effectiveInterestRate);
//            pstm.setLong(28, nominalInterestRate);
//            pstm.setLong(29, installmentCharges);
//            pstm.setString(30, installmentChargesPeriod);
//            pstm.setInt(31, coBorrower);
//            pstm.setLong(32, creditLimit);
//            pstm.setTimestamp(33, processedTime);
//            pstm.setString(34, processedTimeText);
//            pstm.executeUpdate();
//
//        }catch (SQLException e){
//            logger.error("insertIntoGrasLoan: " + e.getMessage());
//            ok = 0;
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        return ok;
//    }

    public boolean upsertLoan(
            String customerId,
            String providerId,
            String financialInstitutionId,
            String loanType,
            String accountId,
            LoanDto loanDto
    ){
        boolean ok = true;


                String sql =
                        "INSERT INTO gras.loan(\n" +
                                "customer_id, provider_id, financial_institution_id, loan_type, received_time, account_id, account_name, original_balance, balance, terms,\n" +
                                "\tinterest_bearing_balance, non_interest_bearing_balance, effective_interest_rate, nominal_interest_rate, installment_charges,\n" +
                                "\tinstallment_charges_period, co_borrower, credit_limit, processed_time,processed_time_text, delete_mark)\n" +
                                "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'false')\n" +
                                "ON CONFLICT ON CONSTRAINT pk_loan\n" +
                                "DO UPDATE SET (received_time, account_name, original_balance, balance, terms, interest_bearing_balance, non_interest_bearing_balance,\n" +
                                "\t\t\t   effective_interest_rate, nominal_interest_rate, installment_charges, installment_charges_period, co_borrower, credit_limit,\n" +
                                "\t\t\t   processed_time, processed_time_text, delete_mark) =(now(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,'false')\n" +
                                "WHERE (gras.loan.customer_id =? and gras.loan.provider_id =? and gras.loan.financial_institution_id = ? and gras.loan.loan_type = ?\n" +
                                "\tand gras.loan.account_id=? and gras.loan.processed_time <= ?)";
        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)){
            /*INSERT STATEMENT*/
            pstm.setString(1, customerId);
            pstm.setString(2, providerId);
            pstm.setString(3, financialInstitutionId);
            pstm.setString(4, loanType);
            pstm.setString(5, accountId);
            pstm.setString(6, loanDto.accountName);
            isFloatNull(7, pstm, loanDto.originalBalance);
            isFloatNull(8, pstm, loanDto.balance);
            pstm.setString(9, loanDto.terms);
            isFloatNull(10, pstm, loanDto.interestBearingBalance);
            pstm.setString(11, loanDto.nonInterestBearingBalance);
            isIntegerNull(12, pstm, loanDto.effectiveInterestRate);
            isIntegerNull(13, pstm, loanDto.nominalInterestRate);
            isFloatNull(14, pstm, loanDto.installmentCharges);
            pstm.setString(15, loanDto.installmentChargePeriod);
            isIntegerNull(16, pstm, loanDto.coBorrower);
            isFloatNull(17, pstm, loanDto.creditLimit);
            pstm.setTimestamp(18, stringToTimestamp(loanDto.receivedTime));
            pstm.setTimestamp(19, stringToTimestamp(loanDto.receivedTime));

            /*PRIMARY KEY CONFLICT*/
            pstm.setString(20, loanDto.accountName);
            isFloatNull(21, pstm, loanDto.originalBalance);
            isFloatNull(22, pstm, loanDto.balance);
            pstm.setString(23, loanDto.terms);
            isFloatNull(24, pstm, loanDto.interestBearingBalance);
            pstm.setString(25, loanDto.nonInterestBearingBalance);
            isIntegerNull(26, pstm, loanDto.effectiveInterestRate);
            isIntegerNull(27, pstm, loanDto.nominalInterestRate);
            isFloatNull(28, pstm, loanDto.installmentCharges);
            pstm.setString(29, loanDto.installmentChargePeriod);
            isIntegerNull(30, pstm, loanDto.coBorrower);
            isFloatNull(31, pstm, loanDto.creditLimit);
            pstm.setTimestamp(32, stringToTimestamp(loanDto.receivedTime));
            pstm.setTimestamp(33, stringToTimestamp(loanDto.receivedTime));

            /*WHERE CLAUSE*/
            pstm.setString(34, customerId);
            pstm.setString(35, providerId);
            pstm.setString(36, financialInstitutionId);
            pstm.setString(37, loanType);
            pstm.setString(38, accountId);
            pstm.setTimestamp(39, stringToTimestamp(loanDto.processedTime));

            pstm.executeUpdate();
        }catch (SQLException | ParseException e){
            logger.error("upsertLoan: " + e.getMessage());
            ok = false;
        }
        return ok;
    }

    public boolean upsertLoanPush(
            String providerId,
            String financialInstitutionId,
            String accountId,
            String accountName,
            String processedTime,
            int deleted){

        boolean ok = true;
        String sql =
                "INSERT INTO gras.loan_push_logs(" +
                        "provider_id, " +
                        "financial_institution_id, " +
                        "received_time, " +
                        "account_id, " +
                        "account_name, " +
                        "processed_time, " +
                        "deleted) " +
                        "VALUES (?, ?, NOW(), ?, ?, ?, ?)";
        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, providerId);
            pstm.setString(2, financialInstitutionId);
            pstm.setString(3, accountId);
            pstm.setString(4, accountName);
            pstm.setTimestamp(5, (stringToTimestamp(processedTime)));
            pstm.setInt(6, deleted);
            pstm.executeUpdate();
        }catch (SQLException e){
            logger.error("upsertLoanPush: " + e.getMessage());
            ok = false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ok;
    }

    public boolean deleteLoan(String customerId,
                          String providerId,
                          String financialInstitutionId,
                          String loanType,
                          String accountId,
                          String processedTime) {
        boolean ok = true;
        String sql =
                "DELETE FROM gras.loan " +
                        "WHERE (gras.loan.customer_id =? " +
                        "and gras.loan.provider_id =? " +
                        "and gras.loan.financial_institution_id = ? " +
                        "and gras.loan.loan_type = ?" +
                        "and gras.loan.account_id=? " +
                        "and gras.loan.processed_time <= ?)";
        try(Connection connection = connection(); PreparedStatement pstm = connection.prepareStatement(sql)){
            pstm.setString(1, customerId);
            pstm.setString(2, providerId);
            pstm.setString(3, financialInstitutionId);
            pstm.setString(4, loanType);
            pstm.setString(5, accountId);
            pstm.setTimestamp(6, (stringToTimestamp(processedTime)));
            pstm.executeUpdate();
        }catch (SQLException e){
            logger.error("deleteLoan: " + e.getMessage());
            ok = false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return ok;
    }

    public boolean deleteCustomersForFi(String customerId, String providerId, String financialInstitutionId){
        boolean ok = true;
        String sql =
                "DELETE FROM gras.loan " +
                        "WHERE customer_id= ? " +
                        "and provider_id = ? " +
                        "and financial_institution_id = ?";
        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, customerId);
            pstm.setString(2, providerId);
            pstm.setString(3, financialInstitutionId);
            pstm.executeUpdate();
        }catch (SQLException e){
            logger.error("deleteCustomersForFi: " + e.getMessage());
            ok = false;
        }
        return  ok;
    }

    public boolean insertOppdateringsLogg(String providerId, String finanalInstitution){
        boolean ok = true;
        String sql =
                "insert into gras.oppdaterings_logg " +
                        "(provider_id,financial_institution_id,start_time)" +
                        "VALUES" +
                        "(?,?,now()::date)" +
                        "ON CONFLICT ON CONSTRAINT pk_oppdaterings_logg " +
                        "DO NOTHING";
        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, providerId);
            pstm.setString(2, finanalInstitution);
            pstm.executeUpdate();
        } catch (SQLException e) {
            logger.error("insertOppdateringsLogg: " + e.getMessage());
            ok = false;
        }
        return ok;
    }

    public boolean updateOppdateringsLogg(String providerId){
        boolean ok = true;
        String sql =
                "UPDATE gras.oppdaterings_logg " +
                        "SET stop_time = now():: date " +
                        "WHERE start_time = now():: date " +
                        "AND provider_id = ?";
        try(Connection conn = connection(); PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, providerId);
            pstm.executeUpdate();
        } catch (SQLException e) {
            logger.error("updateOppdateringsLogg: " + e.getMessage());
            ok = false;
        }
        return ok;
    }
    public static Timestamp stringToTimestamp(String date) throws ParseException {

//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date date1 = new Date();
//        String newDate = format.format(date1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String formmatedDate = format.format(new Date());
        Timestamp timestamp = Timestamp.valueOf(formmatedDate);
        return timestamp;
        //return DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss.SSSSSSSSS").withZone(ZoneId.systemDefault()).format(Instant.parse(date));
        //Date newDate = new SimpleDateFormat(formattedDate).get2DigitYearStart();
        //return new Timestamp(newDate.getTime());
    }

    private void isFloatNull(int index, PreparedStatement pstm, String val){
        try{

                if(val == null) pstm.setObject(index, null);
                else {

                    long i = Long.parseLong(val);
                    pstm.setLong(index, Long.parseLong(val));
                }
        } catch (SQLException e) {
           logger.error("isNull: " + e.getMessage());
        }
    }
    private void isIntegerNull(int index, PreparedStatement pstm, String val){
        try{
                if(val == null) pstm.setObject(index, null);
                else {
                    pstm.setInt(index, Integer.parseInt(val));
                }
        } catch (SQLException e) {
            logger.error("isNull: " + e.getMessage());
        }
    }
}
