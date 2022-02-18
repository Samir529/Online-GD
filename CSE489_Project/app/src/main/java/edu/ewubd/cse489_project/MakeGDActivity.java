package edu.ewubd.cse489_project;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MakeGDActivity extends Activity {

    private EditText dateTF, police_stationTF, full_nameTF, NidTF, addressTF, numberTF, descriptionTF;

    private String existingKey = null;
    private SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences1 = getSharedPreferences("MySharedPref1", MODE_PRIVATE);
        setContentView(R.layout.activity_make_gd);

        String userId = "";
        userId = sharedPreferences1.getString("user_id", "");

        dateTF = findViewById(R.id.etDate);
        setEditTextMaxLength(dateTF,30);
        police_stationTF = findViewById(R.id.etPoliceStation);
        setEditTextMaxLength(police_stationTF,30);
        full_nameTF = findViewById(R.id.etFullName);
        setEditTextMaxLength(full_nameTF,20);
        NidTF = (EditText) findViewById(R.id.etNid);
        NidTF.setEnabled(false);
        setEditTextMaxLength(NidTF,10);
        NidTF.setText(userId);
        addressTF = findViewById(R.id.etCorrAddr);
        setEditTextMaxLength(addressTF,30);
        numberTF = findViewById(R.id.etCell);
        setEditTextMaxLength(numberTF,11);
        descriptionTF = findViewById(R.id.etGdDesc);
        setEditTextMaxLength(descriptionTF,150);


        Intent i = getIntent();
        existingKey = i.getStringExtra("GDKey");

        initializeFormWithExistingData(existingKey);


        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGDData();
            }
        });


    }

    private void saveGDData() {
        String datetime = dateTF.getText().toString();
        String police_station = police_stationTF.getText().toString();
        String full_name = full_nameTF.getText().toString();
        String Nid = NidTF.getText().toString();
        String address = addressTF.getText().toString();
        String number = numberTF.getText().toString();
        String description = descriptionTF.getText().toString();
//        String isAccepted = "false";
//        String isRejected = "false";

        System.out.println("Date: " + datetime);
        System.out.println("Police Station: " + police_station);
        System.out.println("Full Name: " + full_name);
        System.out.println("NID No.: " + Nid);
        System.out.println("Corresponding Address: " + address);
        System.out.println("Cell Number: " + number);
        System.out.println("GD Description: " + description);

        String errorMsg = "";
        if(datetime.isEmpty()) {
            errorMsg += "Date & time can't be empty. ";
        }
        if(police_station.isEmpty()) {
            errorMsg += "Police station can't be empty. ";
        }
        if(full_name.isEmpty()) {
            errorMsg += "Full name can't be empty. ";
        }
        if(Nid.isEmpty()){
            errorMsg += "Nid can't be empty. ";
        }
        if(address.isEmpty()) {
            errorMsg += "Address can't be empty. ";
        }
        if(number == null || number.length() < 11) {
            errorMsg += "Number should have at least 11 digits. ";
        }
        if(description.isEmpty()) {
            errorMsg += "Description can't be empty. ";
        }

        if(errorMsg.isEmpty()) {
            // save the data in database
            String value = datetime+":-;-:"+police_station+":-;-:"+full_name+":-;-:"+Nid+":-;-:"+address+":-;-:"+number+":-;-:"
                    +description;
            String accepted = "Pending";

            String key = "";
            if(existingKey!=null) {
                key = existingKey;
            } else {
                key = Nid + "_" + System.currentTimeMillis();
            }

            System.out.println("Key: "+key);
            System.out.println("Value: "+value);
            System.out.println("accepted: "+accepted);

            // send gd locally in SQLite
            Util.getInstance().setKeyValue(MakeGDActivity.this, key, value, accepted);

            // send save-request to server
            httpRequest(new String[] {"key", "gd", "status"}, new String[] {key, value, accepted});


            showDialog("Information has been saved successfully", "Info", "Ok", false);

        } else {
//            ((TextView)findViewById(R.id.tvErrorMsg)).setText(errorMsg);
            showDialog(errorMsg, "Error in GD Data", "Back", true);
        }
    }

    private void initializeFormWithExistingData(String gdKey) {
        String value = Util.getInstance().getValueByKey(this, gdKey);
        System.out.println("Value "+value);

        if(value != null) {
            String[] fieldValues = value.split(":-;-:");

            String datetime = fieldValues[0];
            String police_station = fieldValues[1];
            String full_name = fieldValues[2];
            String Nid = fieldValues[3];
            String address = fieldValues[4];
            String number = fieldValues[5];
//            String description = "No description";
            String description = fieldValues[6];
//            if(fieldValues.length>6){
//                description = fieldValues[5];
//            }

            dateTF.setText(datetime);
            police_stationTF.setText(police_station);
            full_nameTF.setText(full_name);
            NidTF.setText(Nid);
            addressTF.setText(address);
            numberTF.setText(number);
            descriptionTF.setText(description);

        }
    }

    public void setEditTextMaxLength(EditText et, int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        et.setFilters(filterArray);
    }

    private void showDialog(String message, String title, String buttonLabel, boolean closeDialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(message);
        builder.setTitle(title);

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setNegativeButton(buttonLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(closeDialog) {
                            dialog.cancel();
                        }
                        else {
                            finish();
                        }
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
//        alert.setTitle("Error Dialog");
        alert.show();

    }

    @SuppressLint("StaticFieldLeak")
    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... param) {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    for(int i=0; i<keys.length; i++) {
                        params.add(new BasicNameValuePair(keys[i], values[i]));
                    }
                    JSONObject jObj = JSONParser.getInstance().makeHttpRequest("http://10.0.2.2:80/Online_GD/index.php", "POST", params);
                    return jObj;
//                    System.out.println(jObj);
//                    if(jObj.getInt("success")==1){
//                        return jObj;
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(JSONObject jObj) {
                String msg = "Failed to send request";
                if(jObj != null) {
                    try {
                        msg = jObj.getString("msg");
                    } catch (Exception e) {
//                        showDialog(msg, "Info", "Ok", false);
                        e.printStackTrace();
                    }
                }
//                showDialog(msg, "Info", "Ok", false);
            }
        }.execute();
    }

}