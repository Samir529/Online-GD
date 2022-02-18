package edu.ewubd.cse489_project;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;


public class SignUpActivity extends Activity {

    EditText nameTF, emailTF, NidTF, passwordTF, rePasswordTf, phoneTF;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // to check the email pattern validation
    // Reference Link: https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    RadioButton MaleRB, FemaleRB;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        nameTF = (EditText) findViewById(R.id.etName);
        setEditTextMaxLength(nameTF,20);
        emailTF = (EditText) findViewById(R.id.etEmail);
        NidTF = (EditText) findViewById(R.id.etNid);
        setEditTextMaxLength(NidTF,10);
        passwordTF = (EditText) findViewById(R.id.etPass);
        setEditTextMaxLength(passwordTF,4);
        rePasswordTf = (EditText) findViewById(R.id.etRePass);
        setEditTextMaxLength(rePasswordTf,4);
        phoneTF = (EditText) findViewById(R.id.etPhone);
        setEditTextMaxLength(phoneTF,11);

        MaleRB = (RadioButton) findViewById(R.id.rbMale);
        FemaleRB = (RadioButton) findViewById(R.id.rbFemale);

        DB = new DBHelper(this);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                InsertDataToDatabase();
            }
        });



    }


    private void InsertDataToDatabase(){
        String name = nameTF.getText().toString();
        String email = emailTF.getText().toString();
        String Nid = NidTF.getText().toString();
        String pass = passwordTF.getText().toString();
        String repass = rePasswordTf.getText().toString();
        String phone = phoneTF.getText().toString();

        String gender = "";
        if(MaleRB.isChecked()) {
            gender = "Male";
        }
        else if(FemaleRB.isChecked()) {
            gender = "Female";
        }


        String errorMsg = "";

        if(name.isEmpty()){
            errorMsg += "Name can't be empty. ";
        }

        if(email.isEmpty()){
            errorMsg += "Email can't be empty. ";
        }

        if(!(email.trim().matches(emailPattern))){
            errorMsg += "E-mail address is not valid. ";
        }

        if(Nid.isEmpty()){
            errorMsg += "Nid can't be empty. ";
        }

        if(pass.isEmpty()){
            errorMsg += "Password can't be empty. ";
        }

        if(repass.isEmpty()){
            errorMsg += "Re-password can't be empty. ";
        }

        if(phone.isEmpty() || phone.length() < 11 || phone.length() > 14){
            errorMsg += "Phone number Can't be Empty and must be between 11 and 14 digits. ";
        }

        if(pass.equals(repass)==false){
            errorMsg += "Password and Repassword doesn't match. ";
        }

        if(errorMsg.isEmpty()){

            Boolean checkNid = DB.checkNidNumber(Nid);

            if(checkNid == false){ // This Nid Don't exist in the database

                Boolean insert = DB.insertData(name, email, Nid, pass, phone, gender);

                if(insert == true){
                    showDialog("Registered Successfully", "Info", "Ok", false);
                }
                else{
                    showDialog("Registration Failed", "Registration Error", "Back", true);
                }
            }

            else{
                showDialog("This Nid already signed up previously. ", "Registration Error", "Back", true);
            }
        }
        else{
            showDialog(errorMsg, "Registration Error", "Back", true);
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

                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(i);
                        }

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }


}
