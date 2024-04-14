package com.pt.entity;

import lombok.Data;

@Data
public class MailConfig {
    public static final String HOST_NAME = "smtp.gmail.com";

    public static final int SSL_PORT = 465; // Port for SSL

    public static final int TSL_PORT = 587; // Port for TLS/STARTTLS

    public static final String APP_EMAIL = "phuchtran18@gmail.com"; // your email

    public static final String APP_PASSWORD = "ckzhhbgjdzhcgknb"; // your password

    public static final String RECEIVE_EMAIL = "phuchtran18@gmail.com";
}
