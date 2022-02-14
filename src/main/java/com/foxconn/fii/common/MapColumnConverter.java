package com.foxconn.fii.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Converter
public class MapColumnConverter implements AttributeConverter<Map<String, Object>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObject) {
        try {
            return mapper.writeValueAsString(stringObject);
        } catch (JsonProcessingException e) {
            log.error("### convertToDatabaseColumn", e);
            return "";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        try {
            if (StringUtils.isEmpty(s)) {
                return new HashMap<>();
            }
            return mapper.readValue(s, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            log.error("### convertToEntityAttribute", e);
            return new HashMap<>();
        }
    }
}
