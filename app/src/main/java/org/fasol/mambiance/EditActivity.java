package org.fasol.mambiance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by fasol on 18/11/16.
 */

// TODO récupérer les données saisies
    // TODO sélection aléatoire des adjectifs
    // TODO lier l'application appareil photo au bouton
    // TODO enregistrer les données dans la BDD

public class EditActivity extends AppCompatActivity {

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

    // bouton pour écrire dans la BDD
    private Button save;

    // layout contenant la rose des ambiances
    private FrameLayout layout_rose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        layout_rose = (FrameLayout)findViewById(R.id.frame_layout_rose);

        layout_rose.addView(new RoseSurfaceView(this,(SeekBar)findViewById(R.id.cursor_olfactory)
                ,(SeekBar)findViewById(R.id.cursor_thermal),(SeekBar)findViewById(R.id.cursor_visual)
                ,(SeekBar)findViewById(R.id.cursor_acoustical)));


        site_name=(EditText)findViewById(R.id.edit_site_name);
        description=(EditText)findViewById(R.id.edit_description);

        caract1=(TextView)findViewById(R.id.caract1);
        caract2=(TextView)findViewById(R.id.caract2);
        caract3=(TextView)findViewById(R.id.caract3);

        cursor1=(SeekBar) findViewById(R.id.cursor1);
        cursor2=(SeekBar) findViewById(R.id.cursor2);
        cursor3=(SeekBar) findViewById(R.id.cursor3);

        cursor_acoustical=(SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal=(SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory=(SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual=(SeekBar) findViewById(R.id.cursor_visual);

        cursor_acoustical.setEnabled(true);
        cursor_thermal.setEnabled(true);
        cursor_olfactory.setEnabled(true);
        cursor_visual.setEnabled(true);

        save=(Button)findViewById(R.id.btn_save);


        save.setOnClickListener(saveListener);
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // ----------------            requête pour enregistrer dans la BDD
        }
    };

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
}
