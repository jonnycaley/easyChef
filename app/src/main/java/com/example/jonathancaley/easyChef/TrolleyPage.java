//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ShareActionProvider;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

public class TrolleyPage extends AppCompatActivity {
    //initialise variables
    private GestureDetectorCompat gestureObject;
    private ShareActionProvider mShareActionProvider;
    DatabaseHelper myDb;
    String[] mealsSelected;
    Cursor cursor;
    ArrayList<String> meals = new ArrayList<String>();
    ArrayList<String> ingredients = new ArrayList<String>();
    ArrayList<String> types = new ArrayList<String>();
    ArrayList<Integer> amounts = new ArrayList<Integer>();
    ListView lv;
    AlertDialog dialog;
    String MEAL;
    String INGREDIENTS;
    String TYPE;
    String QUANTITY;
    Cursor calc;
    ArrayList<String> ingredientNames = new ArrayList<String>();
    ArrayList<String> quantities = new ArrayList<String>();
    ArrayList<String> measurements = new ArrayList<String>();
    ArrayList<String> measurementsHolder = new ArrayList<String>();
    ArrayList<String> trolleyListFinal = new ArrayList<String>();
    ArrayList<String> trolleyListAmountsFinal = new ArrayList<String>();
    ArrayList<String> ingredientsFinal = new ArrayList<String>();
    ArrayList<String> trolleyListSplitter;
    String newMeasurements;
    ArrayList<String> trolleyListFinalOne;
    String measurementOnlyOne;
    String measurementOnlyTwo;
    LinearLayout verticalLayout;
    LinearLayout checking;
    TextView finish;
    Integer rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trolley_page);
        myDb = new DatabaseHelper(this);
        if (savedInstanceState != null) {
            mealsSelected = savedInstanceState.getStringArray("mealsSelected");
            if(mealsSelected == null){
                mealsSelected = new String[3];
            }
        }
        loadActivity();
    }

    public void loadActivity() {

        Display display = ((WindowManager) getSystemService(TrolleyPage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        if(rotation.equals(1) || rotation.equals(3)){
            setContentView(R.layout.trolley_page_landscape);
        }
        textsizer();

        lv = (ListView) findViewById(R.id.trolleyList);
        meals.clear();
        ingredients.clear();
        types.clear();
        amounts.clear();

        ingredientNames.clear();
        measurements.clear();
        quantities.clear();
        trolleyListFinal.clear();
        trolleyListAmountsFinal.clear();
        ingredientsFinal.clear();
        measurementsHolder.clear();

        calc = myDb.getAllData();

        while (calc.moveToNext()) {
            ingredientNames.add(0, calc.getString(0));
            measurements.add(0, calc.getString(1));
            quantities.add(0, calc.getString(2));
            measurementsHolder.add(0, calc.getString(1));
            ingredientsFinal.add(calc.getString(0) + calc.getString(1) + calc.getString(2));
        }
        generateListContent();
        //adapter for the trolley listView
        lv.setAdapter(new TrolleyPage.MyListAdaper(this, R.layout.list_view_custom_layout_trolley, meals, ingredients, types, amounts));
        //onitemclick method
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor2 = myDb.getAMealTrolley(meals.get(position));
                if (cursor2.moveToFirst()) {
                    View mView = getLayoutInflater().inflate(R.layout.edit_trolley_item, null);
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(TrolleyPage.this);
                    mBuilder.setView(mView);
                    dialog = mBuilder.show();

                    MEAL = cursor2.getString(1);
                    INGREDIENTS = cursor2.getString(2);
                    INGREDIENTS = INGREDIENTS.replace(",", "<br/>");
                    TYPE = cursor2.getString(3);
                    QUANTITY = cursor2.getString(4);

                    TextView mealName = (TextView) mView.findViewById(R.id.editTrolleyMeal);
                    mealName.setText(MEAL);
                    //if the user tries to edit a meal
                    mealName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(TrolleyPage.this, "If you want to edit the meal, use the meals page", Toast.LENGTH_SHORT).show();
                        }
                    });
                    TextView mealIngredients = (TextView) mView.findViewById(R.id.editTrolleyIngredients);
                    mealIngredients.setText(Html.fromHtml(INGREDIENTS));
                    //if the user tries to edit a meal
                    mealIngredients.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(TrolleyPage.this, "If you want to edit the meal, use the meals page", Toast.LENGTH_SHORT).show();
                        }
                    });
                    TextView mealType = (TextView) mView.findViewById(R.id.editTrolleyType);
                    mealType.setText(TYPE);
                    //if the user tries to edit a meal
                    mealType.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(TrolleyPage.this, "If you want to edit the meal, use the meals page", Toast.LENGTH_SHORT).show();
                        }
                    });
                    final EditText mealQuantity = (EditText) mView.findViewById(R.id.amountChanger);
                    mealQuantity.setText(QUANTITY);
                    Button editMeal = (Button) mView.findViewById(R.id.editQuantity);
                    //when the user submits the edit meal function
                    editMeal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mealQuantity.getText().toString() == QUANTITY) { //if the meal quantity is the same
                                Toast.makeText(TrolleyPage.this, "You didn't change the quantity!", Toast.LENGTH_SHORT).show();
                            }
                            if (mealQuantity.getText().toString().equals("0")) {
                                Toast.makeText(TrolleyPage.this, "If you want to delete the meal, use the DELETE button", Toast.LENGTH_SHORT).show();
                            } else {
                                myDb.increaseAmountTrolley(MEAL, Integer.parseInt(mealQuantity.getText().toString()));
                                Toast.makeText(TrolleyPage.this, MEAL + " quantity updated", Toast.LENGTH_SHORT).show();
                                loadActivity();
                                dialog.dismiss();
                            }
                        }
                    });
                    Button deleteMeal = (Button) mView.findViewById(R.id.deleteMeal);
                    //delete meal method
                    deleteMeal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDb.deleteTrolleyMeal(MEAL);
                            Toast.makeText(TrolleyPage.this, "The meal " + MEAL + " was deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadActivity();
                        }
                    });
                }
            }
        });
        mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());
        ImageButton toWelcomePage = (ImageButton) findViewById(R.id.firstButton);
        //different page listeners (sends meals selected information)
        toWelcomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrolleyPage.this, MainPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toIngredientsPage = (ImageButton) findViewById(R.id.secondButton);
        toIngredientsPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrolleyPage.this, IngredientsPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toTrolleyPage = (ImageButton) findViewById(R.id.thirdButton);
        toTrolleyPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrolleyPage.this, TrolleyPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toManualPage = (ImageButton) findViewById(R.id.fourthButton);
        toManualPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrolleyPage.this, UserManualPage.class);
                mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });
        verticalLayout = (LinearLayout) findViewById(R.id.verticalLayout);
        checking = (LinearLayout) findViewById(R.id.checking);
        finish = (TextView) findViewById(R.id.finishChecker);
        //dynamically changes the layout of the bottom button to confirm users choice to proceed to final page, at which they cannot navigate back from
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trolleyListSplitter = new ArrayList<String>();
                trolleyListFinalOne = new ArrayList<String>();
                measurementOnlyOne = "";
                measurementOnlyTwo = "";
                verticalLayout.removeView(finish);
                checking.setLayoutParams(new TableLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 0, 1f));

                TextView yes = (TextView) findViewById(R.id.yes);
                TextView no = (TextView) findViewById(R.id.no);
                //if "no" is clicked the layout is reset
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verticalLayout.addView(finish);
                        checking.setLayoutParams(new TableLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 0, 0));
                    }
                });
                //if "yes" is clicked the layout is proceeded to the final page, carrying information such as the ingredients the user has used, unused and remaining to complete the meals in the trolley list. All of which is formatted in HTML format
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (Integer j = 0; j < trolleyListFinal.size(); j++) {
                            for (Integer c = 0; c < Integer.parseInt(trolleyListAmountsFinal.get(j)); c++) {
                                trolleyListSplitter.add(trolleyListFinal.get(j)); //adds the ingredients of each meal to the array
                            }
                        }
                        for (Integer a = 0; a < trolleyListSplitter.size(); a++) { //for the ingredients list of each meal
                            String[] trolleyListSplitterColon = trolleyListSplitter.get(a).split(",");
                            for (Integer b = 0; b < trolleyListSplitterColon.length; b++) {
                                trolleyListFinalOne.add(trolleyListSplitterColon[b]);
                            }
                        }
                        for (Integer l = 0; l < trolleyListFinalOne.size() - 1; l++) {
                            for (Integer r = l + 1; r < trolleyListFinalOne.size(); r++) {

                                String first = trolleyListFinalOne.get(l).substring(0, trolleyListFinalOne.get(l).indexOf(":"));
                                String numberWMeasurementOne = trolleyListFinalOne.get(l).substring(trolleyListFinalOne.get(l).indexOf(":") + 1, trolleyListFinalOne.get(l).length());
                                String justNumberOne = numberWMeasurementOne.replaceAll("\\D+", ""); //removes the measurement to just get quantity
                                String measurementTestOne = numberWMeasurementOne.replaceAll("[^A-Za-z]", ""); //gets the quantity of the first ingredient

                                String second = trolleyListFinalOne.get(r).substring(0, trolleyListFinalOne.get(r).indexOf(":"));
                                String numberWMeasurementTwo = trolleyListFinalOne.get(r).substring(trolleyListFinalOne.get(r).indexOf(":") + 1, trolleyListFinalOne.get(r).length());
                                String justNumberTwo = numberWMeasurementTwo.replaceAll("\\D+", ""); //removes the measurement to just get quantity
                                String measurementTestTwo = numberWMeasurementTwo.replaceAll("[^A-Za-z]", ""); //gets the quantity of the second ingredient

                                if (first.toLowerCase().equals(second.toLowerCase()) && measurementTestOne.toLowerCase().equals(measurementTestTwo.toLowerCase())) {

                                    Integer one = Integer.parseInt(justNumberOne);

                                    Integer two = Integer.parseInt(justNumberTwo);

                                    Integer three = one + two;

                                    String replacer = first + ":" + three.toString() + measurementTestOne;

                                    trolleyListFinalOne.set(l, replacer);
                                    trolleyListFinalOne.remove(r.intValue());
                                    r--;
                                }
                            }
                        }

                        ArrayList<String> ingredientsCopy = new ArrayList<String>(ingredientsFinal); //copies of arrays used for calculations in the FinalPage method
                        ArrayList<String> trolleyIngredientsCopy = new ArrayList<String>(trolleyListFinalOne);; //copies of arrays used for calculations in the FinalPage method

                        for (Integer v = 0; v < trolleyListFinalOne.size(); v++) {
                            //from the final ingredients list created from ingredients needed from meals in trolley list
                            String editOne = trolleyListFinalOne.get(v).substring(0, trolleyListFinalOne.get(v).indexOf(trolleyListFinalOne.get(v).replaceAll("\\D+", ""))-1); //gets the name of the ingredient
                            String quantityGetterOne = trolleyListFinalOne.get(v).substring(trolleyListFinalOne.get(v).indexOf(trolleyListFinalOne.get(v).replaceAll("\\D+", "")), trolleyListFinalOne.get(v).length()); //gets the quantity and measurement (e.g. 100kg)

                            measurementOnlyOne = quantityGetterOne.replaceAll("[^A-Za-z]", ""); //gets the measurement (e.g. kg)
                            Integer numberOnlyOne = Integer.parseInt(quantityGetterOne.replaceAll("\\D+", "")); //gets the quantity of measurement (e.g. 100)

                            for (Integer w = 0; w < ingredientsFinal.size(); w++) {
                                //from the ingredients list
                                String editTwo = ingredientsFinal.get(w).substring(0, ingredientsFinal.get(w).indexOf(ingredientsFinal.get(w).replaceAll("\\D+", "")));
                                String quantityGetterTwo = ingredientsFinal.get(w).substring(ingredientsFinal.get(w).indexOf(ingredientsFinal.get(w).replaceAll("\\D+", "")), ingredientsFinal.get(w).length());

                                measurementOnlyTwo = quantityGetterTwo.replaceAll("[^A-Za-z]", ""); //gets the measurement (e.g. kg)
                                Integer numberOnlyTwo = Integer.parseInt(quantityGetterTwo.replaceAll("\\D+", "")); //gets the quantity of measurement (e.g. 100)

                                if (editOne.equals(editTwo) && measurementOnlyOne.toLowerCase().equals(measurementOnlyTwo.toLowerCase())) { //if the name of the ingredient is the same and the quantities (e.g. kg) are the same
                                    Integer finalInt = numberOnlyOne - numberOnlyTwo;
                                    if(finalInt <= 0){
                                        finalInt = 0;
                                    }
                                    String replacerFinal = editOne + ":" + finalInt.toString() + measurementOnlyOne;
                                    trolleyListFinalOne.set(v, replacerFinal);
                                }
                            }
                        }


                        String textViewSetter = "";
                        for(Integer f = 0 ; f < trolleyListFinalOne.size() ; f++){
                            if(!(trolleyListFinalOne.get(f).replaceAll("\\D+", "").equals("0"))){
                                textViewSetter = textViewSetter.concat(trolleyListFinalOne.get(f) + "<br/>");
                            }
                        }
                        Intent intent = new Intent(TrolleyPage.this, FinalPage.class);
                        //relevant information is sent to the final page to be displayed
                        intent.putExtra("textViewSetter", textViewSetter);
                        intent.putExtra("ingredientsHave", ingredientsFinal.toString());
                        intent.putExtra("trolleyListNeed", trolleyListFinalOne);
                        intent.putExtra("ingredientsCopy", ingredientsCopy);
                        intent.putExtra("trolleyIngredientsCopy", trolleyIngredientsCopy);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void textsizer() {
        TextView texttext = (TextView) findViewById(R.id.texttext);
        TextView finishChecker = (TextView) findViewById(R.id.finishChecker);
        TextView m = (TextView) findViewById(R.id.m);
        TextView i = (TextView) findViewById(R.id.i);
        TextView t = (TextView) findViewById(R.id.t);
        TextView qq = (TextView) findViewById(R.id.qq);
        TextView no = (TextView) findViewById(R.id.no);
        TextView yes = (TextView) findViewById(R.id.yes);
        TextView one = (TextView) findViewById(R.id.one);


        Integer screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        Integer textSize;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                textSize = 24;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    m.setTextSize(textSize);
                    i.setTextSize(textSize);
                    t.setTextSize(textSize);
                    qq.setTextSize(textSize);


                    texttext.setTextSize(textSize);
                    finishChecker.setTextSize(textSize+24);
                    no.setTextSize(textSize+20);
                    yes.setTextSize(textSize+20);
                    one.setTextSize(textSize-2);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    m.setTextSize(textSize-2);
                    i.setTextSize(textSize-2);
                    t.setTextSize(textSize-2);
                    qq.setTextSize(textSize-2);

                    texttext.setTextSize(textSize+6);
                    finishChecker.setTextSize(textSize+24);
                    no.setTextSize(textSize+14);
                    yes.setTextSize(textSize+14);
                    one.setTextSize(textSize);
                    break;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                textSize = 18;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    m.setTextSize(textSize);
                    i.setTextSize(textSize);
                    t.setTextSize(textSize);
                    qq.setTextSize(textSize);

                    texttext.setTextSize(textSize);
                    finishChecker.setTextSize(textSize+24);
                    //no.setTextSize(textSize+14);
                    //yes.setTextSize(textSize+14);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    m.setTextSize(textSize-2);
                    i.setTextSize(textSize-2);
                    t.setTextSize(textSize-2);
                    qq.setTextSize(textSize-2);

                    texttext.setTextSize(textSize);
                    finishChecker.setTextSize(textSize+18);
                    //no.setTextSize(textSize+14);
                    //yes.setTextSize(textSize+14);
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
        // Checks the orientation of the screen and sets it accordingly
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.trolley_page_landscape);
            loadActivity();
        } else {
            setContentView(R.layout.trolley_page);
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
        //restores the data when the app experiences orientation changes
        super.onRestoreInstanceState(outState);
        mealsSelected = outState.getStringArray("mealsSelected");
    }

    private void generateListContent() {
        cursor = myDb.getAllDataTrolley();
        while (cursor.moveToNext()) {
            meals.add(cursor.getString(1));
            ingredients.add(cursor.getString(2));
            types.add(cursor.getString(3));
            amounts.add(cursor.getInt(4));
            trolleyListFinal.add(cursor.getString(2));
            trolleyListAmountsFinal.add(cursor.getString(4));
        }
    }

    //listAdapter class to fill the listView
    private class MyListAdaper extends ArrayAdapter<String> {
        private int layout;
        private List<String> mObjects1;
        private List<String> mObjects2;
        private List<String> mObjects3;
        private List<Integer> mObjects4;

        private MyListAdaper(Context context, int resource, List<String> objects1, List<String> objects2, List<String> objects3, List<Integer> objects4) {
            super(context, resource, objects1);
            mObjects1 = objects1;
            mObjects2 = objects2;
            mObjects3 = objects3;
            mObjects4 = objects4;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TrolleyPage.ViewHolder mainViewholder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                TrolleyPage.ViewHolder viewHolder = new TrolleyPage.ViewHolder();
                viewHolder.title1 = (TextView) convertView.findViewById(R.id.mealNameList);
                viewHolder.title2 = (TextView) convertView.findViewById(R.id.mealIngredientsList);
                viewHolder.title3 = (TextView) convertView.findViewById(R.id.mealType);
                viewHolder.title4 = (TextView) convertView.findViewById(R.id.quantityAmount);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (TrolleyPage.ViewHolder) convertView.getTag();

            newMeasurements = mObjects2.get(position).toLowerCase();
            String colorStart;
            String colorEnd = "</font>";
            Integer quantity;
            String one, two, three;

            Integer upToColon;
            String beforeNumber;
            String afterNumber;

            String temp;
            String tempHolder;
            Integer temp1;
            Integer temp2;
            Integer calculator;
            Integer newMeasurement;

            //the following loops split the ingredients list up and insert html code to change the color of the font in accordance with them matching the ingredients
            String[] splitter = newMeasurements.split(",");
            for (String splitters : splitter) {
                upToColon = splitters.indexOf(":");
                beforeNumber = splitters.substring(0, upToColon);
                afterNumber = splitters.substring(upToColon + 1, splitters.length());
                temp = splitters.replaceAll("\\D+", "");
                tempHolder = splitters.replaceAll("\\D+", "");
                temp1 = Integer.parseInt(temp);
                temp2 = Integer.parseInt(tempHolder);
                temp1 = temp1 * mObjects4.get(position); //the new measurement
                temp2 = temp2 * mObjects4.get(position);
                String number = Integer.toString(temp1); //converts it back to string to replace old ingredient quantity in the meals ingredients list
                afterNumber = afterNumber.replaceAll("[^A-Za-z]", ""); //gets the old quantity
                newMeasurements = newMeasurements.replace(splitters, beforeNumber + ":" + number + afterNumber);

                for (Integer t = 0; t < ingredientNames.size(); t++) {
                    if (ingredientNames.get(t).equals(beforeNumber.toLowerCase())) { //if the ingredient in the meal list matches an ingredient in the ingredients list provided
                        one = newMeasurements.substring(0, newMeasurements.indexOf(ingredientNames.get(t))); //substring of the start up until an ingredient from the list is found
                        two = newMeasurements.substring(newMeasurements.indexOf(ingredientNames.get(t)), newMeasurements.indexOf(ingredientNames.get(t)) + splitters.length()); //substring of that ingredient
                        three = newMeasurements.substring(newMeasurements.indexOf(ingredientNames.get(t)) + splitters.length(), newMeasurements.length()); //substring of the remaining list of ingredients
                        Integer index = two.indexOf(":");
                        String quantityEqualsChecker = two.substring(index + 1, two.length());
                        quantity = Integer.parseInt(quantityEqualsChecker.replaceAll("\\D+", "")); //the measurement of the ingredient in the meal (removes all the letters)
                        String measurement = quantityEqualsChecker.replaceAll("[^A-Za-z]", "");    //the quantity of the ingredient in the meal (removes all the numbers)
                        colorStart = "<font color=#FF8D00>";

                        if (measurement.toLowerCase().equals(quantities.get(t))) {//if the quantities are the same
                            calculator = quantity - Integer.parseInt(measurements.get(t));
                            newMeasurement = Integer.parseInt(measurements.get(t)) - quantity;
                            measurements.set(t, newMeasurement.toString());
                            if (calculator <= 0) {
                                calculator = 0;
                                colorStart = "<font color=#2F56E9>";
                            }
                            if( calculator.toString().equals(temp2.toString())){
                                colorStart = "<font color=#000000>";
                            }
                            if (Integer.parseInt(measurements.get(t)) <= 0) {
                                measurements.set(t, "0");
                            }
                            two = two.replace(quantity.toString(), calculator.toString());
                        }
                        newMeasurements = "";
                        newMeasurements = newMeasurements.concat(one).concat(colorStart).concat(two).concat(colorEnd).concat(three);
                        break;
                    }
                }
            }
            newMeasurements = newMeasurements.replace(",", "<br/>");
            mainViewholder.title1.setText(mObjects1.get(position));
            mainViewholder.title2.setText(Html.fromHtml(newMeasurements));
            mainViewholder.title3.setText(mObjects3.get(position));
            mainViewholder.title4.setText(Integer.toString(mObjects4.get(position)));
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title1;
        TextView title2;
        TextView title3;
        TextView title4;
    }

    //the following allows the user to swipe with their finger to navigate throughout the application instead of using the icons

    public boolean onTouchEvent(MotionEvent event) {
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //swipe gesture class
    class LearnGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!rotation.equals(1) && !rotation.equals(3)) { //if the orientation is portrait, implement left/right gestures
                if (e1.getX() > e2.getX()) {
                    Intent intent = new Intent(TrolleyPage.this, UserManualPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                } else if (e1.getX() < e2.getX()) {
                    Intent intent = new Intent(TrolleyPage.this, IngredientsPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }

            if(rotation.equals(1) || rotation.equals(3)){ //if the orientation is landscape, implement up/down gestures
                if (e1.getY() > e2.getY()) {
                    Intent intent = new Intent(TrolleyPage.this, UserManualPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
                if (e1.getY() < e2.getY()) {
                    Intent intent = new Intent(TrolleyPage.this, IngredientsPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }
            return true;
        }
    }
    //shareActionProvider classes
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "YOUR_TEXT");
            shareIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(shareIntent);
        }
        // Return true to display menu
        return true;
    }
    // Call to update the share intent
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