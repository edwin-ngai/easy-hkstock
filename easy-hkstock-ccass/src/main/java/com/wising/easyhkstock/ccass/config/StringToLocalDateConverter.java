package com.wising.easyhkstock.ccass.config;

import java.time.LocalDate;

import org.apache.commons.lang3.Validate;
import org.springframework.core.convert.converter.Converter;

public class StringToLocalDateConverter implements Converter<String, LocalDate> {

	@Override
	public LocalDate convert(String source) {
		Validate.notBlank(source);
		return LocalDate.parse(source);
	}

}
