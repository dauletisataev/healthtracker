package com.example.admin.healthtrack.miBandHelper.listeners;

/**
 * Listener for data notifications
 *
 * @author Dmytro Khmelenko
 */
public interface NotifyListener {

    /**
     * Called when new data arrived
     *
     * @param data Binary data
     */
    void onNotify(byte[] data);
}
