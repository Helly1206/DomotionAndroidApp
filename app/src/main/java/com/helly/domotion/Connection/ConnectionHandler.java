package com.helly.domotion.Connection;

// Created by helly on 1/21/18.

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.helly.domotion.Tables.TableFragment;
import com.helly.domotion.Tables.TableInterface;
import com.helly.domotion.Tables.TableManager;
import org.json.JSONArray;
import java.lang.ref.WeakReference;

public class ConnectionHandler extends Handler {
    private WeakReference<TableManager> tableManager = null;
    private WeakReference<TableFragment> tableFragment = null;
    static final int MESSAGE_BUILD_TABLE = 1;
    static final int MESSAGE_SET_VALUES = 2;
    static final int MESSAGE_UPDATE_VALUES = 3;
    static final int MESSAGE_NO_CONNECTION = 4; // (retry)
    static final int MESSAGE_REQUEST_ERROR = 5; // (retry)
    static final int MESSAGE_RESPONSE_ERROR = 6; // (retry)
    static final int MESSAGE_SSL_ERROR = 7; // (retry)
    static final int MESSAGE_UNAUTHORIZED = 8; // (login)

    public ConnectionHandler(TableManager tableManager, TableFragment tableFragment) {
        super(Looper.getMainLooper());
        this.tableManager = new WeakReference<>(tableManager);
        this.tableFragment = new WeakReference<>(tableFragment);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_BUILD_TABLE:
                tableManager.get().BuildTable((JSONArray)msg.obj);
                break;
            case MESSAGE_SET_VALUES:
                if (!tableManager.get().SetValues((JSONArray)msg.obj)) {
                    tableFragment.get().onChange();
                }
                break;
            case MESSAGE_UPDATE_VALUES:
                // Nothing to be done ...
                break;
            case MESSAGE_NO_CONNECTION:
                tableManager.get().NoConnection(TableInterface.CONNECTION_ERROR_NO_CONNECTION);
                break;
            case MESSAGE_REQUEST_ERROR:
                tableManager.get().NoConnection(TableInterface.CONNECTION_ERROR_REQUEST);
                break;
            case MESSAGE_RESPONSE_ERROR:
                tableManager.get().NoConnection(TableInterface.CONNECTION_ERROR_RESPONSE);
                break;
            case MESSAGE_SSL_ERROR:
                tableManager.get().NoConnection(TableInterface.CONNECTION_ERROR_SSL);
                break;
            case MESSAGE_UNAUTHORIZED:
                tableManager.get().NoConnection(TableInterface.CONNECTION_ERROR_AUTH);
                break;
        }
    }
}
