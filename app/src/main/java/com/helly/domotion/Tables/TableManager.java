package com.helly.domotion.Tables;

// Created by helly on 1/21/18.

import android.app.Activity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.MainActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.List;

public class TableManager {
    private WeakReference<View> view = null;
    private WeakReference<Activity> activity = null;
    private TableBuilder tableBuilder = null;

    TableManager(int Type, View view, Activity activity) {
        this.view = new WeakReference<>(view);
        this.activity = new WeakReference<>(activity);
        switch (Type) {
            case ConnectionThread.ACTUATORS:
                tableBuilder = new TableActuators(view, activity);
                break;
            case ConnectionThread.SENSORS:
                tableBuilder = new TableSensors(view, activity);
                break;
            case ConnectionThread.TIMERS:
                tableBuilder = new TableTimers(view, activity);
                break;
            case ConnectionThread.HOLIDAYS:
                tableBuilder = new TableHolidays(view, activity);
                break;
            case ConnectionThread.LOG:
                tableBuilder = new TableLog(view, activity);
                break;
        }
    }

    public void BuildTable(JSONArray Info) {
        //[[["Id", "Name", "Description", "Toggle", "Digital"], [[1, "AudioVideo", "Audio/ Video", 6, 1], [2, "ReadingLamp", "Reading Lamp", 1, 1], [3, "TableLamp", "Table Lamp", 1, 1], [4, "AVLamp", "Audio/ Video Lamp", 1, 1], [5, "CabinetLamp", "Cabinet Lamp", 1, 1], [6, "HeatingRemote", "Heating Remote", 2, 1], [7, "BlindsTimer", "Blinds Timer", 3, 1], [8, "BlindsEnable", "Blinds Enable", 2, 1], [9, "BlindsManual", "Blinds Manual", 3, 1], [10, "CabinetLampTimer", "Cabinet Lamp Timer", 1, 1], [11, "ReadingLampDim", "Reading Lamp Dimmable", 1, 1]]]]
        if ((view != null) && (activity != null) && (Info != null)) {
            TableLayout tl = tableBuilder.CreateTable();
            List<String> ColumnNames = tableBuilder.BuildColumns(Info);
            JSONArray RowData = tableBuilder.BuildRows(Info);
            BuildTopRow(tl, ColumnNames);
            if (RowData != null) {
                for (int i = 0; i < RowData.length(); i++) {
                    BuildRow(tl, RowData.optJSONArray(i));
                }
            }
            BuildBottomRow(tl);
        }
    }

    private void BuildTopRow(TableLayout tl, List<String> ColumnNames) {
        //["Id", "Name", "Description", "ActuatorType", "Digital"]
        if (ColumnNames != null) {
            TableRow tr = tableBuilder.CreateRow();

            for (int i = 0; i < ColumnNames.size(); i++) {
                String col = ColumnNames.get(i);
                if (col != null) {
                    tableBuilder.CreateTopText(tr, col);
                }
            }
            tableBuilder.AddRow(tl, tr);
        }
    }

    private void BuildRow(TableLayout tl, JSONArray RowData) {
        //[1, "AudioVideo", "Audio/ Video", 6, 1]
        List<String> Row = tableBuilder.BuildRow(RowData);
        if (Row != null) {
            TableRow tr = tableBuilder.CreateRow();

            for (int i = 0; i < Row.size(); i++) {
                String col = Row.get(i);
                if (col != null) {
                    tableBuilder.CreateText(tr, col);
                }
            }
            tableBuilder.CreateButtons(tr, RowData);
            tableBuilder.AddRow(tl, tr);
        }
    }

    private void BuildBottomRow(TableLayout tl) {
        TableRow tr = tableBuilder.CreateBottomRow();
        if (tr != null) {
            tableBuilder.AddRow(tl, tr);
        }
    }

    public boolean SetValues(JSONArray Data) {
        //["14-01-2018 20:10:05", {"1": 1, "2": 1, "3": 1, "4": 1, "5": 1, "6": 0, "7": 1, "8": 1, "9": 0, "10": 0, "11": 0}]
        boolean ReturnValue = false;

        if (tableBuilder.GetSize() > 0) {
            JSONObject Values = tableBuilder.GetValues(Data);
            if (Values != null) {
                // test for consistency, otherwise re-initialize
                if (tableBuilder.GetSize() == Values.length()) {
                    if ((view != null) && (activity != null)) {
                        tableBuilder.UpdateStatus(Data);
                        for (int i = 0; i < Values.length(); i++) {
                            String key = Values.names().optString(i);
                            String Value = Values.optString(key);
                            tableBuilder.SetRowValue(Integer.parseInt(key), Value);
                        }
                    }
                    ReturnValue = true;
                }
            }
        } else {
            tableBuilder.UpdateStatus(Data);
            ReturnValue = true;
        }
        return ReturnValue;
    }

    public void NoConnection(int Reason) {
        if ((view != null) && (activity != null)) {
            TableLayout tl = tableBuilder.CreateNoConnTable();
            if (!tableBuilder.NoConnection(tl, Reason)) { // No retry, reset login
                MainActivity.setLoginDone(false);
            }
        }
    }
}
