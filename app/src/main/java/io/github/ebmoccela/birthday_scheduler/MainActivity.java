package io.github.ebmoccela.birthday_scheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.applandeo.materialcalendarview.CalendarView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CalendarViewFragment.Callback, EventDialogFragment.Event, CalendarViewFragment.myEvent {

    private FragmentManager fm;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.calendar, new CalendarViewFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void myDateSelected(CalendarView c) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.calendarView, new CalendarViewFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEventMade(ArrayList<String> event) {
        Log.d("eventcallback", "onEventMade: " + event);
        list = event;
        eventCreated(event);
    }

    @Override
    public void eventCreated(ArrayList<String> list) {
        CalendarViewFragment fragment = (CalendarViewFragment) getSupportFragmentManager().findFragmentById(R.id.calendar);
        if(fragment != null) {
            fragment.checkEvents(list);
        }
    }
}
