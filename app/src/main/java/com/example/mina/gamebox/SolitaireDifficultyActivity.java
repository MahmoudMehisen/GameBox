package com.example.mina.gamebox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SolitaireDifficultyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitaire_difficulty);

        Spinner difficultySpinner = findViewById(R.id.selectDifficultyLevel);
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,R.array.solitairDifficultyLevel,android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        difficultySpinner.setAdapter(difficultyAdapter);
        difficultySpinner.setOnItemSelectedListener(this);
    }

    public void backButtonOnClickListener(View view) {
        Intent intent= new Intent(this,SolitaireStartpage.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),text ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void playNowButtonOnClickListener(View view) {
        Intent intent = new Intent(this , SolitaireGameActivity.class);
        startActivity(intent)   ;
    }
}
/*


</android.support.constraint.ConstraintLayout>

 */
