package com.urd.triple.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;

public class LocalGameSocket extends GameSocket {
    private static final Logger LOG = LoggerFactory.getLogger(LocalGameSocket.class);

    private PipedInputStream mInputStream;
    private PipedOutputStream mOutputStream;
    private boolean mConnecting;

    public LocalGameSocket(GameSocketListener listener, LocalGameSocket other) {
        super(listener);

        mConnecting = true;
        if (other != null) {
            try {
                other.mOutputStream = new PipedOutputStream();
                other.mInputStream = new PipedInputStream();
                mInputStream = new PipedInputStream(other.mOutputStream);
                mOutputStream = new PipedOutputStream(other.mInputStream);
                mConnecting = false;
            } catch (IOException e) {
                LOG.warn("connect pipe failed.", e);
            }

            notifyConnected();
            start();

            other.notifyConnected();
            other.start();
        }
    }

    @Override
    public void close() {
        super.close();

        if (mConnecting) {
            mConnecting = false;

            notifyConnectFailed();
        }

        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                LOG.warn("close failed.", e);
            }
            mInputStream = null;
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                LOG.warn("close failed.", e);
            }
            mOutputStream = null;
        }
    }

    @Override
    public String getID() {
        return BluetoothAdapter.getDefaultAdapter().getAddress();
    }

    @Override
    public String getName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mInputStream == null) {
            throw new IOException();
        }

        return mInputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mOutputStream == null) {
            throw new IOException();
        }

        return mOutputStream;
    }
}
