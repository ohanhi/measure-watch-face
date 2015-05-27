package com.ohanhi.measure;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ConfigurationActivity extends ActionBarActivity implements ColorChooserDialog.Listener {

    private static final String TAG_BACKGROUND_COLOUR_CHOOSER = "background_chooser";
    private static final String TAG_DATE_AND_TIME_COLOUR_CHOOSER = "date_time_chooser";

    private View backgroundColourImagePreview;
    private View dateAndTimeColourImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.configuration_background_colour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorChooserDialog.newInstance(getString(R.string.pick_background_color))
                        .show(getFragmentManager(), TAG_BACKGROUND_COLOUR_CHOOSER);
            }
        });

        findViewById(R.id.configuration_time_colour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorChooserDialog.newInstance(getString(R.string.pick_date_time_color))
                        .show(getFragmentManager(), TAG_DATE_AND_TIME_COLOUR_CHOOSER);
            }
        });

        backgroundColourImagePreview = findViewById(R.id.configuration_background_colour_preview);
        dateAndTimeColourImagePreview = findViewById(R.id.configuration_date_and_time_colour_preview);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColourSelected(String colour, String tag) {

        if (TAG_BACKGROUND_COLOUR_CHOOSER.equals(tag)) {
            backgroundColourImagePreview.setBackgroundColor(Color.parseColor(colour));
        } else {
            dateAndTimeColourImagePreview.setBackgroundColor(Color.parseColor(colour));
        }
    }
}
