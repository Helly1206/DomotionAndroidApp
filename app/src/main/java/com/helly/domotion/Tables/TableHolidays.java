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
import com.helly.domotion.ConfirmDialog;
import com.helly.domotion.HolidayDialog;
import com.helly.domotion.Misc.DateTimeFormat;
import com.helly.domotion.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

// Created by helly on 1/21/18.
public class TableHolidays extends TableBuilder implements ConfirmDialog.ConfirmDialogListener, HolidayDialog.HolidayDialogListener {
    TableHolidays(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public TableLayout CreateTable() {
        super.CreateTable();
        TextView tvStatus = activity.get().findViewById(R.id.textViewStatus);
        if (tvStatus != null) {
            tvStatus.setText(R.string.header_status_run);
        }

        TableLayout tl = view.get().findViewById(R.id.HolidaysNoConnTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.HolidaysTable);
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

        TableLayout tl = view.get().findViewById(R.id.HolidaysTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.HolidaysNoConnTable);
        tl.removeAllViews();
        return tl;
    }

    @Override
    public List<String> BuildColumns(JSONArray Info) {
        JSONArray ColumnNamesJSON = Info.optJSONArray(0).optJSONArray(0);
        List<String> ColumnNames = new ArrayList<>();

        int len = ColumnNamesJSON.length();
        if (len > 5) {
            len = 5;
        }

        for(int i = 1; i < len; i++) {
            ColumnNames.add(ColumnNamesJSON.optString(i));
        }
        ColumnNames.add(activity.get().getString(R.string.table_today));
        ColumnNames.add(activity.get().getString(R.string.table_edit));
        ColumnNames.add(activity.get().getString(R.string.table_delete));
        return ColumnNames;
    }

    @Override
    public List<String> BuildRow(JSONArray Info) {
        List<String> Row = new ArrayList<>();

        //Row.add(Info.optString(0));
        Row.add("");
        Row.add("");
        Row.add("");
        Row.add("");

        return Row;
    }

    @Override
    public TableRow CreateBottomRow() {
        ButtonInterface.ButtonType buttonType = ButtonInterface.ButtonType.ADD;
        TableRow tr = CreateRow();
        CreateButton(tr, buttonType);
        return tr;
    }

    @Override
    public void CreateButtons(TableRow tr, JSONArray RowData) {
        ButtonInterface.ButtonType buttonType = ButtonInterface.ButtonType.EDIT;
        ButtonInterface buttonView = CreateButton(tr, buttonType);

        if (buttonView != null) {
            RowMap.append(Integer.parseInt(RowData.optString(0)), tr);
            NameMap.append(Integer.parseInt(RowData.optString(0)), "");
        }

        buttonType = ButtonInterface.ButtonType.DELETE;
        CreateButton(tr, buttonType);
    }

    @Override
    public void HandleButton(ButtonInterface button, int id) {
        super.HandleButton(button, id);
        // Build contents (always 2 rows, column names + content
        TableRow row = RowMap.get(id);
        TableRow columnNames = (TableRow)((TableLayout)row.getParent()).getChildAt(0);
        JSONArray columns = new JSONArray();
        if (columnNames.getChildCount() >= 4) {
            for (int i = 0; i < 4; i++) {
                columns.put(((TextView)columnNames.getChildAt(i)).getText());
            }
        }
        JSONArray rowData = new JSONArray();
        if (row.getChildCount() >= 4) {
            for (int i = 0; i < 4; i++) {
                rowData.put(((TextView)row.getChildAt(i)).getText());
            }
        }
        JSONArray contents = new JSONArray();
        contents.put(columns);
        contents.put(rowData);
        switch (button.GetType()) {
            case EDIT:
                // popup --> override
                HolidayDialog holidayDialog = new HolidayDialog();
                holidayDialog.setListener(this);
                holidayDialog.setContents(id, activity.get().getResources().getString(R.string.dialog_holiday_edit), rowData);
                holidayDialog.showDialog(((AppCompatActivity)activity.get()).getSupportFragmentManager());
                break;
            case DELETE:
                // popup --> override
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setListener(this);
                confirmDialog.setContents(id, activity.get().getResources().getString(R.string.dialog_delete), contents);
                confirmDialog.showDialog(((AppCompatActivity)activity.get()).getSupportFragmentManager());
                break;
        }
    }

    @Override
    public void HandleButton(ButtonInterface button) {
        super.HandleButton(button);
        switch (button.GetType()) {
            case ADD:
                // popup --> override
                // get id
                int id = 1;
                if (RowMap.size() > 0) {
                    id = RowMap.keyAt(RowMap.size() - 1) + 1;
                }
                JSONArray rowData = new JSONArray();
                rowData.put(activity.get().getResources().getString(R.string.table_home));
                rowData.put(DateTimeFormat.getTodayAsc());
                rowData.put(DateTimeFormat.getTodayAsc());
                rowData.put(activity.get().getResources().getString(R.string.table_true));
                HolidayDialog holidayDialog = new HolidayDialog();
                holidayDialog.setListener(this);
                holidayDialog.setContents(id, activity.get().getResources().getString(R.string.dialog_holiday_add), rowData);
                holidayDialog.showDialog(((AppCompatActivity)activity.get()).getSupportFragmentManager());
                break;
        }
    }

    @Override
    public JSONObject GetValues(JSONArray Data) {
        JSONObject Values = Data.optJSONObject(1);
        JSONObject NewValues = new JSONObject();
        for (int i = 0; i < Values.length(); i++) {
            String key = Values.names().optString(i);
            String val = Values.optJSONArray(key).toString();
            try {
                NewValues.put(key, val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NewValues;
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

        TextView tvToday = activity.get().findViewById(R.id.textViewToday);
        if (tvToday != null) {
            tvToday.setText(GetToday(Data));
        }
    }

    @Override
    public void SetRowValue(int id, String Value) {
        // Holidays: ["02-02-2018 20:45:19", [{"1": [0, 736728, 736728], "2", [0, 736729, 736729]}]]
        if (NameMap.get(id) != null) {
            try {
                JSONArray JSONValue = new JSONArray(Value);
                TableRow tr = RowMap.get(id);

                TextView tvType = (TextView)tr.getChildAt(0);
                TextView tvStart = (TextView)tr.getChildAt(1);
                TextView tvEnd = (TextView)tr.getChildAt(2);
                TextView tvToday = (TextView)tr.getChildAt(3);
                int type = JSONValue.optInt(0, 0);
                if (type == 0) {
                    tvType.setText(R.string.table_home);
                } else {
                    tvType.setText(R.string.table_trip);
                }
                int Start = JSONValue.optInt(1,0);
                int End = JSONValue.optInt(2,0);
                tvStart.setText(DateTimeFormat.Ord2Asc(Start));
                tvEnd.setText(DateTimeFormat.Ord2Asc(End));
                if (DateTimeFormat.IsToday(Start, End)) {
                    tvToday.setText(R.string.table_true);
                } else {
                    tvToday.setText(R.string.table_false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String GetToday(JSONArray Data) {
        String Today = activity.get().getString(R.string.table_normal_day);
        JSONObject Values = GetValues(Data);
        for (int i = 0; i < Values.length(); i++) {
            String key = Values.names().optString(i);
            String Value = Values.optString(key);
            try {
                JSONArray JSONValue = new JSONArray(Value);
                int type = JSONValue.optInt(0, 0);
                int Start = JSONValue.optInt(1,0);
                int End = JSONValue.optInt(2,0);
                if (DateTimeFormat.IsToday(Start, End)) {
                    if (type == 0) {
                        Today = activity.get().getString(R.string.table_home_day);
                    } else {
                        Today = activity.get().getString(R.string.table_trip_day);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Today;
    }

    @Override
    public void onConfirmDialogYesClick(DialogFragment dialog, int id) {
        JSONObject deleteCommand = new JSONObject();
        JSONArray deleteValues = new JSONArray();
        deleteValues.put(0); // Home/ trip not relevant
        deleteValues.put(0); // Start should be 0
        deleteValues.put(0); // End should be 0
        try {
            deleteCommand.put(String.valueOf(id),deleteValues);
            TableFragment fragment = getFragment();
            if (fragment != null) {
                fragment.Update(activity.get().getResources().getString(R.string.update_holidays), deleteCommand.toString());
            }
            Toast.makeText(activity.get(), R.string.dialog_update, Toast.LENGTH_SHORT).show();
            // Don't delete tablerow, will be updated ...
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfirmDialogNoClick(DialogFragment dialog, int id) {
        Toast.makeText(activity.get(), R.string.dialog_cancel, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHolidayDialogYesClick(DialogFragment dialog, int id, JSONArray contents) {
        JSONObject editCommand = new JSONObject();
        try {
            editCommand.put(String.valueOf(id),contents);
            TableFragment fragment = getFragment();
            if (fragment != null) {
                fragment.Update(activity.get().getResources().getString(R.string.update_holidays), editCommand.toString());
            }
            Toast.makeText(activity.get(), R.string.dialog_update, Toast.LENGTH_SHORT).show();
            // Don't edit tablerow, will be updated ...
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHolidayDialogNoClick(DialogFragment dialog, int id, JSONArray contents) {
        Toast.makeText(activity.get(), R.string.dialog_cancel, Toast.LENGTH_SHORT).show();
    }
}
