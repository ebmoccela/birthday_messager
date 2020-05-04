package io.github.ebmoccela.birthday_scheduler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Objects;


//TODO: validate phone number formats/maybe allow user to select from list of contacts.
public class EventDialogFragment extends DialogFragment implements TimePickerDialogFragment.SelectedTime{

    private TextInputEditText edtTime, edtPhone, edtMessage;
    private RadioGroup rbtnScheduleGroup;
    private Button btnScheduleEvent, btnCancelEvent;
    private ArrayList<String> eventOptions = new ArrayList<>();
    private View root;

    private String time;
    private String occurance;
    private String phone;
    private String message;

    private Event mCallback;

    public interface Event {
        void onEventMade(ArrayList<String> event);
    }

//    @Override
//    public void onAttach(@NonNull Context activity) {
//        super.onAttach(activity);
//        mCallback = (Event) activity;
//    }


    //TODO: implement to mainactivity instead also change
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallback = (Event) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        root = inflater.inflate(R.layout.dialog_event_layout, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.setTitle("Schedule Event");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        edtTime = (TextInputEditText) root.findViewById(R.id.edtTime);

        edtPhone = (TextInputEditText) root.findViewById(R.id.edtPhone);

        edtPhone.setText("+");      //remove this if sending messages to self from emulator

        //remove textChangedListener if sending messages to self from emulator
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //auto-generated
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //auto-generated
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+")){
                    edtPhone.setText("+");
                    //Selection.setSelection(edtPhone.getText(), edtPhone.getText().length());
                }
            }
        });


        edtMessage = (TextInputEditText) root.findViewById(R.id.edtMessage);

        rbtnScheduleGroup = (RadioGroup) root.findViewById(R.id.rbtnScheduleGroup);

        btnScheduleEvent = (Button) root.findViewById(R.id.btnScheduleEvent);

        btnCancelEvent = (Button) root.findViewById(R.id.btnCancelEvent);

        setHasOptionsMenu(true);

        return root;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: dismiss();
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialogFragment timeDialog = new TimePickerDialogFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                timeDialog.setTargetFragment(EventDialogFragment.this, 0);
                timeDialog.show(transaction, "time_dialog");
            }
        });

        rbtnScheduleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb) {
                    //Toast.makeText(getActivity(), rb.getText(), Toast.LENGTH_SHORT).show();
                    occurance = rb.getText().toString();
                }
            }
        });


        btnScheduleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = edtPhone.getText().toString();
                message = edtMessage.getText().toString();

                //checks for format of number,
                if (checkNumber(phone)) {   //remove if sending messages to self from emulator

                    //check to make sure all fields have information
                    if (phone.isEmpty() || time.isEmpty() || message.isEmpty() || (occurance == null || occurance.equals(""))) {

                        //checks if radio was selected
                        if (occurance == null || occurance.equals("")) {
                            occurance = "";
                        } else {
                            Log.d("test", "onClick: field was empty");
                        }
                    } else {
                        Log.d("test", "onClick: all fields filled");
                        //TODO: 1. store changes in database, create event for calendar update calendar
                        eventOptions.add(time);
                        eventOptions.add(phone);
                        eventOptions.add(message);
                        eventOptions.add(occurance);
                        mCallback.onEventMade(eventOptions);

                        dismiss();
                    }

                }   //end of phone format
            }
        });

        btnCancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void updateSelectedTime(String t) {
        time = t;
        edtTime.setText(t);
    }

    //TODO:uncomment if testing to send on emulator
    //used to check if number is valid number
    public Boolean checkNumber(String phone){
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        boolean isValid = false;
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
            isValid = phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return isValid;
        //
    }
}
