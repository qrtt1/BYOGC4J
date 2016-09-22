package com.github.byogc4j.service;

import java.util.Collection;

import com.google.common.collect.Sets;

public class AnalyticsRealtimeScopes {

    public static Collection<String> scopes() {
        return Sets.newHashSet("https://www.googleapis.com/auth/analytics",
                "https://www.googleapis.com/auth/analytics.readonly");
    }
}
