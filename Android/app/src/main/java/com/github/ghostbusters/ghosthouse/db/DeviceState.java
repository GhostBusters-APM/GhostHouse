package com.github.ghostbusters.ghosthouse.db;

public enum DeviceState {
    OFF(0), ON(1);

    private final int stateValue;

    DeviceState(int stateValue) {
        this.stateValue = stateValue;
    }

    public int getStateValue() { return  stateValue; }

    public static DeviceState getByStateValue(int stateValue) {
        for (DeviceState deviceState: DeviceState.values()) {
            if (stateValue == deviceState.stateValue) {
                return deviceState;
            }
        }

        throw new IllegalArgumentException("no state with that value: " + stateValue);
    }
}
