package org.fasol.mambiance.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.fasol.mambiance.MainActivity;

public class LocalDataSource {
    private static final String TAG = "localDataSource";
    //Database fields
    /**
     * database attributes which contains the database (need to open and close it for each actions)
     */
    private SQLiteDatabase database;

    /**
     * database attributes which allow to create the database
     */
    private MySQLiteHelper dbHelper;

    //TODO Adddescription for javadoc
    private String[] allColumnsMarqueur = {MySQLiteHelper.COLUMN_MARQUEURID, MySQLiteHelper.COLUMN_DATECREATION, MySQLiteHelper.COLUMN_LIEUID};
    private String[] allColumnsCurseur = {MySQLiteHelper.COLUMN_CURSEURID, MySQLiteHelper.COLUMN_CURSEURLIBELLE, MySQLiteHelper.COLUMN_CURSEURVALEUR, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsImage = {MySQLiteHelper.COLUMN_IMAGEID, MySQLiteHelper.COLUMN_IMAGEEMP, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsMot = {MySQLiteHelper.COLUMN_MOTID, MySQLiteHelper.COLUMN_MOTLIBELLE, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsLieu = {MySQLiteHelper.COLUMN_LIEUID, MySQLiteHelper.COLUMN_LIEUNOM, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE};
    private String[] allColumnsRoseAmbiance = {MySQLiteHelper.COLUMN_ROSEID, MySQLiteHelper.COLUMN_ACOUSTICAL, MySQLiteHelper.COLUMN_OLFACTORY, MySQLiteHelper.COLUMN_VISUAL, MySQLiteHelper.COLUMN_THERMAL, MySQLiteHelper.COLUMN_MARQUEURID};

    //getters

    /**
     * getter for the MySQLiteHelper
     *
     * @return dbHelper
     */
    public MySQLiteHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * getter for the SQLiteDatabase
     *
     * @return database
     */
    public SQLiteDatabase getDatabase() {
        return database;
    }

    //constructor

    /**
     * constructor only consists in creating dbHelper
     *
     * @param context
     */
    public LocalDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Open database
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * close the database, need to be called to avoid any issue on the database treatment
     */
    public void close() {
        dbHelper.close();
    }

    // ----------------------------------- MARQUEUR METHODES ------------------------------------------
    /**
     * creating a new Marqueur in the database
     * @param lieu_id id of the place linked to the Marqueur
     * @return Marqueur is the created Marqueur
     */
    public Marqueur createMarqueur(long lieu_id) {
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateMnt = new Date(System.currentTimeMillis());
        values.put(MySQLiteHelper.COLUMN_DATECREATION, dateFormat.format(dateMnt));
        values.put(MySQLiteHelper.COLUMN_LIEUID, lieu_id);

        long insertId = database.insert(MySQLiteHelper.TABLE_MARQUEUR, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MARQUEUR,
                allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Marqueur newMarqueur = cursorToMarqueur(cursor);
        cursor.close();
        return newMarqueur;
    }

    /**
     * knowing a Marqueur_id, we want to get the marqueur itself
     *
     * @param id is the id of the marqueur we are looking for
     * @return m1 is the marqueur we were looking for
     */
    public Marqueur getMarqueurWithId(Long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Marqueur m1 = cursorToMarqueur(c);
        c.close();
        return m1;
    }


    /**
     * knowing an id we test if this marqueur exists
     *
     * @param id is the id of the marqueur we ask
     * @return boolean says if the marqueur with this id exists or not
     */
    public Boolean existMarqueurWithId(Long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    /**
     * deleting Marqueur in the database
     * @param m1 marqueur linked to the marqueur in the database
     */
    public void deleteMarqueur(Marqueur m1) {
        long id = m1.getMarqueur_id();
        System.out.println("Marqueur deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_MARQUEURID + " = " + id, null);
    }

    /**
     * deleting all Marqueur in the database
     */
    public void clearMarqueur() {
        System.out.println("Marqueur cleared");
        database.execSQL("DROP TABLE IF EXISTS Marqueur");
        database.execSQL(MySQLiteHelper.getDatabaseCreate4());
    }

    /**
     * return the Marqueur linkede to the cursor
     * @param cursor
     * @return Marqueur linked to the cursor
     */
    private Marqueur cursorToMarqueur(Cursor cursor) {
        Marqueur marqueur = new Marqueur();

        marqueur.setMarqueur_id(cursor.getLong(0));

        Date dateCreation = new Date(cursor.getLong(1) * 1000);
        marqueur.setDate_creation(dateCreation);

        marqueur.setLieu_id(cursor.getLong(2));
        return marqueur;
    }

    //----------------------------------- LIEU METHODES ------------------------------------------

    /**
     * creation a new Lieu in the database
     * @param nom name of the place
     * @param latitude latitude of the place
     * @param longitude longitude of the place
     * @return Lieu is the created Lieu
     */
    public Lieu createLieu (String nom, double latitude, double longitude){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIEUNOM, nom);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        long insertId = database.insert(MySQLiteHelper.TABLE_LIEU, null, values);
        Cursor cursor = database.query(
                        MySQLiteHelper.TABLE_LIEU,
                        allColumnsLieu,
                        MySQLiteHelper.COLUMN_LIEUID+" = "+insertId,
                        null, null, null, null);
        cursor.moveToFirst();
        Lieu newLieu = cursorToLieu(cursor);//method at the end of the class
        cursor.close();
        return newLieu;
    }

    /**
     * update a Lieu
     * @return Lieu updated
     */
    public Lieu updateLieu(Lieu lieu, String nom, double latitude, double longitude, long adresse_id){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIEUNOM, nom);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);

        database.update(MySQLiteHelper.TABLE_LIEU, values, MySQLiteHelper.COLUMN_LIEUID + " = " +lieu.getLieu_id(), null);
        return getLieuWithId(lieu.getLieu_id());
    }

    /**
     * knowing a Mot_id, we want to get the image itself
     * @param id is the id of the image we are looking for
     * @return c1 is the image we were looking for
     */
    public Lieu getLieuWithId(long id){
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEUS, allColumnsLieu, MySQLiteHelper.COLUMN_LIEUSID + " = \"" + id +"\"", null, null, null, null);
        c.moveToFirst();
        Lieu p1 = cursorToLieu(c);
        c.close();
        return p1;
    }

    /**
     * knowing a Mot_id, we want to get the image itself
     * @param id is the id of the image we are looking for
     * @return c1 is the image we were looking for
     */

    public Lieu getLieuWithLatLng(double lat, double lng){
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEUS, allColumnsLieu, MySQLiteHelper.COLUMN_LIEUSLATITUDE + " = \"" + lat +"\"" + " AND " + MySQLiteHelper.COLUMN_LIEUSLONGITUDE + " = \"" + lng +"\"", null, null, null, null);
        c.moveToFirst();
        Lieu p1 = cursorToLieu(c);
        c.close();
        return p1;
    }

    /**
     * knowing an id we test if this image exists
     * @param id is the id of the image we ask
     * @return boolean says if the image with this id exists or not
     */
    public Boolean existLieuWithLatLng(double lat, double lng){
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEUS, allColumnsLieu, MySQLiteHelper.COLUMN_LIEUSLATITUDE + " = \"" + lat +"\"" + " AND " + MySQLiteHelper.COLUMN_LIEUSLONGITUDE + " = \"" + lng +"\"", null, null, null, null);
        if(c.getCount()>0){
            c.close();
            return true;
        }
        else {
            c.close();
            return false;
        }
    }


    /**
     * deleting an Image
     * @param i1 is the image we want to delete
     */
    public void deleteLieu(Lieu p1){
        long id = p1.getLieu_id();
        System.out.println("Image deleted with id: "+ id);
        database.delete(MySQLiteHelper.TABLE_IMAGE, MySQLiteHelper.COLUMN_IMAGEID+" = "+ id, null);
    }

    /**
     * deleting all Lieu
     */
    public void clearLieu(){
        System.out.println("Lieu cleared");
        database.execSQL("DROP TABLE IF EXISTS Lieu");
        database.execSQL(MySQLiteHelper.getDatabaseCreate3());
    }

    /**
     * convert a cursor to an image
     * @param cursor
     * @return image
     */
    private Lieu cursorToLieu(Cursor cursor) {
        Lieu p1 = new Lieu();
        p1.setLieu_id(cursor.getLong(0));
        p1.setLieu_nom(cursor.getString(1));
        p1.setLieu_latitude(cursor.getDouble(2));
        p1.setLieu_longitude(cursor.getDouble(3));
        p1.setAdresse_id(cursor.getLong(4));
        return p1;
    }
}