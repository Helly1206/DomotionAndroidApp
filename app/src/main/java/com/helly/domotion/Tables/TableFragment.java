package com.helly.domotion.Tables;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.helly.domotion.Connection.ConnectionHandler;
import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.R;

// Created by helly on 1/21/18.

public class TableFragment extends Fragment {
    private SharedPreferences sharedPref = null;
    private ConnectionThread connectionThread = null;
    private ConnectionHandler connectionHandler = null;
    private TableManager tableManager = null;
    private int Type = ConnectionThread.MISC;
    private int layoutId = 0;

    public TableFragment() {
        // Required empty public constructor
    }

    public void setArguments(int Type, int layoutId) {
        this.Type = Type;
        this.layoutId = layoutId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(layoutId, container, false);
        if (tableManager == null) {
            tableManager = new TableManager(Type, view, getActivity());
        }
        if (connectionHandler == null) {
            connectionHandler = new ConnectionHandler(tableManager, this);
        }
        if (sharedPref == null) {
            sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
        }
        String URL = sharedPref.getString(getString(R.string.saved_url), getString(R.string.settings_default_url));
        int Timeout = sharedPref.getInt(getString(R.string.saved_timeout), Integer.parseInt(getString(R.string.settings_default_timeout)));
        int timerInterval = 1000;
        connectionThread = new ConnectionThread(connectionHandler,
                Type,
                timerInterval,
                URL,
                Timeout);
        connectionThread.start();

        return view;
    }

    @Override
    public void onPause() {
        connectionThread.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        sharedPref = null;
        connectionThread.quitSafely();
        connectionHandler = null;
        tableManager = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedPref != null) {
            String URL = sharedPref.getString(getString(R.string.saved_url), getString(R.string.settings_default_url));
            int Timeout = sharedPref.getInt(getString(R.string.saved_timeout), Integer.parseInt(getString(R.string.settings_default_timeout)));
            if (!connectionThread.Initialize(URL, Timeout)) {
                Toast.makeText(getActivity(), R.string.error_initializing, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onChange() {
        if (sharedPref != null) {
            String URL = sharedPref.getString(getString(R.string.saved_url), getString(R.string.settings_default_url));
            int Timeout = sharedPref.getInt(getString(R.string.saved_timeout), Integer.parseInt(getString(R.string.settings_default_timeout)));
            if (!connectionThread.InitializeDelayed(URL, Timeout)) {
                Toast.makeText(getActivity(), R.string.error_initializing, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Update(String name, String value) {
        boolean success = false;
        if (connectionThread != null) {
            success = connectionThread.Update(name, value);
        }
        if (!success) {
            Toast.makeText(getActivity(), R.string.error_updating, Toast.LENGTH_SHORT).show();
        }
    }
}
