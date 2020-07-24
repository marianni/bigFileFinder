package com.marianni.bigFileFinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mariannarachelova
 */
public class MainActivity extends AppCompatActivity {

    private static final int FOLDERPICKER_PERMISSIONS = 1;
    private static final int RUNTIME_PERMISSION_CODE = 7;
    private ArrayAdapter<String> adapter ;
    private List<String> fileList = new ArrayList<String>();
    private ListView listView;
    private EditText textInputEditText;
    private static Integer nBiggestFiles = 10;
    private TextInputEditText choosedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.mainToolBar);
        setSupportActionBar(toolbar);
        textInputEditText = findViewById(R.id.editTextNumberSigned);

        Button folderpickerBtn = findViewById(R.id.button_folderpicker);
        folderpickerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                System.out.println("Permissions " + PERMISSIONS);

                if(hasPermissions(MainActivity.this, PERMISSIONS)){
                    if(view != null){
                        view.setSelected(true);
                    }
                    ShowDirectoryPicker();
                } else{
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, FOLDERPICKER_PERMISSIONS);
                }
            }
        });

            listView = (ListView) findViewById(R.id.listview);
            Button startButton = findViewById(R.id.button);
            Button resetButton = findViewById(R.id.reset);

            // Requesting run time permission for Read External Storage.
            AndroidRuntimePermission();

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fileList != null && !fileList.isEmpty()) {
                        adapter = new ArrayAdapter<String>
                                (MainActivity.this, android.R.layout.simple_list_item_1,fileList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);

                        setListViewHeight(listView);
                    } else {
                        Toast.makeText(MainActivity.this, "Dir does not contains files!" , Toast.LENGTH_SHORT).show();
                    }
                }
            });

            resetButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    listView.setAdapter(null);
                    listView.clearFocus();
                    fileList.clear();
                    textInputEditText.setText("");
                    choosedFolder.setText("");
                }
            });

    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, Toolbar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * Method that displays the directory chooser of the StorageChooser.
     */
    public void ShowDirectoryPicker(){
        // 1. Initialize dialog
        //listView.setAdapter(null);
        final StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Retrieve the selected path by the user and show in a toast !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                choosedFolder = findViewById(R.id.choosedFolder);
                Toast.makeText(MainActivity.this, "The selected path is : " + path, Toast.LENGTH_SHORT).show();
                choosedFolder.setText(path);
                fileList.clear();
                if(!String.valueOf(textInputEditText.getText()).isEmpty() && !String.valueOf(textInputEditText.getText()).equals("0")) {
                    nBiggestFiles = Integer.valueOf(String.valueOf(textInputEditText.getText()));
                } else {
                    nBiggestFiles = 1;
                }
                BigFileFinder bigFileFinder = new BigFileFinder();
                fileList.addAll(bigFileFinder.getNBiggestFiles(new File(path), nBiggestFiles));
            }
        });

        // 3. Display File Picker !
        chooser.show();
    }

    /**
     * Helper method that verifies whether the permissions of a given array are granted or not.
     *
     * @param context
     * @param permissions
     * @return {Boolean}
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback that handles the status of the permissions request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FOLDERPICKER_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                            MainActivity.this,
                            "Permission granted! Please click on pick a file once again.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Permission denied to read your External storage :(",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return;
            }
        }
    }



    /**
     * Creating Runtime permission function.
     */
    public void AndroidRuntimePermission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MainActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE
                            );
                        }
                    });
                    alert_builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = alert_builder.create();
                    dialog.show();
                }
                else {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}