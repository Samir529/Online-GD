package edu.ewubd.cse489_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GdDetail extends Activity {
    private EditText dateTF, police_stationTF, full_nameTF, NidTF, addressTF, numberTF, descriptionTF;
    private SharedPreferences sharedPreferences1;
    private String existingKey =null;
    Button acceptButton, rejectButton;
    ArrayList<String> thanaList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thanaList.add("thana1");
        thanaList.add("thana2");
        thanaList.add("thana3");

        sharedPreferences1 = getSharedPreferences("MySharedPref1", MODE_PRIVATE);
        setContentView(R.layout.activity_gd_detail);

        dateTF = findViewById(R.id.etDate);

        police_stationTF = findViewById(R.id.etPoliceStation);

        full_nameTF = findViewById(R.id.etFullName);

        NidTF = findViewById(R.id.etNid);

        addressTF = findViewById(R.id.etCorrAddr);

        numberTF = findViewById(R.id.etCell);

        descriptionTF = findViewById(R.id.etGdDesc);

        acceptButton = findViewById(R.id.btnAccept);

        rejectButton = findViewById(R.id.btnReject);

        String userId = "";

        userId = sharedPreferences1.getString("user_id", "");

//        MainActivity num;
//        num = new MainActivity();
//        ArrayList<String> thanaList = num.getArrayList();

//        if(thanaList.contains(userId)) {
//            acceptButton.setEnabled(true);
//            rejectButton.setEnabled(true);
//        }
        int c = 0;
        for(int i=0; i<thanaList.size(); i++){
            if (userId.equals(thanaList.get(i))) {
//                acceptButton.setEnabled(true);
//                rejectButton.setEnabled(true);
                c++;
                break;
            }
        }
        if (c!=0) {
            acceptButton.setEnabled(true);
            rejectButton.setEnabled(true);
        }
        else if (c==0) {
//            acceptButton.setEnabled(false);
//            rejectButton.setEnabled(false);
            acceptButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }


        Intent i = getIntent();
        existingKey = i.getStringExtra("GDKey");


        initializeFormWithExistingData(existingKey);


        findViewById(R.id.btnAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Accept button pressed.");
                acceptGD();
            }
        });

        findViewById(R.id.btnReject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Reject button pressed.");
                rejectGD();
            }
        });

        findViewById(R.id.btnReturn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GdDetail.this, AllGDActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    private void initializeFormWithExistingData(String gdKey) {
        String value = Util.getInstance().getValueByKey(this, gdKey);
        System.out.println("Value " + value);

        if (value != null) {
            String[] fieldValues = value.split(":-;-:");

            String datetime = fieldValues[0];
            String police_station = fieldValues[1];
            String full_name = fieldValues[2];
            String Nid = fieldValues[3];
            String address = fieldValues[4];
            String number = fieldValues[5];
            String description = fieldValues[6];


            dateTF.setText(datetime);
            police_stationTF.setText(police_station);
            full_nameTF.setText(full_name);
            NidTF.setText(Nid);
            addressTF.setText(address);
            numberTF.setText(number);
            descriptionTF.setText(description);

        }
    }


    private void acceptGD() {

        String datetime = dateTF.getText().toString();
        String police_station = police_stationTF.getText().toString();
        String full_name = full_nameTF.getText().toString();
        String Nid = NidTF.getText().toString();
        String address = addressTF.getText().toString();
        String number = numberTF.getText().toString();
        String description = descriptionTF.getText().toString();

        String value = datetime + ":-;-:" + police_station + ":-;-:" + full_name + ":-;-:" + Nid + ":-;-:" + address + ":-;-:" + number + ":-;-:"
                + description;
        String accepted = "Accepted";
        Util.getInstance().setKeyValue(GdDetail.this, existingKey, value, accepted);
        httpRequest(new String[] {"key", "gd", "status"}, new String[] {existingKey, value, accepted});

        showDialog("GD accepted", "Info", "Ok", false);

        System.out.println("accepted: "+accepted);
//        Intent i = new Intent(GdDetail.this, AllGDActivity.class);
//        startActivity(i);
//        finish();

    }

    private void rejectGD() {

        String datetime = dateTF.getText().toString();
        String police_station = police_stationTF.getText().toString();
        String full_name = full_nameTF.getText().toString();
        String Nid = NidTF.getText().toString();
        String address = addressTF.getText().toString();
        String number = numberTF.getText().toString();
        String description = descriptionTF.getText().toString();

        String value = datetime + ":-;-:" + police_station + ":-;-:" + full_name + ":-;-:" + Nid +  ":-;-:" + address + ":-;-:" + number + ":-;-:"
                + description;
        String accepted = "Rejected";
        Util.getInstance().setKeyValue(GdDetail.this, existingKey, value, accepted);
        httpRequest(new String[] {"key", "gd", "status"}, new String[] {existingKey, value, accepted});

        showDialog("GD rejected", "Info", "Ok", false);

        System.out.println("accepted: "+accepted);
//        Intent i = new Intent(GdDetail.this, AllGDActivity.class);
//        startActivity(i);
//        finish();

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
                    JSONObject jObj = JSONParser.getInstance().makeHttpRequest("http://10.0.2.2:80/Online_GD/update.php", "POST", params);
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
