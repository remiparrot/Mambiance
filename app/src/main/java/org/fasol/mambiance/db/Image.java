package org.fasol.mambiance.db;

import java.util.Vector;


import android.content.ContentValues;

public class Image extends DataObject {


    //Atributes
    /**
     * int id of the object image
     */
    private int image_id;
    /**
     * int id of the marker linked
     */
    private int marqueur_id;
    /**
     * String location of the image
     */
    private String image_emp;


    //Getters
    /**
     * getter for the Image_id
     * @return int image_id
     */
    public int getImage_id() {
        return image_id;
    }
    /**
     * getter for the marqueur_id
     * @return int marqueur_id
     */
    public int getMarqueur_id() {
        return marqueur_id;
    }
    /**
     * getter for the image emplacement
     * @return String image_emp
     */
    public String getImage_emp() {
        return image_emp;
    }

    //Setters
    /**
     * setter for the Image_id
     * @param  image_id
     */
    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
    /**
     * setter for the marqueur_id
     * @param  marqueur_id
     */
    public void setMarqueur_id(int marqueur_id) {
        this.marqueur_id = marqueur_id;
    }
    /**
     * setter for the Image emplacement
     * @param  image_emp
     */
    public void setImage_emp(String image_emp) {
        this.image_emp = image_emp;
    }

    //Abstract methods

    @Override
    public String toString() {
        return "Image [image_id=" + image_id + ", image_emp=" + image_emp + ", marqueur_id=" + marqueur_id +"]";
    }


    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_IMAGEEMP, this.image_emp);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);

        if(this.registredInLocal){
            String str = "image_id "+"="+this.image_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_IMAGE, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_IMAGE, null, values);
            this.setImage_id((int)row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_IMAGE+"."+MySQLiteHelper.COLUMN_IMAGEID+" FROM "
                    + MySQLiteHelper.TABLE_IMAGE
                    +" ORDER BY "+MySQLiteHelper.TABLE_IMAGE+"."+MySQLiteHelper.COLUMN_IMAGEID
                    +" DESC LIMIT 1 ;"
            ;

}