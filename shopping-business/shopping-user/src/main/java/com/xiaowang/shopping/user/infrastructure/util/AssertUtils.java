package com.xiaowang.shopping.user.infrastructure.util;

import java.util.Objects;

public class AssertUtils {
    public static void notNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new RuntimeException(message);
        }
    }
}