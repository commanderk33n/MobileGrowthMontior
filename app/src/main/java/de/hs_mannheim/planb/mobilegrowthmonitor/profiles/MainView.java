package de.hs_mannheim.planb.mobilegrowthmonitor.profiles;

/**
 * MainView.Activity
 * Shows all Profiles - add new profiles and options menu
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import de.hs_mannheim.planb.mobilegrowthmonitor.misc.PermissionDialogFragment;
import de.hs_mannheim.planb.mobilegrowthmonitor.R;
import de.hs_mannheim.planb.mobilegrowthmonitor.database.DbHelper;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.ListAdapter;
import de.hs_mannheim.planb.mobilegrowthmonitor.datavisual.Listener;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AbstractAppLock;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.AppLockView;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.BaseActivity;
import de.hs_mannheim.planb.mobilegrowthmonitor.pinlock.LockManager;

public class MainView extends BaseActivity implements Listener, PermissionDialogFragment.NoticeDialogListener {
    public static final String TAG = MainView.class.getSimpleName();

    private RecyclerView recyclerView;
    private DbHelper dbHelper;
    private ListAdapter listAdapter;
    private FloatingActionButton fab;

    private MenuItem onOffPinLock;
    private MenuItem changePin;

    private static final int PERMISSIONS_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissions();
        }

        dbHelper = DbHelper.getInstance(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.rv_profileList);
        listAdapter = new ListAdapter(this, dbHelper.getAllProfiles());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, R.string.snackbar_create_profile, Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), CreateProfileView.class);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (id == R.id.agbs) {

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.agb_view, null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.gtc);
            alertDialog.setView(view);
            AlertDialog alert = alertDialog.create();
            alert.show();

        }
        if (id == R.id.open_source_license) {

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.open_source_licenses_view, null);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.open_source_licenses);
            alertDialog.setView(view);
            AlertDialog alert = alertDialog.create();
            alert.show();
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
        super.onBackPressed();
        updateMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        final int permission = dialog.getArguments().getInt("permission");
        switch (permission) {
            case PERMISSIONS_REQUEST_CAMERA:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);
                break;
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Activity activity = getParent();
        if (activity != null) {
            activity.finish();
        }
    }

    private void getPermissions() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                DialogFragment newFragment = new PermissionDialogFragment();
                Bundle args = new Bundle();
                args.putInt("permission", PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "permissionDialog");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    DialogFragment newFragment2 = new PermissionDialogFragment();
                    Bundle args2 = new Bundle();
                    args2.putInt("permission", PERMISSIONS_REQUEST_CAMERA);
                    newFragment2.setArguments(args);
                    newFragment2.show(getFragmentManager(), "permissionDialog");

                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                DialogFragment newFragment = new PermissionDialogFragment();
                Bundle args = new Bundle();
                args.putInt("permission", PERMISSIONS_REQUEST_CAMERA);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "permissionDialog");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File folder = new File(Environment.getExternalStorageDirectory().getPath(), "growpics");
                    if (!(folder.exists())) {
                        folder.mkdirs();
                        Log.i(TAG, "Success! Folder created!");
                    }
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        DialogFragment newFragment = new PermissionDialogFragment();
                        Bundle args = new Bundle();
                        args.putInt("permission", PERMISSIONS_REQUEST_CAMERA);
                        newFragment.setArguments(args);
                        newFragment.show(getFragmentManager(), "permissionDialog");
                    }
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        DialogFragment newFragment = new PermissionDialogFragment();
                        Bundle args = new Bundle();
                        args.putInt("permission", PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        newFragment.setArguments(args);
                        newFragment.show(getFragmentManager(), "permissionDialog");
                    }
                    break;
                }
            }
        }
    }
}
