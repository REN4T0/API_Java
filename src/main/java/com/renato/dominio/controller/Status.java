package com.renato.dominio.controller;

public class Status {
    public String code;
    public String status;
    public String message;
    public Object more_info;

    public Status(String code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
