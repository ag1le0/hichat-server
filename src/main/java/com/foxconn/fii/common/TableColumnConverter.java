package com.foxconn.fii.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Converter
public class TableColumnConverter implements AttributeConverter<List<List<String>>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<List<String>> stringObject) {
        try {
            return mapper.writeValueAsString(stringObject);
        } catch (JsonProcessingException e) {
            log.error("### convertToDatabaseColumn", e);
            return "";
        }
    }

    @Override
    public List<List<String>> convertToEntityAttribute(String s) {
        try {
            if (StringUtils.isEmpty(s)) {
                return new ArrayList<>();
            }
            return mapper.readValue(s, new TypeReference<List<List<String>>>(){});
        } catch (IOException e) {
            log.error("### convertToEntityAttribute", e);
            return new ArrayList<>();
        }
    }
}
