package dhbw.timetable.navfragments.notifications.alarm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import dhbw.timetable.R;
import dhbw.timetable.dialogs.ListDialog;

/**
 * Created by Hendrik Ulbrich (C) 2017
 */
public class AlarmFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_alarm, container, false);

        final SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        final Switch aOFESwitch = (Switch) view.findViewById(R.id.AlarmOnFirstEventSwitch);

        final TextView toneView = (TextView) view.findViewById(R.id.AlarmTone);
        final TextView toneValueView = (TextView) view.findViewById(R.id.AlarmToneValue);

        final TextView shiftView = (TextView) view.findViewById(R.id.ShiftTextField);
        final Switch shiftSwitch = (Switch) view.findViewById(R.id.ShiftSwitch);
        final TextView firstShiftView = (TextView) view.findViewById(R.id.FirstShiftTextField);
        final TextView firstShiftValueView = (TextView) view.findViewById(R.id.FirstShiftValue);

        final TextView secondShiftView = (TextView) view.findViewById(R.id.SecondShiftTextField);
        final TextView secondShiftValueView = (TextView) view.findViewById(R.id.SecondShiftValue);

        aOFESwitch.setChecked(sharedPref.getBoolean("alarmOnFirstEvent", false));
        aOFESwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("alarmOnFirstEvent", isChecked);
                editor.apply();
                toneView.setEnabled(isChecked);
                toneValueView.setEnabled(isChecked);
                shiftView.setEnabled(isChecked);
                shiftSwitch.setEnabled(isChecked);

                firstShiftView.setEnabled(isChecked && shiftSwitch.isChecked());
                firstShiftValueView.setEnabled(isChecked && shiftSwitch.isChecked());
                secondShiftView.setEnabled(isChecked && shiftSwitch.isChecked());
                secondShiftValueView.setEnabled(isChecked && shiftSwitch.isChecked());

                // TODO Remove test
                if(isChecked) {
                    GregorianCalendar soon = (GregorianCalendar) Calendar.getInstance();
                    soon.set(Calendar.MINUTE, soon.get(Calendar.MINUTE) + 1);
                    Log.i("ALARM", "Soon is " + new SimpleDateFormat("HH:mm").format(soon.getTime()));
                    // AlarmSupervisor.getInstance().scheduleAlarm(soon, AlarmFragment.this.getActivity());
                    // activateAlarm(getActivity());
                } else {
                    // AlarmSupervisor.getInstance().cancelAllAlarms();
                    // deactivateAlarm(getActivity());
                }
            }
        });

        View.OnClickListener onToneClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog.newInstance("Select a tone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            ListView lw = ((AlertDialog)dialog).getListView();
                            String checkedItem = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                            // TODO Play the tone
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("alarmToneIndex", which);
                            editor.putString("alarmTone", checkedItem);
                            editor.apply();
                            toneValueView.setText(checkedItem);
                        }
                    }
                }, sharedPref.getInt("alarmToneIndex", 0), "Default")
                        .show(getActivity().getFragmentManager(), "alarm_tone");
            }
        };

        toneView.setEnabled(aOFESwitch.isChecked());
        toneView.setOnClickListener(onToneClick);

        toneValueView.setEnabled(aOFESwitch.isChecked());
        toneValueView.setText(sharedPref.getString("alarmTone", "Default"));
        toneValueView.setOnClickListener(onToneClick);

        shiftView.setEnabled(aOFESwitch.isChecked());

        shiftSwitch.setEnabled(aOFESwitch.isChecked());
        shiftSwitch.setChecked(sharedPref.getBoolean("shift", false));
        shiftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("shift", isChecked);
                firstShiftView.setEnabled(isChecked);
                firstShiftValueView.setEnabled(isChecked);
                secondShiftView.setEnabled(isChecked);
                secondShiftValueView.setEnabled(isChecked);
                editor.apply();
            }
        });

        View.OnClickListener onFirstShiftViewClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog.newInstance("Select a shift", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            ListView lw = ((AlertDialog)dialog).getListView();
                            String checkedItem = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("alarmFirstShiftIndex", which);
                            editor.putString("alarmFirstShift", checkedItem);
                            editor.apply();
                            if(which > 0) {
                                checkedItem += " before";
                            }
                            firstShiftValueView.setText(checkedItem);
                        }
                    }
                }, sharedPref.getInt("alarmFirstShiftIndex", 0),
                        "15min", "30min", "45min", "1h", "1,5h", "2h")
                        .show(getActivity().getFragmentManager(), "alarm_first_shift");
            }
        };

        firstShiftView.setEnabled(shiftSwitch.isChecked() && shiftSwitch.isEnabled());
        firstShiftView.setOnClickListener(onFirstShiftViewClick);

        firstShiftValueView.setEnabled(shiftSwitch.isChecked() && shiftSwitch.isEnabled());
        String fShift = sharedPref.getString("alarmFirstShift", "Immediately");
        firstShiftValueView.setText(fShift.equals("Immediately") || fShift.equals("None") ? fShift : fShift + " before" );
        firstShiftValueView.setOnClickListener(onFirstShiftViewClick);

        View.OnClickListener onSecondShiftViewClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog.newInstance("Select a shift", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which >= 0) {
                                    ListView lw = ((AlertDialog)dialog).getListView();
                                    String checkedItem = (String) lw.getAdapter().getItem(lw.getCheckedItemPosition());
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt("alarmSecondShiftIndex", which);
                                    editor.putString("alarmSecondShift", checkedItem);
                                    editor.apply();
                                    if(which > 0) {
                                        checkedItem += " before";
                                    }
                                    secondShiftValueView.setText(checkedItem);
                                }
                            }
                        }, sharedPref.getInt("alarmSecondShiftIndex", 1), "None",
                        "15min", "30min", "45min", "1h", "1,5h", "2h")
                        .show(getActivity().getFragmentManager(), "alarm_second_shift");
            }
        };

        secondShiftView.setEnabled(shiftSwitch.isChecked() && shiftSwitch.isEnabled());
        secondShiftView.setOnClickListener(onSecondShiftViewClick);

        secondShiftValueView.setEnabled(shiftSwitch.isChecked() && shiftSwitch.isEnabled());
        String sShift = sharedPref.getString("alarmSecondShift", "None");
        secondShiftValueView.setText(sShift.equals("Immediately") || sShift.equals("None") ? sShift : sShift + " before" );
        secondShiftValueView.setOnClickListener(onSecondShiftViewClick);

        return view;
    }
}