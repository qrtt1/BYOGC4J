package com.github.byogc4j.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.util.IOUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NamedTemplate {

    public String template(String path, Map<String, Object> variables) {

        String template = readTemplate(path);
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group(1);
            if (!variables.containsKey(variable)) {
                throw new IllegalArgumentException("no such variable: " + variable);
            }
            matcher.appendReplacement(sb, "" + variables.get(variable));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public JsonObject templateJson(String path, Map<String, Object> variables) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(template(path, variables)).getAsJsonObject();
    }

    protected String readTemplate(String path) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = null;
        try {
            input = NamedTemplate.class.getResourceAsStream(path);
            IOUtils.copy(input, output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
        }

        return new String(output.toByteArray());
    }

}
