package com.github.byogc4j.service;

import com.github.byogc4j.Param;
import com.github.byogc4j.annotation.Defaults;
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
        @Defaults({ "metadata=" })
        public JsonObject insert(@Name("project") String project, @Name("zone") String zone, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/zones/:zone/instances")
        public JsonObject list(@Name("project") String project, @Name("zone") String zone);

    }

    public interface ForwardingRules {

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/regions/:region/forwardingRules")
        @RequestBodyTemplate("/forwardingRules.insert.json")
        public JsonObject insert(@Name("project") String project, @Name("region") String region,
                @Name("name") String name, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/regions/:region/forwardingRules")
        public JsonObject list(@Name("project") String project, @Name("region") String region);
    }

    public interface TargetPools {

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/regions/:region/targetPools/:targetPool/addInstance")
        @RequestBodyTemplate("/targetPools.addInstance.json")
        public JsonObject addInstance(@Name("project") String project, @Name("region") String region,
                @Name("targetPool") String targetPool, @Name("instance") String instance);

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/regions/:region/targetPools")
        @RequestBodyTemplate("/targetPools.insert.json")
        public JsonObject insert(@Name("project") String project, @Name("region") String region,
                @Name("name") String name, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/regions/:region/targetPools")
        public JsonObject list(@Name("project") String project, @Name("region") String region);
    }

    public interface HttpHealthChecks {

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/global/httpHealthChecks")
        @RequestBodyTemplate("/httpHealthChecks.insert.json")
        @Defaults({ "checkIntervalSec=5", "healthyThreshold=2", "unhealthyThreshold=2", "timeoutSec=5", "port=80" })
        public JsonObject insert(@Name("project") String project, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/global/httpHealthChecks")
        public JsonObject list(@Name("project") String project);

    }
}
