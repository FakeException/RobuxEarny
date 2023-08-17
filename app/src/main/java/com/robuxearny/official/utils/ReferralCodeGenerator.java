/*
 * Created by FakeException on 8/17/23, 11:05 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/17/23, 11:05 AM
 */

package com.robuxearny.official.utils;

import java.security.SecureRandom;

public class ReferralCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    public static String generateReferralCode() {
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }
}