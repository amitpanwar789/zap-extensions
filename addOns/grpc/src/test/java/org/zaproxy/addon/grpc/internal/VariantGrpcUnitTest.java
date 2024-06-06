/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2024 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.grpc.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.core.scanner.NameValuePair;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;

class VariantGrpcUnitTest {
    private VariantGrpc variantGrpc;

    @BeforeEach
    void setUp() {
        variantGrpc = new VariantGrpc();
    }

    @Test
    void shouldSetParameterForNestedMessage() throws HttpMalformedHeaderException {
        String encodedRequestBody =
                "AAAAAEEKEEhlbGxvLCBQcm90b2J1ZiESJwoESm9obhIGTWlsbGVyGhcKBEpvaG4QAhoNCgtIZWxsbyBXb3JsZBjqrcDlJA";
        String expectedOutput =
                "1:2::\"Hello, Protobuf!\"\n2:2N::{\n1:2::\"John\"\n2:2::\"Miller\"\n3:2N::{\n1:2::\"John\"\n2:0::2\n3:2N::{\n1:2::\"../../../../admin/\"\n}\n}\n}\n3:0::9876543210\n";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);
        variantGrpc.setMessage(httpMessage);
        String param = "2:2N.3:2N.3:2N.1:2";
        String payload = "../../../../admin/";
        NameValuePair originalPair =
                new NameValuePair(VariantGrpc.TYPE_GRPC_WEB_TEXT, param, "Hello World", 0);
        String newMessageWithPayload =
                variantGrpc.setParameter(httpMessage, originalPair, param, payload);

        assertEquals(
                "AAAAAEgKEEhlbGxvLCBQcm90b2J1ZiESLgoESm9obhIGTWlsbGVyGh4KBEpvaG4QAhoUChIuLi8uLi8uLi8uLi9hZG1pbi8Y6q3A5SQ=",
                httpMessage.getRequestBody().toString());
        assertEquals(expectedOutput, newMessageWithPayload);
    }

    @Test
    void shouldSetParameterWithSpecialCharacterPayload() throws HttpMalformedHeaderException {

        String encodedRequestBody =
                "AAAAAEEKEEhlbGxvLCBQcm90b2J1ZiESJwoESm9obhIGTWlsbGVyGhcKBEpvaG4QAhoNCgtIZWxsbyBXb3JsZBjqrcDlJA";
        String expectedOutput =
                "1:2::\"John\r\rSmith:\t67 Marcus' Rd\"\n2:2N::{\n1:2::\"John\"\n2:2::\"Miller\"\n3:2N::{\n1:2::\"John\"\n2:0::2\n3:2N::{\n1:2::\"Hello World\"\n}\n}\n}\n3:0::9876543210\n";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);

        variantGrpc.setMessage(httpMessage);
        String param = "1:2";
        String payload = "John\r\rSmith:\t67 Marcus' Rd";
        NameValuePair originalPair =
                new NameValuePair(VariantGrpc.TYPE_GRPC_WEB_TEXT, param, "Hello World", 0);
        String newMessageWithPayload =
                variantGrpc.setParameter(httpMessage, originalPair, param, payload);

        assertEquals(
                "AAAAAEsKGkpvaG4NDVNtaXRoOgk2NyBNYXJjdXMnIFJkEicKBEpvaG4SBk1pbGxlchoXCgRKb2huEAIaDQoLSGVsbG8gV29ybGQY6q3A5SQ=",
                httpMessage.getRequestBody().toString());
        assertEquals(expectedOutput, newMessageWithPayload);
    }

    @Test
    void shouldSetParameterWithSimplePayload() throws HttpMalformedHeaderException {

        String encodedRequestBody =
                "AAAAAEEKEEhlbGxvLCBQcm90b2J1ZiESJwoESm9obhIGTWlsbGVyGhcKBEpvaG4QAhoNCgtIZWxsbyBXb3JsZBjqrcDlJA";
        String expectedOutput =
                "1:2::\"ls ../../../../../admin/\"\n2:2N::{\n1:2::\"John\"\n2:2::\"Miller\"\n3:2N::{\n1:2::\"John\"\n2:0::2\n3:2N::{\n1:2::\"Hello World\"\n}\n}\n}\n3:0::9876543210\n";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);

        variantGrpc.setMessage(httpMessage);
        String param = "1:2";
        String payload = "ls ../../../../../admin/";
        NameValuePair originalPair =
                new NameValuePair(VariantGrpc.TYPE_GRPC_WEB_TEXT, param, "Hello World", 0);
        String newMessageWithPayload =
                variantGrpc.setParameter(httpMessage, originalPair, param, payload);
        assertEquals(
                "AAAAAEkKGGxzIC4uLy4uLy4uLy4uLy4uL2FkbWluLxInCgRKb2huEgZNaWxsZXIaFwoESm9obhACGg0KC0hlbGxvIFdvcmxkGOqtwOUk",
                httpMessage.getRequestBody().toString());
        assertEquals(expectedOutput, newMessageWithPayload);
    }

    @Test
    void shouldExtractParametersFromGrpcMessage() throws HttpMalformedHeaderException {
        String encodedRequestBody =
                "AAAAADEKC2pvaG4gTWlsbGVyEB4aIDEyMzQgTWFpbiBTdC4gQW55dG93biwgVVNBIDEyMzQ1";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);

        variantGrpc.setMessage(httpMessage);
        List<NameValuePair> expectedParamList = new ArrayList<>();
        expectedParamList.add(
                new NameValuePair(VariantGrpc.TYPE_GRPC_WEB_TEXT, "1:2", "\"john Miller\"", 0));
        expectedParamList.add(new NameValuePair(VariantGrpc.TYPE_GRPC_WEB_TEXT, "2:0", "30", 1));
        expectedParamList.add(
                new NameValuePair(
                        VariantGrpc.TYPE_GRPC_WEB_TEXT,
                        "3:2",
                        "\"1234 Main St. Anytown, USA 12345\"",
                        2));

        assertEquals(expectedParamList, variantGrpc.getParamList());
    }

    @Test
    void shouldExtractParametersFromEmptyGrpcMessage() throws HttpMalformedHeaderException {
        String encodedRequestBody = "";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);

        variantGrpc.setMessage(httpMessage);
        List<NameValuePair> expectedParamList = new ArrayList<>();

        assertEquals(expectedParamList, variantGrpc.getParamList());
    }

    @Test
    void shouldExtractParametersFromCorruptedGrpcMessage() throws HttpMalformedHeaderException {
        String encodedRequestBody = "AAAAADEKC2pvaG4gTWpbiBTdC4gQW55dG93biwgVVNBIDEyMzQ1";

        HttpMessage httpMessage = createHttpMessage(encodedRequestBody);

        List<NameValuePair> expectedParamList = new ArrayList<>();
        assertEquals(expectedParamList, variantGrpc.getParamList());
    }

    HttpMessage createHttpMessage(String encodedRequestBody) throws HttpMalformedHeaderException {
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
        httpRequestHeader.setMessage("POST /abc/xyz HTTP/1.1");
        httpRequestHeader.setHeader(HttpHeader.CONTENT_TYPE, "application/grpc-web-text");
        HttpMessage httpMessage = new HttpMessage(httpRequestHeader);
        httpMessage.setRequestBody(encodedRequestBody);
        return httpMessage;
    }
}
