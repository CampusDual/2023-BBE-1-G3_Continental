package com.hotel.continental.model.core.tools;
public class ErrorMessages {
    //Generales
    public static final String NECESSARY_KEY = "M_NECESSARY_KEY";
    public static final String NECESSARY_DATA = "M_NECESSARY_DATA";
    public static final String COLUMN_NOT_EDITABLE = "M_COLUMN_NOT_EDITABLE";
    //CountryCode
    public static final String COUNTRY_CODE_NOT_VALID = "M_COUNTRY_CODE_NOT_VALID";
    public static final  String COUNTRY_CODE_FORMAT_ERROR = "M_COUNTRY_CODE_FORMAT_ERROR";
    //Document
    public static final String DOCUMENT_NOT_VALID = "M_DOCUMENT_NOT_VALID";
    public static final String DOCUMENT_ALREADY_EXIST = "M_DOCUMENT_ALREADY_EXIST";
    //Reservas
    public static final String BOOKING_NOT_EXIST = "M_BOOKING_NOT_EXIST";
    public static final String ROOM_NOT_FREE = "M_ROOM_NOT_FREE";
    public static final String BOOKING_ALREADY_CHECKED_IN = "M_BOOKING_ALREADY_CHECKED_IN";
    public static final String BOOKING_ALREADY_CHECKED_OUT = "M_BOOKING_ALREADY_CHECKED_OUT";
    public static final String BOOKING_NOT_CHECKED_IN = "M_BOOKING_NOT_CHECKED_IN";
    public static final String MORE_THAN_ONE_BOOKING = "M_MORE_THAN_ONE_BOOKING";
    public static final String BOOKING_CHECK_IN_SUCCESS = "M_BOOKING_CHECK_IN_SUCCESS";
    public static final String BOOKING_CHECK_OUT_SUCCESS = "M_BOOKING_CHECK_OUT_SUCCESS";
    public static final String BOOKING_DOESNT_BELONG_CLIENT = "M_BOOKING_DOESNT_BELONG_CLIENT";
    //Fechas
    public static final String DATE_FORMAT_ERROR = "M_DATE_FORMAT_ERROR";
    public static final String FINAL_DATE_BEFORE_INITIAL_DATE = "M_FINAL_DATE_BEFORE_INITIAL_DATE";
    public static final String INITIAL_DATE_BEFORE_CURRENT_DATE = "M_INITIAL_DATE_BEFORE_CURRENT_DATE";
    //Clientes
    public static final String CLIENT_NOT_EXIST = "M_CLIENT_NOT_EXIST";
    public static final String CLIENT_ALREADY_DELETED = "M_CLIENT_ALREADY_DELETED";
    //Hotel
    public static final String HOTEL_NOT_EXIST = "M_HOTEL_NOT_EXIST";
    public static final String HOTEL_ALREADY_INACTIVE = "M_HOTEL_ALREADY_INACTIVE";
    //Habitaciones
    public static final String ROOM_NOT_EXIST = "M_ROOM_NOT_EXIST";
    public static final String ROOM_ALREADY_INACTIVE = "M_ROOM_ALREADY_INACTIVE";
    public static final String ROOM_ALREADY_EXIST = "M_ROOM_ALREADY_EXIST";
    public static final String TYPE_NOT_EXISTENT = "M_TYPE_NOT_EXISTENT";

    //Roles
    public static final String ROLE_DOESNT_EXIST = "M_ROLE_DOESNT_EXIST";
    public static final String ROLE_ALREADY_EXISTS = "M_ROLE_ALREADY_EXISTS";
    public static final String ADMIN_ROLE_NOT_EDITABLE = "M_ROLE_NOT_EDITABLE";
    //Usuarios
    public static final String USER_ALREADY_EXIST = "M_USER_ALREADY_EXIST";
    public static final String INCORRECT_PASSWORD = "M_INCORRECT_PASSWORD";
    public static final String USER_DOESNT_EXIST = "M_USER_DOESNT_EXIST";
    //Empleados
    public static final String EMPLOYEE_NOT_EXIST = "M_EMPLOYEE_NOT_EXIST";
    public static final String EMPLOYEE_ALREADY_INACTIVE = "M_EMPLOYEE_ALREADY_INACTIVE";
    //AccessCard
    public static final String ACCESS_CARD_ALREADY_GIVEN = "M_ACCESS_CARD_ALREADY_GIVEN";
    public static final String ACCESS_CARD_NOT_GIVEN = "M_ACCESS_CARD_NOT_GIVEN";
    public static final String ACCESS_CARD_NOT_EXIST = "M_ACCESS_CARD_NOT_EXIST";
    public static final String ACCESS_CARD_RECOVERED = "M_ACCESS_CARD_RECOVERED";
    public static final String ACCESS_CARD_NOT_RECOVERED = "M_ERROR_ACCESS_CARD_NOT_RECOVERED";
    public static final String ACCESS_CARD_LOST = "M_ERROR_ACCESS_CARD_LOST";
    public static final String ACCESS_CARD_SUCCESSFULLY_MODIFY = "M_ACCESS_SUCCESSFULLY_MODIFY";
    public static final String HOTEL_INCORRECT = "M_HOTEL_INCORRECT";
    public static final String CARD_DOESNT_BELONG_BOOKING = "M_CARD_DOESNT_BELONG_BOOKING";
    //Criterios
    public static final String CRITERIA_NOT_EXIST = "M_CRITERIA_NOT_EXIST";
    //Neveras
    public static final String CAPACITY_NOT_NUMBER = "M_CAPACITY_NOT_NUMBER";
    public static final String CAPACITY_NOT_POSITIVE = "M_CAPACITY_NOT_POSITIVE";
    //RoomType
    public static final String ROOMTYPE_NOT_EXIST = "M_ROOMTYPE_NOT_EXIST";


  private ErrorMessages() {
        throw new IllegalStateException("Utility class");
    }
}
