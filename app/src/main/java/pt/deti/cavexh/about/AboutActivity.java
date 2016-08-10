package pt.deti.cavexh.about;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import pt.deti.cavexh.R;
import pt.deti.cavexh.tutorial.Tutorial;
import pt.deti.cavexh.tutorial.TutorialGame;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView textView = (TextView)findViewById(R.id.tutorial);
        textView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(AboutActivity.this, Tutorial.class);
                startActivity(i);
            }
        });

        TextView textView2 = (TextView)findViewById(R.id.tutorial_2);
        textView2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(AboutActivity.this, TutorialGame.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
