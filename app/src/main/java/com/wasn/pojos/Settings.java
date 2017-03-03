package com.wasn.pojos;

/**
 * POJO class to hold settings attributes
 */
public class Settings {
    String agent;
    String telephone;
    String printerAddress;

    public Settings(String agent, String telephone, String printerAddress) {
        this.agent = agent;
        this.telephone = telephone;
        this.printerAddress = printerAddress;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPrinterAddress() {
        return printerAddress;
    }

    public void setPrinterAddress(String printerAddress) {
        this.printerAddress = printerAddress;
    }
}

