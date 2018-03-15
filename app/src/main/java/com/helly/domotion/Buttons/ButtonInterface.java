package com.helly.domotion.Buttons;

// Created by helly on 2/17/18.

public interface ButtonInterface {
    enum ButtonType {
        SWITCH,
        ANALOG,
        BLINDS,
        CONTROL,
        EDIT,
        DELETE,
        RECALC,
        ADD,
        VOID
    }

    void Set(String Value);
    String Get();
    ButtonType GetType();
}
