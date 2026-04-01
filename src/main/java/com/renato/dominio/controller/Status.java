package com.renato.dominio.controller;

public class Status {
    public String code;
    public String status;
    public String message;
    public long totalTime;
    public Object more_info;

    public Status(String code, String status, String message, long totalTime, Object more_info) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.totalTime = totalTime;
        this.more_info = more_info;
    }
}
