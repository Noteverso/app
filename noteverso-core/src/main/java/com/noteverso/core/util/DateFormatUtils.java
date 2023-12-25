package com.noteverso.core.util;

import java.time.Instant;

public class DateFormatUtils {
    // Get UTC String date
    public static String getUTCTime() {
        return Instant.now().toString();
    }

    public static void main(String[] args) {
        System.out.println(getUTCTime());
    }
}
