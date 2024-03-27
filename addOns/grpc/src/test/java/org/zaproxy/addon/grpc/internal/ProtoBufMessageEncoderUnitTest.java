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

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProtoBufMessageEncoderUnitTest {

    private ProtoBufMessageEncoder encoder;

    @BeforeEach
    public void setUp() {
        encoder = new ProtoBufMessageEncoder();
    }

    @Test
    void testStartEncodingWithEmptyInput() {
        String inputString = "";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(inputString);
        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assertEquals(null, encoder.getOutputEncodedMessage());
    }

    @Test
    void testStartEncodingWithNullInput() {
        try {
            encoder.encode(null);
        } catch (Exception e) {
        }
        assert (Arrays.equals(null, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithSimpleValidInput() {
        String expectedOutput =
                "AAAAADEKC2pvaG4gTWlsbGVyEB4aIDEyMzQgTWFpbiBTdC4gQW55dG93biwgVVNBIDEyMzQ1";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString =
                "1:2::\"john Miller\"\n2:0::30\n3:2::\"1234 Main St. Anytown, USA 12345\"\n";

        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithNestedMessageValidInput() {
        String expectedOutput =
                "AAAAAEEKEEhlbGxvLCBQcm90b2J1ZiESJwoESm9obhIGTWlsbGVyGhcKBEpvaG4QAhoNCgtIZWxsbyBXb3JsZBjqrcDlJA";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString =
                "1:2::\"Hello, Protobuf!\"\n2:2N::{\n1:2::\"John\"\n2:2::\"Miller\"\n3:2N::{\n1:2::\"John\"\n2:0::2\n3:2N::{\n1:2::\"Hello World\"\n}\n}\n}\n3:0::9876543210\n";
        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithEnumAndRepeatedFieldsInput() {
        // Example corrupted input byte array
        String expectedOutput = "AAAAAA4IARIBYRIBYhIBYxIBZA";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString = "1:0::1\n2:2::\"a\"\n2:2::\"b\"\n2:2::\"c\"\n2:2::\"d\"\n";

        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithDoubleAndFloatInput() {
        String expectedOutput = "AAAAAA4JzczMzMzcXkAVrseHQg";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString = "1:1D::123.45\n2:5F::67.89\n";
        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception ignored) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithWireType1And6Input() {
        String expectedOutput = "AAAAAA4NQEIPABHMm5cAyicBAA";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString = "1:5::1000000\n2:1::325223523523532\n";

        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithCorruptedWireTypeInput() {
        // Example corrupted input byte array
        String expectedOutput = "";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString = "1:8::1000000\n2:2::\"Hello\"\n";

        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
            assertEquals("Invalid Wire type", e.getMessage());
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }

    @Test
    void testStartEncodingWithOnlyRandomStringInput() {
        // Example corrupted input byte array
        String expectedOutput = "";
        byte[] decodedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(expectedOutput);
        String inputString =
                "Failed to decode protobuf message: The message format is invalid or corrupted.";

        try {
            List<String> messageFields = EncoderUtils.parseIntoList(inputString);
            encoder.encode(messageFields);
        } catch (Exception e) {
        }
        assert (Arrays.equals(decodedBytes, encoder.getOutputEncodedMessage()));
    }
}
