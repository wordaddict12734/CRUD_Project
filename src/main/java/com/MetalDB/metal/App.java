package com.MetalDB.metal;

import java.sql.Connection;

import LongTermDBManagement.DBConnection;

public class App {
    public static void main(String[] args) {
        // Try to get a connection
        Connection conn = DBConnection.getConnection();

        // Check if connection was successful
        if (conn != null) {
            System.out.println("✅ DB connection established from App.java!");
        } else {
            System.out.println("❌ DB connection failed in App.java.");
        }
    }
}
