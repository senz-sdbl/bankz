package com.wasn.pojos;

/**
 * To hold attributes of transaction
 *
 * @author erangaeb@gmail.com (eranga bandara)
 * TODO implements Parcelable
 */
public class Transaction {

    int id;
    String clientName;
    String clientNic;
    String clientAccountNo;
    String previousBalance;
    String transactionAmount;
    String transactionTime;
    String transactionType;

    public Transaction(int id,
                       String clientName,
                       String clientNic,
                       String clientAccountNo,
                       String previousBalance,
                       String transactionAmount,
                       String transactionTime,
                       String transactionType) {
        this.id = id;
        this.clientName = clientName;
        this.clientNic = clientNic;
        this.clientAccountNo = clientAccountNo;
        this.previousBalance = previousBalance;
        this.transactionAmount = transactionAmount;
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientNic() {
        return clientNic;
    }

    public void setClientNic(String clientNic) {
        this.clientNic = clientNic;
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
