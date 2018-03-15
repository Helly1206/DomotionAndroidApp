package com.helly.domotion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Created by helly on 3/4/18.

public class LoginDialog extends DialogFragment  {
    protected View mLayout;

    private EditText mUserView;
    private EditText mPasswordView;

    public void showDialog(FragmentManager manager) {
        show(manager, "login");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ViewGroup nullParent = null;
        mLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, nullParent);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setCancelable(false);
        builder.setTitle(R.string.title_activity_login);
        builder.setView(mLayout);
        builder.setPositiveButton(R.string.action_login, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                attemptLogin();
                ((MainActivity)getActivity()).Restart();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.setLoginDone(false);
                MainActivity.setCredentials("");
                ((MainActivity)getActivity()).Restart();
            }
        });
        setContents();
        return builder.create();
    }

    private void setContents() {
        if (mLayout != null) {
            mUserView = mLayout.findViewById(R.id.LoginUser);

            mPasswordView = mLayout.findViewById(R.id.LoginPassword);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        dismiss();
                        ((MainActivity)getActivity()).Restart();
                        return true;
                    }
                    return false;
                }
            });
        }

        // Set user to default setting value
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
        String SettingsUser = sharedPref.getString(getString(R.string.saved_user), "");
        if (!SettingsUser.isEmpty()) {
            mUserView.setText(SettingsUser);
            mPasswordView.requestFocus();
        } else {
            mUserView.requestFocus();
        }

        MainActivity.setLoginDone(false);
        MainActivity.setCredentials("");
    }

    private void attemptLogin() {
        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String User = mUserView.getText().toString();
        String Password = mPasswordView.getText().toString();

        String secretKey = "@%^&123_domotion_$%#!@";
        String SaltedPassword = Password + secretKey;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(SaltedPassword.getBytes("UTF-8"));
            byte byteData[] = md.digest();
            StringBuilder hexData = new StringBuilder();
            for (byte b: byteData) hexData.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            String HashedPass = hexData.toString();

            String UserPassword = User + ":" + HashedPass;
            String EncodedUserPassword = Base64.encodeToString(UserPassword.getBytes("UTF-8"), Base64.DEFAULT);
            MainActivity.setCredentials(EncodedUserPassword);
            MainActivity.setLoginDone(true);
        } catch (NoSuchAlgorithmException |UnsupportedEncodingException e){
            e.printStackTrace();
            MainActivity.setCredentials("");
        }
    }
}
