package com.cscjoke.bitcoinj.bean;

public class MessageBean {
    private int type;
    private String msg;

    public MessageBean(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public MessageBean() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
