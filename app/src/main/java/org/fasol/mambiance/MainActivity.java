package org.fasol.mambiance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.fasol.mambiance.db.Lieu;
import org.fasol.mambiance.db.LocalDataSource;
import org.fasol.mambiance.db.Marqueur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_user=null, btn_history=null, btn_map=null, btn_info=null, btn_edit=null;

    public static LocalDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new LocalDataSource(this);
        datasource.open();

        // test ajout dans la BDD
        Lieu l = datasource.createLieu("Parc à touristes","3 rue du trottoir 44000 NANTES",0.0f,0.0f);
        Marqueur m = datasource.createMarqueur(l.getLieu_id());

        btn_user=(ImageButton)findViewById(R.id.btn_user);
        btn_edit=(ImageButton)findViewById(R.id.btn_edit);
        btn_history=(ImageButton)findViewById(R.id.btn_history);
        btn_map=(ImageButton)findViewById(R.id.btn_map);
        btn_info=(ImageButton)findViewById(R.id.btn_info);

        // Lien avec l'activité historique
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, HistoryActivity.class);

                startActivity(secondeActiv);
            }
        });

        // Lien avec l'activité saisie des marqueurs
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, EditActivity.class);

                startActivity(secondeActiv);
            }
        });

        // Lien avec l'activité carte
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, MapMarkerActivity.class);

                startActivity(secondeActiv);
            }
        });

    }
    /**
     * Method to inflate the xml menu file
     * @param menu the menu
     * @return true if everything went good
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        //On sérialise le fichier menu.xml pour l'afficher dans la barre de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method to handle the clicks on the items of the toolbar
     *
     * @param item the item
     * @return true if everything went good
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent ;

        switch (item.getItemId()) {
            case R.id.home:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        datasource.close();
    }

    // START PERMISSION CHECK
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "osmdroid permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            message += "\nStorage access to store map tiles.";
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            requestPermissions(params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } // else: We already have permissions, so handle as normal
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and WRITE_EXTERNAL_STORAGE
                Boolean location = perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                Boolean storage = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (location && storage) {
                    // All Permissions Granted
                    Toast.makeText(MainActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
                } else if (location) {
                    Toast.makeText(this, "Storage permission is required to store map tiles to reduce data usage and for offline usage.", Toast.LENGTH_LONG).show();
                } else if (storage) {
                    Toast.makeText(this, "Location permission is required to show the user's location on map.", Toast.LENGTH_LONG).show();
                } else { // !location && !storage case
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Storage permission is required to store map tiles to reduce data usage and for offline usage." +
                            "\nLocation permission is required to show the user's location on map.", Toast.LENGTH_SHORT).show();
                }

            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // END PERMISSION CHECK
}
