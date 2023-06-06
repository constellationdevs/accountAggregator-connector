package coop.constellation.connectorservices.demoexternalaccounts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.Utility;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.custom.cufx.ExternalAccountContainer;
import com.xtensifi.cufx.*;
import com.xtensifi.custom.cufx.*;
import com.xtensifi.custom.cufx.TransactionContainer;
import com.xtensifi.connectorservices.common.workflow.ConnectorRequestData;
import com.xtensifi.connectorservices.common.workflow.CufxMapper;
import com.xtensifi.dspco.ConnectorMessage;
import com.xtensifi.dspco.ExternalServicePayload;
import com.xtensifi.dspco.ResponseStatusMessage;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemoExternalAccountImp {
    private final ConnectorLogging logger;
    static ObjectMapper jsonMapper = new ObjectMapper();
    private static Utility mspUtil;
    private static Map<String, String> headers;

    public void init(ConnectorMessage connectorMessage) {
        logger.info(connectorMessage, "Initializing Demo External Account Connector");

        // Getting the MspUtil instance.
        mspUtil = Utility.getInstance();

        // init static attributes
        headers = new HashMap<String, String>();

        // Setting the default headers
        setHeaders();
    }

    public static TransactionContainer mockTransactionResponse(ConnectorMessage connectorMessage, String accounts)
            throws JsonProcessingException {
        // logger.info(connectorMessage, "Creating transaction container for all
        // external accounts");

        // Returning a Transaction Container, using passed in list of external accounts
        // for the accountId array
        return jsonMapper.readValue(
                "{\"transactionMessage\":{\"messageContext\":{\"fiId\":\"symxchange\",\"includeBlankFields\":true,\"includeZeroNumerics\":true},\"transactionFilter\":{\"accountIdList\":{\"accountId\":["
                        + accounts + "]}},\"transactionList\":{\"transaction\":[]}}}",
                TransactionContainer.class);
    }

    private Deposit updateDeposit(ConnectorMessage connectorMessage)
            throws JsonMappingException, JsonProcessingException {
        // Pull the deposit id to update from global params
        String depositId = ConnectorRequestData.getConnectorParam("depositId", connectorMessage);
        // Get this member's accounts from connector message
        AccountContainer acctContainer = CufxMapper.convertToAccountContainer(
                ConnectorRequestData.getConnectorParam("accountContainer", connectorMessage));
        // Grab the from the core data
        List<Deposit> depositList = acctContainer.getDepositMessage().getDepositList().getDeposit().stream()
                .filter(d -> d.getAccountId().equals(depositId)).collect(Collectors.toList());

        if (depositList.size() == 1) {
            Deposit deposit = depositList.get(0);
            deposit.setDescription("I was updated by acct agg");
            // Add some custom data
            ValuePair vp = new ValuePair();
            vp.setName("custom");
            vp.setValue("data");
            CustomData customData = new CustomData();
            deposit.setCustomData(customData);
            deposit.getCustomData().getValuePair().add(vp);
            return deposit;
        } else {
            return null;
        }

    }

    private Deposit createDeposit(ConnectorMessage connectorMessage) {
        // Creates a new account to be added to the depositList, and maps mock values to
        // the new deposit object
        logger.info(connectorMessage, "Creating Deposit Account for Acct ID 4444-demoexternalaccount");
        Deposit deposit = new Deposit();
        deposit.setAccountId("4444-demoexternalaccount");
        Money money = new Money();
        money.setCurrencyCode(ISOCurrencyCodeType.USD);
        money.setExchangeRate(new BigDecimal(1.0));
        money.setValue(new BigDecimal(1111));
        deposit.setAvailableBalance(money);
        deposit.setDescription("Created from Connector 3");
        deposit.setIdType(IdType.ACTUAL);
        money = new Money();
        money.setCurrencyCode(ISOCurrencyCodeType.USD);
        money.setExchangeRate(new BigDecimal(1.0));
        money.setValue(new BigDecimal(500));
        deposit.setMinimumBalance(money);
        deposit.setSubType("0001");
        deposit.setType(AccountType.CHECKING);
        Instant instant = Instant.now();
        String dateTimeString = instant.toString();
        XMLGregorianCalendar date = null;
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
            date.setYear(1970);
            deposit.setCloseDate(date);
        } catch (DatatypeConfigurationException e) {
        }
        return deposit;
    }

    private Loan updateLoan(ConnectorMessage connectorMessage) throws JsonMappingException, JsonProcessingException {
        // Pull the loan id to update from global params
        String loanId = ConnectorRequestData.getConnectorParam("loanId", connectorMessage);
        // Get this member's accounts from connector message
        AccountContainer acctContainer = CufxMapper.convertToAccountContainer(
                ConnectorRequestData.getConnectorParam("accountContainer", connectorMessage));
        // Grab the from the core data
        List<Loan> loanList = acctContainer.getLoanMessage().getLoanList().getLoan().stream()
                .filter(d -> d.getAccountId().equals(loanId)).collect(Collectors.toList());

        if (loanList.size() == 1) {
            Loan loan = loanList.get(0);
            loan.setDescription("I was updated by acct agg");
            // Add some custom data
            ValuePair vp = new ValuePair();
            vp.setName("custom");
            vp.setValue("data");
            CustomData customData = new CustomData();
            loan.setCustomData(customData);
            loan.getCustomData().getValuePair().add(vp);
            return loan;
        } else {
            return null;
        }
    }

    private Loan createLoan(ConnectorMessage connectorMessage) {
        // Creates a new account to be added to the loanList, and maps mock values to
        // the new loan object
        logger.info(connectorMessage, "Creating Loan Account for Acct ID 5555-demoexternalaccount");
        Loan loan = new Loan();
        loan.setAccountId("5555-demoexternalaccount");
        Money money = new Money();
        money.setCurrencyCode(ISOCurrencyCodeType.USD);
        money.setExchangeRate(new BigDecimal(1.0));
        money.setValue(new BigDecimal(1111));
        loan.setAvailableBalance(money);
        loan.setDescription("Test Account");
        loan.setIdType(IdType.ACTUAL);
        money = new Money();
        money.setCurrencyCode(ISOCurrencyCodeType.USD);
        money.setExchangeRate(new BigDecimal(1.0));
        money.setValue(new BigDecimal(500));
        loan.setMinimumBalance(money);
        loan.setSubType("0001");
        loan.setType(AccountType.LOAN);
        Instant instant = Instant.now();
        String dateTimeString = instant.toString();
        XMLGregorianCalendar date = null;
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
            date.setYear(1970);
            loan.setCloseDate(date);
        } catch (DatatypeConfigurationException e) {
        }
        return loan;
    }

    private TransactionList.Transaction createTransaction(ConnectorMessage connectorMessage, String accountId) {
        // Creates new transaction, using the accountId passed in
        logger.info(connectorMessage, "Creating transaction for account: " + accountId);
        Money money = new Money();
        TransactionList.Transaction transaction = new TransactionList.Transaction();
        transaction.setType(TransactionType.DEBIT);
        transaction.setAccountId(accountId);
        money.setCurrencyCode(ISOCurrencyCodeType.USD);
        money.setExchangeRate(new BigDecimal("1.31"));
        money.setValue(new BigDecimal("58.42"));
        transaction.setAmount(money);
        transaction.setTransactionId("5555");
        transaction.setStatus(TransactionStatus.POSTED);

        Instant instant = Instant.now();
        String dateTimeString = instant.toString();
        XMLGregorianCalendar date = null;
        try {
            date = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
            date.setYear(2020);
            date.setDay(10);
            date.setMonth(7);
            transaction.setDateTimePosted(date);
            transaction.setDateTimeEffective(date);
            transaction.setDescription("This is a fake transaction");
            transaction.setSource(TransactionSource.BILL_PAY);
        } catch (DatatypeConfigurationException e) {
        }

        ValuePair vp = new ValuePair();
        vp.setName("custom");
        vp.setValue("data");
        CustomData customData = new CustomData();
        transaction.setCustomData(customData);
        transaction.getCustomData().getValuePair().add(vp);

        return transaction;
    }

    public ConnectorMessage getExternalAccounts(ConnectorMessage connectorMessage) throws Exception {
        logger.info(connectorMessage, "Invoked getExternalAccounts");
        String method = "DemoExternalAccountImp::getExternalAccounts";
        logger.info(connectorMessage, "Invoked " + method);
        ResponseStatusMessage responseStatusMessage = mspUtil.getSuccessResponse();
        connectorMessage.setResponseStatus(responseStatusMessage);
        AccountContainer acctContainer = CufxMapper.convertToAccountContainer(
                ConnectorRequestData.getConnectorParam("accountContainer", connectorMessage));
        // Create our mock response and add our new mock accounts to it
        ExternalAccountContainer mockResponse = new ExternalAccountContainer();

        mockResponse.setDepositMessage(acctContainer.getDepositMessage());
        mockResponse.setLoanMessage(acctContainer.getLoanMessage());
        mockResponse.getDepositMessage().getDepositList().getDeposit().clear();
        mockResponse.getLoanMessage().getLoanList().getLoan().clear();

        mockResponse.getDepositMessage().getDepositList().getDeposit().add(createDeposit(connectorMessage));
        mockResponse.getLoanMessage().getLoanList().getLoan().add(createLoan(connectorMessage));

        Deposit updatedDeposit = updateDeposit(connectorMessage);
        if (updatedDeposit != null) {
            mockResponse.getDepositMessage().getDepositList().getDeposit().add(updatedDeposit);
        }

        Loan updatedLoan = updateLoan(connectorMessage);
        if (updatedLoan != null) {
            mockResponse.getLoanMessage().getLoanList().getLoan().add(updatedLoan);
        }

        String json = CufxMapper.convertToExternalAccountJson(mockResponse);

        connectorMessage.setResponse(json);
        return connectorMessage;
    }

    public ConnectorMessage getExternalTransactions(ConnectorMessage connectorMessage) throws Exception {
        logger.info(connectorMessage, "Invoked getExternalTransactions");

        String method = "DemoExternalAccountImp::getExternalTransactions";
        logger.info(connectorMessage, "Invoked " + method);

        ResponseStatusMessage responseStatusMessage = mspUtil.getSuccessResponse();
        connectorMessage.setResponseStatus(responseStatusMessage);

        // Get incoming parameters in order to retrieve all external accounts this
        // connector has created / edited
        Map<String, String> params = getParams(connectorMessage);
        logger.info(connectorMessage, "PARAMS:");
        logger.info(connectorMessage, jsonMapper.writeValueAsString(params));
        // Initializing comma seperated string of accountId's
        String accountsString = "";
        // Initializing list of create / edited external accounts to be iterated over
        // below
        ArrayList<String> accountsArr = new ArrayList<String>();
        // Loop over params
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Search for params including accountId
            if (entry.getKey().toLowerCase().contains("accountid")) {
                // Add value to accounts array
                accountsArr.add(entry.getValue());
                // Create accounts string, handling comma seperation and quotes
                accountsString = accountsString.equals("") ? "\"" + entry.getValue() + "\""
                        : accountsString + ", \"" + entry.getValue() + "\"";
            }
        }

        // Create transaction container for all external accounts
        ExternalTransactionContainer externalTransactionContainer = new ExternalTransactionContainer();
        TransactionResult transactionResult = new TransactionResult();
        TransactionMessage transactionMessage = new TransactionMessage();
        TransactionList transactionList = new TransactionList();

        TransactionFilter transactionFilter = new TransactionFilter();
        AccountIdList accountIdList = new AccountIdList();

        // // create a new transaction for each external account and add to transaction
        // container
        for (String accountId : accountsArr) {
            transactionList.getTransaction()
                    .add(createTransaction(connectorMessage, accountId));
            accountIdList.getAccountId().add(accountId);
            transactionFilter.setAccountIdList(accountIdList);
        }

        transactionMessage.setTransactionList(transactionList);
        transactionMessage.setTransactionFilter(transactionFilter);

        transactionResult.setTransactionMessage(transactionMessage);
        externalTransactionContainer.setTransactionResult(transactionResult);

        String json = CufxMapper.convertToJson(externalTransactionContainer);

        logger.info(connectorMessage, json);

        connectorMessage.setResponse(json);

        return connectorMessage;
    }

    private static void setHeaders() {
        // Setting the default headers
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
    }

    public Map<String, String> getParams(final ConnectorMessage connectorMessage) {
        final Map<String, String> params = new HashMap<>();

        final ExternalServicePayload externalServicePayload = connectorMessage.getExternalServicePayload();

        if (externalServicePayload != null) {

            final CustomData paramPayload = externalServicePayload.getPayload();
            if (paramPayload != null)
                for (ValuePair valuePair : paramPayload.getValuePair()) {
                    params.putIfAbsent(valuePair.getName(), valuePair.getValue());
                }
        }
        return params;
    }
}