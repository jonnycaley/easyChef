//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainPage extends AppCompatActivity {
    //initialise variables
    private GestureDetectorCompat gestureObject;
    private ShareActionProvider mShareActionProvider;
    String[] mealsSelected;
    Intent intent;
    ImageButton toManualPage;
    ImageButton toWelcomePage;
    ImageButton toIngredientsPage;
    ImageButton toTrolleyPage;
    TextView newMeal;
    AlertDialog dialog;
    EditText mMealName;
    EditText mIngredient;
    RadioButton mStarter;
    RadioButton mMain;
    RadioButton mDesert;
    DatabaseHelper myDb;
    CheckBox checkBox;
    ArrayList<String> meals = new ArrayList<String>();
    ArrayList<String> ingredients = new ArrayList<String>();
    ArrayList<String> types = new ArrayList<String>();
    EditText mealNameChanger;
    String MEAL;
    String INGREDIENTS;
    String TYPE;
    RadioButton starter;
    RadioButton main;
    RadioButton desert;
    Cursor cursor;
    String POSITION;
    int POSITION1;
    ArrayList<String> mealsNameUser = new ArrayList<String>();
    //the following variables are used to apply calculations to the ingredients within the meals based on the ingredients provided by the user
    Cursor calc;
    ArrayList<String> calculator = new ArrayList<String>();
    ArrayList<String> ingredientNames = new ArrayList<String>();
    ArrayList<String> quantities = new ArrayList<String>();
    ArrayList<String> measurements = new ArrayList<String>();
    String searchQuery;
    SearchView searchView;
    Integer rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        searchQuery=null;

        if (savedInstanceState != null) {
            mealsSelected = savedInstanceState.getStringArray("mealsSelected"); //initialises meal type array
            if(mealsSelected == null){
                mealsSelected = new String[3];
            }
        }

        loadActivity();
    }

    public void loadActivity() {

        Display display = ((WindowManager) getSystemService(MainPage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        if(rotation.equals(1) || rotation.equals(3)){
            setContentView(R.layout.main_page_landscape); //sets the layout of the screen if it is loaded landscape, if this wasn't here it would only load the landscape layout if it rotates too it and not if it is loaded into it from another activity
            textsizer();
        }
        myDb = new DatabaseHelper(this);

        searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //when the search view is submitted
                searchQuery=query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //when the search view is typed into to giving real time search queries as the user enters a string
                searchQuery=newText;
                loadActivity();
                return false;
            }
        });
        textsizer();
        ListView lv = (ListView) findViewById(R.id.mealLayout);
        meals.clear();
        ingredients.clear();
        types.clear();
        mealsNameUser.clear();//used to keep track of what meal is clicked as cannot order table alphabetically on the onCreate method in DatabaseHelper :/

        mealsSelected = getIntent().getStringArrayExtra("mealsSelected");

        //following initialises checkboxes
        if (Arrays.toString(mealsSelected).contains("Starter")) {
            checkBox = (CheckBox) findViewById(R.id.checkBoxStarter1);
            checkBox.setChecked(true);
        }
        if (Arrays.toString(mealsSelected).contains("Main")) {
            checkBox = (CheckBox) findViewById(R.id.checkBoxMain2);
            checkBox.setChecked(true);
        }
        if (Arrays.toString(mealsSelected).contains("Desert")) {
            checkBox = (CheckBox) findViewById(R.id.checkBoxDesert3);
            checkBox.setChecked(true);
        }

        generateListContent(searchQuery);

        calculator.clear();

        ingredientNames.clear();
        quantities.clear();
        measurements.clear();

        calc = myDb.getAllData();
        //gets the ingredients from the database to be used to subtract from the meals ingredients
        while (calc.moveToNext()) {
            ingredientNames.add(0, calc.getString(0));
            measurements.add(0, calc.getString(1));
            quantities.add(0, calc.getString(2));
        }

        //adapter used to load meals onto listView
        lv.setAdapter(new MyListAdaper(this, R.layout.list_view_custom_layout_meals, meals, ingredients, types));
        //onClickListener to load inflated view of editable meals
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor = myDb.getAMeal2(meals.get(position));

                if (cursor.moveToFirst()) {

                    MEAL = cursor.getString(1);
                    INGREDIENTS = cursor.getString(2);
                    TYPE = cursor.getString(3);
                    POSITION = cursor.getString(0);
                    POSITION1 = Integer.parseInt(POSITION);
                    POSITION1 = POSITION1 - 1;

                    View mView = getLayoutInflater().inflate(R.layout.edit_meal, null);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPage.this);
                    mBuilder.setView(mView);
                    dialog = mBuilder.show();

                    mealNameChanger = (EditText) mView.findViewById(R.id.mealNameChanger);
                    mealNameChanger.setText(MEAL);

                    final EditText mealIngredientsChanger = (EditText) mView.findViewById(R.id.mealIngredientChanger);
                    mealIngredientsChanger.setText(INGREDIENTS);

                    starter = (RadioButton) mView.findViewById(R.id.Starter);
                    if (TYPE.matches("Starter")) {
                        starter.setChecked(true);
                    }

                    main = (RadioButton) mView.findViewById(R.id.Main);
                    if (TYPE.matches("Main")) {
                        main.setChecked(true);
                    }
                    desert = (RadioButton) mView.findViewById(R.id.Desert);
                    if (TYPE.matches("Desert")) {
                        desert.setChecked(true);
                    }

                    Button editMeal = (Button) mView.findViewById(R.id.editIngredient);
                    //once the user clicks "edit meal" to attempt to edit a meal all format variations are checked
                    editMeal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mealNameChanger.toString().matches("")) {
                                Toast.makeText(MainPage.this, "The meal must have a name!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!mealIngredientsChanger.toString().contains(":")) {
                                Toast.makeText(MainPage.this, "The ingredients need to be in the format INGREDIENT:QUANTITY", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String[] separated = mealIngredientsChanger.getText().toString().split(",");  //separated ingredients at each ","

                            for (Integer x = 0; x < separated.length; x++) {
                                String[] separated1 = separated[x].split(":");
                                for (Integer y = 0; y < separated1.length; y++) {
                                    separated1[y] = separated1[y].trim(); //trims each ingredient and quantity to eliminate spaces at start and end
                                    if ((y % 2) == 0) {//every ingredient
                                        if (separated1[y].matches(".*\\d.*")) {
                                            Toast.makeText(MainPage.this, "You can't have a number in the ingredient name!", Toast.LENGTH_SHORT).show();
                                            return;
                                            //maybe loose the s of each ingredient to help with the merging of ingredients and their quantaties
                                        }
                                    }//every quantity
                                    else {
                                        if (separated1[y].charAt(0) == '0') {
                                            Toast.makeText(MainPage.this, "You can't start a quantity with 0! ", Toast.LENGTH_SHORT).show(); //if quantity starts with 0
                                            return;
                                        }
                                        if (!(separated1[y].matches(".*\\d.*"))) {
                                            Toast.makeText(MainPage.this, "Make sure you add a quantity!", Toast.LENGTH_SHORT).show(); //if it doesn't include a number, prompt a number
                                            return;
                                        }
                                    }
                                }
                            }
                            if ((!mealNameChanger.getText().toString().matches(MEAL)) && myDb.CheckIsDataAlreadyInDBorNotMeals("meals_table", mealNameChanger.getText().toString())) {
                                Toast.makeText(MainPage.this, "Your meal already exists within the database, click on it to edit it or choose a different meal name for this meal!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                return;
                            } else

                            {
                                String type = null;
                                if (starter.isChecked()) {
                                    type = "Starter";
                                }
                                if (main.isChecked()) {
                                    type = "Main";
                                }
                                if (desert.isChecked()) {
                                    type = "Desert";
                                }
                                myDb.updateDataMeal(MEAL, mealNameChanger.getText().toString(), mealIngredientsChanger.getText().toString(), type);
                                if(myDb.CheckIsDataAlreadyInDBorNotMeals("trolley_table",MEAL)) { //if it is also in the trolley table, update that too!
                                    Toast.makeText(MainPage.this, "Trolley meal updated too!", Toast.LENGTH_SHORT).show();
                                    myDb.updateDataTrolley(MEAL, mealNameChanger.getText().toString(), mealIngredientsChanger.getText().toString(), type);
                                }
                                Toast.makeText(MainPage.this, "Meal updated!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                loadActivity();
                                return;
                            }
                        }
                    });
                    //meal deleted listener
                    Button deleteMeal = (Button) mView.findViewById(R.id.deleteIngredient);
                    deleteMeal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDb.deleteDataMeal(MEAL);
                            myDb.deleteTrolleyMeal(MEAL);
                            dialog.dismiss();
                            loadActivity();
                            Toast.makeText(MainPage.this, "Meal deleted!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                cursor.close();
            }
        });

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        //the following code enables the user to navigate throughout the application using the icons at the top of the screen

        toWelcomePage = (ImageButton) findViewById(R.id.firstButton);
        toWelcomePage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(MainPage.this, MainPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        toIngredientsPage = (ImageButton) findViewById(R.id.secondButton);
        toIngredientsPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(MainPage.this, IngredientsPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        toTrolleyPage = (ImageButton) findViewById(R.id.thirdButton);
        toTrolleyPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(MainPage.this, TrolleyPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        toManualPage = (ImageButton) findViewById(R.id.fourthButton);
        toManualPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(MainPage.this, UserManualPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        newMeal = (TextView) findViewById(R.id.addNewMeal);
        newMeal.setOnClickListener(new addMeal());
    }

    private void textsizer() {
        TextView addNewMeal = (TextView) findViewById(R.id.addNewMeal);
        CheckBox checkBoxStarter1 = (CheckBox) findViewById(R.id.checkBoxStarter1);
        CheckBox checkBoxMain2 = (CheckBox) findViewById(R.id.checkBoxMain2);
        CheckBox checkBoxDesert3 = (CheckBox) findViewById(R.id.checkBoxDesert3);
        TextView Yh = (TextView) findViewById(R.id.Yh);
        TextView Al = (TextView) findViewById(R.id.Al);
        TextView Sm = (TextView) findViewById(R.id.Sm);
        TextView Non = (TextView) findViewById(R.id.Non);
        TextView ofin = (TextView) findViewById(R.id.ofin);
        TextView Mn = (TextView) findViewById(R.id.Mn);
        TextView RI = (TextView) findViewById(R.id.RI);
        TextView T = (TextView) findViewById(R.id.T);


        Integer screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        Integer textSize;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                textSize = 24;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    addNewMeal.setTextSize(textSize+6);
                    checkBoxStarter1.setTextSize(textSize+4);
                    checkBoxMain2.setTextSize(textSize+4);
                    checkBoxDesert3.setTextSize(textSize+4);

                    Yh.setTextSize(textSize);
                    Al.setTextSize(textSize);
                    Sm.setTextSize(textSize);
                    Non.setTextSize(textSize);
                    ofin.setTextSize(textSize);
                    Mn.setTextSize(textSize);
                    RI.setTextSize(textSize);
                    T.setTextSize(textSize);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    addNewMeal.setTextSize(textSize+8);
                    checkBoxStarter1.setTextSize(textSize+6);
                    checkBoxMain2.setTextSize(textSize+6);
                    checkBoxDesert3.setTextSize(textSize+6);

                    Yh.setTextSize(textSize);
                    Al.setTextSize(textSize);
                    Sm.setTextSize(textSize);
                    Non.setTextSize(textSize);
                    ofin.setTextSize(textSize);
                    Mn.setTextSize(textSize);
                    RI.setTextSize(textSize);
                    T.setTextSize(textSize);
                    break;
                }
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                textSize = 18;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    addNewMeal.setTextSize(textSize+6);
                    checkBoxStarter1.setTextSize(textSize+4);
                    checkBoxMain2.setTextSize(textSize+4);
                    checkBoxDesert3.setTextSize(textSize+4);

                    Yh.setTextSize(textSize);
                    Al.setTextSize(textSize);
                    Sm.setTextSize(textSize);
                    Non.setTextSize(textSize);
                    ofin.setTextSize(textSize);
                    Mn.setTextSize(textSize);
                    RI.setTextSize(textSize);
                    T.setTextSize(textSize);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    addNewMeal.setTextSize(textSize+8);
                    checkBoxStarter1.setTextSize(textSize+6);
                    checkBoxMain2.setTextSize(textSize+6);
                    checkBoxDesert3.setTextSize(textSize+6);

                    Yh.setTextSize(textSize);
                    Al.setTextSize(textSize);
                    Sm.setTextSize(textSize);
                    Non.setTextSize(textSize);
                    ofin.setTextSize(textSize);
                    Mn.setTextSize(textSize);
                    RI.setTextSize(textSize);
                    T.setTextSize(textSize);
                    break;
                }
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                textSize = 14;
                break;
            default:
                textSize = 15;
        }
    }

    //when the orientation of the screen changes the method onConfigurationChanged is invoked

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main_page_landscape);//landscape layout loaded
            textsizer();
            loadActivity();
        } else {
            setContentView(R.layout.main_page);//portrait layout loaded
            textsizer();
            loadActivity();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //saves the data when the app experiences orientation changes
        super.onSaveInstanceState(outState);
        outState.putStringArray("mealsSelected", mealsSelected);
    }
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mealsSelected = outState.getStringArray("mealsSelected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        //Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "YOUR_TEXT");
            shareIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(shareIntent);
        }
        //Return true to display menu
        return true;
    }

    //Call to update the share intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_share) {
            doShare();
        }
        return super.onOptionsItemSelected(item);
    }

    //shareActionProvider
    private void doShare() {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I'm using easyChef");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I used easyChef to create delicious meals using spare ingredients!");

        startActivity(Intent.createChooser(shareIntent, "Share this to everyone!"));
    }

    //when a checkbox for the meal types is clicked
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkBoxStarter1:
                if (checked) {
                    mealsSelected[0] = "Starter";
                } else {
                    mealsSelected[0] = "null";
                }
                break;

            case R.id.checkBoxMain2:
                if (checked) {
                    mealsSelected[1] = "Main";
                } else {
                    mealsSelected[1] = "null";
                }
                break;
            case R.id.checkBoxDesert3:
                if (checked) {
                    mealsSelected[2] = "Desert";
                } else {
                    mealsSelected[2] = "null";
                }
                break;
        }
        loadActivity();
    }

    //the following allows the user to swipe with their finger to navigate throughout the application instead of using the icons

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //add meal method
    public class addMeal implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {

            View mView = getLayoutInflater().inflate(R.layout.addnewmeal, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPage.this);
            mBuilder.setView(mView);
            dialog = mBuilder.show();
            mMealName = (EditText) mView.findViewById(R.id.mealName);
            mIngredient = (EditText) mView.findViewById(R.id.firstIngredient);
            mStarter = (RadioButton) mView.findViewById(R.id.Starter);
            mMain = (RadioButton) mView.findViewById(R.id.Main);
            mDesert = (RadioButton) mView.findViewById(R.id.Desert);

            Button mSubmitNewMeal = (Button) mView.findViewById(R.id.submitNewMeal);
            mSubmitNewMeal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tester = mIngredient.getText().toString();
                    String[] separated = tester.split(",");  //separated ingredients at each ","

                    String mealType = null;

                    if (!(mStarter.isChecked() || mMain.isChecked() || mDesert.isChecked())) {
                        Toast.makeText(MainPage.this, "You need to choose a type of meal!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mStarter.isChecked()) {
                        mealType = "Starter";
                    }

                    if (mMain.isChecked()) {
                        mealType = "Main";
                    }

                    if (mDesert.isChecked()) {
                        mealType = "Desert";
                    }

                    for (Integer x = 0; x < separated.length; x++) {
                        String[] separated1 = separated[x].split(":");
                        for (Integer y = 0; y < separated1.length; y++) {
                            separated1[y] = separated1[y].trim(); //trims each ingredient and quantity to eliminate spaces at start and end
                            if ((y % 2) == 0) {//every ingredient
                                if (separated1[y].matches(".*\\d.*")) {
                                    Toast.makeText(MainPage.this, "You can't have a number in the ingredient name!", Toast.LENGTH_SHORT).show();
                                    return;
                                    //maybe loose the s of each ingredient to help with the merging of ingredients and their quantaties
                                }
                            } else { //every quantity
                                if (separated1[y].charAt(0) == '0') {
                                    Toast.makeText(MainPage.this, "You can't start a quantity with 0! ", Toast.LENGTH_SHORT).show(); //if quantity starts with 0
                                    return;
                                }
                                if (!(separated1[y].matches(".*\\d.*"))) {
                                    Toast.makeText(MainPage.this, "Make sure you add a quantity!", Toast.LENGTH_SHORT).show(); //if it doesn't include a number, prompt a number
                                    return;
                                }
                            }
                            //Toast.makeText(MainPage.this, separated1[y], Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!tester.contains(":")) {
                        Toast.makeText(MainPage.this, "The ingredients need to be in the format INGREDIENT:QUANTITY", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!mMealName.getText().toString().isEmpty() && !mIngredient.getText().toString().isEmpty()) {
                        myDb.insertDataMeals(mMealName.getText().toString(), mIngredient.getText().toString(), mealType);
                        Toast.makeText(MainPage.this, "Meal added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); //closes the inflated window
                        loadActivity();
                    }
                }
            });
        }
    }
    //method for capturing swipe gestures
    private class LearnGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!rotation.equals(1) && !rotation.equals(3)){ //if the orientation is portrait, implement left/right gestures
                if (e1.getX() > e2.getX()) {
                    intent = new Intent(MainPage.this, IngredientsPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }
            if(rotation.equals(1) || rotation.equals(3)){ //if the orientation is landscape, implement up/down gestures
                if (e1.getY() > e2.getY()) {
                    intent = new Intent(MainPage.this, IngredientsPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }
            return true;
        }
    }
    //the following method is for the search bar results
    public void generateListContent(String searchQuery) {
        String mealType;
        if (searchQuery!= null && !searchQuery.equals("")) {
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("Desert")) {
                cursor = myDb.getAllMealsSearch(searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                    mealsNameUser.add(cursor.getString(1));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("null") && mealsSelected[2].equals("null")) {
                cursor = myDb.getAllMealsSearch(searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                    mealsNameUser.add(cursor.getString(1));
                }
            }

            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("null") && mealsSelected[2].equals("null")) {
                mealType = "Starter";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("null")) {
                mealType = "StarterMain";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("null") && mealsSelected[2].equals("Desert")) {
                mealType = "StarterDesert";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("null")) {
                mealType = "Main";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("Desert")) {
                mealType = "MainDesert";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("null") && mealsSelected[2].equals("Desert")) {
                mealType = "Desert";
                cursor = myDb.getMealTypeSearch(mealType,searchQuery);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
        }
        //the following else statement is for loading of the listview if the searchView has not been utilised
        else{
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("Desert")) {
                cursor = myDb.getAMeal();
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                    mealsNameUser.add(cursor.getString(1));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("null") && mealsSelected[2].equals("null")) {
                cursor = myDb.getAMeal();
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                    mealsNameUser.add(cursor.getString(1));
                }
            }

            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("null") && mealsSelected[2].equals("null")) {
                mealType = "Starter";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("null")) {
                mealType = "StarterMain";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("Starter") && mealsSelected[1].equals("null") && mealsSelected[2].equals("Desert")) {
                mealType = "StarterDesert";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("null")) {
                mealType = "Main";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("Main") && mealsSelected[2].equals("Desert")) {
                mealType = "MainDesert";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            if (mealsSelected[0].equals("null") && mealsSelected[1].equals("null") && mealsSelected[2].equals("Desert")) {
                mealType = "Desert";
                cursor = myDb.getMealType(mealType);
                while (cursor.moveToNext()) {
                    meals.add(cursor.getString(1));
                    ingredients.add(cursor.getString(2));
                    types.add(cursor.getString(3));
                }
            }
            return;
        }
    }
    //listViewAdapter class
    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects1;
        private List<String> mObjects2;
        private List<String> mObjects3;

        private MyListAdaper(Context context, int resource, List<String> objects1, List<String> objects2, List<String> objects3) {
            super(context, resource, objects1);
            mObjects1 = objects1;
            mObjects2 = objects2;
            mObjects3 = objects3;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title1 = (TextView) convertView.findViewById(R.id.mealNameList);
                viewHolder.title2 = (TextView) convertView.findViewById(R.id.mealIngredientsList);
                viewHolder.title3 = (TextView) convertView.findViewById(R.id.mealType);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor = myDb.getAMealName(mObjects1.get(position));
                    if (cursor.moveToFirst()) {
                        if (!myDb.CheckIsDataAlreadyInDBorNotMeals("trolley_table", mObjects1.get(position))) {
                            Toast.makeText(MainPage.this, mObjects1.get(position) + " - meal inserted", Toast.LENGTH_SHORT).show();
                            myDb.insertDataTrolley(mObjects1.get(position));
                        } else {
                            Cursor trolleyCursor = myDb.getAMealTrolley(mObjects1.get(position));
                            trolleyCursor.moveToFirst();
                            Integer amount = trolleyCursor.getInt(4);
                            amount = amount + 1;
                            myDb.increaseAmountTrolley(mObjects1.get(position), amount);
                            Toast.makeText(MainPage.this, "The meal - " + mObjects1.get(position) + " - already exists in trolley, quantity was increased by one!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            String newMeasurements = mObjects2.get(position).toLowerCase();
            String colorStart;
            String colorEnd = "</font>";
            String[] splitter = newMeasurements.split(",");

            //the following loops are used to change the color of ingredients within the meals based off of the ingredients in the ingredients list

            for (String splitters : splitter) {
                String[] splitter2 = splitters.split(":");
                for (String splitters2 : splitter2) {
                    for (Integer t = 0; t < ingredientNames.size(); t++) {

                        if (ingredientNames.get(t).equals(splitters2)) {

                            String one = newMeasurements.substring(0, newMeasurements.indexOf(ingredientNames.get(t)));
                            String two = newMeasurements.substring(newMeasurements.indexOf(ingredientNames.get(t)), newMeasurements.indexOf(ingredientNames.get(t)) + splitters.length());
                            String three = newMeasurements.substring(newMeasurements.indexOf(ingredientNames.get(t)) + splitters.length(), newMeasurements.length());

                            Integer index = two.indexOf(":");
                            String quantityEqualsChecker = two.substring(index+1,two.length());
                            colorStart = "<font color=#FF8D00>"; //if some of an ingredient (but not all) is in the ingredients list set its color to orange

                            Integer quantity = Integer.parseInt(quantityEqualsChecker.replaceAll("\\D+",""));       //the measurement of the ingredient in the meal
                            String measurement = quantityEqualsChecker.replaceAll("[^A-Za-z]","");                  //the quantity of the ingredient in the meal

                            if (measurement.toLowerCase().equals(quantities.get(t))) {//if the quantities are the same
                                Integer ingredientQuant = Integer.parseInt(measurements.get(t));
                                Integer calculator = quantity - ingredientQuant;
                                if (calculator <= 0) {
                                    calculator = 0;
                                    colorStart = "<font color=#2F56E9>"; //if all of an ingredient in a meal is in the ingredients list, set its color to blue
                                }
                                two = two.replace(quantity.toString(), calculator.toString());
                                splitters = splitters.substring(0, splitters.length() - 1);
                            }
                            newMeasurements = "";
                            newMeasurements = newMeasurements.concat(one).concat(colorStart).concat(two).concat(colorEnd).concat(three);
                        }
                    }
                }
            }
            newMeasurements = newMeasurements.replace(",","<br/>"); //each ingredient is on a new line
            mainViewholder.title1.setText(mObjects1.get(position));
            mainViewholder.title2.setText(Html.fromHtml(newMeasurements));
            mainViewholder.title3.setText(mObjects3.get(position));
            return convertView;
        }
    }
    public class ViewHolder {

        TextView title1;
        TextView title2;
        TextView title3;
        Button button;
    }
}