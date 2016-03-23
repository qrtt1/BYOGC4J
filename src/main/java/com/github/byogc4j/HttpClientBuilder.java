package com.github.byogc4j;

import com.github.byogc4j.annotation.RootUri;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.util.Utils;

public class HttpClientBuilder extends AbstractGoogleJsonClient.Builder {

    protected HttpClientBuilder(Class<?> serviceClass, GoogleCredential httpRequestInitializer) {
        super(Utils.getDefaultTransport(), Utils.getDefaultJsonFactory(), getRootUrl(serviceClass),
                getServicePath(serviceClass), httpRequestInitializer, false);
        setApplicationName("ClientFor" + serviceClass.getName());
    }

    private static String getServicePath(Class<?> serviceClass) {
        return serviceClass.getAnnotation(RootUri.class).rootUrl();
    }

    private static String getRootUrl(Class<?> serviceClass) {
        return serviceClass.getAnnotation(RootUri.class).servicePath();
    }

    @Override
    public AbstractGoogleJsonClient build() {
        return new DefaultAPICaller(this);
    }

    static class DefaultAPICaller extends AbstractGoogleJsonClient {

        protected DefaultAPICaller(Builder builder) {
            super(builder);
        }

    }

}
