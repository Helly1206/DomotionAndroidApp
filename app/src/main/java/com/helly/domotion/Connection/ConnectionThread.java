package com.helly.domotion.Connection;

// Created by helly on 1/21/18.

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.helly.domotion.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ConnectionThread extends HandlerThread {
    public static final int ACTUATORS = 1;
    public static final int SENSORS = 2;
    public static final int TIMERS = 3;
    public static final int HOLIDAYS = 4;
    public static final int LOG = 5;
    public static final int MISC = 0;
    private final int MESSAGE_INIT = 1;
    private final int MESSAGE_LOOP = 2;
    private final int MESSAGE_UPDATE = 3;
    private Handler mHandler = null;
    private BWAClient bwaClient = null;
    private boolean connected = false;
    private boolean isInitialized = false;
    private boolean newStart = false;
    private WeakReference<ConnectionHandler> connectionHandler = null;
    private int TimerInterval = 1000;
    private String URL = "";
    private int Timeout = 3000;
    private int Type = MISC;
    private String Tag = "";

    public ConnectionThread(ConnectionHandler connectionHandler, int Type, int TimerInterval, String URL, int Timeout) {
        super("ConnectionThread_" + Integer.toString(Type),
                Process.THREAD_PRIORITY_DEFAULT);
        this.Type = Type;
        this.connectionHandler = new WeakReference<>(connectionHandler);
        this.TimerInterval = TimerInterval;
        this.URL = URL;
        this.Timeout = Timeout;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_INIT:
                        try {
                            final JSONArray Info;
                            Info = bwaClient.GetInfo(Tag);
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_BUILD_TABLE;
                            msg2.obj = Info;
                            connectionHandler.get().sendMessage(msg2);
                            connected = true;
                            mHandler.sendEmptyMessage(MESSAGE_LOOP);
                        } catch (BWAClient.NotFoundException | BWAClient.ServiceUnavailableException | BWAClient.GateWayTimeoutException | IOException | BWAClient.InternalServerErrorException e) {
                            e.printStackTrace();
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_NO_CONNECTION;
                            connectionHandler.get().sendMessage(msg2);
                            connected = false;
                        } catch (BWAClient.BadRequestException e) {
                            e.printStackTrace();
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_REQUEST_ERROR;
                            connectionHandler.get().sendMessage(msg2);
                            connected = false;
                        } catch (BWAClient.Response_Error_Exception | JSONException e) {
                            e.printStackTrace();
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_RESPONSE_ERROR;
                            connectionHandler.get().sendMessage(msg2);
                            connected = false;
                        } catch (NoSuchAlgorithmException | KeyManagementException e) {
                            e.printStackTrace();
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_SSL_ERROR;
                            connectionHandler.get().sendMessage(msg2);
                            connected = false;
                        } catch (BWAClient.UnauthorizedException e) {
                            e.printStackTrace();
                            Message msg2 = new Message();
                            msg2.what = ConnectionHandler.MESSAGE_UNAUTHORIZED;
                            connectionHandler.get().sendMessage(msg2);
                            connected = false;
                        }
                        break;
                    case MESSAGE_LOOP:
                        if (connected) {
                            try {
                                final JSONArray Data = bwaClient.GetAll(Tag);
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_SET_VALUES;
                                msg2.obj = Data;
                                connectionHandler.get().sendMessage(msg2);
                                mHandler.sendEmptyMessageDelayed(MESSAGE_LOOP, TimerInterval);
                            } catch (BWAClient.NotFoundException | BWAClient.ServiceUnavailableException | BWAClient.GateWayTimeoutException | IOException | BWAClient.InternalServerErrorException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_NO_CONNECTION;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.BadRequestException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_REQUEST_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.Response_Error_Exception | JSONException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_RESPONSE_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_SSL_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.UnauthorizedException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_UNAUTHORIZED;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            }
                        }
                        break;
                    case MESSAGE_UPDATE:
                        if (connected) {
                            try {
                                bwaClient.Set(((String[])msg.obj)[0],((String[])msg.obj)[1]);
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_UPDATE_VALUES;
                                connectionHandler.get().sendMessage(msg2);
                                mHandler.sendEmptyMessageDelayed(MESSAGE_LOOP, TimerInterval);
                            } catch (BWAClient.NotFoundException | BWAClient.ServiceUnavailableException | BWAClient.GateWayTimeoutException | IOException | BWAClient.InternalServerErrorException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_NO_CONNECTION;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.BadRequestException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_REQUEST_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.Response_Error_Exception | JSONException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_RESPONSE_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_SSL_ERROR;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            } catch (BWAClient.UnauthorizedException e) {
                                e.printStackTrace();
                                Message msg2 = new Message();
                                msg2.what = ConnectionHandler.MESSAGE_UNAUTHORIZED;
                                connectionHandler.get().sendMessage(msg2);
                                connected = false;
                            }
                        }
                        break;
                }
            }
        };

        Initialize(URL, Timeout, 0);
    }

    @Override public void start() {
        super.start();
        newStart = true;
    }

    private boolean Initialize(String URL, int Timeout, int Delay) {
        boolean success = true;
        this.URL = URL;
        this.Timeout = Timeout;
        onPause(); // Break pending messages
        if (bwaClient == null) {
            bwaClient = new BWAClient(URL, MainActivity.getCredentials(), Timeout);
        } else {
            bwaClient.SetSettings(URL, MainActivity.getCredentials(), Timeout);
        }
        switch (Type) {
            case ACTUATORS:
                Tag = bwaClient.TagActuators;
                break;
            case SENSORS:
                Tag = bwaClient.TagSensors;
                break;
            case TIMERS:
                Tag = bwaClient.TagTimers;
                break;
            case HOLIDAYS:
                Tag = bwaClient.TagHolidays;
                break;
            case LOG:
                Tag = bwaClient.TagLog;
                break;
            default:
                Tag = "";
        }
        if (mHandler != null) {
            if (Delay > 0) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_INIT, Delay);
            } else{
                mHandler.sendEmptyMessage(MESSAGE_INIT);
            }
            isInitialized = true;
        } else {
            success = false;
        }
        return success;
    }

    public boolean Initialize(String URL, int Timeout) {
        boolean isNewStart = newStart;
        newStart = false;
        return (isInitialized | isNewStart) || Initialize(URL, Timeout, 0);
    }

    public boolean InitializeDelayed(String URL, int Timeout) {
        return Initialize(URL, Timeout, TimerInterval);
    }

    public boolean Update(String tag, String value) {
        boolean success = true;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(this);
            mHandler.removeMessages(MESSAGE_INIT);
            mHandler.removeMessages(MESSAGE_LOOP);
            mHandler.removeMessages(MESSAGE_UPDATE);
            Message msg = new Message();
            msg.what = MESSAGE_UPDATE;
            msg.obj = new String[] {tag, value};
            mHandler.sendMessage(msg);
        } else {
            success =false;
        }

        return success;
    }

    public void onPause() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(this);
            mHandler.removeMessages(MESSAGE_INIT);
            mHandler.removeMessages(MESSAGE_LOOP);
            mHandler.removeMessages(MESSAGE_UPDATE);
        }
        isInitialized = false;
    }
}
