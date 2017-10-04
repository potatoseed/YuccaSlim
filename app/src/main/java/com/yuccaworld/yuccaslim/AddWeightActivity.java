package com.yuccaworld.yuccaslim;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddWeightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        Button button = (Button) findViewById(R.id.buttonAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = ((EditText) findViewById(R.id.editTextWeightInput)).getText().toString();
                if (input.length() == 0) {
                    return;
                }
                ContentValues contentValues = new ContentValues();
                //contentValues.put(SlimContract.SlimDB.COLUMN_ATIVITY_TYPE_ID)
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }
}
