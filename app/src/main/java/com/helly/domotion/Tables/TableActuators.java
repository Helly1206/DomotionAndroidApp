package com.helly.domotion.Tables;

import android.app.Activity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.helly.domotion.Buttons.ButtonInterface;
import com.helly.domotion.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

// Created by helly on 1/21/18.

public class TableActuators extends TableBuilder {
    TableActuators(View view, Activity activity) {
        super(view, activity);
    }

    @Override
    public TableLayout CreateTable() {
        super.CreateTable();
        TextView tvStatus = activity.get().findViewById(R.id.textViewStatus);
        if (tvStatus != null) {
            tvStatus.setText(R.string.header_status_run);
        }

        TableLayout tl = view.get().findViewById(R.id.DevicesNoConnTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.DevicesTable);
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

        TableLayout tl = view.get().findViewById(R.id.DevicesTable);
        tl.removeAllViews();
        tl = view.get().findViewById(R.id.DevicesNoConnTable);
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
        ColumnNames.add(activity.get().getString(R.string.table_value));
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
        return Row;
    }

    @Override
    public void CreateButtons(TableRow tr, JSONArray RowData) {
        ButtonInterface.ButtonType buttonType;
        if (RowData.optInt(4) > 0) { // Digital
            if (RowData.optInt(3) == 3) { // Blinds
                buttonType = ButtonInterface.ButtonType.BLINDS;
            } else { // On/ off device
                buttonType = ButtonInterface.ButtonType.SWITCH;
            }
        } else { // Analog
            buttonType = ButtonInterface.ButtonType.ANALOG;
        }

        ButtonInterface buttonView = CreateButton(tr, buttonType);

        if (buttonView != null) {
            RowMap.append(Integer.parseInt(RowData.optString(0)), tr);
            NameMap.append(Integer.parseInt(RowData.optString(0)), RowData.optString(1));
        }
    }

    @Override
    public JSONObject GetValues(JSONArray Data) {
        return Data.optJSONObject(1);
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
    }

    @Override
    public void SetRowValue(int id, String Value) {
        if (NameMap.get(id) != null) {
            TableRow tr = RowMap.get(id);
            ButtonInterface button = (ButtonInterface) tr.getChildAt(3);
            ButtonInterface.ButtonType buttonType = button.GetType();
            SetButtonValue(button, Value, buttonType);
        }
    }
}
