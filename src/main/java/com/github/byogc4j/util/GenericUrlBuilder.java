package com.github.byogc4j.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.GenericUrl;

public class GenericUrlBuilder {

    private String template;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public GenericUrlBuilder(String template) {
        this.template = template;

    }

    public GenericUrlBuilder param(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public GenericUrlBuilder params(Map<String, Object> params) {
        parameters.putAll(params);
        return this;
    }

    public GenericUrl build() {

        Pattern pattern = Pattern.compile(":([^:/]+)");
        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if (!parameters.containsKey(matcher.group(1))) {
                throw new IllegalArgumentException("No value for the parameter [" + matcher.group() + "]");
            }
            matcher.appendReplacement(sb, "" + parameters.get(matcher.group(1)));
        }
        matcher.appendTail(sb);
        return new GenericUrl(sb.toString());

    }

}
