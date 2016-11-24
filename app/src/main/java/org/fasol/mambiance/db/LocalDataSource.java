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

import fr.turfu.urbapp2.MainActivity;

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

	// MARQUEUR METHODES
    public Marqueur createMarqueur() {
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateMnt = new Date(System.currentTimeMillis());
        values.put(MySQLiteHelper.COLUMN_DATECREATION, dateFormat.format(dateMnt));

    }


    /**
     * creating a new marker in the database
     *
     * @param str represents the name of the new project
     * @return project is the created project
     */
    public Project createProject(String str) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
        long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null, values);
        //TODO check the utily of autoincrement
        Cursor cursor =
                database.query(
                        MySQLiteHelper.TABLE_PROJECT,
                        allColumnsProject,
                        MySQLiteHelper.COLUMN_PROJECTID + " = " + insertId,
                        null, null, null, null);
        cursor.moveToFirst();
        Project newProject = cursorToProject(cursor);//method at the end of the class
        cursor.close();
        return newProject;
    }

    /**
     * overload of previous method
     * creating a new project in the database
     *
     * @param id  is the project_id
     * @param str is the project name
     * @return project is the created project
     */
    public Project createProject(long id, String str) {
        Boolean exist = existProjectWithId(id);

        if (exist == true) {
            Project existProject = getProjectWithId(id);
            Project updatedProject = updateProject(existProject, str);
            return updatedProject;
        } else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_PROJECTID, id);
            values.put(MySQLiteHelper.COLUMN_PROJECTNAME, str);
            long insertId = database.insert(MySQLiteHelper.TABLE_PROJECT, null,
                    values);
            Cursor cursor = database.query(MySQLiteHelper.TABLE_PROJECT,
                    allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Project p2 = cursorToProject(cursor);
            cursor.close();
            return p2;
        }
    }

    /**
     * update a project
     *
     * @param project we ant to update
     * @param descr   we want to change for
     * @return project updated
     */
    public Project updateProject(Project project, String descr) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PROJECTNAME, descr);
        database.update(MySQLiteHelper.TABLE_PROJECT, values, MySQLiteHelper.COLUMN_PROJECTID + " = " + project.getProjectId(), null);
        return getProjectWithId(project.getProjectId());
    }

    /**
     * knowing a project_id, we want to get the project itself
     *
     * @param id is the id of the project we are looking for
     * @return p1 is the project we were looking for
     */
    public Project getProjectWithId(Long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_PROJECT, allColumnsProject, MySQLiteHelper.COLUMN_PROJECTID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Project p1 = cursorToProject(c);
        c.close();
        return p1;
    }

    /**
     * knowing an id we test if this project exists
     *
     * @param id is the id of the project we ask
     * @return boolean says if the project with this id exists or not
     */
    public Boolean existProjectWithId(Long id) {
        Cursor c = database.rawQuery("SELECT * FROM "
                + MySQLiteHelper.TABLE_PROJECT + " where " + MySQLiteHelper.COLUMN_PROJECTID + "=" + id, null);
        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    /**
     * deleting a project
     *
     * @param p1 is the project we want to delete
     */
    public void deleteProject(Project p1) {
        long id = p1.getProjectId();
        System.out.println("Project deleted with id: " + id);
        System.out.println(database.getPath());
        database.delete(MySQLiteHelper.TABLE_PROJECT, MySQLiteHelper.COLUMN_PROJECTID + " = " + id, null);
    }

    /**
     * query to get project information
     */
    private static final String
            GETALLPROJECTS =
            "SELECT * FROM "
                    + MySQLiteHelper.TABLE_PROJECT
                    + " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM
                    + " ON " + MySQLiteHelper.TABLE_PROJECT + "." + MySQLiteHelper.COLUMN_GPSGEOMID + "=" + MySQLiteHelper.TABLE_GPSGEOM + "." + MySQLiteHelper.COLUMN_GPSGEOMID
                    + ";";

    /**
     * query to get project information
     */
    private static final String
            GETALLGPSGEOM =
            "SELECT * FROM "
                    + MySQLiteHelper.TABLE_GPSGEOM
                    + ";";

    /**
     * query to get photo informations
     */
    private static final String
            GETALLPHOTOS =
            "SELECT * FROM "
                    + MySQLiteHelper.TABLE_PHOTO
                    + " INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM
                    + " ON " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_GPSGEOMID + "=" + MySQLiteHelper.TABLE_GPSGEOM + "." + MySQLiteHelper.COLUMN_GPSGEOMID
                    + ";";

    /**
     * query to get the biggest photo_id from local db
     */
    private static final String
            GETMAXPHOTOID =
            "SELECT " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID + " FROM "
                    + MySQLiteHelper.TABLE_PHOTO
                    + " ORDER BY DESC LIMIT 1 ;";

    /**
     * query to get information of every photos and theirs geolocalisation knowing a project id
     * need to add the project id and the ";" in the method argument
     */
    private static final String
            GETPHOTOLINK =
            "SELECT " + MySQLiteHelper.TABLE_PHOTO + ".*, " + MySQLiteHelper.TABLE_GPSGEOM + ".* FROM ("
                    + MySQLiteHelper.TABLE_PHOTO
                    + " INNER JOIN " + MySQLiteHelper.TABLE_COMPOSED
                    + " ON " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID + "=" + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PHOTOID
                    + ") INNER JOIN " + MySQLiteHelper.TABLE_GPSGEOM
                    + " ON " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_GPSGEOMID + "=" + MySQLiteHelper.TABLE_GPSGEOM + "." + MySQLiteHelper.COLUMN_GPSGEOMID
                    + " WHERE " + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PROJECTID + " = ";

    /**
     * execution of the query GETALLPROJECTS
     *
     * @return projectsList that is a List of found projects
     */
    public List<Project> getAllProjects() {
        List<Project> projectsList = new ArrayList<Project>();

        Cursor cursor = database.rawQuery(GETALLPROJECTS, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Project p1 = cursorToProject(cursor);
            projectsList.add(p1);
            cursor.moveToNext();
        }
        cursor.close();
        return projectsList;
    }

    /**
     * execution of the query GETALLGPSGEOM
     *
     * @return gpsGeomList that contains the gpsGeom found
     */
    public List<GpsGeom> getAllGpsGeom() {
        List<GpsGeom> gpsGeomList = new ArrayList<GpsGeom>();

        Cursor cursor = database.rawQuery(GETALLGPSGEOM, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            /**
             * we now translate the content of the cursor into several projects
             */
            GpsGeom p1 = cursorToGpsGeom(cursor);
            gpsGeomList.add(p1);
            cursor.moveToNext();
        }
        cursor.close();
        return gpsGeomList;
    }

    /**
     * convert a cursor to a project
     *
     * @param cursor
     * @return project
     */
    private Project cursorToProject(Cursor cursor) {
        Project p1 = new Project();
        if (cursor.moveToFirst()) {
            p1.setProjectId(cursor.getLong(0));
            p1.setProjectName(cursor.getString(1));
            p1.setGpsGeom_id(cursor.getLong(2));
        }
        //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
        /*try{
	    	p1.setExt_GpsGeomCoord(cursor.getString(4));
	    }
	    catch (Exception e){e.printStackTrace();};
	    */
        return p1;
    }

    // PHOTO METHODS

    /**
     * call the query to get all photos
     *
     * @return a list of every photo
     */
    public List<Photo> getAllPhotos() {
        List<Photo> photosList = new ArrayList<Photo>();

        Cursor cursor = database.rawQuery(GETALLPHOTOS, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo p1 = cursorToPhoto(cursor);
            photosList.add(p1);
            cursor.moveToNext();
        }
        cursor.close();
        return photosList;
    }

    /**
     * we want every photos linked to a project knowing its id
     *
     * @param project_id
     * @return photosList found by the query
     */
    public List<Photo> getAllPhotolinkedtoProject(long project_id) {
        List<Photo> photosList = new ArrayList<Photo>();

        Cursor cursor = database.rawQuery(GETPHOTOLINK + project_id + ";", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Photo p1 = cursorToPhoto(cursor);
            photosList.add(p1);
            cursor.moveToNext();
        }
        cursor.close();
        return photosList;
    }

    /**
     * delete a photo
     *
     * @param p1 is a photo
     */
    public void deletePhoto(Photo p1) {
        long id = p1.getPhoto_id();
        System.out.println("Photo deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_PHOTO, MySQLiteHelper.COLUMN_PHOTOID + " = " + id, null);
    }

    /**
     * create a photo with the following attributes
     *
     * @param descr
     * @param author
     * @param url    is the name of the pohot with its extension
     * @return the created photo
     */
    public Photo createPhoto(String descr, String author, String url) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PHOTODESCRIPTION, descr);
        values.put(MySQLiteHelper.COLUMN_PHOTOAUTHOR, author);
        values.put(MySQLiteHelper.COLUMN_PHOTOURL, url);
        long insertId = database.insert(MySQLiteHelper.TABLE_PHOTO, null, values);
        //TODO check the utily of autoincrement
        Cursor cursor =
                database.query(
                        MySQLiteHelper.TABLE_PHOTO,
                        allColumnsPhoto,
                        MySQLiteHelper.COLUMN_PHOTOID + " = " + insertId,
                        null, null, null, null);
        cursor.moveToFirst();
        Photo newPhoto = cursorToPhoto(cursor);//method at the end of the class
        cursor.close();
        return newPhoto;
    }

    /**
     * translate a cursor to a photo
     *
     * @param cursor
     * @return the photo created
     */
    private Photo cursorToPhoto(Cursor cursor) {
        Photo p1 = new Photo();
        p1.setPhoto_id(cursor.getLong(0));
        p1.setPhoto_description(cursor.getString(1));
        p1.setPhoto_author(cursor.getString(2));
        p1.setPhoto_url(cursor.getString(3));
        p1.setPhoto_adresse(cursor.getString(4));
        p1.setPhoto_nbrPoints(cursor.getLong(5));
        p1.setPhoto_derniereModif(cursor.getInt(6));
        p1.setGpsGeom_id(cursor.getLong(7));
        //TODO créer 2 fonctions, une pour l'instanciation du projet, une pour la recopie des gpsgeom
        try {
            p1.setExt_GpsGeomCoord(cursor.getString(9));
        } catch (Exception e) {
        }
        ;
        return p1;
    }

    /**
     * knowing an id we want to get the related photo
     *
     * @param id of the photo
     * @return p1 is the photo with this id
     */
    public Photo getPhotoWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_PHOTO, allColumnsPhoto, MySQLiteHelper.COLUMN_PHOTOID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Photo p1 = cursorToPhoto(c);
        c.close();
        return p1;
    }

    // GPS GEOM METHODS

    /**
     * create a GPSGeom in the database and update the photo tuple where photo_id = id with this gpsgeom_id
     *
     * @param str
     * @param id
     * @return
     */
    public GpsGeom createGPSGeomToPhoto(String str, long id) {
        GpsGeom gps1 = createGPSGeom(str);
        //TODO TRANSACTION
        ContentValues args = new ContentValues();
        args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
        int d = database.update(MySQLiteHelper.TABLE_PHOTO, args, MySQLiteHelper.COLUMN_PHOTOID + "=" + id, null);
        return gps1;
    }

    public GpsGeom getGpsGeomWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_GPSGEOM, allColumnsGpsGeom, MySQLiteHelper.COLUMN_GPSGEOMID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        GpsGeom g1 = cursorToGpsGeom(c);
        c.close();
        return g1;
    }

    /**
     * create a GPSGeom in the database and update the project tuple where project_id = id with this gpsgeom_id
     *
     * @param str need to use convertion method of utils package
     * @param id  is the project_id were we need to update the gpsGeom_id foreign key
     * @return gs1 is the gpsGeom created
     */
    public GpsGeom createGPSGeomToProject(String str, long id) {
        GpsGeom gps1 = createGPSGeom(str);
        //TODO TRANSACTION
        ContentValues args = new ContentValues();
        args.put(MySQLiteHelper.COLUMN_GPSGEOMID, gps1.getGpsGeomsId());
        int d = database.update(MySQLiteHelper.TABLE_PROJECT, args, MySQLiteHelper.COLUMN_PROJECTID + "=" + id, null);
        return gps1;
    }

    /**
     * create a GPSGeom with the gpsgeom_coord str
     *
     * @param str
     * @return GpsGeom
     */
    public GpsGeom createGPSGeom(String str) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GPSGEOMCOORD, str);
        long insertId = database.insert(MySQLiteHelper.TABLE_GPSGEOM, null, values);
        //TODO check the utily of autoincrement
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_GPSGEOM,
                allColumnsGpsGeom,
                MySQLiteHelper.COLUMN_GPSGEOMID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        GpsGeom newGpsGeom = cursorToGpsGeom(cursor);//method at the end of the class
        cursor.close();
        return newGpsGeom;
    }

    /**
     * convert the cursor to the object gpsGeom
     *
     * @param cursor
     * @return GpsGeom
     */
    private GpsGeom cursorToGpsGeom(Cursor cursor) {
        GpsGeom p1 = new GpsGeom();
        p1.setGpsGeomId(cursor.getLong(0));
        p1.setGpsGeomCoord(cursor.getString(1));
        return p1;
    }

    // methods related to Composed.java that represents the link between Photos and projects

    /**
     * create an object Composed that link a photo and a project
     *
     * @param proj_id  of the project
     * @param photo_id of the photo
     * @return Composed object that links both elements
     */
    public Composed createLink(long proj_id, long photo_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PROJECTID, proj_id);
        values.put(MySQLiteHelper.COLUMN_PHOTOID, photo_id);
        database.insert(MySQLiteHelper.TABLE_COMPOSED, null, values);
        //TODO check the utily of autoincrement
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_COMPOSED,
                allColumnsComposed,
                MySQLiteHelper.COLUMN_PROJECTID + " = " + proj_id + " AND " + MySQLiteHelper.COLUMN_PHOTOID + " = " + photo_id,
                null, null, null, null);
        cursor.moveToFirst();
        Composed link1 = cursorToComposed(cursor);//method at the end of the class
        cursor.close();
        return link1;
    }

    /**
     * translate a cursor to a composed
     *
     * @param cursor
     * @return
     */
    private Composed cursorToComposed(Cursor cursor) {
        Composed link1 = new Composed();
        link1.setProject_id(cursor.getLong(0));
        link1.setPhoto_id(cursor.getLong(1));
        return link1;
    }

    // OTHER METHODS TO GET LOCAL ITEMS FROM ID. ADDED FOR USE IN THE SYNC CLASS

    /**
     * knowing an id, we find the related element
     *
     * @param id
     * @return element
     */
    public Marqueur getElementWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_ELEMENT, allColumnsElement,
                MySQLiteHelper.COLUMN_ELEMENTID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Marqueur e = cursorToElement(c);
        c.close();
        return e;
    }

    /**
     * translate a cursor to an element
     *
     * @param cursor
     * @return element created
     */
    public Marqueur cursorToElement(Cursor cursor) {
        Marqueur e = new Marqueur();
        e.setElement_id(cursor.getLong(0));
        e.setPhoto_id(cursor.getLong(1));
        e.setMaterial_id(cursor.getLong(2));
        e.setElementType_id(cursor.getLong(3));
        e.setPixelGeom_id(cursor.getLong(4));
        e.setGpsGeom_id(cursor.getLong(5));
        e.setElement_color(cursor.getString(6));
        return e;
    }

    /**
     * knowing an id, we find the pixelGeom
     *
     * @param id
     * @return pixelGeom
     */
    public PixelGeom getPixelGeomWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_PIXELGEOM, allColumnsPixelGeom,
                MySQLiteHelper.COLUMN_PIXELGEOMID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        PixelGeom p = cursorToPixelGeom(c);
        c.close();
        return p;
    }

    /**
     * translate a cursor to a pixelGeom
     *
     * @param cursor
     * @return pixelGeom
     */
    public PixelGeom cursorToPixelGeom(Cursor cursor) {
        PixelGeom p = new PixelGeom();
        p.setPixelGeomId(cursor.getLong(0));
        p.setPixelGeom_the_geom(cursor.getString(1));
        return p;
    }

    /**
     * knowing an id we find the related material
     *
     * @param id
     * @return material
     */
    public Material getMaterialWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_MATERIAL, allColumnsMaterial,
                MySQLiteHelper.COLUMN_MATERIALID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Material m = cursorToMaterial(c);
        c.close();
        return m;
    }

    /**
     * translate a cursor to a Material
     *
     * @param cursor
     * @return material created
     */
    public Material cursorToMaterial(Cursor cursor) {
        Material m = new Material();
        m.setMaterial_id(cursor.getLong(0));
        m.setMaterial_name(cursor.getString(1));
        return m;
    }

    /**
     * knowing an id we want to find the related lementType
     *
     * @param id
     * @return ElementType
     */
    public ElementType getElementTypeWithID(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_ELEMENTTYPE, allColumnsElementType,
                MySQLiteHelper.COLUMN_ELEMENTTYPEID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        ElementType e = cursorToElementType(c);
        c.close();
        return e;
    }

    /**
     * translate a cursor to an elementtype
     *
     * @param cursor
     * @return ElementType
     */
    public ElementType cursorToElementType(Cursor cursor) {
        ElementType e = new ElementType();
        e.setElementType_id(cursor.getLong(0));
        e.setElementType_name(cursor.getString(1));
        return e;
    }

    //METHODS FOR ELMENTS TYPE

    //Create ellementType in the database
    //TODO sync with the external database

    /**
     * method that register a new type in the DB
     */
    public void createElementTypeInDB(String str) {
        boolean flag = true;
        Cursor cursor = database.rawQuery(GETALLELEMENTTYPEID, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(1).equals(str)) {
                flag = false;
            }
            cursor.moveToNext();
        }
        if (flag) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_ELEMENTTYPENAME, str);
            long insertId = database.insert(MySQLiteHelper.TABLE_ELEMENTTYPE, null, values);
        }
    }

    /**
     * sql query that returns every elementtype_id
     */
    private static final String
            GETALLELEMENTTYPEID =
            "SELECT * FROM "
                    + MySQLiteHelper.TABLE_ELEMENTTYPE
                    + ";";

    /**
     * TODO WTF execute the query and update elementType list
     *//*
	public void getAllElementType(){
		List<ElementType> elementTypeList = new ArrayList<ElementType>();
		Cursor cursor = database.rawQuery(GETALLELEMENTTYPEID,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			ElementType p1 = cursorToElementType(cursor);
			elementTypeList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
	MainActivity.elementType=(ArrayList<ElementType>) elementTypeList;
	}
	//METHODS FOR MATERIALS
	
	//Create ellementType in the database
	//TODO sync with the external database
	/**
	 * method that register a new type in the DB

	public void createMaterialInDB(String str){
		boolean flag = true;
		Cursor cursor = database.rawQuery(GETALLMATERIALID,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			if(cursor.getString(1).equals(str)){
				flag=false;
			}
			cursor.moveToNext();
		}
		if(flag){
			ContentValues values = new ContentValues(); 
			values.put(MySQLiteHelper.COLUMN_MATERIALNAME, str);
			long insertId = database.insert(MySQLiteHelper.TABLE_MATERIAL, null, values);
		}
	}
	
	/**
	 * sql query that counts the number of element type

	private static final String
	GETALLMATERIALID = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_MATERIAL 
		+";"
	;
	
	/**
	 * use the query to get all material from the db and updating static field

	public void getAllMaterial(){
		List<Material> materialList = new ArrayList<Material>();
		Cursor cursor = database.rawQuery(GETALLMATERIALID,null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Material p1 = cursorToMaterial(cursor);
			materialList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.material=(ArrayList<Material>) materialList;		
	}

	/**
	 * get information from datasource.database to public static fields from main activity once a local project is loaded

	
	/**
	 * SQL query that select every pixelgeom linked to the registred photo
	 * need to complete with PHOTO_ID and ";"

	private static final String
	GETALLPIXELGEOMFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_PIXELGEOM+"."+MySQLiteHelper.COLUMN_PIXELGEOMID+", "
		+ MySQLiteHelper.TABLE_PIXELGEOM+"."+MySQLiteHelper.COLUMN_PIXELGEOMCOORD
		+" FROM "
		+ MySQLiteHelper.TABLE_PIXELGEOM
		+" INNER JOIN " + MySQLiteHelper.TABLE_ELEMENT 
		+" ON " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PIXELGEOMID +" = " + MySQLiteHelper.TABLE_PIXELGEOM + "." + MySQLiteHelper.COLUMN_PIXELGEOMID
		+" WHERE " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity

	public void instanciateAllpixelGeom(){
		ArrayList<PixelGeom> pixelGeomList = new ArrayList<PixelGeom>();
		
		Cursor cursor = database.rawQuery(GETALLPIXELGEOMFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			PixelGeom p1 = cursorToPixelGeom(cursor);
			p1.setRegistredInLocal(true);
			pixelGeomList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.pixelGeom= pixelGeomList;
	}
	
	/**
	 * SQL query that select every element linked to the registred photo knowing a photo_id
	 * need to complete with PHOTO_ID and ";"

	private static final String
	GETALLELEMENTFROMAPHOTO = 
		"SELECT "
		+ "* "
		+" FROM "
		+ MySQLiteHelper.TABLE_ELEMENT
		+" WHERE " + MySQLiteHelper.TABLE_ELEMENT + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity

	public void instanciateAllElement(){
		ArrayList<Marqueur> elementList = new ArrayList<Marqueur>();
		
		Cursor cursor = database.rawQuery(GETALLELEMENTFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Marqueur p1 = cursorToElement(cursor);
			p1.setRegistredInLocal(true);
			elementList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.element= elementList;
	}
	
	/**
	 * SQL query that select every gpsgeom linked to the registred photo knowing a photo_id
	 * need to complete with PHOTO_ID and ";"

	private static final String
	GETALLGPSGEOMFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMID+", "
		+ MySQLiteHelper.TABLE_GPSGEOM+"."+MySQLiteHelper.COLUMN_GPSGEOMCOORD
		+" FROM "
		+ MySQLiteHelper.TABLE_GPSGEOM
		+" INNER JOIN " + MySQLiteHelper.TABLE_PHOTO 
		+" ON " + MySQLiteHelper.TABLE_GPSGEOM + "." + MySQLiteHelper.COLUMN_GPSGEOMID +" = " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_GPSGEOMID
		+" WHERE " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID+" = "
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity

	public void instanciateAllGpsGeom(){
		ArrayList<GpsGeom> gpsGeomList = new ArrayList<GpsGeom>();
		
		Cursor cursor = database.rawQuery(GETALLGPSGEOMFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			GpsGeom p1 = cursorToGpsGeom(cursor);
			p1.setRegistredInLocal(true);
			gpsGeomList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		MainActivity.gpsGeom= gpsGeomList;
	}
	
	/**
	 * SQL query that select every projectss linked to the registred photo knowing this photo_id
	 * need to complete with PHOTO_ID and ";"

	private static final String
	GETALLPROJECTFROMAPHOTO = 
		"SELECT "
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_PROJECTID+", "
		
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_PROJECTNAME+", "
		+ MySQLiteHelper.TABLE_PROJECT+"."+MySQLiteHelper.COLUMN_GPSGEOMID
		+" FROM "
		+ MySQLiteHelper.TABLE_PROJECT
		+" INNER JOIN " + MySQLiteHelper.TABLE_COMPOSED 
		+" ON " + MySQLiteHelper.TABLE_PROJECT + "." + MySQLiteHelper.COLUMN_PROJECTID +" = " + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PROJECTID
		+" WHERE " + MySQLiteHelper.TABLE_COMPOSED + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity

	public void instanciateAllProject(){
		ArrayList<Project> projectList = new ArrayList<Project>();
		
		Cursor cursor = database.rawQuery(GETALLPROJECTFROMAPHOTO + MainActivity.photo.getPhoto_id() +" ;",null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Project p1 = cursorToProject(cursor);
			p1.setRegistredInLocal(true);
			projectList.add(p1);
			cursor.moveToNext();
		}
		cursor.close();
		for(Project p :MainActivity.project)
			Log.i(TAG+"before","id="+p.getProjectId()+" name = "+p.getProjectName());
		MainActivity.project= projectList;
		for(Project p :MainActivity.project)
			Log.i(TAG+"after","id="+p.getProjectId()+" name = "+p.getProjectName());
	}

	/**
	 * get information from datasource.database to public static fields photo in main activity once a local project is loaded

	
	/**
	 * SQL query that select every pixelgeom link to the registred photo

	private static final String
	GETPHOTO = 
		"SELECT * FROM "
		+ MySQLiteHelper.TABLE_PHOTO
		+" WHERE " + MySQLiteHelper.TABLE_PHOTO + "." + MySQLiteHelper.COLUMN_PHOTOID+" = " 
		//need to complete with PHOTO_ID and ";"
	;

	/**
	 * register values from the above query in the static public field pixelGeom (instance of arrayList) from MainActivity

	public void instanciatePhoto(long id ){
		Cursor cursor = database.rawQuery(GETPHOTO + id +" ;",null);
		cursor.moveToFirst();
		Photo photoLoaded = cursorToPhoto(cursor);
		cursor.close();
		MainActivity.photo= photoLoaded;
	}*/
}