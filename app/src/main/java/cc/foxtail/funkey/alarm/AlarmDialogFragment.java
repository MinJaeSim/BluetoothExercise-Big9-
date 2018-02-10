package cc.foxtail.funkey.alarm;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.foxtail.funkey.R;

public class AlarmDialogFragment extends DialogFragment {
    private int getHour;
    private int getMinute;

    private CheckBox monday_button;
    private CheckBox tuesday_button;
    private CheckBox wednesday_button;
    private CheckBox thursday_button;
    private CheckBox friday_button;
    private CheckBox saturday_button;
    private CheckBox sunday_button;

    private EditText hourEditText;
    private EditText minEditText;
    private Alarm alarm;

    private List<String> nameList;
    private List<Map<String, Object>> userList;
    private Spinner spinner;
    private String userName;
    private String userGender;

    public interface OnAlarmDialogEventListener {
        void onConfirm(Alarm alarm);
    }

    private OnAlarmDialogEventListener onAlarmDialogEventListener;

    public void setOnAlarmDialogEventListener(OnAlarmDialogEventListener onAlarmDialogEventListener) {
        this.onAlarmDialogEventListener = onAlarmDialogEventListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_dialog, container, false);

        monday_button = view.findViewById(R.id.monday_button);
        tuesday_button = view.findViewById(R.id.tuesday_button);
        wednesday_button = view.findViewById(R.id.wednesday_button);
        thursday_button = view.findViewById(R.id.thursday_button);
        friday_button = view.findViewById(R.id.friday_button);
        saturday_button = view.findViewById(R.id.saturday_button);
        sunday_button = view.findViewById(R.id.sunday_button);

        CheckBox[] dayCheckBox = {
                sunday_button, monday_button, tuesday_button, wednesday_button, thursday_button, friday_button, saturday_button
        };

        spinner = view.findViewById(R.id.user_spinner);

        loadUserChildren();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userName = adapterView.getItemAtPosition(i).toString();
                userGender = userList.get(i).get("sex").toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        hourEditText = view.findViewById(R.id.time_hour_edit_text);
        Calendar c = Calendar.getInstance();
        getHour = c.get(Calendar.HOUR_OF_DAY);

        hourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getHour = Integer.parseInt(charSequence.toString());
                else
                    getHour = -1;

                if (getHour > 23) {
                    getHour = 23;
                    hourEditText.setText("" + getHour);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        minEditText = view.findViewById(R.id.time_min_edit_text);
        getMinute = c.get(Calendar.MINUTE);
        minEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getMinute = Integer.parseInt(charSequence.toString());
                else
                    getMinute = -1;

                if (getMinute > 59) {
                    getMinute = 59;
                    minEditText.setText("" + getMinute);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (alarm != null) {
            for (int i = 1; i < alarm.getDay().size(); i++) {
                if (alarm.getDay().get(i))
                    dayCheckBox[i - 1].setChecked(true);
            }

            String[] time = alarm.getTime().split(":");

            getHour = Integer.parseInt(time[0]);
            getMinute = Integer.parseInt(time[1]);
        }

        hourEditText.setText("" + getHour);
        minEditText.setText("" + getMinute);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkError())
                    addNewAlarm();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    private void loadUserChildren() {
        nameList = new ArrayList<>();
        userList = new ArrayList<>();

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, nameList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Children")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        nameList.clear();

                        for (DocumentSnapshot document : value) {
                            nameList.add(document.getString("userName"));
                            userList.add(document.getData());
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void addNewAlarm() {
        boolean[] week = {
                false,
                sunday_button.isChecked(),
                monday_button.isChecked(),
                tuesday_button.isChecked(),
                wednesday_button.isChecked(),
                thursday_button.isChecked(),
                friday_button.isChecked(),
                saturday_button.isChecked()
        };

        List<Boolean> day = new ArrayList<>();


        for (boolean e : week) {
            day.add(e);
        }
        int id;
        String time = String.format(Locale.KOREA, "%02d:%02d", getHour, getMinute);

        if (alarm != null) {
            id = alarm.getAlarmId();
        } else {
            id = (FirebaseAuth.getInstance().getCurrentUser().getUid() + time + userName).hashCode();
        }

        Alarm alarm = new Alarm(userName, userGender, time, day, true, id, FirebaseAuth.getInstance().getUid());

        if (onAlarmDialogEventListener != null) {
            onAlarmDialogEventListener.onConfirm(alarm);
        }

        dismiss();
    }

    private boolean checkError() {
        if (getHour > 23) {
            hourEditText.requestFocus();
            Snackbar.make(getView(), "23 이하로 입력해 주세요", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getHour == -1) {
            hourEditText.requestFocus();
            Snackbar.make(getView(), "시간을 입력해 주세요", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getMinute > 59) {
            minEditText.requestFocus();
            Snackbar.make(getView(), "59 이하로 입력해 주세요", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getMinute == -1) {
            minEditText.requestFocus();
            Snackbar.make(getView(), "시간을 입력해 주세요", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
