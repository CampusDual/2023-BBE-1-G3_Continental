package com.hotel.continental.model.core.tools;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

class test {
    public static void main(String[] args) {
        LocalDate localDate = LocalDate.now();
        DateCondition dateConditionOriginal = new DateCondition(new DateField(localDate), DateOperator.DAYS_BETWEEN, new DateField(localDate.plusDays(5)), new DateField(5));//5 dias entre
        DateCondition dateConditionOriginal2 = new DateCondition(null, DateOperator.DAYS_BEFORE, null, new DateField(5));//5 dias antes
        DateCondition dateConditionOriginal3 = new DateCondition(new DateField(localDate), DateOperator.DAY_OF_WEEK, null, new DateField(3));//3 es miercoles
        //Creo el modulo para serializar y deserializar
        DateConditionModule dateConditionModule = new DateConditionModule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(dateConditionModule.getModule());
        try {
            //Serializo el objeto
            String json = objectMapper.writeValueAsString(dateConditionOriginal);
            //Deserializo el objeto
            DateCondition dateConditionRecuperada = objectMapper.readValue(json, DateCondition.class);
            //Evaluo la condicion
            System.out.println(dateConditionOriginal.evaluate(localDate));
            System.out.println(dateConditionOriginal2.evaluate(localDate.minusDays(5)));
            System.out.println(dateConditionOriginal3.evaluate(localDate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class DateCondition {
    private DateField dateFieldLeft;
    private DateField dateFieldRight;
    private DateField daysField;
    private DateOperator operator;

    /**
     * Constructor para serializacion
     * Dependiendo del operador se requieren diferentes parametros
     * DATE_BETWEEN: dateFieldLeft, dateFieldRight
     * EQUALS: dateFieldLeft
     * BEFORE: dateFieldLeft
     * AFTER: dateFieldLeft
     * DAY_OF_WEEK: daysField
     * DAYS_BEFORE: daysField
     * DAYS_BETWEEN: dateFieldLeft, dateFieldRight, daysField
     *
     * @param dateFieldLeft  fecha izquierda
     * @param operator       operador
     * @param dateFieldRight fecha derecha
     * @param daysField      dias
     */
    public DateCondition(DateField dateFieldLeft, DateOperator operator, DateField dateFieldRight, DateField daysField) {
        validateArguments(operator, dateFieldLeft, dateFieldRight, daysField);
        this.dateFieldLeft = dateFieldLeft;
        this.dateFieldRight = dateFieldRight;
        this.daysField = daysField;
        this.operator = operator;
    }


    public DateField getDateFieldLeft() {
        return dateFieldLeft;
    }

    public DateField getDateFieldRight() {
        return dateFieldRight;
    }

    public DateOperator getOperator() {
        return operator;
    }

    public DateField getDaysField() {
        return daysField;
    }

    @Override
    public String toString() {
        return "DateCondition{" +
                "dateFieldLeft=" + dateFieldLeft +
                ", dateFieldRight=" + dateFieldRight +
                ", operator=" + operator +
                '}';
    }

    /**
     * Evalua la condicion
     *
     * @param date fecha a evaluar,en algunos casos es ignorada
     * @return true si la condicion se cumple, false en caso contrario
     */
    public boolean evaluate(LocalDate date) {
        Object leftValue = dateFieldLeft != null ? dateFieldLeft.getValue() : null;
        Object rightValue = dateFieldRight != null ? dateFieldRight.getValue() : null;
        Object daysValue = daysField != null ? daysField.getValue() : null;

        switch (operator) {
            case DATE_BETWEEN:
                if (leftValue instanceof LocalDate && rightValue instanceof LocalDate) {
                    LocalDate leftDate = (LocalDate) leftValue;
                    LocalDate rightDate = (LocalDate) rightValue;
                    return date.compareTo(leftDate) >= 0 && date.compareTo(rightDate) <= 0;
                }
                break;
            case EQUALS:
                if (leftValue instanceof LocalDate) {
                    LocalDate leftDate = (LocalDate) leftValue;
                    return date.equals(leftDate);
                }
                break;
            case BEFORE:
                if (leftValue instanceof LocalDate) {
                    LocalDate leftDate = (LocalDate) leftValue;
                    return date.compareTo(leftDate) < 0;
                }
                break;
            case AFTER:
                if (leftValue instanceof LocalDate) {
                    LocalDate leftDate = (LocalDate) leftValue;
                    return date.compareTo(leftDate) > 0;
                }
                break;
            case DAY_OF_WEEK:
                if (daysValue instanceof Integer) {
                    int dayOfWeek = (int) daysValue;
                    return date.getDayOfWeek().getValue() == dayOfWeek;
                }
                break;
            case DAYS_BEFORE:
                if (daysValue instanceof Integer) {
                    int daysBefore = (int) daysValue;
                    boolean result = date.compareTo(LocalDate.now().minusDays(daysBefore)) <= 0;
                    return result;
                }
                break;
            case DAYS_BETWEEN:
                if (leftValue instanceof LocalDate && rightValue instanceof LocalDate && daysValue instanceof Integer) {
                    //Calcular la diferencia entre leftValue y rightValue
                    LocalDate leftDate = (LocalDate) leftValue;
                    LocalDate rightDate = (LocalDate) rightValue;
                    int daysBetween = (int) daysValue;
                    Period period = Period.between(leftDate, rightDate.plusDays(1));
                    return period.getDays() >= daysBetween;
                }
                break;
        }

        return false;
    }

    protected void validateArguments(DateOperator operator, DateField dateFieldLeft, DateField dateFieldRight, DateField daysField) {
        if (operator == DateOperator.DATE_BETWEEN && (dateFieldLeft == null || dateFieldRight == null)) {
            throw new IllegalArgumentException("The BETWEEN operator requires dateFieldLeft & dateFieldRight fields.");
        }
        if (operator == DateOperator.DAYS_BEFORE && daysField == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires days field.");
        }
        if (operator == DateOperator.DAYS_BETWEEN && (dateFieldLeft == null || dateFieldRight == null || daysField == null)) {
            throw new IllegalArgumentException("The " + operator + " operator requires two date fields and days field.");
        }
        if (operator == DateOperator.EQUALS && dateFieldLeft == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires a dateFieldLeft field.");
        }
        if (operator == DateOperator.BEFORE && dateFieldLeft == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires a dateFieldLeft field.");
        }
        if (operator == DateOperator.AFTER && dateFieldLeft == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires a dateFieldLeft field.");
        }
        if (operator == DateOperator.DAY_OF_WEEK && daysField == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires a days field.");
        }
    }

}

enum DateOperator {
    DATE_BETWEEN,
    EQUALS,
    BEFORE,
    AFTER,
    DAY_OF_WEEK,
    DAYS_BEFORE,
    DAYS_BETWEEN
}


class DateConditionModule {
    public static SimpleModule getModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(DateCondition.class, new DateConditionSerializer());
        module.addDeserializer(DateCondition.class, new DateConditionDeserializer());
        module.addSerializer(DateField.class, new DateFieldSerializer());
        module.addDeserializer(DateField.class, new DateFieldDeserializer());
        return module;
    }

    public static class DateConditionSerializer extends JsonSerializer<DateCondition> {
        @Override
        public void serialize(DateCondition dateCondition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("dateFieldLeft", dateCondition.getDateFieldLeft());
            jsonGenerator.writeObjectField("dateFieldRight", dateCondition.getDateFieldRight());
            jsonGenerator.writeObjectField("operator", dateCondition.getOperator());
            jsonGenerator.writeObjectField("daysField", dateCondition.getDaysField());
            jsonGenerator.writeEndObject();
        }
    }

    public static class DateConditionDeserializer extends JsonDeserializer<DateCondition> {
        @Override
        public DateCondition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            DateField dateFieldLeft = null;
            DateField dateFieldRight = null;
            DateOperator operator = null;
            DateField daysField = null;
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.getCurrentName();

                if ("dateFieldLeft".equals(fieldName)) {
                    jsonParser.nextToken();
                    dateFieldLeft = jsonParser.readValueAs(DateField.class);
                } else if ("dateFieldRight".equals(fieldName)) {
                    jsonParser.nextToken();
                    dateFieldRight = jsonParser.readValueAs(DateField.class);
                } else if ("operator".equals(fieldName)) {
                    jsonParser.nextToken();
                    operator = DateOperator.valueOf(jsonParser.getValueAsString());
                } else if ("daysField".equals(fieldName)) {
                    jsonParser.nextToken();
                    daysField = jsonParser.readValueAs(DateField.class);
                }
            }

            return new DateCondition(dateFieldLeft, operator, dateFieldRight, daysField);
        }
    }

    public static class DateFieldDeserializer extends JsonDeserializer<DateField> {
        @Override
        public DateField deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonToken currentToken = jsonParser.getCurrentToken();
            if (currentToken == JsonToken.VALUE_NUMBER_INT) {
                return new DateField(jsonParser.getIntValue());
            } else if (currentToken == JsonToken.VALUE_STRING) {
                String dateString = jsonParser.readValueAs(String.class);
                try {
                    LocalDate date = LocalDate.parse(dateString);
                    return new DateField(date);
                } catch (DateTimeParseException e) {
                    // Handle parsing error
                    throw new IOException("Invalid date format: " + dateString, e);
                }
            }
            return null;
        }
    }

    public static class DateFieldSerializer extends JsonSerializer<DateField> {
        @Override
        public void serialize(DateField dateField, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            Object value = dateField.getValue();
            if (value instanceof LocalDate) {
                LocalDate date = (LocalDate) value;
                jsonGenerator.writeString(date.toString());
            } else if (value instanceof Integer) {
                int days = (int) value;
                jsonGenerator.writeNumber(days);
            }
        }
    }

}

class DateField {
    private LocalDate date;
    private Integer days;

    public DateField(LocalDate date) {
        this.date = date;
    }

    public DateField(Integer days) {
        this.days = days;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getDays() {
        return days;
    }

    public Object getValue() {
        if (date != null) {
            return date;
        } else if (days != null) {
            return days;
        }
        return null;
    }
}
