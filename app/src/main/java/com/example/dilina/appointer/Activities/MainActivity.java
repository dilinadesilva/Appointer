package com.example.dilina.appointer.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dilina.appointer.Entities.Appointment;
import com.example.dilina.appointer.Database.SQLiteDatabaseHelper;
import com.example.dilina.appointer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CalendarView calendarView;                                                      // calenderview to get the date from the user
    Button btnCreate, btnDelete, btnMove, btnSearch, btnView;                       // buttons to get the user actions

    long currentDate;                                                               // variable to store the current date
    Calendar calendar = new GregorianCalendar();                                    // variable to store calander object
    SQLiteDatabaseHelper dbHelper;                                                  // variable to access database helper

    /**
     * OnCreate method which is invoked when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        btnCreate = findViewById(R.id.btnCreate);
        btnDelete = findViewById(R.id.btnDelete);
        btnMove = findViewById(R.id.btnMove);
        btnSearch = findViewById(R.id.btnSearch);
        btnView = findViewById(R.id.btnView);

        btnCreate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnMove.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnView.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, Calendar.getInstance().getActualMinimum(Calendar.DATE));
        currentDate = calendar.getTime().getTime();

        calendarView.setMinDate(currentDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                changeDate(year, month, dayOfMonth);
            }
        });

        dbHelper = new SQLiteDatabaseHelper(this);

    }

    /**
     * Method to assign user selected date to the calender variable
     * @param year
     * @param month
     * @param day
     */
    void changeDate(int year, int month, int day) {
        calendar = new GregorianCalendar(year, month, day);
    }

    /**
     * Method to handle onClick events
     * @param view
     */
    @Override
    public void onClick(View view) {
        final Intent intent;
        switch (view.getId()) {
            case R.id.btnCreate:
                intent = new Intent(MainActivity.this, NewAppointmentActivity.class);
                intent.putExtra("myCal", calendar);
                startActivity(intent);
                break;
            case R.id.btnDelete:
                final CharSequence[] items = {"Delete all appointments for the date", "Select appointment to delete"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select the option").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            dbHelper.deleteAllAppointments(calendar);
                            Toast.makeText(getApplicationContext(),"All appointments deleted for the day",Toast.LENGTH_LONG).show();
                        } else if (which == 1) {
                            showPopup(btnDelete);
                        }
                    }
                });
                builder.show();
                break;
            case R.id.btnMove:
                showPopup(btnMove);
                break;
            case R.id.btnSearch:
                intent = new Intent(MainActivity.this, SearchAppointmentActivity.class);
                startActivity(intent);
                break;
            case R.id.btnView:
                intent = new Intent(MainActivity.this, ViewAppointmentActivity.class);
                intent.putExtra("myCal", calendar);
                startActivity(intent);
                break;
        }
    }

    /**
     * Method to display the list of synonyms
     * @param view
     */
    void showPopup(final View view) {
        final List<Appointment> appointments = dbHelper.getAllNotes(calendar);
        StringBuilder sb = new StringBuilder("");
        for (Appointment l : appointments) {
            sb.append(appointments.indexOf(l) + 1 + ". " + l.getTime() + " " + l.getTitle());
            sb.append("\n");
        }
        final EditText edtText = new EditText(this);
        edtText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("List of Appointments");
        builder.setMessage(sb);
        builder.setCancelable(false);
        builder.setView(edtText);
        builder.setCancelable(true);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(edtText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter a number",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }else{
                    switch (view.getId()){
                        case R.id.btnDelete:
                            confirmDialog(appointments.get((Integer.parseInt(edtText.getText().toString())) - 1));
                            break;
                        case R.id.btnMove:
                            showPicker(appointments.get((Integer.parseInt(edtText.getText().toString())) - 1));
                            break;
                    }

                }
            }
        });
        if(appointments.size()==0){
            Toast.makeText(getApplicationContext(),"No appointments for the day",Toast.LENGTH_LONG).show();
        }else{
            builder.show();

        }
    }

    /**
     * Method to get the confirmation from the user to delete appointment
     * @param appointment - appointment object to delete
     */
    void confirmDialog(final Appointment appointment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete Appointment");
        builder.setMessage("Would you like to delete event: " + appointment.getTitle() + "?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Event Successfully Deleted", Toast.LENGTH_SHORT).show();
                dbHelper.deleteAppointment(appointment);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Event Deletion Failed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * Method to show date picker to user in order to move the appointment to another date
     * @param appointment - appointment object to move
     */
    void showPicker(final Appointment appointment) {
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.MyDatePickerStyle,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        changeDate(year, monthOfYear, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeDate(datePickerDialog.getDatePicker().getYear(), datePickerDialog.getDatePicker().getMonth(), datePickerDialog.getDatePicker().getDayOfMonth());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Toast.makeText(getApplicationContext(), "ok" + sdf.format(calendar.getTime()), Toast.LENGTH_LONG).show();
                dbHelper.moveAppointment(appointment, calendar);
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
            }
        });
        datePickerDialog.show();

    }

}
