package com.williamhsieh.fruitandessence.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // JSON 要輸出的時間格式
    public static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy/MM/dd";

    @Bean
    public Module javaTimeModule() {

        JavaTimeModule module = new JavaTimeModule();

        // LocalDateTime 序列化
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));

        // LocalDate 序列化
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        // LocalDateTime 反序列化
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));

        // LocalDate 反序列化
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));

        return module;
    }

    // BigDecimal 輸出時保留兩位小數
    @Bean
    public Module bigDecimalModule() {

        SimpleModule module = new SimpleModule();

        module.addSerializer(BigDecimal.class, new CustomBigDecimalSerializer());

        return module;
    }

    public class CustomBigDecimalSerializer extends StdSerializer<BigDecimal> {

        public CustomBigDecimalSerializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value != null) {
                // 固定小數點 2 位
                gen.writeNumber(value.setScale(2, RoundingMode.HALF_UP));
            } else {
                gen.writeNull();
            }
        }
    }
}

