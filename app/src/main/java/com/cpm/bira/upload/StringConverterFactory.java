package com.cpm.bira.upload;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import retrofit.Converter;

public class StringConverterFactory implements Converter.Factory {
    public StringConverterFactory() {
    }
    @Override
    public Converter<String> get(Type type) {
        Class<?> cls = (Class<?>) type;
        if (String.class.isAssignableFrom(cls)) {
            return new StringConverter();
        }
        return null;
    }
}

class  StringConverter implements Converter<String> {
    private static final MediaType PLAIN_TEXT = MediaType.parse("text/plain; charset=UTF-8");

    @Override
    public String fromBody(ResponseBody body) throws IOException {
        return new String(body.bytes());
    }

    @Override
    public RequestBody toBody(String value) {
        return RequestBody.create(PLAIN_TEXT, convertToBytes(value));
    }

    private static byte[] convertToBytes(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}