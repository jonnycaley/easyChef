//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;

public class IngredientsPage extends AppCompatActivity {

    ArrayAdapter adapter;
    DatabaseHelper myDb;
    ListView listview;
    ArrayList<String> ingredientNames = new ArrayList<String>();
    ArrayList<String> quantities = new ArrayList<String>();
    ArrayList<String> measurements = new ArrayList<String>();
    ArrayList<String> populateView = new ArrayList<String>(); //this arraylist will be used to populate the listview and NOTHING ELSE
    String[] mealsSelected = new String[3]; //the array that carries the meal types selected
    Spinner spinnerDropdown;
    Cursor res;
    Cursor res1;
    CheckBox checkBox;
    Intent intent;
    View mView;
    AlertDialog dialog;
    Button addNewIngredient;
    Button mSubmitNewIngredient;
    EditText mIngredient;
    EditText mQuantity;
    Spinner mMeasurement;
    ArrayAdapter adapter1;
    String ingredient;
    String quantity;
    String measurement;
    AlertDialog.Builder mBuilder;
    Button doReset;
    Button dontReset;
    ImageButton toWelcomePage;
    Integer rotation;

    GestureDetectorCompat gestureObject;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_page);

        if (savedInstanceState != null) {
            mealsSelected = savedInstanceState.getStringArray("mealsSelected");
            if(mealsSelected == null){
                mealsSelected = new String[3];
            }
        }
        loadActivity();
    }

    public void loadActivity() {

        Display display = ((WindowManager) getSystemService(IngredientsPage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        if(rotation.equals(1) || rotation.equals(3)) {
            setContentView(R.layout.ingredients_page_landscape);
        }
        myDb = new DatabaseHelper(this); //

        res = myDb.getAllData();

        populateView.clear();
        ingredientNames.clear();
        quantities.clear();
        measurements.clear();

        mealsSelected = getIntent().getStringArrayExtra("mealsSelected");

        listview = (ListView) findViewById(R.id.IngredientsList);

        while (res.moveToNext()) {
            populateView.add(0, res.getString(0) + " " + res.getString(1) + res.getString(2));
            ingredientNames.add(0, res.getString(0));
            quantities.add(0, res.getString(1));
            measurements.add(0, res.getString(2));
            listview.setAdapter(adapter);
        }

        textsizer();

        adapter = new ArrayAdapter<String>(IngredientsPage.this, R.layout.ingredients_list, populateView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ItemList());

        //synchronising the checkboxes

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

        gestureObject = new GestureDetectorCompat(this, new LearnGesture());

        toWelcomePage = (ImageButton) findViewById(R.id.firstButton);
        toWelcomePage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(IngredientsPage.this, MainPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toIngredientsPage = (ImageButton) findViewById(R.id.secondButton);
        toIngredientsPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                intent = new Intent(IngredientsPage.this, IngredientsPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toTrolleyPage = (ImageButton) findViewById(R.id.thirdButton);
        toTrolleyPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientsPage.this, TrolleyPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toManualPage = (ImageButton) findViewById(R.id.fourthButton);
        toManualPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IngredientsPage.this, UserManualPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });


        addNewIngredient = (Button) findViewById(R.id.addNewIngredient);
        addNewIngredient.setOnClickListener(new addIngredient());
        //reset list button

        Button resetListButton = (Button) findViewById(R.id.resetList);
        resetListButton.setOnClickListener(new resetList());

    }

    private void textsizer() {
        Button addNewIngredient = (Button) findViewById(R.id.addNewIngredient);
        Button resetList = (Button) findViewById(R.id.resetList);
        CheckBox checkBoxStarter1 = (CheckBox) findViewById(R.id.checkBoxStarter1);
        CheckBox checkBoxMain2 = (CheckBox) findViewById(R.id.checkBoxMain2);
        CheckBox checkBoxDesert3 = (CheckBox) findViewById(R.id.checkBoxDesert3);

        Integer screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        Integer textSize = 1;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                textSize = 24;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    addNewIngredient.setTextSize(textSize+6);
                    resetList.setTextSize(textSize+12);
                    checkBoxStarter1.setTextSize(textSize+4);
                    checkBoxMain2.setTextSize(textSize+4);
                    checkBoxDesert3.setTextSize(textSize+4);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    addNewIngredient.setTextSize(textSize+6);
                    resetList.setTextSize(textSize+14);
                    checkBoxStarter1.setTextSize(textSize+10);
                    checkBoxMain2.setTextSize(textSize+10);
                    checkBoxDesert3.setTextSize(textSize+10);
                    break;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                textSize = 18;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    addNewIngredient.setTextSize(textSize+6);
                    resetList.setTextSize(textSize+12);
                    checkBoxStarter1.setTextSize(textSize+4);
                    checkBoxMain2.setTextSize(textSize+4);
                    checkBoxDesert3.setTextSize(textSize+4);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    addNewIngredient.setTextSize(textSize+6);
                    resetList.setTextSize(textSize+14);
                    checkBoxStarter1.setTextSize(textSize+6);
                    checkBoxMain2.setTextSize(textSize+6);
                    checkBoxDesert3.setTextSize(textSize+6);
                    break;
                }
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                textSize = 14;
                break;
            default:
                textSize = 15;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.ingredients_page_landscape);
            loadActivity();
        } else {
            setContentView(R.layout.ingredients_page);
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

    //the following allows the user to swipe with their finger to navigate throughout the application instead of using the icons

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class resetList implements AdapterView.OnClickListener{
        @Override
        public void onClick(View view) {
            mView = getLayoutInflater().inflate(R.layout.reset_list, null);
            mBuilder = new AlertDialog.Builder(IngredientsPage.this);
            mBuilder.setView(mView);
            dialog = mBuilder.show();

            doReset = (Button) mView.findViewById(R.id.doReset);
            doReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDb.deleteAll();
                    loadActivity();
                    dialog.dismiss();
                }
            });
            dontReset = (Button) mView.findViewById(R.id.dontReset);
            dontReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!rotation.equals(1) && !rotation.equals(3)) { //if the orientation is portrait, implement left/right gestures
                if (e1.getX() > e2.getX()) {
                    Intent intent = new Intent(IngredientsPage.this, TrolleyPage.class);
                    String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                } else if (e1.getX() < e2.getX()) {
                    Intent intent = new Intent(IngredientsPage.this, MainPage.class);
                    String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }

            if(rotation.equals(1) || rotation.equals(3)){ //if the orientation is landscape, implement up/down gestures
                if (e1.getY() > e2.getY()) {
                    intent = new Intent(IngredientsPage.this, TrolleyPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
                if (e1.getY() < e2.getY()) {
                    intent = new Intent(IngredientsPage.this, MainPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }

            return true;
        }
    }


    public void onCheckboxClicked(View view) {
        String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
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
    }

    class addIngredient implements AdapterView.OnClickListener{
        @Override
        public void onClick(View view) {

            mView = getLayoutInflater().inflate(R.layout.add_ingredient, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(IngredientsPage.this);
            mBuilder.setView(mView);
            dialog = mBuilder.show();

            mSubmitNewIngredient = (Button) mView.findViewById(R.id.addIngredientFinal);
            mIngredient = (EditText) mView.findViewById(R.id.ingredientChanger);
            mQuantity = (EditText) mView.findViewById(R.id.quantityChanger);
            mMeasurement = (Spinner) mView.findViewById(R.id.Measurements);

            adapter1 = ArrayAdapter.createFromResource(IngredientsPage.this, R.array.spinner_options, android.R.layout.simple_spinner_dropdown_item);
            spinnerDropdown = (Spinner) mView.findViewById(R.id.Measurements);
            spinnerDropdown.setAdapter(adapter1);

            mSubmitNewIngredient.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ingredient = mIngredient.getText().toString().trim();
                    quantity = mQuantity.getText().toString().trim();
                    measurement = mMeasurement.getSelectedItem().toString();


                    if (myDb.CheckIsDataAlreadyInDBorNot("ingredients_table", ingredient.toLowerCase())) {
                        Toast.makeText(IngredientsPage.this, "Your list already contains that ingredient, click on it to edit it!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                WelcomePage.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        return;
                    }
                    if (ingredient.matches(".*\\d+.*")) {
                        Toast.makeText(IngredientsPage.this, "An ingredient can't contain numbers!", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                WelcomePage.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        return;
                    }
                    if (!(quantity.matches(".*\\d.*"))) {
                        Toast.makeText(IngredientsPage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        boolean isInserted = myDb.insertData(mIngredient.getText().toString().trim().toLowerCase(), Integer.parseInt(mQuantity.getText().toString().trim()), mMeasurement.getSelectedItem().toString());
                        if (isInserted) {
                            Toast.makeText(IngredientsPage.this, "Ingredient added!", Toast.LENGTH_LONG).show();
                            loadActivity();
                            dialog.dismiss();
                        }

                    }
                }
            });
        }
    }

    class ItemList implements AdapterView.OnItemClickListener {

        EditText ingredientChanger, quantityChanger;
        Spinner spinnerDropdown;
        Button editIngredient;
        Button deleteingredient;
        Integer b;
        String one;
        String two;
        String three;

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)

        {
            View mView = getLayoutInflater().inflate(R.layout.edit_ingredient, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(IngredientsPage.this);
            mBuilder.setView(mView);
            dialog = mBuilder.show(); //no idea what this does

            ingredientChanger = (EditText) mView.findViewById(R.id.ingredientChanger);
            quantityChanger = (EditText) mView.findViewById(R.id.quantityChanger);
            spinnerDropdown = (Spinner) mView.findViewById(R.id.Measurements);
            editIngredient = (Button) mView.findViewById(R.id.editIngredient);
            deleteingredient = (Button) mView.findViewById(R.id.deleteIngredient);

            b = position;

            res1 = myDb.getName(ingredientNames.get(b));
            if (res1 != null && res1.moveToNext()) {
                one = res1.getString(0);
                two = res1.getString(1);
                three = res1.getString(2);

                ingredientChanger.setText(one);

                quantityChanger.setText(two);

                adapter = ArrayAdapter.createFromResource(IngredientsPage.this, R.array.spinner_options, android.R.layout.simple_spinner_dropdown_item);
                spinnerDropdown.setAdapter(adapter);
                for (Integer x = 0; x < spinnerDropdown.getCount(); x++) {
                    if ((spinnerDropdown.getItemAtPosition(x).toString()).matches(three)) {
                        spinnerDropdown.setSelection(x);
                    }
                }
            }
            editIngredient.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View view) {

                    if (ingredientChanger.getText().toString().matches("")) {
                        Toast.makeText(IngredientsPage.this, "You need to enter an ingredient name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!(quantityChanger.getText().toString().matches(".*\\d.*"))) {
                        Toast.makeText(IngredientsPage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((ingredientChanger.getText().toString().matches(".*\\d.*"))) {
                        Toast.makeText(IngredientsPage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((ingredientChanger.equals(one)) && (quantityChanger.equals(two)) && spinnerDropdown.getSelectedItem().toString().equals(three)) {
                        Toast.makeText(IngredientsPage.this, "You need to change the ingredient to update it!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        res1 = myDb.getName(ingredientNames.get(b));
                        if (!(ingredientChanger.equals(one)) && !(quantityChanger.equals(two))) {
                            myDb.updateData(one, ingredientChanger.getText().toString().trim(), quantityChanger.getText().toString().trim(), spinnerDropdown.getSelectedItem().toString());
                            Toast.makeText(IngredientsPage.this, "Ingredient updated", Toast.LENGTH_SHORT).show();

                            loadActivity();
                            dialog.dismiss();

                        }

                    }
                }
            });

            deleteingredient.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View view) {
                    myDb.deleteData(one);

                    Toast.makeText(IngredientsPage.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();

                    res = myDb.getAllData();
                    populateView.clear();
                    ingredientNames.clear();
                    quantities.clear();
                    measurements.clear();
                    adapter = new ArrayAdapter<String>(IngredientsPage.this, android.R.layout.simple_list_item_1, populateView);

                    while (res.moveToNext()) {
                        populateView.add(0, res.getString(0) + " " + res.getString(1) + res.getString(2));
                        ingredientNames.add(0, res.getString(0));
                        quantities.add(0, res.getString(1));
                        measurements.add(0, res.getString(2));
                    }

                    listview.setAdapter(adapter);

                    String IngredientS = "";
                    String QuantitieS = "";
                    String MeasurementS = "";
                    //because the arrays will all be of the same size we can apply the functions to one loop
                    for (Integer x = 0; x < ingredientNames.size(); x++) {
                        IngredientS = IngredientS.concat(ingredientNames.get(x));
                        QuantitieS = QuantitieS.concat(quantities.get(x));
                        MeasurementS = MeasurementS.concat(measurements.get(x));
                    }
                    dialog.dismiss();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        //Get and store ShareActionProvider
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "YOUR_TEXT");
            shareIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(shareIntent);
        }
        //True to display menu
        return true;
    }

    //Update the share intent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_share) {
            doShare();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doShare() {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I'm using easyChef");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I used easyChef to create delicious meals using spare ingredients!");

        startActivity(Intent.createChooser(shareIntent, "Share this to everyone!"));
    }
}