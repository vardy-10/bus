package com.zah.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;

public class DateFormatSerializer extends JsonSerializer<Time> {

	@Override
	public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String datestr = sdf.format(value);
		gen.writeString(datestr);
	}

}