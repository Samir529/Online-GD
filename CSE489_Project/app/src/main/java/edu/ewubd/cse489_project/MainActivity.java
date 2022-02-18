package edu.ewubd.cse489_project;

//import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.content.SharedPreferences;

import android.widget.CheckBox;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences.Editor prefsEditor1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences1;
    private CheckBox rememberNidCB, rememberLoginCB;
    EditText Nid, password;
    DBHelper DB;
    ArrayList<String> thanaList = new ArrayList<String>();
    ArrayList<String> thanaPassList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        thanaList.add("thana1");
        thanaList.add("thana2");
        thanaList.add("thana3");
        thanaPassList.add("1234");
        thanaPassList.add("5678");
        thanaPassList.add("9101");

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences("MySharedPref1", MODE_PRIVATE);
        String rememberVal = sharedPreferences.getString("remember", "");
        String userId = "";

        if(rememberVal.equals("login")){
            Intent i = new Intent(MainActivity.this, AllGDActivity.class);
            startActivity(i);
            finish();
        }

        else if(rememberVal.equals("user_id")){
            userId = sharedPreferences.getString("user_id", "");
        }

        setContentView(R.layout.activity_main);

        Nid = (EditText) findViewById(R.id.etNid);
        setEditTextMaxLength(Nid,10);
        password = (EditText) findViewById(R.id.etPassword);
        setEditTextMaxLength(password,4);


        DB = new DBHelper(this);

        prefsEditor = sharedPreferences.edit();
        prefsEditor1 = sharedPreferences1.edit();

        rememberNidCB = findViewById(R.id.cbRememberNid);
        rememberLoginCB = findViewById(R.id.cbRememberLogin);


        Nid.setText(userId);


        rememberNidCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                rememberNidCB.setChecked(true);
                rememberLoginCB.setChecked(false);
                prefsEditor.putString("remember", "user_id");
                prefsEditor.commit();


            }
        });

        rememberLoginCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                rememberLoginCB.setChecked(true);
                rememberNidCB.setChecked(false);
                prefsEditor.putString("remember", "login");
                prefsEditor.commit();

            }
        });



        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Login();

            }
        });

        findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

//                MainActivity.this.finish();
                finishAffinity();

            }
        });

        findViewById(R.id.linkSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });


    }



    private void Login(){
        String user = Nid.getText().toString();
        String pass = password.getText().toString();

        String errorMsg = "";
        if(user.isEmpty()){
            errorMsg +="NID can't be empty. ";
        }
        if(pass.isEmpty()){
            errorMsg +="Password can't be empty. ";
        }

//        ArrayList<String> thanaList = new ArrayList<String>();
//        ArrayList<String> thanaPassList = new ArrayList<String>();
//        thanaList.add("thana1");
//        thanaList.add("thana2");
//        thanaList.add("thana3");
//        thanaPassList.add("1234");
//        thanaPassList.add("5678");
//        thanaPassList.add("9101");


        if(errorMsg.isEmpty()){
//            Thana Start
            for(int i=0; i<thanaList.size(); i++){
                if(user.equals(thanaList.get(i)) && pass.equals(thanaPassList.get(i))){
                    showDialog("Log in successful", "Info", "ok", false);
                    prefsEditor1.putString("user_id", user);
                    prefsEditor1.putString("password", pass);
                    prefsEditor1.commit();

//                    Intent intent = new Intent(MainActivity.this, GdDetail.class);
//                    intent.putExtra("key", thanaList);
//                    Intent intent1 = new Intent(MainActivity.this, AllGDActivity.class);
//                    intent1.putExtra("key", thanaList);
//                    Intent intent2 = new Intent(MainActivity.this, AcceptedGDActivity.class);
//                    intent2.putExtra("key", thanaList);
                    return;
                }
            }

//             Thana End

            Boolean checkNidPass = DB.checkNidPassword(user, pass);
            if(checkNidPass == true){

                prefsEditor.putString("user_id", user);
                prefsEditor.putString("password", pass);
                prefsEditor.commit();

                prefsEditor1.putString("user_id", user);
                prefsEditor1.putString("password", pass);
                prefsEditor1.commit();


                showDialog("Log in successful", "Info", "ok", false);
            }
            else{
                showDialog("incorrect NID or Password", "Login Error", "Back", true);
            }
        }
        else{
            showDialog(errorMsg, "Login Error", "Back", true);
        }
    }

    public void setEditTextMaxLength(EditText et, int length){
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        et.setFilters(filterArray);
    }


    private void showDialog(String message, String title, String buttonLabel, boolean closeDialog){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setCancelable(false)
                .setNegativeButton(buttonLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(closeDialog){
                            dialog.cancel();
                        }
                        else{
                            Intent i = new Intent(MainActivity.this, AllGDActivity.class);
                            startActivity(i);
                        }

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public ArrayList<String> getArrayList() {
        return thanaList;
    }

}
