package de.hs_mannheim.planb.mobilegrowthmonitor;

/**
 * MainView of MobileGrowthMonitor
 * Shows all Profiles - add new profiles
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.ListAdapter;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.Listener;
import de.hs_mannheim.planb.mobilegrowthmonitor.imageprocessing.CameraView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AbstractAppLock;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLockView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.GalleryView;

public class MainView extends BaseActivity implements Listener {
    public static final String TAG = MainView.class.getSimpleName();

    private RecyclerView recyclerView;
    private DbHelper dbHelper;
    private ListAdapter listAdapter;
    private FloatingActionButton fab;

    private MenuItem onOffPinLock;
    private MenuItem changePin;
    private MenuItem camera;
    private MenuItem gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.rv_profileList);
        listAdapter = new ListAdapter(this, dbHelper.getAllProfiles());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        File folder = new File(getFilesDir(), "MobileGrowthMonitor_pictures");
        if (!(folder.exists())) {
            folder.mkdirs();
            //Toast.makeText(MainView.this, "Success! Folder created!", Toast.LENGTH_SHORT).show();
        }


        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Create a new Profile?", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), CreateProfileView.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        onOffPinLock = menu.getItem(0);
        changePin = menu.getItem(1);
        camera = menu.getItem(2);
        gallery = menu.getItem(3);
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
        // StartCamera TODO: move this to specific profile view
        if (id == R.id.start_cam) {
            Intent intent = new Intent(this, CameraView.class);
            startActivity(intent);
        }
        // OpenGallery TODO: move this to specific profile view
        if (id == R.id.open_gallery) {
            Intent intent = new Intent(this, GalleryView.class);
            startActivity(intent);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            updateMenu();
        }
    }

    private void updateMenu() {
        listAdapter = new ListAdapter(this, dbHelper.getAllProfiles());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (LockManager.getInstance().getAppLock().isPasscodeSet()) {
            onOffPinLock.setTitle(R.string.disable_passcode);
            changePin.setEnabled(true);
        } else {
            onOffPinLock.setTitle(R.string.enable_passcode);
            changePin.setEnabled(false);
        }
    }


    @Override
    public void selectProfile(int index) {
        Intent intent = new Intent(this, ProfileView.class);
        intent.putExtra("profile_Id", index);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        updateMenu();

        super.onBackPressed();
    }

}
