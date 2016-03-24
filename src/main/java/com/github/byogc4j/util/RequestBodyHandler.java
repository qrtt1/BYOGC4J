package com.github.byogc4j.util;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.api.client.http.HttpContent;

public abstract class RequestBodyHandler {

    public abstract HttpContent createRequestBody(Method method, Object[] args, Map<String, Object> parameters);
}
