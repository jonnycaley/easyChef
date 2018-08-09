//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

public class WelcomePage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //initialise local variables
    DatabaseHelper myDb;
    EditText mIngredient, mQuantity;
    Spinner mMeasurement;
    Button mSubmitNewIngredient;
    Button toMainPage;
    ListView listview;
    AlertDialog dialog;
    ShareActionProvider mShareActionProvider;
    ArrayList<String> ingredientNames = new ArrayList<String>();
    ArrayList<String> quantities = new ArrayList<String>();
    ArrayList<String> measurements = new ArrayList<String>();
    ArrayList<String> populateView = new ArrayList<String>(); //this arraylist will be used to populate the listview and NOTHING ELSE
    String[] mealsSelected = new String[3]; //the array that carries the meal types selected
    Spinner spinnerDropdown;
    ArrayAdapter adapter;
    ArrayAdapter adapterHour;
    ArrayAdapter adapterMinute;
    Cursor res;
    Cursor res1;
    Spinner spinnerHour;
    Spinner spinnerMinute;
    Integer hour;
    Integer minute;
    String PREFS_NAME;
    SharedPreferences settings;
    Integer rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialise layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        PREFS_NAME = "MyPrefsFile";

        settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("first_time", true)) { //the app is being launched for first time, ask them about their meal time...

            //layout inflated to get the meal time of the user, which is then used to set a daily notification 30 minutes before their meal time to optimise usage
            View Vieww = getLayoutInflater().inflate(R.layout.notification_time_getter, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomePage.this);
            mBuilder.setView(Vieww);
            dialog = mBuilder.show();

            spinnerHour = (Spinner) Vieww.findViewById(R.id.hour);
            spinnerMinute = (Spinner) Vieww.findViewById(R.id.minute);
            //adapters for spinners
            adapterHour = ArrayAdapter.createFromResource(WelcomePage.this, R.array.spinner_hours, android.R.layout.simple_spinner_dropdown_item);
            spinnerHour.setAdapter(adapterHour);

            adapterMinute = ArrayAdapter.createFromResource(WelcomePage.this, R.array.spinner_minutes, android.R.layout.simple_spinner_dropdown_item);
            spinnerMinute.setAdapter(adapterMinute);

            //submit button listener
            Button submit = (Button) Vieww.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hour = Integer.parseInt(spinnerHour.getSelectedItem().toString());
                    minute = Integer.parseInt(spinnerMinute.getSelectedItem().toString());
                    dialog.dismiss();
                    Toast.makeText(WelcomePage.this, "Thank You! :)", Toast.LENGTH_SHORT).show();
                    Calendar calendar = Calendar.getInstance();
                    if(minute <= 29){
                        minute = 30 + minute;
                        if (hour == 00){
                            hour = 23;
                        }
                        else{
                            hour = hour-1;
                        }
                    }
                    else if (minute >= 30){
                        minute = minute-30;
                    }
                    // notifications are set daily, 30 minutes before the users provided meal time
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.SECOND,1);
                    Intent intent = new Intent (getApplicationContext(), Notification_reciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmManager.INTERVAL_DAY,pendingIntent);
                }
            });
            // record the fact that the app has been started once before to ensure the user does not have to enter their meal time information every time they reset the app of the final page
            settings.edit().putBoolean("first_time", false).commit();
        }

        if (savedInstanceState != null) {
            mealsSelected = savedInstanceState.getStringArray("mealsSelected");
            if(mealsSelected == null){
                mealsSelected = new String[3];
            }
        }
        if (mealsSelected[0] == null){
            mealsSelected[0] = "null";
        }
        if (mealsSelected[1] == null){
            mealsSelected[1] = "null";
        }
        if (mealsSelected[2] == null){
            mealsSelected[2] = "null";
        }
        loadActivity();
    }

    public void loadActivity() {
        Display display = ((WindowManager) getSystemService(WelcomePage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        if(rotation.equals(1) || rotation.equals(3)){
            setContentView(R.layout.welcome_page_landscape);
        }
        textSizer();
        myDb = new DatabaseHelper(this);
        res = myDb.getAllData();

        populateView.clear();
        ingredientNames.clear();
        quantities.clear();
        measurements.clear();

        mSubmitNewIngredient = (Button) findViewById(R.id.submitNewIngredient);
        mIngredient = (EditText) findViewById(R.id.ingredientName);
        mQuantity = (EditText) findViewById(R.id.Quantity);
        mMeasurement = (Spinner) findViewById(R.id.Measurements);

        adapter = ArrayAdapter.createFromResource(WelcomePage.this, R.array.spinner_options, android.R.layout.simple_spinner_dropdown_item);
        spinnerDropdown = (Spinner) findViewById(R.id.Measurements);
        spinnerDropdown.setAdapter(adapter);
        spinnerDropdown.setOnItemSelectedListener(WelcomePage.this);

        listview = (ListView) findViewById(R.id.IngredientsList); //initialise listView

        adapter = new ArrayAdapter<String>(WelcomePage.this, R.layout.ingredients_list, populateView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ItemList()); //onClickListener for listView

        for(Integer k = 0 ; k < mealsSelected.length ; k++ ){
            if((!(mealsSelected[k] == "null")) && (k == 0)){
                CheckBox starterBox = (CheckBox) findViewById(R.id.checkBoxStarter);
                starterBox.setChecked(true);
            }
            if((!(mealsSelected[k] == "null")) && (k == 1)){
                CheckBox mainBox = (CheckBox) findViewById(R.id.checkBoxMain);
                mainBox.setChecked(true);
            }
            if((!(mealsSelected[k] == "null")) && (k == 2)){
                CheckBox DesBox = (CheckBox) findViewById(R.id.checkBoxDesert);
                DesBox.setChecked(true);
            }
        }

        while (res.moveToNext()) {
            populateView.add(0, res.getString(0) + " " + res.getString(1) + res.getString(2));
            ingredientNames.add(0, res.getString(0));
            quantities.add(0, res.getString(1));
            measurements.add(0, res.getString(2));
            listview.setAdapter(adapter);
        }

        mSubmitNewIngredient.setOnClickListener(new itemSubmit()); //when a new ingredient is added to the ingredients list (method)

        //instructions for the button to the main page
        toMainPage = (Button) findViewById(R.id.welcome_page_button);
        toMainPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomePage.this, MainPage.class);
                //sends the following information to MainPage
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });
    }

    private void textSizer() {

        TextView intro = (TextView) findViewById(R.id.intro);
        CheckBox checkBoxStarter = (CheckBox) findViewById(R.id.checkBoxStarter);
        CheckBox checkBoxMain = (CheckBox) findViewById(R.id.checkBoxMain);
        CheckBox checkBoxDesert = (CheckBox) findViewById(R.id.checkBoxDesert);
        Button submitNewIngredient = (Button) findViewById(R.id.submitNewIngredient);
        EditText ingredientName = (EditText) findViewById(R.id.ingredientName);
        EditText Quantity = (EditText) findViewById(R.id.Quantity);
        Button welcome_page_button = (Button) findViewById(R.id.welcome_page_button);

        Integer screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        Integer textSize;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                textSize = 24;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    intro.setTextSize(textSize);
                    checkBoxStarter.setTextSize(textSize+4);
                    checkBoxMain.setTextSize(textSize+4);
                    checkBoxDesert.setTextSize(textSize+4);
                    submitNewIngredient.setTextSize(textSize+6);
                    ingredientName.setTextSize(textSize+4);
                    Quantity.setTextSize(textSize+4);
                    welcome_page_button.setTextSize(textSize+6);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    TextView Meas = (TextView) findViewById(R.id.Meas);
                    Meas.setTextSize(textSize+4);
                    intro.setTextSize(textSize);
                    checkBoxStarter.setTextSize(textSize+2);
                    checkBoxMain.setTextSize(textSize+2);
                    checkBoxDesert.setTextSize(textSize+2);
                    submitNewIngredient.setTextSize(textSize+2);
                    ingredientName.setTextSize(textSize+2);
                    Quantity.setTextSize(textSize+4);
                    welcome_page_button.setTextSize(textSize+6);
                    break;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                textSize = 18;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    intro.setTextSize(textSize);
                    checkBoxStarter.setTextSize(textSize+4);
                    checkBoxMain.setTextSize(textSize+4);
                    checkBoxDesert.setTextSize(textSize+4);
                    submitNewIngredient.setTextSize(textSize+6);
                    ingredientName.setTextSize(textSize+4);
                    Quantity.setTextSize(textSize+4);
                    welcome_page_button.setTextSize(textSize);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    TextView Meas = (TextView) findViewById(R.id.Meas);
                    Meas.setTextSize(textSize+4);
                    intro.setTextSize(textSize);
                    checkBoxStarter.setTextSize(textSize+2);
                    checkBoxMain.setTextSize(textSize+2);
                    checkBoxDesert.setTextSize(textSize+2);
                    submitNewIngredient.setTextSize(textSize+2);
                    ingredientName.setTextSize(textSize+2);
                    Quantity.setTextSize(textSize+4);
                    welcome_page_button.setTextSize(textSize+6);
                    break;
                }
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                textSize = 14;
                break;
            default:
                textSize = 15;
        }
    }

    class itemSubmit implements AdapterView.OnClickListener { //to add an ingredient into the ingredients list
        public void onClick(View view) {

            String ingredient = mIngredient.getText().toString().trim();
            String quantity = mQuantity.getText().toString().trim();
            String measurement = mMeasurement.getSelectedItem().toString();
            if (ingredient.matches("reset")) {
                myDb.deleteAll();
                loadActivity();
                return;
            }
            if (myDb.CheckIsDataAlreadyInDBorNot("ingredients_table", ingredient.toLowerCase())) {
                Toast.makeText(WelcomePage.this, "You already added that ingredient, click on it to edit it!", Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        WelcomePage.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return;
            }
            if (ingredient.matches(".*\\d+.*")) {
                Toast.makeText(WelcomePage.this, "An ingredient can't contain numbers!", Toast.LENGTH_SHORT).show();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        WelcomePage.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return;
            }
            if (!(quantity.matches(".*\\d.*"))) {
                Toast.makeText(WelcomePage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                boolean isInserted = myDb.insertData(mIngredient.getText().toString().trim().toLowerCase(), Integer.parseInt(mQuantity.getText().toString().trim()), mMeasurement.getSelectedItem().toString());
                if (isInserted) {
                    Toast.makeText(WelcomePage.this, "Ingredient added", Toast.LENGTH_SHORT).show();
                    loadActivity();
                }
            }
        }
    }

    //when the orientation of the screen changes the method onConfigurationChanged is invoked
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.welcome_page_landscape);
            loadActivity();
        } else {
            setContentView(R.layout.welcome_page);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView spinnerDialogText = (TextView) view;
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    //the class that changes the array of meal types depending on the boxes checked
    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkBoxStarter:
                if (checked) {
                    mealsSelected[0] = "Starter";
                } else {
                    mealsSelected[0] = "null";
                }
                break;

            case R.id.checkBoxMain:
                if (checked) {
                    mealsSelected[1] = "Main";
                } else {
                    mealsSelected[1] = "null";
                }
                break;
            case R.id.checkBoxDesert:
                if (checked) {
                    mealsSelected[2] = "Desert";
                } else {
                    mealsSelected[2] = "null";
                }
                break;
        }
    }

    //the class ItemList is used for editing and deleting ingredients within the listview

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
            final View mView = getLayoutInflater().inflate(R.layout.edit_ingredient, null);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(WelcomePage.this);
            mBuilder.setView(mView);
            dialog = mBuilder.show();

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

                adapter = ArrayAdapter.createFromResource(WelcomePage.this, R.array.spinner_options, android.R.layout.simple_spinner_dropdown_item);
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
                        Toast.makeText(WelcomePage.this, "You need to enter an ingredient name!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!(quantityChanger.getText().toString().matches(".*\\d.*"))) {
                        Toast.makeText(WelcomePage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((ingredientChanger.getText().toString().matches(".*\\d.*"))) {
                        Toast.makeText(WelcomePage.this, "The quantity must only contain numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if ((ingredientChanger.equals(one)) && (quantityChanger.equals(two)) && spinnerDropdown.getSelectedItem().toString().equals(three)) {
                        Toast.makeText(WelcomePage.this, "You need to change the ingredient to update it!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        res1 = myDb.getName(ingredientNames.get(b));
                        if (!(ingredientChanger.equals(one)) && !(quantityChanger.equals(two))) {
                            myDb.updateData(one, ingredientChanger.getText().toString().trim(), quantityChanger.getText().toString().trim(), spinnerDropdown.getSelectedItem().toString());
                            Toast.makeText(WelcomePage.this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                            loadActivity();
                            dialog.dismiss();
                            InputMethodManager imm = (InputMethodManager) getSystemService(
                                    WelcomePage.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }

                    }
                }
            });

            deleteingredient.setOnClickListener(new Button.OnClickListener() {

                public void onClick(View view) {
                    myDb.deleteData(one);
                    Toast.makeText(WelcomePage.this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    loadActivity();
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        //Fetch and store ShareActionProvider
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);

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

    //method used for shareActionProvider
    private void doShare() {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I'm using easyChef");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I used easyChef to create delicious meals using spare ingredients!");
        startActivity(Intent.createChooser(shareIntent, "Share this to everyone!"));
    }
}