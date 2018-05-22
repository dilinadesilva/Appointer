package com.example.dilina.appointer.Entities;

public class Appointment {

    public static final String APPOINTMENT_TABLE_NAME = "appointments";

    public static final String APPOINTMENT_COLUMN_ID = "id";
    public static final String APPOINTMENT_COLUMN_TITLE = "title";
    public static final String APPOINTMENT_COLUMN_TIME = "time";
    public static final String APPOINTMENT_COLUMN_DETAILS = "details";
    public static final String APPOINTMENT_COLUMN_TIMESTAMP = "timestamp";

    public static final String APPOINTMENT_COLUMN_DATE = "date";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + APPOINTMENT_TABLE_NAME + "("
                    + APPOINTMENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + APPOINTMENT_COLUMN_TITLE + " TEXT,"
                    + APPOINTMENT_COLUMN_TIME + " TEXT,"
                    + APPOINTMENT_COLUMN_DETAILS + " TEXT,"
                    + APPOINTMENT_COLUMN_DATE + " DATE"
                    + ")";
    private int id;
    private String title;
    private String time;
    private String details;
    private String timestamp;

    private String date;

    /**
     * Default Constructor
     */
    public Appointment() {

    }

    /**
     * Parameterized Constructor
     * @param id
     * @param title
     * @param time
     * @param details
     * @param date
     */
    public Appointment(int id, String title, String time, String details,String date) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.details = details;
        this.date = date;
    }

    /**
     * Method to get the id of the appointment
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Method to set the id of the appointment
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Method to get the title of the appointment
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to set the title of the appointment
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method to get the time of the appointment
     * @return
     */
    public String getTime() {
        return time;
    }

    /**
     * Method to set the time of the appointment
     * @param time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Method to get the details of the appointment
     * @return
     */
    public String getDetails() {
        return details;
    }

    /**
     * Method to set the details of the appointment
     * @param details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Method to get the date of the appointment
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Method to set the date of the appointment
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }
}
