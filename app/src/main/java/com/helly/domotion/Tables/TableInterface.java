package com.helly.domotion.Tables;

// Created by helly on 1/21/18.

import android.widget.TableLayout;
import android.widget.TableRow;
import com.helly.domotion.Buttons.ButtonInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public interface TableInterface {
    int CONNECTION_ERROR_NO_CONNECTION = 0;
    int CONNECTION_ERROR_REQUEST = 1;
    int CONNECTION_ERROR_RESPONSE = 2;
    int CONNECTION_ERROR_SSL = 3;
    int CONNECTION_ERROR_AUTH = 4;

    TableLayout CreateTable();
    TableLayout CreateNoConnTable();
    List<String> BuildColumns(JSONArray Info);
    JSONArray BuildRows(JSONArray Info);
    List<String> BuildRow(JSONArray Info);
    TableRow CreateRow();
    void CreateTopText(TableRow tr, String text);
    TableRow CreateBottomRow();
    void CreateText(TableRow tr, String text);
    void CreateButtons(TableRow tr, JSONArray RowData);
    ButtonInterface CreateButton(TableRow tr, ButtonInterface.ButtonType buttonType);
    void HandleButton(ButtonInterface button, int id);
    void HandleButton(ButtonInterface button);
    void AddRow(TableLayout tl, TableRow tr);
    JSONObject GetValues(JSONArray Data);
    void UpdateStatus(JSONArray Data);
    void SetRowValue(int id, String Value);
    void SetButtonValue(ButtonInterface Button, String Value, ButtonInterface.ButtonType buttonType);
    int GetSize();
    boolean NoConnection(TableLayout tl, int Reason);
}
