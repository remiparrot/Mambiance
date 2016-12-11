package org.fasol.mambiance;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fasol.mambiance.db.Curseur;
import org.fasol.mambiance.db.Image;
import org.fasol.mambiance.db.Lieu;
import org.fasol.mambiance.db.Marqueur;
import org.fasol.mambiance.db.Mot;
import org.fasol.mambiance.db.RoseAmbiance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

// TODO récupérer les données saisies
// TODO lier l'application appareil photo au bouton
// TODO enregistrer les données dans la BDD

public class EditActivity extends AppCompatActivity implements LocationListener {

    // liste des caractéristiques possibles
    public final static String[] l_caract = {"Cozy", "Palpitant", "Formel", "Accueillant", "Sécurisant", "Inspirant", "Intime", "Animé",
            "Luxueux", "Chill", "Personnel", "Romantique", "Ennuyeux", "Chaleureux", "Business", "Reposant"};

    // champs à remplir
    private EditText site_name;
    private EditText description;
    private TextView caract1;
    private TextView caract2;
    private TextView caract3;
    private SeekBar cursor1;
    private SeekBar cursor2;
    private SeekBar cursor3;
    private SeekBar cursor_acoustical;
    private SeekBar cursor_thermal;
    private SeekBar cursor_olfactory;
    private SeekBar cursor_visual;
    private Button btn_photo;
    private String photo_emp = null;

    // bouton pour écrire dans la BDD
    private Button save;

    // layout contenant la rose des ambiances
    private FrameLayout layout_rose;

    // locationManager pour récupérer latitude et longitude
    private LocationManager locationManager;

    // current latitude, longitude and adress
    private float lat,lng;
    private String adresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        // ajout de la rose des ambiances
        layout_rose = (FrameLayout) findViewById(R.id.frame_layout_rose);

        layout_rose.addView(new RoseSurfaceView(this, (SeekBar) findViewById(R.id.cursor_olfactory)
                , (SeekBar) findViewById(R.id.cursor_thermal), (SeekBar) findViewById(R.id.cursor_visual)
                , (SeekBar) findViewById(R.id.cursor_acoustical)));

        // liens vers les éléments de l'interface
        site_name = (EditText) findViewById(R.id.edit_site_name);
        description = (EditText) findViewById(R.id.edit_description);

        caract1 = (TextView) findViewById(R.id.caract1);
        caract2 = (TextView) findViewById(R.id.caract2);
        caract3 = (TextView) findViewById(R.id.caract3);

        cursor1 = (SeekBar) findViewById(R.id.cursor1);
        cursor2 = (SeekBar) findViewById(R.id.cursor2);
        cursor3 = (SeekBar) findViewById(R.id.cursor3);

        cursor_acoustical = (SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal = (SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory = (SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual = (SeekBar) findViewById(R.id.cursor_visual);

        cursor_acoustical.setEnabled(true);
        cursor_thermal.setEnabled(true);
        cursor_olfactory.setEnabled(true);
        cursor_visual.setEnabled(true);

        btn_photo = (Button) findViewById(R.id.btn_photo);
        save = (Button) findViewById(R.id.btn_save);

        // sélection des adjectifs aléatoirement
        ArrayList<Integer> caract_pos = new ArrayList<Integer>();
        for (int i = 0; i < l_caract.length; i++) caract_pos.add(i);

        Random rd = new Random();
        int rand = rd.nextInt(caract_pos.size());
        int caract_sel = caract_pos.get(rand);
        caract1.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);
        rand = rd.nextInt(caract_pos.size());
        caract_sel = caract_pos.get(rand);
        caract2.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);
        rand = rd.nextInt(caract_pos.size());
        caract_sel = caract_pos.get(rand);
        caract3.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);

        // ajout d'un clicklistener sur les boutons
        btn_photo.setOnClickListener(photoListener);
        save.setOnClickListener(saveListener);

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null) location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
        photo_emp ="";
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            if(isFormularyCompleted()) {

                // récupère les adresses possibles de localisation
                List<Address> l_address = null;
                Geocoder geocoder = new Geocoder(EditActivity.this);
                try {
                    l_address = geocoder.getFromLocation(EditActivity.this.lat,EditActivity.this.lng,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // affiche une fenêtre demandant de valider l'adresse calculer
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(EditActivity.this);
                builderSingle.setTitle("Adresse calculée");
                builderSingle.setMessage(l_address.get(0).getAddressLine(0)+" " +
                            l_address.get(0).getPostalCode() +" "+l_address.get(0).getLocality());
                // bouton recalculer
                builderSingle.setNegativeButton(
                        "recalculer",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                // bouton valider l'adresse
                final List<Address> finalL_address = l_address;
                builderSingle.setPositiveButton(
                        "valider",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditActivity.this.adresse= finalL_address.get(0).getAddressLine(0)+" " +
                                        finalL_address.get(0).getPostalCode() +" "+ finalL_address.get(0).getLocality();

                                datasource.open();

                                Lieu lieu = datasource.createLieu(site_name.getText().toString(), adresse, lat,lng);
                                Marqueur marqueur = datasource.createMarqueur(lieu.getLieu_id());
                                datasource.createRoseAmbiance(cursor_olfactory.getProgress()/4.f-1.f, cursor_visual.getProgress()/4.f-1.f,
                                        cursor_thermal.getProgress()/4.f-1.f, cursor_acoustical.getProgress()/4.f-1.f, marqueur.getMarqueur_id());
                                datasource.createImage(marqueur.getMarqueur_id(), photo_emp);
                                String [] mots = description.getText().toString().split("[\\p{Punct}\\s]+");
                                for(int i=0;i<mots.length;i++) {
                                    datasource.createMot(mots[i], marqueur.getMarqueur_id());
                                }
                                datasource.createCurseur(caract1.getText().toString(), cursor1.getProgress(), marqueur.getMarqueur_id());
                                datasource.createCurseur(caract1.getText().toString(), cursor1.getProgress(), marqueur.getMarqueur_id());
                                datasource.createCurseur(caract1.getText().toString(), cursor1.getProgress(), marqueur.getMarqueur_id());

                                datasource.close();

                                dialog.dismiss();
                                Toast.makeText(view.getContext(),"Enregistrement effectué !",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builderSingle.show();

            }
            else{
                Toast.makeText(view.getContext(),"Le formulaire n'est pas complet !",Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO lien avec l'application appareil photo
            // image_emp=...;
            Toast.makeText(view.getContext(),"Le formulaire n'est pas complet !",Toast.LENGTH_LONG).show();
        }
    };


    private boolean isFormularyCompleted(){
        boolean flag=(!site_name.getText().toString().matches(""))&&(!description.getText().toString().matches(""))&&(photo_emp!=null);
        return flag;
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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
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

    @Override
    public void onLocationChanged(Location location) {
        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
