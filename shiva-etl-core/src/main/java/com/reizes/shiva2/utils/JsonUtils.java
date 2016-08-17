package com.reizes.shiva2.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static Map<String, Object> fromJson(String stringJson) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> data = objectMapper.readValue(stringJson, new TypeReference<Map<String, Object>>() {
	    });
		return data;
	}
	
	public static Map<String, Object> fromJson(InputStream is) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
	    });
		return data;
	}
	
	public static String toJson(Object mapData) throws JsonProcessingException {
		return objectMapper.writeValueAsString(mapData);
	}
}
