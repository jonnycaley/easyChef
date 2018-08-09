//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class UserManualPage extends AppCompatActivity {

    private GestureDetectorCompat gestureObject;
    private ShareActionProvider mShareActionProvider;
    String[] mealsSelected = new String[3]; //the array that carries the meal types selected
    Integer rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manual_page);

        if (savedInstanceState != null) {
            mealsSelected = savedInstanceState.getStringArray("mealsSelected");
            if (mealsSelected == null) {
                mealsSelected = new String[3];
            }
        }
        loadActivity();
    }

    private void loadActivity() {
        Display display = ((WindowManager) getSystemService(UserManualPage.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation(); //0 is portrait, else it is landscape(1 or 3)
        if(rotation.equals(1) || rotation.equals(3)){
            setContentView(R.layout.user_manual_page_landscape);
        }
        // initialises listView to the userManual webPage
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.loadUrl("http://cowc2.sci-project.lboro.ac.uk");

        mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
        gestureObject = new GestureDetectorCompat(this, new LearnGesture());
        //the following listeners are for navigating to other pages
        ImageButton toWelcomePage = (ImageButton) findViewById(R.id.firstButton);
        toWelcomePage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManualPage.this, MainPage.class);
                String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toIngredientsPage = (ImageButton) findViewById(R.id.secondButton);
        toIngredientsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManualPage.this, IngredientsPage.class);
                String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toTrolleyPage = (ImageButton) findViewById(R.id.thirdButton);
        toTrolleyPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManualPage.this, TrolleyPage.class);
                String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });

        ImageButton toManualPage = (ImageButton) findViewById(R.id.fourthButton);
        toManualPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManualPage.this, UserManualPage.class);
                String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                intent.putExtra("mealsSelected", mealsSelected);
                startActivity(intent);
            }
        });
    }

    //when the configuration changes the layout follows
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.user_manual_page_landscape);
            loadActivity();
        } else {
            setContentView(R.layout.user_manual_page);
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

    public boolean onTouchEvent (MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //class for capturing touch gestures (swiping left and right)
    class LearnGesture extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(!rotation.equals(1) && !rotation.equals(3)){ //if the orientation is portrait, implement left/right gestures
                if(e1.getX() < e2.getX()){
                    Intent intent = new Intent(UserManualPage.this,TrolleyPage.class);
                    String[] mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }
            if(rotation.equals(1) || rotation.equals(3)){ //if the orientation is landscape, implement up/down gestures
                if (e1.getY() < e2.getY()) {
                    Intent intent = new Intent(UserManualPage.this, TrolleyPage.class);
                    mealsSelected = getIntent().getStringArrayExtra("mealsSelected");
                    intent.putExtra("mealsSelected", mealsSelected);
                    finish();
                    startActivity(intent);
                }
            }
            return true;
        }
    }

    //shareActionProvider methods
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