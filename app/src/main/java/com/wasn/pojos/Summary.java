package com.wasn.pojos;

/**
 * POJO class to hold attributes of transaction summary
 *
 * @author erangaeb@gmail.com (eranga bandara)
 */
public class Summary {
    private String agent;
    private int count;
    private int total;
    private String time;

    public Summary(String agent, int count, int total, String time) {
        this.agent = agent;
        this.count = count;
        this.total = total;
        this.time = time;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
