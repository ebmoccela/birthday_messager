package io.github.ebmoccela.birthday_scheduler;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//get target fragment

public class TimePickerDialogFragment extends DialogFragment {

    private View root;
    private TimePicker timeSpinner;
    private Button btnConfirmTime, btnCancelTime;
    private int hour;
    private int minute;
    private String status = "AM";
    private String time;
    private String outputTimeStr;
    private Calendar calendar;

    public SimpleDateFormat HHmmFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public SimpleDateFormat hhmmampmFormat = new SimpleDateFormat("hh:mm a", Locale.US);

    private SelectedTime tCallback;

    public interface SelectedTime {
        void updateSelectedTime(String t);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //getDialog().setTitle("Time");
        root = inflater.inflate(R.layout.dialog_time_picker_layout, container, true);
        calendar = Calendar.getInstance();
        setCancelable(false);

        try {
            tCallback = (SelectedTime) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement SelectedTime");
        }

        btnConfirmTime = (Button) root.findViewById(R.id.btnConfirmTime);
        btnCancelTime = (Button) root.findViewById(R.id.btnCancelTime);

        timeSpinner = (TimePicker) root.findViewById(R.id.timeSpinner);

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog timeDialog = super.onCreateDialog(savedInstanceState);
        timeDialog.setTitle("Time");


        //timeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        return timeDialog;
    }

    @Override
    public void onResume() {
        super.onResume();

//        calendar = Calendar.getInstance();
//
//        int currentMinute = calendar.get(Calendar.MINUTE);
//        int currentHour = calendar.get(Calendar.HOUR);
//        timeSpinner.setMinute(currentMinute);
//        timeSpinner.setHour(currentHour);
//        //int currentHour
//
//        if (currentHour > 11 ) {
//            status = "PM";
//        }
//
//        if (currentHour > 11 && status.equals("PM")) {
//            if (currentHour == 12) {
//                hour = currentHour;
//            } else {
//                hour = currentHour - 12;
//            }
//        } else {
//            if (currentHour < 1 && status.equals("AM")) {
//                hour = currentHour + 12;
//            } else {
//                hour = currentHour;
//            }
//        }
//
//        //hour = selectedHour;
//        minute = currentMinute;
//        hour = currentHour;
//
//        time = hour + ":" + minute + " " + status;
//
//        outputTimeStr = parseDate(time, HHmmFormat, hhmmampmFormat);

        timeSpinner.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int selectedHour, int selectedMinute) {
//              String status;
//
                if (selectedHour > 11 ) {
                    status = "PM";
                }

                if (selectedHour > 11 && status.equals("PM")) {
                    if (selectedHour == 12) {
                        hour = selectedHour;
                    } else {
                        hour = selectedHour - 12;
                    }
                } else {
                    if (selectedHour < 1 && status.equals("AM")) {
                        hour = selectedHour + 12;
                    } else {
                        hour = selectedHour;
                    }
                }

                //hour = selectedHour;
                minute = selectedMinute;
                hour = selectedHour;

                time = hour + ":" + minute + " " + status;

                outputTimeStr = parseDate(time, HHmmFormat, hhmmampmFormat);
                Log.i("output_string", outputTimeStr);


                timeSpinner.setHour(selectedHour);
                timeSpinner.setMinute(minute);
            }
        });

        btnConfirmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: fix if time is not changed
                String t = outputTimeStr;

                tCallback.updateSelectedTime(t);
                dismiss();
            }
        });

        btnCancelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });


    }

    //used to format the time
    private static String parseDate(String inputDateString, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat) {
        Date date = null;
        String outputDateString = null;
        try {
            date = inputDateFormat.parse(inputDateString);
            outputDateString = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }
}
