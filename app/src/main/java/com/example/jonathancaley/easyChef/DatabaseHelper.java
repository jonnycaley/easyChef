//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //variables for the ingredients table
    public static final String DATABASE_NAME = "ingredients.db";
    public static final String TABLE_NAME = "ingredients_table";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "QUANTITY";
    public static final String COL_3 = "MEASUREMENT";

    //variables for the meals table
    public static final String TABLE_NAME2 = "meals_table";
    public static final String COL_12 = "MEAL";
    public static final String COL_22 = "INGREDIENTS";
    public static final String COL_32 = "TYPE";
    public static final String COL_42 = "AMOUNT"; // always be 1, used to make it easy to insert data into trolley table

    //variables for the trolley table
    public static final String TABLE_NAME3 = "trolley_table";
    public static final String COL_13 = "MEAL";
    public static final String COL_23 = "INGREDIENTS";
    public static final String COL_33 = "TYPE";
    public static final String COL_43 = "AMOUNT"; //quantity of a certain meal

    public DatabaseHelper(Context context) { //whenever this constructor is called the database ingredients will be created
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db){ //initialises tables
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (NAME TEXT NOT NULL PRIMARY KEY,QUANTITY INTEGER,MEASUREMENT STRING)");
        db.execSQL("CREATE TABLE " + TABLE_NAME2 + " (MEAL TEXT NOT NULL PRIMARY KEY,INGREDIENTS TEXT NOT NULL,TYPE TEXT NOT NULL,AMOUNT INTEGER)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Spaghetti Bolognese','Minced Beef:200g,Spaghetti:150g,Bolognese Sauce:175g','Main',1)"); //initial meals are added into the database upon creation
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Fish and Chips','Cod Fillet:200g,Chips:150g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Sausage and Mash','Sausages:10,Mashed Potato:200g,Gravy:100g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Jacket Potato','Jacket Potato:250g,Beans:110g,Cheese:105g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Apple Pie','Apples:105g,Pastry:200g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Strawberries and Chocolate','Strawberries:150g,Chocolate Spread:100g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Mango Cupcakes','Cake:150g,Icing:100g,Mango:250g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Pancakes','Flour:210g,Milk:250ml,Eggs:10','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Garlic Bread','Garlic:100g,Bread:150g','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Cheesy Nachos','Nachos:100g,Cheese:150g,Salsa:100ml','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Prawn Toast','Bread:10,Prawn Seasoning:10g,Sesame Seeds:10g','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Garlic Dough Balls','Dough Balls:10,Garlic Spread:100ml','Starter',1)");
        db.execSQL("CREATE TABLE " + TABLE_NAME3 + " (MEAL TEXT NOT NULL PRIMARY KEY,INGREDIENTS TEXT NOT NULL,TYPE TEXT NOT NULL,AMOUNT INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        onCreate(db);
    }

    //the following methods are for dealing with data inside the ingredients_table

    public boolean insertData(String name, Integer quantity, String measurement) { //method used to insert ingredient into table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, quantity);
        contentValues.put(COL_3, measurement);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() { //method that gets all info from ingredients_table and orders alphabetically
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY NAME COLLATE NOCASE DESC", null);

    }

    public void deleteAll() { //method that clears ingredients_table
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

    public Cursor getName(String x) { //method that gets information about an ingredient within ingredients_table
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE NAME = " + "\"" + x + "\"" , null);
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String ingredient) { //method that checks if a row already exists within a table
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE NAME = " + "\"" +  ingredient + "\"";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public void updateData(String oldName, String newName, String newQuantity, String newMeasurement){ //method used to update a row of the ingredients table
        SQLiteDatabase db = this.getWritableDatabase();
        String sql2 = "UPDATE ingredients_table SET " + COL_2 + " = " + "\"" + newQuantity+ "\"" + " WHERE NAME = " + "\"" + oldName + "\"";
        String sql3 = "UPDATE ingredients_table SET " + COL_3 + " = " + "\"" + newMeasurement + "\"" + " WHERE NAME = " + "\"" + oldName + "\"";
        String sql1 = "UPDATE ingredients_table SET " + COL_1 + " = " + "\"" + newName + "\"" + " WHERE NAME = " + "\"" + oldName + "\"";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    public void deleteData(String ingredientName){ //method used to delete ingredient from ingredients_table
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM ingredients_table WHERE " + COL_1 + " = " + "\"" + ingredientName + "\"";
        db.execSQL(delete);
    }

    //the following methods are for dealing with data inside the meals_table

    public void insertDataMeals(String name, String ingredients, String type) { //method used to insert meal into meal_table
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_12, name);
        contentValues.put(COL_22, ingredients);
        contentValues.put(COL_32, type);
        contentValues.put(COL_42, 1);
        db.insert(TABLE_NAME2, null, contentValues);
    }

    public Cursor getAMeal2(String x) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT rowid _id,* FROM " + TABLE_NAME2 +" WHERE MEAL = " + "\"" + x + "\"", null);
    }

    public void updateDataMeal(String oldName, String newName, String newIngredient, String newType){ //method used to update a row of the meal table
        SQLiteDatabase db = this.getWritableDatabase();
        String sql1 = "UPDATE meals_table SET " + COL_32 + " = " + "\"" + newType+ "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        String sql2 = "UPDATE meals_table SET " + COL_22 + " = " + "\"" + newIngredient + "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        String sql3 = "UPDATE meals_table SET " + COL_12 + " = " + "\"" + newName + "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    public boolean CheckIsDataAlreadyInDBorNotMeals(String TableName, String meal) { //method that checks if a row already exists within a table
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT rowid _id,* FROM " + TableName + " WHERE MEAL = " + "\"" +  meal + "\"";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false; //if it is not within the database
        }
        cursor.close();
        return true; //if it is within database
    }

    public void deleteDataMeal(String mealName){ //method used to delete meal within meal_table
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM meals_table WHERE " + COL_12 + " = " + "\"" + mealName + "\"";
        db.execSQL(delete);
    }

    public Cursor getAMeal() { //method used to retrieve all meals from meal_table
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " ORDER BY MEAL COLLATE NOCASE", null);
    }

    public Cursor getMealType(String types) { //method used to retrieve meals from meal_table based off of the options selected by the user (Starter/Main/Desert)
        SQLiteDatabase db = this.getWritableDatabase();

        if (types.contains("Starter") && types.contains("Main")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Starter" + "\"" + " OR TYPE = " + "\"" + "Main" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (types.contains("Starter") && types.contains("Desert")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Starter" + "\"" + " OR TYPE = " + "\"" + "Desert" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (types.contains("Main") && types.contains("Desert")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Main" + "\"" + " OR TYPE = " + "\"" + "Desert" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (types.contains("Starter")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Starter" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (types.contains("Main")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Main" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        else  {
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Desert" + "\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
    }
    public Cursor getAMealName(String x) { //method used to retrieve a meal by meal name
        SQLiteDatabase db = this.getWritableDatabase();
        String get = "SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE MEAL = " + "\"" + x + "\"" + " ORDER BY MEAL COLLATE NOCASE";
        return db.rawQuery(get, null);
    }

    public Cursor getAllMealsSearch(String searchQuery){ //method used in conjunction with the search bar to filter meals onto the listview
        SQLiteDatabase db = this.getWritableDatabase();
        String get = "SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE";
        return db.rawQuery(get, null);
    }

    public Cursor getMealTypeSearch(String mealType, String searchQuery){ //method used to retrieve meals based off of the type of meal and the search bar query
        SQLiteDatabase db = this.getWritableDatabase();

        if (mealType.contains("Starter") && mealType.contains("Main")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE (TYPE = " + "\"" + "Starter" + "\"" + " OR TYPE = " + "\"" + "Main" + "\"" + ") AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (mealType.contains("Starter") && mealType.contains("Desert")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE (TYPE = " + "\"" + "Starter" + "\"" + " OR TYPE = " + "\"" + "Desert" + "\"" + ") AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (mealType.contains("Main") && mealType.contains("Desert")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE (TYPE = " + "\"" + "Main" + "\"" + " OR TYPE = " + "\"" + "Desert" + "\"" + ") AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (mealType.contains("Starter")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Starter" + "\"" + " AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        if (mealType.contains("Main")){
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Main" + "\"" + " AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }
        else  {
            return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME2 + " WHERE TYPE = " + "\"" + "Desert" + "\"" + " AND MEAL LIKE " + "\"%" + searchQuery + "%\"" + " ORDER BY MEAL COLLATE NOCASE", null);
        }

    }

    //the following methods are for dealing with data inside the trolley_table

    public Cursor getAllDataTrolley() { //method used to retrieve all meals within the trolley_table
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME3 + " ORDER BY MEAL COLLATE NOCASE", null);

    }

    public void insertDataTrolley(String meal) { //method used to insert meal into trolley_table
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "INSERT INTO " + TABLE_NAME3 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) SELECT MEAL,INGREDIENTS,TYPE,AMOUNT FROM " + TABLE_NAME2 + " WHERE MEAL = " + "\"" + meal + "\""; //inserts a meal from meals_table into trolley_table to be displayed
        db.execSQL(insert);
    }

    public Cursor getAMealTrolley(String x) { //method used to get a meal from ingredients_table
        SQLiteDatabase db = this.getWritableDatabase();
        String get = "SELECT rowid _id, * FROM " + TABLE_NAME3 + " WHERE MEAL = " + "\"" + x + "\"";
        return db.rawQuery(get, null);
    }

    public void increaseAmountTrolley(String meal, Integer x) { //method used to increase the quantity of a meal in trolley_table
        SQLiteDatabase db = this.getWritableDatabase();
        String insert = "UPDATE " + TABLE_NAME3 + " SET " + COL_43 + " = " + x + " WHERE MEAL = " + "\"" + meal + "\""; //inserts a meal from meals_table into trolley_table to be displayed
        db.execSQL(insert);
    }

    public void deleteTrolleyMeal(String meal) { //method used to delete a meal from the trolley_table
        SQLiteDatabase db = this.getWritableDatabase();
        String delete = "DELETE FROM trolley_table WHERE " + COL_13 + " = " + "\"" + meal + "\"";
        db.execSQL(delete);
    }

    public void updateDataTrolley(String oldName, String newName, String newIngredient, String newType){ //method used to update a row of the meal table
        SQLiteDatabase db = this.getWritableDatabase();
        String sql1 = "UPDATE trolley_table SET " + COL_33 + " = " + "\"" + newType+ "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        String sql2 = "UPDATE trolley_table SET " + COL_23 + " = " + "\"" + newIngredient + "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        String sql3 = "UPDATE trolley_table SET " + COL_13 + " = " + "\"" + newName + "\"" + " WHERE MEAL = " + "\"" + oldName + "\"";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    public void reset(){ //method used to reset all tables within the database
        SQLiteDatabase db = this.getWritableDatabase();
        String del1 = "DELETE FROM ingredients_table";
        String del2 = "DELETE FROM meals_table";
        String del3 = "DELETE FROM trolley_table";
        db.execSQL(del1);
        db.execSQL(del2);
        db.execSQL(del3);
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Spaghetti Bolognese','Minced Beef:200g,Spaghetti:150g,Bolognese Sauce:175g','Main',1)"); //initial meals are added into the database upon creation
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Fish and Chips','Cod Fillet:200g,Chips:150g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Sausage and Mash','Sausages:10,Mashed Potato:200g,Gravy:100g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Jacket Potato','Jacket Potato:250g,Beans:110g,Cheese:105g','Main',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Apple Pie','Apples:105g,Pastry:200g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Strawberries and Chocolate','Strawberries:150g,Chocolate Spread:100g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Mango Cupcakes','Cake:150g,Icing:100g,Mango:250g','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Pancakes','Flour:210g,Milk:250ml,Eggs:10','Desert',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Garlic Bread','Garlic:100g,Bread:150g','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Cheesy Nachos','Nachos:100g,Cheese:150g,Salsa:100ml','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Prawn Toast','Bread:10,Prawn Seasoning:10g,Sesame Seeds:10g','Starter',1)");
        db.execSQL("INSERT INTO " + TABLE_NAME2 + " (MEAL,INGREDIENTS,TYPE,AMOUNT) VALUES ('Garlic Dough Balls','Dough Balls:10,Garlic Spread:100ml','Starter',1)");
    }
}