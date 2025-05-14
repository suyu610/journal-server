package com.uuorb.journal.util;

import java.util.UUID;

public class IDUtil {

    private static final String ACTIVITY_PREDIX = "ac";

    private static final String USER_PREFIX = "us";

    private static final String MESSAGE_PREFIX = "ms";

    private static final String ORDER_PREFIX = "od";

    private static final String SMS_PREFIX = "sms";

    private static final String EXPENSE_PREFIX = "ex";

    public static String random(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length).toLowerCase();
    }

    public static String activityId() {
        return ACTIVITY_PREDIX + random(16);
    }

    public static String userId() {
        return USER_PREFIX + random(16);
    }

    public static String expenseId() {
        return EXPENSE_PREFIX + random(16);
    }

    public static String msgID() {
        return MESSAGE_PREFIX + random(16);
    }

    public static String getHashIdByUserId(String userId1, String userId2) {
        int hashCode1 = userId1.hashCode();
        int hashCode2 = userId2.hashCode();

        if (hashCode1 > hashCode2) {
            return String.format("%08x", Math.abs(hashCode1)) + String.format("%08x", Math.abs(hashCode2));
        } else {
            return String.format("%08x", Math.abs(hashCode2)) + String.format("%08x", Math.abs(hashCode1));
        }
    }

    public static String orderId() {
        return ORDER_PREFIX + random(16);
    }

    public static String smsId() {
        return SMS_PREFIX + random(15);
    }

    public static String generateUUID() {
        String uuidString = UUID.randomUUID().toString().replace("-", "");
        return uuidString.length() > 32 ? uuidString.substring(0, 32) : uuidString;
    }

}
