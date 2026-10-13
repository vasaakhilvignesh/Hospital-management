package com.hospital.doctorservice.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalTimeFlexibleDeserializer extends JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .optionalStart()
            .appendPattern(":ss")
            .optionalEnd()
            .toFormatter();

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(value.trim(), FORMATTER);
    }
}
