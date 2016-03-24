package com.github.byogc4j.service.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.github.byogc4j.util.RequestBodyHandler;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.util.IOUtils;

public class CloudStorageObjectUploadHandler extends RequestBodyHandler {

    @Override
    public HttpContent createRequestBody(Method method, Object[] args, Map<String, Object> parameters) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (Object o : args) {
            if (o instanceof InputStream) {
                InputStream input = (InputStream) o;
                try {
                    IOUtils.copy(input, output);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ByteArrayContent content = new ByteArrayContent(null, output.toByteArray());
        content.setType("application/octet-stream");
        return content;
    }

}
