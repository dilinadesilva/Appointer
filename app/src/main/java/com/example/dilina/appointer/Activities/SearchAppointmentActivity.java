package com.example.dilina.appointer.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dilina.appointer.Entities.Appointment;
import com.example.dilina.appointer.Util.AppointmentAdapter;
import com.example.dilina.appointer.Database.SQLiteDatabaseHelper;
import com.example.dilina.appointer.Util.DividerItem;
import com.example.dilina.appointer.R;
import com.example.dilina.appointer.Util.RecyclerItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAppointmentActivity extends AppCompatActivity {

    private AppointmentAdapter mAdapter;
    private List<Appointment> notesList = new ArrayList<>();
    private RecyclerView recyclerView;

    private SQLiteDatabaseHelper db;

    Button btnSearch;
    EditText searchText;

    List<Appointment>searchList = new ArrayList<>();


    View view;

    AlertDialog.Builder alertDialog;

    int indexNo;


    /**
     * OnCreate method which is invoked when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);
        setTitle("Appointer - Search Appointments");

        btnSearch = findViewById(R.id.btnSearch);
        searchText = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.recycler_view);


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        view = layoutInflaterAndroid.inflate(R.layout.appointment_dialog, null);


        db = new SQLiteDatabaseHelper(this);

        notesList.addAll(db.getAllAppointments());

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(searchText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please enter a word to search appointments",Toast.LENGTH_LONG).show();
                }else{
                    searchAppointments();
                }
            }
        });
    }

    /**
     * Method to search for appointments and display to the user
     */
    void searchAppointments(){
        String text = searchText.getText().toString();

        if(searchList.size()>0){
            searchList.clear();
        }

        for(Appointment x:notesList) {
            String stringToSearch = x.getTitle().toString();
            String stringToSearch1 = x.getDetails().toString();
            String patternToSeachFor = text;
            Pattern p = Pattern.compile(patternToSeachFor, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(stringToSearch);
            Pattern q = Pattern.compile(patternToSeachFor, Pattern.CASE_INSENSITIVE);
            Matcher n = q.matcher(stringToSearch1);
            if (m.find() || (n.find())){
                searchList.add(x);
            }
        }
        mAdapter = new AppointmentAdapter(this,searchList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        final EditText appTitle = view.findViewById(R.id.appTitle);
        final EditText appTime = view.findViewById(R.id.appTime);
        final EditText appDetails = view.findViewById(R.id.appDetails);

        appTitle.setFocusable(false);
        appTime.setFocusable(false);
        appDetails.setFocusable(false);

        alertDialog = new AlertDialog.Builder(SearchAppointmentActivity.this);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = alertDialog.create();

        recyclerView.addOnItemTouchListener(new RecyclerItemListener(this,
                recyclerView, new RecyclerItemListener.ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                appTitle.setText(searchList.get(position).getTitle());
                appTime.setText(searchList.get(position).getTime());
                appDetails.setText(searchList.get(position).getDetails());
                indexNo = position;
                alert.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if(searchList.size()==0){
            Toast.makeText(getApplicationContext(),"No appointments for the day",Toast.LENGTH_LONG).show();
        }




    }
}
