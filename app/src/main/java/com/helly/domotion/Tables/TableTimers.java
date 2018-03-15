package com.helly.domotion.Tables;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.helly.domotion.Buttons.ButtonInterface;
import com.helly.domotion.Misc.DateTimeFormat;
import com.helly.domotion.R;
import com.helly.domotion.TimerDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

// Created by helly on 1/21/18.
public class TableTimers extends TableBuilder implements TimerDialog.TimerDialogListener {
    TableTimers(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public TableLayout CreateTable() {
        super.CreateTable();
        TextView tvStatus = activity.get().findViewById(R.id.textViewStatus);
        if (tvStatus != null) {
            tvStatus.setText(R.string.header_status_run);
        }

        TableLayout tl = view.get().findViewById(R.id.TimersNoConnTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.TimersTable);
        tl.removeAllViews();
        return tl;
    }

    @Override
    public TableLayout CreateNoConnTable() {
        super.CreateNoConnTable();
        TextView tvStatus = activity.get().findViewById(R.id.textViewStatus);
        if (tvStatus != null) {
            tvStatus.setText(R.string.header_status_nocon);
        }

        TableLayout tl = view.get().findViewById(R.id.TimersTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.TimersNoConnTable);
        tl.removeAllViews();
        return tl;
    }

    @Override
    public List<String> BuildColumns(JSONArray Info) {
        JSONArray ColumnNamesJSON = Info.optJSONArray(0).optJSONArray(0);
        List<String> ColumnNames = new ArrayList<>();

        int len = ColumnNamesJSON.length();
        if (len > 3) {
            len = 3;
        }

        for(int i = 0; i < len; i++){
            ColumnNames.add(ColumnNamesJSON.optString(i));
        }
        ColumnNames.add(activity.get().getString(R.string.table_time));
        ColumnNames.add(activity.get().getString(R.string.table_active));
        ColumnNames.add(activity.get().getString(R.string.table_edit));
        return ColumnNames;
    }

    @Override
    public List<String> BuildRow(JSONArray Info) {
        List<String> Row = new ArrayList<>();

        int len = Info.length();
        if (len > 3) {
            len = 3;
        }

        for(int i = 0; i < len; i++){
            Row.add(Info.optString(i));
        }
        Row.add("");
        Row.add("");

        return Row;
    }

    @Override
    public TableRow CreateBottomRow() {
        ButtonInterface.ButtonType buttonType = ButtonInterface.ButtonType.RECALC;
        TableRow tr = CreateRow();
        CreateButton(tr, buttonType);
        CreateText(tr, activity.get().getResources().getString(R.string.table_recalculate));
        return tr;
    }

    @Override
    public void CreateButtons(TableRow tr, JSONArray RowData) {
        ButtonInterface.ButtonType buttonType = ButtonInterface.ButtonType.EDIT;
        ButtonInterface buttonView = CreateButton(tr, buttonType);

        if (buttonView != null) {
            RowMap.append(Integer.parseInt(RowData.optString(0)), tr);
            NameMap.append(Integer.parseInt(RowData.optString(0)), RowData.optString(1));
        }
    }

    @Override
    public void HandleButton(ButtonInterface button, int id) {
        super.HandleButton(button, id);
        TableRow row = RowMap.get(id);
        JSONArray rowData = new JSONArray();
        if (row.getChildCount() >= 5) {
            for (int i = 1; i < 5; i++) {
                rowData.put(((TextView)row.getChildAt(i)).getText());
            }
        }
        switch (button.GetType()) {
            case EDIT:
                // popup --> override
                TimerDialog holidayDialog = new TimerDialog();
                holidayDialog.setListener(this);
                holidayDialog.setContents(id, activity.get().getResources().getString(R.string.dialog_timer_edit), rowData);
                holidayDialog.showDialog(((AppCompatActivity)activity.get()).getSupportFragmentManager());
                break;
        }
    }

    @Override
    public void HandleButton(ButtonInterface button) {
        super.HandleButton(button);
        switch (button.GetType()) {
            case RECALC:
                TableFragment fragment = getFragment();
                if (fragment != null) {
                    fragment.Update(activity.get().getResources().getString(R.string.update_recalc), "");
                }
                break;
        }
    }

    @Override
    public JSONObject GetValues(JSONArray Data) {
        return Data.optJSONObject(2);
    }

    @Override
    public void UpdateStatus(JSONArray Data) {
        TextView tvStatus = activity.get().findViewById(R.id.textViewStatus);
        if (tvStatus != null) {
            tvStatus.setText(R.string.header_status_run);
        }

        String DomotionTime = Data.optString(0);
        TextView tvTime = activity.get().findViewById(R.id.textViewTime);
        if ((DomotionTime != null) && (tvTime != null)) {
            tvTime.setText(DomotionTime);
        }

        int sunRise = Data.optJSONArray(1).optInt(0);
        int sunSet = Data.optJSONArray(1).optInt(1);
        TextView tvSunRise = activity.get().findViewById(R.id.textViewSunrise);
        TextView tvSunSet = activity.get().findViewById(R.id.textViewSunset);
        if (tvSunRise != null) {
            tvSunRise.setText(DateTimeFormat.Mod2Asc(sunRise));
        }
        if (tvSunSet != null) {
            tvSunSet.setText(DateTimeFormat.Mod2Asc(sunSet));
        }
    }

    @Override
    public void SetRowValue(int id, String Value) {
        // Timers: ["02-02-2018 20:44:22", [491, 1048], {"1": 1046, "2": -1, "3": -1, "4": 1410, "5": -1, "6": -1, "7": 360, "8": -1, "9": -1, "10": 1350, "11": 481, "12": 1063}]
        if (NameMap.get(id) != null) {
            int MODValue = Integer.valueOf(Value);
            TableRow tr = RowMap.get(id);

            TextView tvTime = (TextView)tr.getChildAt(3);
            TextView tvActive = (TextView)tr.getChildAt(4);
            if (MODValue == -1) {
                tvActive.setText(R.string.table_false);
                tvTime.setText(DateTimeFormat.Mod2Asc(0));
            } else {
                tvActive.setText(R.string.table_true);
                tvTime.setText(DateTimeFormat.Mod2Asc(MODValue));
            }
        }
    }

    @Override
    public void onTimerDialogYesClick(DialogFragment dialog, int id, int time) {
        TableFragment fragment = getFragment();
        if (fragment != null) {
            fragment.Update(NameMap.get(id), String.valueOf(time));
        }
        Toast.makeText(activity.get(), R.string.dialog_update, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerDialogNoClick(DialogFragment dialog, int id, int time) {
        Toast.makeText(activity.get(), R.string.dialog_cancel, Toast.LENGTH_SHORT).show();
    }
}
