package org.fasol.mambiance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.fasol.mambiance.db.MySQLiteHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

public class HistoryActivity extends AppCompatActivity {

    ListView v_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        v_list=(ListView)findViewById(R.id.listview_history);

        datasource.open();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item_history, datasource.getHistoriqueCursor(),
                new String[]{MySQLiteHelper.COLUMN_LIEUNOM, MySQLiteHelper.COLUMN_ADRESSE, MySQLiteHelper.COLUMN_DATECREATION},
                new int[]{R.id.site_name,R.id.site_adress,R.id.date});

        // ------------------ TEST de remplissage, d'affichage et de réaction au clic de la liste -----------
        /*// remplissage
        String[][] historique_test = new String[][]{
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"},
                {"Parc à touristes", "3 rue du trottoir 44000 NANTES", "08/11/2016 - 16:05"},
                {"La chaussure géante", "1 chemin des sans-papiers 44000 NANTES", "15/08/2016 - 8:11"},
                {"Garage Mario&Luigi", "24 boulevard pavé 44000 NANTES", "16/01/2013 - 21:36"}};
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        for(int i = 0 ; i < historique_test.length ; i++) {
            element = new HashMap<String, String>();
            element.put("nom_site", historique_test[i][0]);
            element.put("adresse_site", historique_test[i][1]);
            element.put("date", historique_test[i][2]);
            liste.add(element);
        }
        // création
        ListAdapter adapter = new SimpleAdapter(this, liste, R.layout.list_item_history,
                new String[]{"nom_site","adresse_site","date"}, new int[]{R.id.site_name,R.id.site_adress,R.id.date});*/

        // affichage
        v_list.setAdapter(adapter);
        // réaction au clic
        v_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v_item, int pos, long id) {

                LinearLayout l_item = (LinearLayout)v_item;
                TextView nom = (TextView)l_item.getChildAt(0);
                TextView adresse = (TextView)l_item.getChildAt(1);
                TextView date = (TextView)l_item.getChildAt(2);
                Toast.makeText(HistoryActivity.this, nom.getText()+" "+adresse.getText()+" "+date.getText(), Toast.LENGTH_LONG).show();
            }
        });
        // --------------------- FIN DU TEST ---------------------
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
}
