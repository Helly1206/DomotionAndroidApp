package com.helly.domotion;

import com.helly.domotion.Connection.ConnectionThread;
import com.helly.domotion.Tables.TableFragment;

public class DevicesFragment extends TableFragment {

    public DevicesFragment() {
        super.setArguments(ConnectionThread.ACTUATORS, R.layout.fragment_devices);
    }
}
