package com.github.byogc4j.service;

import com.github.byogc4j.annotation.HttpMethod;
import com.github.byogc4j.annotation.Name;
import com.github.byogc4j.annotation.PathParamsTemplate;
import com.github.byogc4j.annotation.RootUri;
import com.github.byogc4j.annotation.Verb;
import com.google.gson.JsonObject;

@RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/analytics/v3/data/realtime")
public interface AnalyticsRealtime {

    public interface Realtime {

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("?ids=:id&metrics=rt%3AactiveUsers")
        public JsonObject getActiveUser(@Name("id") String id);

    }

}
