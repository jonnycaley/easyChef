//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class FinalPage extends AppCompatActivity {
    //initialise variables
    String textViewSetter;
    DatabaseHelper myDb;
    Cursor cursor;
    String ingredients;
    ArrayList<String> trolleyListNeed;
    String ingredientsHave;
    ArrayList<String> ingredientsCopy;
    ArrayList<String> trolleyIngredientsCopy;
    ShareActionProvider mShareActionProvider;
    String sharer;
    Cursor cur;
    Integer rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_page); //sets layout
        myDb = new DatabaseHelper(this); //initialises database
        loadActivity();
    }

    private void loadActivity() {
        Display display = ((WindowManager) getSystemService(WelcomePage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        ingredients = "";
        textViewSetter = getIntent().getStringExtra("textViewSetter"); //ingredients needed - format in html so can be easily placed into edit text with new lines for each ingredient
        ingredientsHave = getIntent().getStringExtra("ingredientsHave");
        trolleyListNeed = getIntent().getStringArrayListExtra("trolleyListNeed");
        ingredientsCopy = getIntent().getStringArrayListExtra("ingredientsCopy");
        trolleyIngredientsCopy = getIntent().getStringArrayListExtra("trolleyIngredientsCopy");

        String ingredientsUsed = "";
        String ingredientsUnused = "";
        for (Integer a = 0; a < ingredientsCopy.size(); a++) {
            String number = ingredientsCopy.get(a).replaceAll("\\D+", ""); //gets the quantity of the ingredient
            Integer place = ingredientsCopy.get(a).indexOf(number); //gets index of first number in string, then used to split ingredient
            String name = ingredientsCopy.get(a).substring(0, place); //gets the name of each ingredient
            String measurementWNo = ingredientsCopy.get(a).substring(ingredientsCopy.get(a).indexOf(number), ingredientsCopy.get(a).length());
            String measurement = measurementWNo.replaceAll("[^A-Za-z]", "");
            for (Integer b = 0; b < trolleyIngredientsCopy.size(); b++) {
                String nameTwo = trolleyIngredientsCopy.get(b).substring(0, trolleyIngredientsCopy.get(b).indexOf(":"));
                String measurementWNoTwo = trolleyIngredientsCopy.get(b).substring(trolleyIngredientsCopy.get(b).indexOf(":") + 1, trolleyIngredientsCopy.get(b).length());
                String measurementTwo = measurementWNoTwo.replaceAll("[^A-Za-z]", "");
                if (nameTwo.toLowerCase().equals(name.toLowerCase()) && measurement.toLowerCase().equals(measurementTwo.toLowerCase())) {
                    Integer firstInt = Integer.parseInt(number); //how much the user has
                    Integer secondInt = Integer.parseInt(measurementWNoTwo.replaceAll("\\D+", "")); //how much the user needs (not including what they have)
                    Integer result = secondInt - firstInt; //what the user needs -  what the user has
                    if ((result) >= 0) { //if he still needs something from an ingredients, there is none of that ingredient that has gone unused
                        ingredientsUsed = ingredientsUsed.concat(firstInt.toString() + measurementTwo + " of " + name + "<br/>");
                    }
                    if ((result) < 0) {
                        ingredientsUsed = ingredientsUsed.concat(secondInt.toString() + measurementTwo + " of " + name + "<br/>");
                        Integer temp = firstInt - secondInt;
                        String temper = temp.toString();
                        ingredientsUnused = ingredientsUnused.concat((temper + measurementTwo + " of " + name + "<br/>"));
                    }
                }
            }
        }

        for (Integer a = 0; a < ingredientsCopy.size(); a++) {
            String number = ingredientsCopy.get(a).replaceAll("\\D+", ""); //gets the quantity of the ingredient
            Integer place = ingredientsCopy.get(a).indexOf(number); //gets index of first number in string, then used to split ingredient
            String name = ingredientsCopy.get(a).substring(0, place); //gets the name of each ingredient
            String measurementWNo = ingredientsCopy.get(a).substring(ingredientsCopy.get(a).indexOf(number), ingredientsCopy.get(a).length());
            String measurement = measurementWNo.replaceAll("[^A-Za-z]", "");
            if (!(trolleyIngredientsCopy.toString().toLowerCase().contains(name.toLowerCase()))) {
                ingredientsUnused = ingredientsUnused.concat(number + measurement + " of " + name + "<br/>");
            }
        }

        cursor = myDb.getAllData();
        while (cursor.moveToNext()) {
            String ingredient = cursor.getString(0);
            String first = ingredient.substring(0, 1).toUpperCase();
            ingredient = first + ingredient.substring(1);
            ingredients = ingredients.concat(ingredient);
            ingredients = ingredients.concat(" ");
            ingredients = ingredients.concat(cursor.getString(1));
            ingredients = ingredients.concat(cursor.getString(2));
            ingredients = ingredients.concat("<br/>"); //sets the string to be format well with textView using HTML (<br/>)
        }
        if(!(ingredientsUsed.equals(""))){
            ingredientsUsed = ingredientsUsed.substring(0,ingredientsUsed.length()-5);
        }
        if(ingredientsUsed.equals("")){
            ingredientsUsed = "None";
        }
        sharer = ingredientsUsed.replaceAll("<br/>",", ");

        TextView ingredientsHere = (TextView) findViewById(R.id.ingredientsHere);
        ingredientsHere.setText(Html.fromHtml(ingredientsUsed)); //ingredientsUsed set to textview
        if(!(ingredientsUnused.equals(""))){
            ingredientsUnused = ingredientsUnused.substring(0,ingredientsUnused.length()-5);
        }
        if(ingredientsUnused.equals("")){
            ingredientsUnused = "None";
        }
        TextView ingredientsUnusedHere = (TextView) findViewById(R.id.ingredientsUnusedHere);
        ingredientsUnusedHere.setText(Html.fromHtml(ingredientsUnused)); //ingredientsUnused set to textview

        for (int i = 0; i < trolleyIngredientsCopy.size(); i++) {
            String start = trolleyIngredientsCopy.get(i).substring(0, trolleyIngredientsCopy.get(i).indexOf(":"));
            String measure = trolleyIngredientsCopy.get(i).substring(trolleyIngredientsCopy.get(i).indexOf(":") + 1, trolleyIngredientsCopy.get(i).length()).replaceAll("[^A-Za-z]", "");
            String noStr = trolleyIngredientsCopy.get(i).replaceAll("\\D+", "");
            Integer noInt = Integer.parseInt(noStr);
            for (Integer j = 0; j < ingredientsCopy.size(); j++) {
                String noTwo = ingredientsCopy.get(j).replaceAll("\\D+", "");
                String startTwo = ingredientsCopy.get(j).substring(0, ingredientsCopy.get(j).indexOf(noTwo));
                String measureTwo = ingredientsCopy.get(j).substring(ingredientsCopy.get(j).indexOf(noTwo), ingredientsCopy.get(j).length()).replaceAll("[^A-Za-z]", "");
                Integer noTwoInt = Integer.parseInt(noTwo);
                if (start.toLowerCase().equals(startTwo.toLowerCase()) && measure.equals(measureTwo)) {
                    Integer result = noInt - noTwoInt;
                    if (result < 0) {
                        result = 0;
                    }
                    trolleyIngredientsCopy.set(i, start + ":" + result + measure);
                }
            }
            if (!(trolleyIngredientsCopy.get(i).replaceAll("\\D+", "").equals("0"))) {

            }
            else if (trolleyIngredientsCopy.get(i).replaceAll("\\D+", "").equals("0")) {
                trolleyIngredientsCopy.remove(i);
                i--;
            }
        }
        String ingredientsRequired = "";
        for(Integer h = 0; h < trolleyIngredientsCopy.size(); h++){
            ingredientsRequired = ingredientsRequired.concat(trolleyIngredientsCopy.get(h).replaceAll("\\D+", "") + trolleyIngredientsCopy.get(h).substring(trolleyIngredientsCopy.get(h).indexOf(":") + 1, trolleyIngredientsCopy.get(h).length()).replaceAll("[^A-Za-z]", "") + " of " + trolleyIngredientsCopy.get(h).substring(0, trolleyIngredientsCopy.get(h).indexOf(":")) + "<br/>");
        }
        if(!(ingredientsRequired.equals(""))){
            ingredientsRequired = ingredientsRequired.substring(0,ingredientsRequired.length()-5);
        }
        if(ingredientsRequired.equals("")){
            ingredientsRequired = "None";
        }
        TextView ingredientsRequiredText = (TextView) findViewById(R.id.ingredientsRequired);
        ingredientsRequiredText.setText(Html.fromHtml(ingredientsRequired)); //ingredientsRequired set to textview
        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.reset(); //finish button resets database and restarts app
                Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restart);
            }
        });
        String mealFiller = "";
        cur = myDb.getAllDataTrolley();
        while (cur.moveToNext()) {
            mealFiller = mealFiller.concat(cur.getString(4) + "x " +cur.getString(1)  + "<br/>");

        }
        if(!(mealFiller.equals(""))){
            mealFiller = mealFiller.substring(0,mealFiller.length()-5);
        }
        if(mealFiller.equals("")){
            mealFiller = "None";
        }
        TextView mealsMadeHere = (TextView) findViewById(R.id.mealsMadeHere);
        mealsMadeHere.setText(Html.fromHtml(mealFiller));
        textSizer();
    }

    private void textSizer() {

        TextView TOne = (TextView) findViewById(R.id.TOne);
        TextView ingredientsHere = (TextView) findViewById(R.id.ingredientsHere);
        TextView b = (TextView) findViewById(R.id.b);
        TextView TTwo = (TextView) findViewById(R.id.TTwo);
        TextView ingredientsUnusedHere = (TextView) findViewById(R.id.ingredientsUnusedHere);
        TextView TThree = (TextView) findViewById(R.id.TThree);
        TextView ingredientsRequired = (TextView) findViewById(R.id.ingredientsRequired);
        TextView TFour = (TextView) findViewById(R.id.TFour);
        TextView mealsMadeHere = (TextView) findViewById(R.id.mealsMadeHere);
        TextView TFive = (TextView) findViewById(R.id.TFive);
        Button finishButton = (Button) findViewById(R.id.finishButton);

        Integer screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        Integer textSize;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                textSize = 24;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    TOne.setTextSize(textSize+4);
                    ingredientsHere.setTextSize(textSize+4);
                    b.setTextSize(textSize+3);
                    TTwo.setTextSize(textSize+4);
                    ingredientsUnusedHere.setTextSize(textSize+4);
                    TThree.setTextSize(textSize+4);
                    ingredientsRequired.setTextSize(textSize+4);
                    TFour.setTextSize(textSize+4);
                    mealsMadeHere.setTextSize(textSize+4);
                    TFive.setTextSize(textSize+10);
                    finishButton.setTextSize(textSize+20);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    TOne.setTextSize(textSize+4);
                    ingredientsHere.setTextSize(textSize+4);
                    b.setTextSize(textSize+10);
                    TTwo.setTextSize(textSize+4);
                    ingredientsUnusedHere.setTextSize(textSize+4);
                    TThree.setTextSize(textSize+4);
                    ingredientsRequired.setTextSize(textSize+4);
                    TFour.setTextSize(textSize+4);
                    mealsMadeHere.setTextSize(textSize+4);
                    TFive.setTextSize(textSize+10);
                    finishButton.setTextSize(textSize+20);
                    break;
                }
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                textSize = 18;
                if(!rotation.equals(1) && !rotation.equals(3)) {
                    TOne.setTextSize(textSize+4);
                    ingredientsHere.setTextSize(textSize+4);
                    b.setTextSize(textSize+3);
                    TTwo.setTextSize(textSize+4);
                    ingredientsUnusedHere.setTextSize(textSize+4);
                    TThree.setTextSize(textSize+4);
                    ingredientsRequired.setTextSize(textSize+4);
                    TFour.setTextSize(textSize+4);
                    mealsMadeHere.setTextSize(textSize+4);
                    TFive.setTextSize(textSize+8);
                    finishButton.setTextSize(textSize+12);
                    break;
                }
                if(rotation.equals(1) || rotation.equals(3)) {
                    TOne.setTextSize(textSize+4);
                    ingredientsHere.setTextSize(textSize+4);
                    b.setTextSize(textSize+10);
                    TTwo.setTextSize(textSize+4);
                    ingredientsUnusedHere.setTextSize(textSize+4);
                    TThree.setTextSize(textSize+4);
                    ingredientsRequired.setTextSize(textSize+4);
                    TFour.setTextSize(textSize+4);
                    mealsMadeHere.setTextSize(textSize+4);
                    TFive.setTextSize(textSize+8);
                    finishButton.setTextSize(textSize+12);
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

    private void doShare() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I'm using easyChef");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I used up " + sharer + " using easyChef today!" ); //allows the user to share the ingredients they have used up
        startActivity(Intent.createChooser(shareIntent, "Share this to everyone!"));
    }
}