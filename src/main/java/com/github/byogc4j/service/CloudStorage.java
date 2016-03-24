package com.github.byogc4j.service;

import java.io.InputStream;

import com.github.byogc4j.Param;
import com.github.byogc4j.annotation.HttpMethod;
import com.github.byogc4j.annotation.Name;
import com.github.byogc4j.annotation.OptionalQueryParameters;
import com.github.byogc4j.annotation.PathParamsTemplate;
import com.github.byogc4j.annotation.RequestBody;
import com.github.byogc4j.annotation.RootUri;
import com.github.byogc4j.annotation.Verb;
import com.github.byogc4j.service.handler.CloudStorageObjectUploadHandler;
import com.google.gson.JsonObject;

@RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/storage/v1/b")
public interface CloudStorage {

    public interface Buckets {

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("?project=:project")
        @OptionalQueryParameters({ "maxResults", "pageToken", "prefix", "projection" })
        public JsonObject list(@Name("project") String project, Param... param);

    }

    public interface Objects {

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:bucket/o")
        @OptionalQueryParameters({ "delimiter", "maxResults", "pageToken", "prefix", "projection" })
        public JsonObject list(@Name("bucket") String bucket, Param... param);

        // TODO support more uploadType
        @RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/upload/storage/v1/b")
        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:bucket/o?uploadType=media&name=:name")
        @RequestBody(CloudStorageObjectUploadHandler.class)
        public JsonObject insert(@Name("bucket") String bucket, @Name("name") String name, InputStream inputStream);

    }

}
