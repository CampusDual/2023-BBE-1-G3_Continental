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
import java.util.ArrayList;
import java.util.List;

class test {
    //TODO Cambiar el DAY_OF_WEEK por una lista de dias,para poder comprobar varios dias
    //TODO Crear enum con los dias de la semana?
    //TODO Crear un nuevo operador que sea SEASON
    //TODO Days_Between deberia de aceptar el numero de dias, el evaulador deberia de recibir dos fechas y calcular los dias entre ellas listo
    public static void main(String[] args) {
        List<DateCondition> lista = new ArrayList<>();
        //DateCondition que comprueba si es entre el 1 del 6 y el 31 del 8
        DateCondition dateConditionHighSeason = new DateCondition(new DateField(LocalDate.of(2020, 6, 1)), DateOperator.DATE_BETWEEN_SEASON, new DateField(LocalDate.of(2020, 8, 31)), null);
        lista.add(dateConditionHighSeason);
        //DateCondition que comprueba si es entre el 1 del 9 y el 30 del 11
        DateCondition dateConditionLowSeason = new DateCondition(new DateField(LocalDate.of(2020, 9, 1)), DateOperator.DATE_BETWEEN_SEASON, new DateField(LocalDate.of(2020, 11, 30)), null);
        lista.add(dateConditionLowSeason);
        //DateCondition que comprueba si la reserva se realizo con 10 dias de antelacion
        DateCondition dateCondition10DaysBefore = new DateCondition(null, DateOperator.DAYS_BEFORE, null, new DateField(10));
        lista.add(dateCondition10DaysBefore);
        //DateCondition que comprueba si la reserva dura mas de 5 dias
        DateCondition dateCondition5DaysBetween = new DateCondition(null, DateOperator.DAYS_BETWEEN, null, new DateField(5));
        lista.add(dateCondition5DaysBetween);
        //DateCondition que comprueba si el dia es sabado
        DateCondition dateConditionSaturday = new DateCondition(null, DateOperator.DAY_OF_WEEK, null, new DateField(6));
        lista.add(dateConditionSaturday);
        //DateCondition que comprueba si el dia es domingo
        DateCondition dateConditionSunday = new DateCondition(null, DateOperator.DAY_OF_WEEK, null, new DateField(7));
        lista.add(dateConditionSunday);

        //Serializacion
        DateConditionModule dateConditionModule = new DateConditionModule();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(dateConditionModule.getModule());
        try {
            for (DateCondition dateCondition : lista) {
                String json = objectMapper.writeValueAsString(dateCondition);
                System.out.println(json);
            }
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
            case DATE_BETWEEN_SEASON:
                if (leftValue instanceof LocalDate && rightValue instanceof LocalDate) {
                    LocalDate leftDate = (LocalDate) leftValue;
                    LocalDate rightDate = (LocalDate) rightValue;
                    int leftMonth = leftDate.getMonthValue();
                    int leftDay = leftDate.getDayOfMonth();
                    int rightMonth = rightDate.getMonthValue();
                    int rightDay = rightDate.getDayOfMonth();
                    int currentMonth = date.getMonthValue();
                    int currentDay = date.getDayOfMonth();
                    return (currentMonth > leftMonth || (currentMonth == leftMonth && currentDay >= leftDay))
                            && (currentMonth < rightMonth || (currentMonth == rightMonth && currentDay <= rightDay));
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
        }

        return false;
    }

    /**
     * Evalua la condicion
     *
     * @param initialDate fecha inicial a evaluar
     * @param endDate     fecha final a evaluar
     * @return true si la condicion se cumple, false en caso contrario
     */
    public boolean evaluate(LocalDate initialDate, LocalDate endDate) {
        Object daysValue = daysField != null ? daysField.getValue() : null;

        switch (operator) {
            case DAYS_BETWEEN:
                if (daysValue instanceof Integer) {
                    //Calcular la diferencia entre leftValue y rightValue
                    int daysBetween = (int) daysValue;
                    Period period = Period.between(initialDate, endDate.plusDays(1));
                    return period.getDays() >= daysBetween;
                }
                break;
        }

        return false;
    }


    protected void validateArguments(DateOperator operator, DateField dateFieldLeft, DateField dateFieldRight, DateField daysField) {
        if(operator == null) {
            throw new IllegalArgumentException("The operator is required.");
        }
        if (operator == DateOperator.DATE_BETWEEN && (dateFieldLeft == null || dateFieldRight == null)) {
            throw new IllegalArgumentException("The " + operator + " operator requires two date fields.");
        }
        if (operator == DateOperator.DAYS_BEFORE && daysField == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires days field.");
        }
        if (operator == DateOperator.DAYS_BETWEEN &&  daysField == null) {
            throw new IllegalArgumentException("The " + operator + " operator requires  days field.");
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
        if (operator == DateOperator.DATE_BETWEEN_SEASON && (dateFieldLeft == null || dateFieldRight == null)) {
            throw new IllegalArgumentException("The " + operator + " operator requires dateFieldLeft & dateFieldRight fields.");
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
    DAYS_BETWEEN,
    DATE_BETWEEN_SEASON
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
