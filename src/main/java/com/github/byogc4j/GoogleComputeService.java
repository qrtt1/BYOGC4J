package com.github.byogc4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.byogc4j.annotation.Defaults;
import com.github.byogc4j.annotation.HttpMethod;
import com.github.byogc4j.annotation.Name;
import com.github.byogc4j.annotation.PathParamsTemplate;
import com.github.byogc4j.annotation.RequestBodyTemplate;
import com.github.byogc4j.annotation.RootUri;
import com.github.byogc4j.annotation.Verb;
import com.github.byogc4j.util.GenericUrlBuilder;
import com.github.byogc4j.util.NamedTemplate;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.Json;
import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoogleComputeService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleComputeService.class);

    private Map<Class<?>, AbstractGoogleJsonClient> serviceClients = new HashMap<Class<?>, AbstractGoogleJsonClient>();

    public void registerClient(Class<?> serviceClass, AbstractGoogleJsonClient client) {
        serviceClients.put(serviceClass, client);
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> serviceClass, Class<T> resourceClass) {
        checkServiceClassPrecondition(serviceClass);

        RootUri rootUri = serviceClass.getAnnotation(RootUri.class);
        return (T) Proxy.newProxyInstance(GoogleComputeService.class.getClassLoader(), new Class[] { resourceClass },
                new ServiceInvocationHander<T>(serviceClients.get(serviceClass), resourceClass, rootUri));
    }

    protected void checkServiceClassPrecondition(Class<?> serviceClass) {
        if (!serviceClass.isAnnotationPresent(RootUri.class)) {
            throw new RuntimeException("class[" + serviceClass + "] not annotated with @RootUri");
        }

        if (!serviceClients.containsKey(serviceClass)) {
            throw new RuntimeException("api client for class[" + serviceClass + "] not found");
        }
    }

    public JsonObject getUrl(Class<?> serviceClass, String url) {
        try {
            checkServiceClassPrecondition(serviceClass);
            AbstractGoogleJsonClient httpClient = serviceClients.get(serviceClass);
            return toJson(httpClient.getRequestFactory().buildGetRequest(new GenericUrl(url)).execute());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public JsonObject waitOperationDone(Class<?> serviceClass, JsonObject result) {
        String kind = result.get("kind").getAsString();
        String status = result.get("status").getAsString();
        if (!"compute#operation".equals(kind)) {
            return result;
        }
        if ("DONE".equals(status)) {
            return result;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        String selfLink = result.get("selfLink").getAsString();
        return waitOperationDone(serviceClass, getUrl(serviceClass, selfLink));
    }

    protected static JsonObject toJson(HttpResponse response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(response.getContent(), outputStream);
            return new JsonParser().parse(new String(outputStream.toByteArray())).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.disconnect();
            } catch (IOException ignored) {
            }
        }
    }

    static class ServiceInvocationHander<T> extends AbstractInvocationHandler {

        private AbstractGoogleJsonClient client;
        private RootUri rootUri;
        private Class<T> resourceClass;

        public ServiceInvocationHander(AbstractGoogleJsonClient client, Class<T> resourceClass, RootUri rootUri) {
            this.client = client;
            this.rootUri = rootUri;
            this.resourceClass = resourceClass;
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {

            Map<String, Object> parameters = createParametersMap(method, args);
            GenericUrl url = createRequestURL(method, parameters);
            logger.debug("create resource[{}] url {}.", resourceClass, url);

            HttpResponse response = null;
            try {
                response = doRequest(method, parameters, url);
            } catch (HttpResponseException e) {
                logger.error("exception with resource[{}] url {}.", resourceClass, url);
                logger.error(e.getMessage(), e);
                String message = e.getMessage();
                int jsonIndex = message.indexOf("\n");
                try {
                    return new JsonParser().parse(message.substring(jsonIndex)).getAsJsonObject();
                } catch (Exception ignored) {
                    JsonObject json = new JsonObject();
                    json.addProperty("exception", e.getMessage());
                    return json;
                }
            }

            return toJson(response);
        }

        protected Map<String, Object> createParametersMap(Method method, Object[] args) {
            Map<String, Object> parameters = Maps.newHashMap();

            Defaults defaults = method.getAnnotation(Defaults.class);
            if (defaults != null) {
                for (String defaultConfig : defaults.value()) {
                    String[] keyValuePair = defaultConfig.split("=");
                    parameters.put(keyValuePair[0], keyValuePair[1]);
                }
            }

            Annotation[][] annotations = method.getParameterAnnotations();
            for (int argIndex = 0; argIndex < annotations.length; argIndex++) {
                Annotation[] annotation = annotations[argIndex];
                if (annotation.length == 0) {
                    continue;
                }
                for (Annotation paramAnnotation : annotation) {
                    if (paramAnnotation instanceof Name) {
                        Name param = (Name) paramAnnotation;
                        parameters.put(param.value(), args[argIndex]);
                    }
                }
            }

            for (Object o : args) {
                if (o instanceof Param[]) {
                    Param[] params = (Param[]) o;
                    for (Param param : params) {
                        parameters.putAll(param.getMap());
                    }
                }
                if (o instanceof Param) {
                    Param param = (Param) o;
                    parameters.putAll(param.getMap());
                }
            }
            return parameters;
        }

        protected GenericUrl createRequestURL(Method method, Map<String, Object> parameters) {
            PathParamsTemplate template = method.getAnnotation(PathParamsTemplate.class);
            String url = rootUri.rootUrl() + rootUri.servicePath() + template.value();

            GenericUrl genericUrl = new GenericUrlBuilder(url).params(parameters).build();
            return genericUrl;
        }

        protected HttpContent createRequestBody(Method method, Map<String, Object> parameters) {
            RequestBodyTemplate requestBodyTemplate = method.getAnnotation(RequestBodyTemplate.class);
            HttpContent content = new ByteArrayContent(Json.MEDIA_TYPE, new NamedTemplate()
                    .templateJson(requestBodyTemplate.value(), parameters).toString().getBytes());
            return content;
        }

        protected HttpResponse doRequest(Method method, Map<String, Object> parameters, GenericUrl url)
                throws IOException {
            HttpResponse response = null;
            Verb verb = method.getAnnotation(Verb.class);
            if (verb.value() == HttpMethod.GET) {
                response = client.getRequestFactory().buildGetRequest(url).execute();
            }
            if (verb.value() == HttpMethod.POST) {
                HttpContent content = createRequestBody(method, parameters);
                response = client.getRequestFactory().buildPostRequest(url, content).execute();
            }
            return response;
        }

    }

}
