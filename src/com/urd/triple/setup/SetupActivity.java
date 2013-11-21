package com.urd.triple.setup;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.urd.triple.BaseActivity;
import com.urd.triple.MenuActivity;
import com.urd.triple.R;
import com.urd.triple.core.GameCore;

public class SetupActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    private GameCore mGameCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameCore = GameCore.getInstance();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            if (adapter.isEnabled()) {
                init();
            } else {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            }
        } else {
            showToast("设备不支持蓝牙");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                init();
            } else {
                exit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exit();
    }

    private void init() {
        String nickName = getDefaultSharedPreferences().getString("nickName", "").trim();
        if (!(TextUtils.isEmpty(nickName))) {
            initWithNickName(nickName);
        } else {
            enterNickName();
        }
    }

    private void initWithNickName(String name) {
        mGameCore.setup(name);

        finish();
        startActivity(MenuActivity.class);
    }

    private void enterNickName() {
        final EditText input = (EditText) getLayoutInflater().inflate(R.layout.input, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入呢称")
                .setView(input)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = input.getText().toString().trim();
                        if (!(TextUtils.isEmpty(name))) {
                            SharedPreferences prefs = getDefaultSharedPreferences();
                            prefs.edit().putString("nickName", name).commit();

                            initWithNickName(name);
                        } else {
                            enterNickName();
                        }
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        exit();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
