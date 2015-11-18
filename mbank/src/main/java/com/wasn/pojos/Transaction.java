package com.wasn.pojos;

/**
 * To hold attributes of transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class Transaction {

    int id;
    String clinetName;
    String clinetNic;
    String clientAccountNo;
    String previousBalance;
    String transactionAmount;
    String currentBalance;
    String transactionTime;
    String transactionType;

    public Transaction(int id,
                       String clinetName,
                       String clinetNic,
                       String clientAccountNo,
                       String previousBalance,
                       String transactionAmount,
                       String currentBalance,
                       String transactionTime,
                       String transactionType) {
        this.id = id;
        this.clinetName = clinetName;
        this.clinetNic = clinetNic;
        this.clientAccountNo = clientAccountNo;
        this.previousBalance = previousBalance;
        this.transactionAmount = transactionAmount;
        this.currentBalance = currentBalance;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClinetName() {
        return clinetName;
    }

    public void setClinetName(String clinetName) {
        this.clinetName = clinetName;
    }

    public String getClinetNic() {
        return clinetNic;
    }

    public void setClinetNic(String clinetNic) {
        this.clinetNic = clinetNic;
    }

    public String getClientAccountNo() {
        return clientAccountNo;
    }

    public void setClientAccountNo(String clientAccountNo) {
        this.clientAccountNo = clientAccountNo;
    }

    public String getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(String previousBalance) {
        this.previousBalance = previousBalance;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

}
