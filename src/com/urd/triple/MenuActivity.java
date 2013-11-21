package com.urd.triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Player;
import com.urd.triple.setup.DeviceListAdapter;

public class MenuActivity extends BaseActivity {
    private static final UUID GAME_UUID = UUID.fromString("8B5A68B7-D57F-4C0A-8CF5-BB04C5C31D6A");

    private GameCore mGameCore;
    private AlertDialog mMenuDialog;
    private ProgressDialog mProgressDialog;

    private static class MenuItem {
        private static final int MENU_CREATE = 0;
        private static final int MENU_JOIN = 1;

        public int id;
        public String title;

        public MenuItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameCore = GameCore.getInstance();

        final MenuItem[] items = new MenuItem[] {
                new MenuItem(MenuItem.MENU_CREATE, getString(R.string.create)),
                new MenuItem(MenuItem.MENU_JOIN, getString(R.string.join))
        };
        mMenuDialog = (new AlertDialog.Builder(this))
                .setAdapter(new ArrayAdapter<MenuItem>(this, R.layout.menu_item, items), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which].id) {
                        case MenuItem.MENU_CREATE:
                            GameCore.getInstance().startGameServer(GAME_UUID);
                            mProgressDialog.setMessage(getString(R.string.creating));
                            mProgressDialog.show();
                            break;

                        case MenuItem.MENU_JOIN:
                            selectDevice();
                            break;

                        default:
                            break;
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        exit();
                    }
                })
                .create();
        mMenuDialog.setCanceledOnTouchOutside(false);
        mMenuDialog.show();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mGameCore.close();

                mMenuDialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGameCore.registerListener(mGameListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mGameCore.unregisterListener(mGameListener);
    }

    private void selectDevice() {
        final DeviceListAdapter adapter = new DeviceListAdapter();
        AlertDialog dialog = (new AlertDialog.Builder(this))
                .setAdapter(adapter, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice device = adapter.getItem(which);
                        mGameCore.connect(GAME_UUID, device);
                        mProgressDialog.setMessage(getString(R.string.connecting));
                        mProgressDialog.show();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mMenuDialog.show();
                    }
                })
                .create();
        dialog.setTitle(R.string.host);
        dialog.show();
    }

    private final GameListener mGameListener = new GameListener() {

        @Override
        public void onPlayerLogout(Player player) {
        }

        @Override
        public void onPlayerLogin(Player player) {
        }

        @Override
        public void onLoginSuccess(Collection<Player> players) {
            List<String> names = new ArrayList<String>();
            for (Player player : players) {
                names.add(player.name);
            }

            mProgressDialog.hide();

            startActivity(PlayerListActivity.class);
        }

        @Override
        public void onLoginFailed(int errorCode) {
            showToast("登陆失败.");

            mProgressDialog.hide();
            mMenuDialog.show();
        }

        @Override
        public void onNetworkError() {
            showToast("网络错误.");

            mProgressDialog.hide();
            mMenuDialog.show();
        }
    };
}
