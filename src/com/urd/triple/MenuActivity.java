package com.urd.triple;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.urd.triple.core.Card;
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
        private static final int MENU_SHARE = 2;

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
                new MenuItem(MenuItem.MENU_JOIN, getString(R.string.join)),
                new MenuItem(MenuItem.MENU_SHARE, getString(R.string.share))
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

                        case MenuItem.MENU_SHARE:
                            shareFile();
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

    private void shareFile() {
        try {
            ApplicationInfo ap = this.getPackageManager().getApplicationInfo(this.getPackageName(),
                    PackageManager.GET_SHARED_LIBRARY_FILES);
            share(this, "share", "分享文件", ap.sourceDir);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void share(Context context, String shareMsg, String activityTitle, String filePath)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, activityTitle);
        intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
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
        public void onGameStart(int role, Player lord) {
        }

        @Override
        public void onHeroList(List<Integer> heroes) {
        }

        @Override
        public void onPlayerHeroSelected(Player player, int hero) {
        }

        @Override
        public void onCardAction(Card card, int srcArea, int dstArea, Player src, Player dst) {
        }

        @Override
        public void onDeskClean() {
        }

        @Override
        public void onPlayerHPChanged(Player player) {
        }

        @Override
        public void onPlayerRole(Player player) {
        }

        @Override
        public void onNetworkError() {
            showToast("网络错误.");

            mProgressDialog.hide();
            mMenuDialog.show();
        }

    };
}
