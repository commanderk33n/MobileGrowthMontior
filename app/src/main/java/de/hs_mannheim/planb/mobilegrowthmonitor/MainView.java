package de.hs_mannheim.planb.mobilegrowthmonitor;

/**
 * MainView of MobileGrowthMonitor
 * Shows all Profiles - add new profiles
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import de.hs_mannheim.planb.mobilegrowthmonitor.imagemanagement.CameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AbstractAppLock;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLockView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;

public class MainView extends BaseActivity {
    public static final String TAG = "MainView";

    private MenuItem onOffPinLock;
    private MenuItem changePin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn_test = (Button) findViewById(R.id.btn_test);

        if (btn_test != null) {
            btn_test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startCam();
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with createProfile", Snackbar.LENGTH_LONG)
                            .setAction("createProfile", null).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        onOffPinLock = menu.getItem(0);
        changePin = menu.getItem(1);
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.on_off_pin) {
            int type = LockManager.getInstance().getAppLock().isPasscodeSet() ? AbstractAppLock.DISABLE_PASSLOCK
                    : AbstractAppLock.ENABLE_PASSLOCK;
            Intent intent = new Intent(this, AppLockView.class);
            intent.putExtra(AbstractAppLock.TYPE, type);
            startActivityForResult(intent, type);
        }
        if (id == R.id.change_pin) {
            Intent intent = new Intent(this, AppLockView.class);
            intent.putExtra(AbstractAppLock.TYPE, AbstractAppLock.CHANGE_PASSWORD);
            intent.putExtra(AbstractAppLock.MESSAGE,
                    getString(R.string.enter_old_passcode));
            startActivityForResult(intent, AbstractAppLock.CHANGE_PASSWORD);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AbstractAppLock.DISABLE_PASSLOCK:
                break;
            case AbstractAppLock.ENABLE_PASSLOCK:
            case AbstractAppLock.CHANGE_PASSWORD:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, getString(R.string.setup_passcode),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        updateMenu();
    }

    private void updateMenu() {
        if (LockManager.getInstance().getAppLock().isPasscodeSet()) {

            onOffPinLock.setTitle(R.string.disable_passcode);
            changePin.setEnabled(true);
        } else {
            onOffPinLock.setTitle(R.string.enable_passcode);
            changePin.setEnabled(false);
        }
    }

    public void startCam() {

        Intent intent = new Intent(this, CameraView.class);

        startActivity(intent);
    }


}