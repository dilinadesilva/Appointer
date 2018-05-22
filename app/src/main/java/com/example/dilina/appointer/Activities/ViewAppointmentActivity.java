package com.example.dilina.appointer.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dilina.appointer.Entities.Appointment;
import com.example.dilina.appointer.Util.AppointmentAdapter;
import com.example.dilina.appointer.Database.SQLiteDatabaseHelper;
import com.example.dilina.appointer.Util.DividerItem;
import com.example.dilina.appointer.R;
import com.example.dilina.appointer.Util.RecyclerItemListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewAppointmentActivity extends AppCompatActivity {

    protected AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointments = new ArrayList<>();
    private RecyclerView recyclerView;

    private SQLiteDatabaseHelper db;

    Calendar cald;

    AlertDialog.Builder alertDialog;

    int indexNo;

    /**
     * OnCreate method which is invoked when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cald = (Calendar) intent.getSerializableExtra("myCal");

        setContentView(R.layout.activity_view_appointment);
        setTitle("Appointer - View Appointments");


        recyclerView = findViewById(R.id.recycler_view);

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.appointment_dialog, null);

        db = new SQLiteDatabaseHelper(this);

        appointments.addAll(db.getAllNotes(cald));

        appointmentAdapter = new AppointmentAdapter(this, appointments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(appointmentAdapter);

        final EditText appTitle = view.findViewById(R.id.appTitle);
        final EditText appTime = view.findViewById(R.id.appTime);
        final EditText appDetails = view.findViewById(R.id.appDetails);

        alertDialog = new AlertDialog.Builder(ViewAppointmentActivity.this);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Update",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                appointments.get(indexNo).setTitle(appTitle.getText().toString());
                appointments.get(indexNo).setTime(appTime.getText().toString());
                appointments.get(indexNo).setDetails(appDetails.getText().toString());
                Toast.makeText(getApplicationContext(),"Appointment Successfully Updated",Toast.LENGTH_LONG).show();
                db.updateAppointment(appointments.get(indexNo));
                appointmentAdapter.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = alertDialog.create();

        recyclerView.addOnItemTouchListener(new RecyclerItemListener(this,
                recyclerView, new RecyclerItemListener.ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                appTitle.setText(appointments.get(position).getTitle());
                appTime.setText(appointments.get(position).getTime());
                appDetails.setText(appointments.get(position).getDetails());
                indexNo = position;
                alert.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if(appointments.size()==0){
            Toast.makeText(getApplicationContext(),"No appointments for the day",Toast.LENGTH_LONG).show();
        }

    }

}
