package com.example.dilina.appointer.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



import android.app.TimePickerDialog;
import android.widget.TimePicker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.example.dilina.appointer.Entities.Appointment;
import com.example.dilina.appointer.Database.SQLiteDatabaseHelper;
import com.example.dilina.appointer.R;
import com.example.dilina.appointer.Util.NotificationAlarmReceiver;

public class NewAppointmentActivity extends AppCompatActivity implements View.OnClickListener{

    EditText title,time,details,thesaurusText,appointDetails;                       // variables to refer to EditText fields
    Button btnSave,btnThesaurus,btnReplace;                                         // variables to refer to buttons

    Context context;

    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour,currentMinute;
    String amPm;

    SQLiteDatabaseHelper dbHelper;

    Calendar cald;

    NodeList nodelist;
    ProgressDialog pDialog;
    String URL = "http://thesaurus.altervista.org/thesaurus/v1?word=peace&language=en_US&key=06I24gzrPFpaU7FwN0he&output=xml";

    StringBuilder sb = new StringBuilder("");
    ArrayList<String>myWords = new ArrayList<String>();

    private List<Appointment> appointments = new ArrayList<>();


    String selectedText;


    /**
     * OnCreate method which is invoked when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cald = (Calendar) intent.getSerializableExtra("myCal");

        setContentView(R.layout.activity_new_appointment);
        setTitle("Appointer - New Appointment");

        //setAlarm(2018,04,01,13,23,00);
        //setAlarm(2018,04,01,13,26,00);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(cald.getTime());

        this.context = context;

        dbHelper = new SQLiteDatabaseHelper(this);

        appointments.addAll(dbHelper.getAllNotes(cald));

        title = findViewById(R.id.appointTitle);
        time =  findViewById(R.id.appointTime);
        details = findViewById(R.id.appointDetails);
        thesaurusText = findViewById(R.id.thesaurusText);
        appointDetails = findViewById(R.id.appointDetails);

        btnSave = findViewById(R.id.btnSave);
        btnThesaurus = findViewById(R.id.btnThesaurus);
        btnReplace = findViewById(R.id.btnReplace);

        btnSave.setOnClickListener(this);
        btnThesaurus.setOnClickListener(this);
        btnReplace.setOnClickListener(this);

        time.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    calendar = Calendar.getInstance();
                    currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    currentMinute = calendar.get(Calendar.MINUTE);

                    timePickerDialog = new TimePickerDialog(NewAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            if (hourOfDay >= 12) {
                                amPm = "PM";
                            } else {
                                amPm = "AM";
                            }
                            time.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                        }
                    }, currentHour, currentMinute, false);

                    timePickerDialog.show();

                }
            }
        });






        final Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.reminder_times, android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(staticSpinner.getSelectedItem().toString() == "Select Notification Time"){

                }else{
                    Toast.makeText(NewAppointmentActivity.this, staticSpinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    void setAlarm(int y,int m,int d,int hours,int min,int sec){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationAlarmReceiver.class);
        final int alarmId = (int) System.currentTimeMillis();
        PendingIntent broadcast = PendingIntent.getBroadcast(this, alarmId, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cSchedStartCal.setTimeZone(TimeZone.getTimeZone("Asia/Colombo"));
        cSchedStartCal = new GregorianCalendar();
        cSchedStartCal.set(y,m,d,hours,min,sec);
        System.out.println(cSchedStartCal.getTimeInMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, cSchedStartCal.getTimeInMillis(), broadcast);

    }








    /**
     * Method to handle onClick events
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                if(TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(time.getText().toString()) || TextUtils.isEmpty(details.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please fill the required fields",Toast.LENGTH_LONG).show();
                }else{
                    boolean validTitle = true;
                    for(Appointment x:appointments){
                        if((x.getTitle().equals(title.getText().toString()))){
                            validTitle = false;
                            Toast.makeText(getApplicationContext(),"Appointment "+x.getTitle()+" already exists, please choose a different event title",Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    if(validTitle){
                        new ProgressTask(NewAppointmentActivity.this).execute();

                        int y = (cald.get(Calendar.YEAR));
                        int m = (cald.get(Calendar.MONTH));
                        int d = (cald.get(Calendar.DAY_OF_MONTH));

                        System.out.println(currentHour);
                        System.out.println(currentMinute);

                        setAlarm(y,m,d,currentHour,currentMinute,00);

                    }
                }
                break;
            case R.id.btnThesaurus:
                if(TextUtils.isEmpty(thesaurusText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please fill the field with a word",Toast.LENGTH_LONG).show();
                }else{
                    if(connectedToInternet()){
                        URL = "http://thesaurus.altervista.org/thesaurus/v1?word="+thesaurusText.getText().toString()+"&language=en_US&key=06I24gzrPFpaU7FwN0he&output=xml";
                        Toast.makeText(getApplicationContext(),thesaurusText.getText().toString(),Toast.LENGTH_LONG).show();
                        new DownloadXML(btnThesaurus).execute(URL);
                        //showPopup();
                    }else {
                        Toast.makeText(NewAppointmentActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.btnReplace:
                int startSelection=appointDetails.getSelectionStart();
                int endSelection=appointDetails.getSelectionEnd();
                selectedText = appointDetails.getText().toString().substring(startSelection, endSelection);


                if(TextUtils.isEmpty(selectedText)){
                    Toast.makeText(getApplicationContext(),"Please select a word from appointment details",Toast.LENGTH_LONG).show();
                }else{
                    if(connectedToInternet()){
                        URL = "http://thesaurus.altervista.org/thesaurus/v1?word="+selectedText+"&language=en_US&key=06I24gzrPFpaU7FwN0he&output=xml";
                        Toast.makeText(getApplicationContext(),selectedText,Toast.LENGTH_LONG).show();

                        if(myWords.size()>0){
                            myWords.clear();
                        }

                        new DownloadXML(btnReplace).execute(URL);


                        //replaceWords();
                    }else{
                        Toast.makeText(NewAppointmentActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
        }
    }


    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;
        private NewAppointmentActivity activity;

        /**
         * Parameterized Constructor
         * @param activity
         */
        public ProgressTask(NewAppointmentActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        private Context context;

        /**
         * Method executes the tasks before the background processes begin
         */
        protected void onPreExecute() {
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        /**
         * Invoked after doInBackground method with the result of the background processes
         * @param success
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(context, "Appointment added Successfully", Toast.LENGTH_LONG).show();
                this.dialog.dismiss();
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Method carries out inserting the data to the database without affecting the UI thread
         * @param args
         * @return
         */
        protected Boolean doInBackground(final String... args) {
            try{
                dbHelper.addAppointment(title.getText().toString(),time.getText().toString(),details.getText().toString(),cald);
                return true;
            } catch (Exception e){
                return false;
            }
        }

    }



    private class DownloadXML extends AsyncTask<String, Void, Void> {

        private View view;

        /**
         * Parameterized Constructor
         * @param view
         */
        DownloadXML(View view){
            this.view = view;
        }

        /**
         * Method executes the tasks before the background processes begin
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewAppointmentActivity.this);
            pDialog.setTitle("Synonyms for the Word");
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        /**
         * Method carries out retrieving data from the web service without affecting the UI thread
         * @param Url
         * @return
         */
        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                nodelist = doc.getElementsByTagName("list");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Invoked after doInBackground method with the result of the background processes
         * @param args
         */
        @Override
        protected void onPostExecute(Void args) {
            for (int temp = 0; temp < nodelist.getLength(); temp++) {
                Node nNode = nodelist.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    //sb.append(getNode("synonyms", eElement) + "\n");
                    //sb.append("\n");
                    //myWords.add(getNode("synonyms", eElement) + "\n");

                    String line = getNode("synonyms",eElement).toString();

                    String words[] = line.split(" | ");

                    System.out.println(words.length);

                    for(int i=0;i<words.length;i++){
                        sb.append(words[i]);
                        myWords.add(words[i]);
                    }

                    //sb.append(line+"\n");
                    sb.append("\n");
                    sb.append("\n");

                }
            }
            pDialog.dismiss();

            switch (view.getId()){
                case R.id.btnThesaurus:
                    showPopup();
                    break;
                case R.id.btnReplace:
                    replaceWords();
                    break;
            }

        }
    }

    /**
     * Method parses the xml and returns the node values
     * @param sTag
     * @param eElement
     * @return
     */
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    /**
     * Method displays the list of synonyms to the user
     */
    void showPopup(){


        AlertDialog alertDialog = new AlertDialog.Builder(NewAppointmentActivity.this).create();
        alertDialog.setTitle("List of Synonyms");
        alertDialog.setMessage(sb);
        alertDialog.show();
        sb.setLength(0);
    }

    /**
     * Method to check if mobile is connected to internet or not
     * @return
     */
    boolean connectedToInternet(){
        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            return true;
        }else{
            return false;
        }
    }


    /**
     * Method to replace the selected word with the highlighted word
     */
    void replaceWords(){
        CharSequence[] items = new CharSequence[myWords.size()];
        for(int i=0;i<myWords.size();i++){
            items[i]=myWords.get(i).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAppointmentActivity.this);
        builder.setTitle("Select the option").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = appointDetails.getText().toString();
                String replacingWord = myWords.get(which).toString();;
                String modfifiedDescription=description.replace(selectedText,replacingWord);
                appointDetails.setText(modfifiedDescription);
                selectedText="";
            }
        });
        builder.show();
    }

}
