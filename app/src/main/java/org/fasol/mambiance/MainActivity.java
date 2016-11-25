package org.fasol.mambiance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.fasol.mambiance.db.Lieu;
import org.fasol.mambiance.db.LocalDataSource;
import org.fasol.mambiance.db.Marqueur;

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
        Lieu l = datasource.createLieu("Parc à touristes","3 rue du trottoir 44000 NANTES",0,0);
        Marqueur m = datasource.createMarqueur(l.getLieu_id());

        btn_user=(ImageButton)findViewById(R.id.btn_user);
        btn_edit=(ImageButton)findViewById( R.id.btn_edit);
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
                Intent secondeActiv = new Intent(MainActivity.this, MapActivity.class);

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
}
