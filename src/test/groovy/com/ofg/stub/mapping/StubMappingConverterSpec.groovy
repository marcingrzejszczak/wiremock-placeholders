package com.ofg.stub.mapping

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.http.RequestMethod.GET

class StubMappingConverterSpec extends Specification {

    public static final File STUB_WITHOUT_PLACEHOLDERS = new File('src/test/resources/stubs/no_placeholders.json')
    public static final File STUB_WITH_PLACEHOLDERS = new File('src/test/resources/stubs/with_placeholder.json')
    public static final File STUB_WITH_JSON_BODY_PLACEHOLDER = new File('src/test/resources/stubs/with_json_body_placeholder.json')

    def 'should convert file with stub mapping without placeholders for server side'() {
        when:
            StubMapping stubMapping = StubMappingConverter.toStubMappingOnServerSide(STUB_WITHOUT_PLACEHOLDERS.text)

        then:
            with(stubMapping) {
                request.method == GET
                request.urlPattern == '/ping'
                response.status == 200
                response.body == 'pong'
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }

    def 'should convert a placeholder containing JSON field by returning a concrete value for the server side test'() {
        when:
            StubMapping stubMapping = StubMappingConverter.toStubMappingOnServerSide(STUB_WITH_PLACEHOLDERS.text)

        then:
            with(stubMapping) {
                request.method == GET
                request.urlPattern == '/12'
                response.status == 200
                response.body == 'pong'
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }

    def 'should convert file with stub mapping without placeholders for client side'() {
        when:
            StubMapping stubMapping = StubMappingConverter.toStubMappingOnClientSide(STUB_WITHOUT_PLACEHOLDERS.text)

        then:
            with(stubMapping) {
                request.method == GET
                request.urlPattern == '/ping'
                response.status == 200
                response.body == 'pong'
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }

    def 'should convert a placeholder containing JSON field by returning a regexp for the client side test'() {
        when:
            StubMapping stubMapping = StubMappingConverter.toStubMappingOnClientSide(STUB_WITH_PLACEHOLDERS.text)

        then:
            with(stubMapping) {
                request.method == GET
                request.urlPattern == '/[0-9]{2}'
                response.status == 200
                response.body == 'pong'
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }

    def 'should convert a placeholder containing JSON field by returning a regexp for the client side test2'() {
        when:
            StubMapping stubMapping = StubMappingConverter.toStubMappingOnClientSide(STUB_WITH_JSON_BODY_PLACEHOLDER.text)

        then:
            with(stubMapping) {
                request.method == GET
                request.urlPattern == '/[0-9]{2}'
                response.status == 200
                response.body == '''{"date":"2015-01-14"}'''
                response.headers.contentTypeHeader.mimeTypePart() == 'text/plain'
            }
    }

}
