package com.habitrpg.android.habitica.helpers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import com.habitrpg.android.habitica.R;
import com.magicmicky.habitrpgwrapper.lib.models.tasks.RemindersItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class RemindersManager {

    private DateFormat dateFormater;

    public RemindersManager(String taskType) {
        if (taskType.equals("todo")) {
            dateFormater = DateFormat.getDateTimeInstance();
        } else {
            dateFormater = DateFormat.getTimeInstance();
        }
    }

    @Nullable
    private RemindersItem createReminderFromDateString(String dateString) {
        try {
            Date date = dateFormater.parse(dateString);
            RemindersItem item = new RemindersItem();
            UUID randomUUID = UUID.randomUUID();
            item.setId(randomUUID.toString());
            item.setStartDate(date);
            item.setTime(date);
            return item;
        } catch (ParseException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
    }

    public String reminderTimeToString (Date time) {
        return dateFormater.format(time);
    }

    public void createReminderTimeDialog(@Nullable ReminderTimeSelectedCallback callback, String taskType,
                                         Context context, @Nullable RemindersItem reminder) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        if (taskType.equals("todo")) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_date_time_dialogue);
            dialog.setTitle("Select Date and Time");

            Button dialogConfirmButton = (Button) dialog.findViewById(R.id.customDialogConfirmButton);
            TimePicker dialogTimePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
            DatePicker dialogDatePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

            dialogTimePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(context));

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String dayOfTheWeek = sharedPreferences.getString("FirstDayOfTheWeek",
                    Integer.toString(Calendar.getInstance().getFirstDayOfWeek()));
            FirstDayOfTheWeekHelper firstDayOfTheWeekHelper =
                    FirstDayOfTheWeekHelper.newInstance(Integer.parseInt(dayOfTheWeek));
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                dialogDatePicker.getCalendarView().setFirstDayOfWeek(
                        firstDayOfTheWeekHelper.getFirstDayOfTheWeek());
            } else {
                dialogDatePicker.setFirstDayOfWeek(firstDayOfTheWeekHelper.getFirstDayOfTheWeek());
            }

            dialogConfirmButton.setOnClickListener(view -> {
                int day = dialogDatePicker.getDayOfMonth();
                int month = dialogDatePicker.getMonth();
                int year =  dialogDatePicker.getYear();
                int hour1 = dialogTimePicker.getCurrentHour();
                int minute1 = dialogTimePicker.getCurrentMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour1, minute1, 0);

                onReminderTimeSelected(callback, reminder, calendar);
                dialog.hide();
            });
            dialog.show();
        } else {
            TimePickerDialog timePickerDialog;
            timePickerDialog = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), selectedHour, selectedMinute, 0);

                onReminderTimeSelected(callback, reminder, calendar);
            }, hour, minute, android.text.format.DateFormat.is24HourFormat(context));
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        }
    }

    private void onReminderTimeSelected(@Nullable ReminderTimeSelectedCallback callback, @Nullable RemindersItem reminder, Calendar calendar) {
        RemindersItem remindersItem = reminder;
        if (remindersItem == null) {
            remindersItem = createReminderFromDateString(dateFormater.format(calendar.getTime()));
        } else {
            remindersItem.setTime(calendar.getTime());
        }
        if (callback != null) {
            callback.onReminderTimeSelected(remindersItem);
        }
    }

    public interface ReminderTimeSelectedCallback {
        void onReminderTimeSelected(@Nullable RemindersItem remindersItem);
    }
}
