package uk.ac.masts.sifids;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class AddFish1FormActivity extends AppCompatActivity {

    EditText comment;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fish_1_form);

        comment = (EditText) findViewById(R.id.comment);
        button = (Button)findViewById(R.id.button);


        final CatchDatabase db = CatchDatabase.getInstance(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!comment.getText().toString().equals("")){

                    final Fish1Form fish1Form= new Fish1Form();
                    fish1Form.setCommentsAndBuyersInformation(comment.getText().toString());

                    //save the item before leaving the activity


                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.catchDao().insertFish1Form(fish1Form);
                        }
                    });


                    Intent i = new Intent(AddFish1FormActivity.this,Fish1FormsActivity.class);
                    startActivity(i);

                    finish();
                }
            }
        });
    }

}
