package com.example.dilina.appointer.Util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dilina.appointer.Entities.Appointment;
import com.example.dilina.appointer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointments;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView appointment;
        public TextView dot;

        /**
         * Parameterized Constructor
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            appointment = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
        }
    }

    /**
     * Parameterized Constructor
     * @param context
     * @param appointments
     */
    public AppointmentAdapter(Context context, List<Appointment>appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    /**
     * Method is invoked when the adpater is created and to initialize view holders
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_row, parent, false);
        return new ViewHolder(itemView);
    }

    /**
     * Method is used to bind the ViewHolder to the adapter
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.appointment.setText(appointment.getTime()+" "+appointment.getTitle());
        holder.dot.setText(Html.fromHtml("&#8226;"));
    }

    /**
     * Method to get the size of the ArrayList
     * @return
     */
    @Override
    public int getItemCount() {
        return appointments.size();
    }

}