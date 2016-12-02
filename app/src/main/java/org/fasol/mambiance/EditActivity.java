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

/**
 * Created by fasol on 18/11/16.
 */

// TODO récupérer les données saisies
    // TODO lier l'application appareil photo au bouton
    // TODO enregistrer les données dans la BDD

public class EditActivity extends AppCompatActivity {

    EditText site_name;
    EditText description;
    SeekBar cursor1;
    SeekBar cursor2;
    SeekBar cursor3;
    SeekBar cursor_acoustical;
    SeekBar cursor_thermal;
    SeekBar cursor_olfactory;
    SeekBar cursor_visual;

    Button save;


    FrameLayout layout_rose;
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

        cursor1=(SeekBar) findViewById(R.id.cursor1);
        cursor2=(SeekBar) findViewById(R.id.cursor2);
        cursor3=(SeekBar) findViewById(R.id.cursor3);

        cursor_acoustical=(SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal=(SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory=(SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual=(SeekBar) findViewById(R.id.cursor_visual);

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
