package com.github.byogc4j.service;

import java.util.Collection;

import com.google.common.collect.Sets;

public class ComputeEnginsScopes {

    public static Collection<String> scopes() {
        return Sets.newHashSet("https://www.googleapis.com/auth/compute",
                "https://www.googleapis.com/auth/cloud-platform");
    }
}
