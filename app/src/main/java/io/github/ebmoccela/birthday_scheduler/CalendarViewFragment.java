package io.github.ebmoccela.birthday_scheduler;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


//TODO:maybe format date


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarViewFragment extends Fragment{

    private View root;
    private CalendarView calendar;
    private Calendar calendarInstance = Calendar.getInstance();

    //something i thought I was going to use but didn't
    List<Calendar> selectedDays = new ArrayList<>();

    //information that can be passed
    private String date;
    private int day;
    private int month;
    private int year;

    //arraylist to store event information
    private ArrayList<String> e = new ArrayList<>();

    //list to store events for calendar view
    private List<EventDay> events = new ArrayList<>();

    //how far back the calendar goes
    private Calendar min = Calendar.getInstance();

    //how far forward the calendar goes
    private Calendar max = Calendar.getInstance();

    //arbitrary code used for bundles and alarmmanager, is unique
    private static int requestCodeId = 0;

    //arbitrary code for getting text permissions
    private static final int SEND_SMS_CODE = 100;

    //use this for the number of years you would like the calendar to repeat
    private static final int maxNumberOfYears = 110;

    private Callback mCallbacks;

    private myEvent eCallback;

    //not currently used
    public interface Callback {
        void myDateSelected(CalendarView c);
    }

    public interface myEvent {
        void eventCreated(ArrayList<String> e);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callback) activity;
        eCallback = (myEvent) activity;
    }

    public CalendarViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity();
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_calendar_view, container, false);
        calendar = root.findViewById(R.id.calendarView);

        //setting the min and max year
        min.add(Calendar.YEAR, -10);
        max.add(Calendar.YEAR, 110);
        calendar.setMinimumDate(min);
        calendar.setMaximumDate(max);

        //checking permissions
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            //true;
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
            Log.d("sendSMS", "onCreateView: " + true);
        } else {
            //Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
            requestSendSMS();
            Log.d("sendSMS", "onCreateView: " + false);
        }

        return root;

    }

    //TODO:check if events are already scheduled
    @Override
    public void onResume() {
        super.onResume();

        //TODO: some view so user can see currently scheduled events when single clicked
        calendar.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                //Log.d("test", "onDayClick: " + clickedDayCalendar);
                year = clickedDayCalendar.get(Calendar.YEAR);
                day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                month = clickedDayCalendar.get(Calendar.MONTH);

                Log.d("test", "onDayClick: " + date);


                //checking to see if the user clicked on the same date twice
                if(date != null && date.equals(month + "/" + day + "/" + year)){

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    //DialogFragment dialog = new EventDialogFragment(); // creating new object
                    EventDialogFragment dialog = new EventDialogFragment();
                    fm.beginTransaction()
                            .add(android.R.id.content, dialog)
                            .addToBackStack(null)
                            .commit();

                }


                //setting date for passing later
                date = month + "/" + day + "/" + year;
            }
        });

    }

    //single clicks work for many days, recurring days do not
    //TODO: may need to change the event list formatting if scheduled message requires it.
    //TODO: add check for occurence tag
    public void checkEvents(ArrayList<String> event) {
        //at this point e contains the time, number message and occurance
        e = event;
        Log.d("check", "checkEvents: " + e.size());

        if (!e.isEmpty()) {
            //e contains information stated above and the formatted date
            e.add(date);

            String time = e.get(0);

            //parsing time user selected from timepicker
            String timeDelims = "[: ]";
            String[] timeTokens = time.split(timeDelims);
            ArrayList<String> hourAndMin = new ArrayList<>();
            for (String t : timeTokens) {
                Log.d("tokens", "startAlarm: " + t);
                hourAndMin.add(t);
            }

            int hours = Integer.parseInt(hourAndMin.get(0));
            int minutes = Integer.parseInt(hourAndMin.get(1));

            String AM_PM = (String) (hourAndMin.get(2));
            if (AM_PM.equals("PM")) {
                calendarInstance.set(Calendar.AM_PM, Calendar.PM);

                //changing date to a format that the alarmmanager likes
                if (hours == 12) {
                    hours = 12;
                } else {
                    hours = hours + 12;
                }
            } else if (AM_PM.equals("AM")) {
                calendarInstance.set(Calendar.AM_PM, Calendar.AM);
            }

            calendarInstance.set(Calendar.MONTH, month);
            calendarInstance.set(Calendar.DAY_OF_MONTH, day);
            calendarInstance.set(Calendar.YEAR, year);
            calendarInstance.set(Calendar.HOUR_OF_DAY, hours);
            calendarInstance.set(Calendar.MINUTE, minutes);
            calendarInstance.set(Calendar.SECOND, 0);

            Log.d("currentevent", "checkEvents: " + e);
            try {
                calendar.setDate(calendar.getFirstSelectedDate());
                //calendar.setDate(calendar.getF());
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }

            String occurence = e.get(3);

            long millis = calendarInstance.getTimeInMillis();
            long sysMillis = System.currentTimeMillis();

            if(millis >= sysMillis) {
                Log.d("system time in millis", "checkEvents: " + System.currentTimeMillis());

                Log.d("instance time in millis", "checkEvents: " + millis);
                //check the events for yearly or one time occurrence
                if (occurence.equals("once")) {
                    events.add(new EventDay(calendar.getFirstSelectedDate(), R.drawable.ic_arrow_drop_down_circle_black_24dp, Color.parseColor("#000000")));
                    startAlarm(calendarInstance);   //this works

                } else if (occurence.equals("yearly")) {
                    Log.d("yearly", "yearly was selected");

                    Calendar theDate = calendar.getFirstSelectedDate();
                    Date date = new Date();
//
                    //getting all the time information from the current instance
                    int hourODay = calendarInstance.get(Calendar.HOUR_OF_DAY);

                    int theHour = calendarInstance.get(Calendar.HOUR);
                    int theMinute = calendarInstance.get(Calendar.MINUTE);
                    int theSecond = calendarInstance.get(Calendar.SECOND);
                    int theHalf = calendarInstance.get(Calendar.AM_PM);

                    //variables for old and new years
                    int newYear = 0;
                    int oldYear = 0;

                    //for loop to add the events and start alarm
                    for (int i = 0; i <= maxNumberOfYears; i++) {
                        Calendar newInstance = Calendar.getInstance();

                        //adding information to instance, necessary for alarmmanager
                        if (newYear > oldYear) {
                            newInstance.set(Calendar.YEAR, newYear);
                        } else {
                            oldYear = newInstance.get(Calendar.YEAR);
                            newInstance.set(Calendar.YEAR, oldYear);
                        }

                        newInstance.set(Calendar.MONTH, month);
                        newInstance.set(Calendar.DAY_OF_MONTH, day);
                        newInstance.set(Calendar.HOUR, theHour);
                        newInstance.set(Calendar.HOUR_OF_DAY, hourODay);
                        newInstance.set(Calendar.MINUTE, theMinute);
                        newInstance.set(Calendar.SECOND, theSecond);
                        newInstance.set(Calendar.AM_PM, theHalf);
                        newInstance.set(Calendar.MILLISECOND, 0);


                        //start alarm here passes the correct information
                        startAlarm(newInstance);
                        events.add(new EventDay(calendar.getFirstSelectedDate(), R.drawable.ic_arrow_drop_down_circle_black_24dp, Color.parseColor("#F00000")));


                        //increment year
                        newInstance.add(Calendar.YEAR, 1);

                        //getting the newly incremented year
                        newYear = newInstance.get(Calendar.YEAR);


                        //checking the old and new year
                        if (newYear > oldYear) {
                            newInstance.set(Calendar.YEAR, newYear);
                        } else {
                            newInstance.set(Calendar.YEAR, oldYear);
                        }

                        newInstance.getTime();
                        //Log.d("date", "checkEvents: ");


                        //try catch to see if range date is out of range
                        try {
                            calendar.setDate(newInstance);
                            //newYear = theYear;
                        } catch (OutOfDateRangeException e) {
                            Log.d("OutOfDateRangeException", "checkEvents: " + e);
                        }
                    }

                    try {
                        calendar.setDate(theDate);
                    } catch (OutOfDateRangeException e) {
                        Log.d("OutOfDateRangeException", "checkEvents: " + e);
                    }
                    //events.add(new EventDay(calendar.getFirstSelectedDate(), R.drawable.ic_arrow_drop_down_circle_black_24dp, Color.parseColor("#000000")));

                }

                //adds all events to calendar view
                calendar.setEvents(events);
            } else {
                Toast.makeText(getActivity(),"Past date entered.", Toast.LENGTH_LONG).show();
            }

        }
    }

    //function for starting the alarm
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);

        //TODO: Pass data from event to alarmreceiver
    //adding event information to the alarmreceiver
        Bundle bundle = new Bundle();
            bundle.putStringArrayList("eventInfo", e);
            intent.putExtras(bundle);

        //necessary to pass the 4th parameter
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestCodeId, intent, 0); //might need to add a flag
        requestCodeId = requestCodeId + 1;

        Log.d("time in millis", "startAlarm: " + c.getTimeInMillis());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    //used for getting the sms permissions REQUIRED FOR PRIMARY APPLICAITON USE
    private void requestSendSMS(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("Permissions required to schedule SMS messages.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            requestPermissions(new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_CODE);
        }
    }

    //displayed when user made a permission decision.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == SEND_SMS_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                requestSendSMS();
            }
        }
    }
}
