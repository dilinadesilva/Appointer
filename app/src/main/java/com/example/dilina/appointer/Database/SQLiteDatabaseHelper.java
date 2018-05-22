package com.example.dilina.appointer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dilina.appointer.Entities.Appointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyAppointments.db";

    /**
     * Parameterized Constructor
     * @param context
     */
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Method is invoked first time when the tables are required to create.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Appointment.CREATE_TABLE);
    }

    /**
     * Method is invoked when the database version is upgraded
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("");
    }

    /**
     * Method is invoked to add a new appointment to the appointments table
     * @param title
     * @param time
     * @param details
     * @param date
     * @return
     */
    public boolean addAppointment(String title, String time, String details, Calendar date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Appointment.APPOINTMENT_COLUMN_TITLE,title);
        values.put(Appointment.APPOINTMENT_COLUMN_TIME,time);
        values.put(Appointment.APPOINTMENT_COLUMN_DETAILS,details);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        values.put(Appointment.APPOINTMENT_COLUMN_DATE,sdf.format(date.getTime()));

        db.insert(Appointment.APPOINTMENT_TABLE_NAME, null, values);
        db.close();
        return true;
    }

    /**
     * Method is invoked to delete a new appointment from the appointments table
     * @param appointment
     */
    public void deleteAppointment(Appointment appointment){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Appointment.APPOINTMENT_TABLE_NAME, Appointment.APPOINTMENT_COLUMN_ID + " = ?",new String[]{String.valueOf(appointment.getId())});
        db.close();
    }

    /**
     * Method is invoked to move an appointment to another date
     * @param note
     * @param date
     */
    public void moveAppointment(Appointment note,Calendar date){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        values.put(Appointment.APPOINTMENT_COLUMN_DATE,sdf.format(date.getTime()));
        db.update(Appointment.APPOINTMENT_TABLE_NAME, values, Appointment.APPOINTMENT_COLUMN_ID+"="+note.getId(), null);

    }

    /**
     * Method is invoked to update an appointment in the appointments table
     * @param note
     */
    public void updateAppointment(Appointment note){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Appointment.APPOINTMENT_COLUMN_TITLE,note.getTitle());
        values.put(Appointment.APPOINTMENT_COLUMN_TIME,note.getTime());
        values.put(Appointment.APPOINTMENT_COLUMN_DETAILS,note.getDetails());

        db.update(Appointment.APPOINTMENT_TABLE_NAME, values, Appointment.APPOINTMENT_COLUMN_ID+"="+note.getId(), null);

    }

    /**
     * Method is invoked to get all the appointments in the appointment table
     * @return
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> notes = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String selectQuery = "SELECT  * FROM " + Appointment.APPOINTMENT_TABLE_NAME + " ORDER BY " +
                Appointment.APPOINTMENT_COLUMN_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Appointment note = new Appointment();
                note.setId(cursor.getInt(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_TITLE)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_TIME)));

                note.setDetails(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_DETAILS)));

                note.setDate(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_DATE)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        db.close();

        return notes;
    }


    /**
     * Method is invoked to get all the appointments for a given date
     * @param cal
     * @return
     */
    public List<Appointment> getAllNotes(Calendar cal) {
        List<Appointment> notes = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String selectQuery = "SELECT  * FROM " + Appointment.APPOINTMENT_TABLE_NAME +" WHERE "+ Appointment.APPOINTMENT_COLUMN_DATE +"=" +"'"+sdf.format(cal.getTime())+"'"+ " ORDER BY " +
                Appointment.APPOINTMENT_COLUMN_TIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Appointment note = new Appointment();
                note.setId(cursor.getInt(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_TITLE)));
                note.setTime(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_TIME)));
                note.setDate(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_DATE)));
                note.setDetails(cursor.getString(cursor.getColumnIndex(Appointment.APPOINTMENT_COLUMN_DETAILS)));
                notes.add(note);
            } while (cursor.moveToNext());
        }

        db.close();

        return notes;
    }

    /**
     * Method is invoked to delete all the appointments for a given date
     * @param cal
     * @return
     */
    public boolean deleteAllAppointments(Calendar cal){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        db.execSQL("DELETE FROM "+Appointment.APPOINTMENT_TABLE_NAME+" WHERE "+ Appointment.APPOINTMENT_COLUMN_DATE +"=" +"'"+sdf.format(cal.getTime())+"'");
        db.close();
        return true;
    }
}
