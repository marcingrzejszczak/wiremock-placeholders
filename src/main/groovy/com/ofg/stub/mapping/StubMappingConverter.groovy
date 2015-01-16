package com.ofg.stub.mapping

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.util.regex.Pattern

class StubMappingConverter {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(/^\$\{(.*)\}:\$\{(.*)\}$/)

    static StubMapping toStubMappingOnServerSide(String stubMapping) {
        def json = new JsonSlurper().parseText(stubMapping)
        convertPlaceholders(json as Map, getSecondMatchingGroupForPlaceholderPattern())
        return StubMapping.buildFrom(JsonOutput.toJson(json))
    }

    static StubMapping toStubMappingOnClientSide(String stubMapping) {
        def json = new JsonSlurper().parseText(stubMapping)
        convertPlaceholders(json as Map, getFirstMatchingGroupForPlaceholderPattern())
        return StubMapping.buildFrom(JsonOutput.toJson(json))
    }

    private static void convertPlaceholders(Map map, Closure closure) {
        map.each {
            if(it instanceof Map.Entry) {
                Map.Entry entry = it as Map.Entry
                if (entry.value instanceof String) {
                    String value = entry.value as String
                    entry.value = closure(value)
                } else if (entry.value instanceof Map) {
                    convertPlaceholders(entry.value as Map, closure)
                }
            }
        }
    }

    private static Closure<Object> getFirstMatchingGroupForPlaceholderPattern() {
        return { String value ->
            getGroupFromMatchingPattern(value, 1)
        }
    }

    private static Closure<Object>getSecondMatchingGroupForPlaceholderPattern() {
        return { String value ->
            getGroupFromMatchingPattern(value, 2)
        }
    }

    private static Object getGroupFromMatchingPattern(String value, int groupNumber) {
        return value.matches(PLACEHOLDER_PATTERN) ? PLACEHOLDER_PATTERN.matcher(value)[0][groupNumber] : value
    }

}
