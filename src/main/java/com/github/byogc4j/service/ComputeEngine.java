package com.github.byogc4j.service;

import com.github.byogc4j.Param;
import com.github.byogc4j.annotation.HttpMethod;
import com.github.byogc4j.annotation.Name;
import com.github.byogc4j.annotation.PathParamsTemplate;
import com.github.byogc4j.annotation.RequestBodyTemplate;
import com.github.byogc4j.annotation.RootUri;
import com.github.byogc4j.annotation.Verb;
import com.google.gson.JsonObject;

@RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/compute/v1/projects")
public interface ComputeEngine {

    public interface Instances {

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/zones/:zone/instances")
        @RequestBodyTemplate("/instances.insert.json")
        public JsonObject insert(@Name("project") String project, @Name("zone") String zone, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/zones/:zone/instances")
        public JsonObject list(@Name("project") String project, @Name("zone") String zone);

    }

}
