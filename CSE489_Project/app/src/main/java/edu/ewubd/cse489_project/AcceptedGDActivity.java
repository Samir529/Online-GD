package edu.ewubd.cse489_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AcceptedGDActivity extends Activity {

    private ListView lvGDs;
    private ArrayList<GD> gds;
    private CustomGDAdapter adapter;
    private SharedPreferences sharedPreferences1;
    ArrayList<String> thanaList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thanaList.add("thana1");
        thanaList.add("thana2");
        thanaList.add("thana3");



        setContentView(R.layout.activity_accepted_gd);

        lvGDs = findViewById(R.id.lvGDs);



        findViewById(R.id.btnMakeGD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(AcceptedGDActivity.this, MakeGDActivity.class);
                startActivity(i);

            }
        });


        findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpRequest(new String[] {"is_future"}, new String[] {"false"});
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText pass = findViewById(R.id.etPassword);
//                pass.setText("");
//                AcceptedGDActivity.this.finish();
//                System.exit(0);
                Intent i = new Intent(AcceptedGDActivity.this, MainActivity.class);
                startActivity(i);
            }

        });

    }


    @Override
    public void onStart() {
        super.onStart();
        sharedPreferences1 = getSharedPreferences("MySharedPref1", MODE_PRIVATE);
        String userId = "";
        userId = sharedPreferences1.getString("user_id", "");
        initializeCustomGDList(userId);
    }


    private void initializeCustomGDList(String userId){


        KeyValueDB db = new KeyValueDB(this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs where acceptedvalue = 'Accepted'");
        if(rows.getCount() == 0) {
            return;
        }


        gds = new ArrayList<>();

        while(rows.moveToNext()) {
            String key = rows.getString(0);
            String gdData = rows.getString(1);
            String status = rows.getString(2);
            String[] fieldValues = gdData.split(":-;-:");

            String datetime = fieldValues[0];
            String police_station = fieldValues[1];
            String full_name = fieldValues[2];
            String Nid = fieldValues[3];
            String address = fieldValues[4];
            String number = fieldValues[5];
            String description = fieldValues[6];

            GD g = new GD(key, datetime, police_station, full_name, Nid, address, number, description, status);
            if (thanaList.contains(userId)) {
                if (userId.equals(g.police_station)) {
                    gds.add(g);
                }
            }
            else if (userId.equals(g.Nid)) {
                gds.add(g);
            }
//            gds.add(g);
        }
        db.close();


        adapter = new CustomGDAdapter(this, gds);
        lvGDs.setAdapter(adapter);


        lvGDs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//                String item = (String) parent.getItemAtPosition(position);
                System.out.println(position);

                Intent i = new Intent(AcceptedGDActivity.this, GdDetail.class);
                i.putExtra("GDKey", gds.get(position).key);
                startActivity(i);
            }
        });

//        lvGDs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                String message = "Do you want to delete GD of "+gds.get(position).full_name +"?";
//                showDialog(message, "Delete GD", position);
//                return true;
//            }
//        });
    }

//    private void showDialog(String message, String title, int position){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        //Uncomment the below code to Set the message and title from the strings.xml file
//        builder.setMessage(message);
//        builder.setTitle(title);
//
//        //Setting message manually and performing action on button click
//        builder.setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        String userId = "";
//                        userId = sharedPreferences1.getString("user_id", "");
//                        ArrayList<String> thanaList = (ArrayList<String>) getIntent().getSerializableExtra("key");
//                        if (userId.equals(gds.get(position).Nid)) {
//                            Util.getInstance().deleteByKey(AcceptedGDActivity.this, gds.get(position).key);
//                            httpRequest2(new String[] {"key"}, new String[] {gds.get(position).key});
//                            dialog.cancel();
//                            initializeCustomGDList(userId);
//                            adapter.notifyDataSetChanged();
////                            lvGDs.notifyAll();
//                        }
//
//
////                        else if (userId.equals(g.police_station)) {
////                            Util.getInstance().deleteByKey(AcceptedGDActivity.this, gds.get(position).key);
////                            httpRequest2(new String[] {"key"}, new String[] {gds.get(position).key});
////                            dialog.cancel();
////                            initializeCustomGDList(userId);
////                            adapter.notifyDataSetChanged();
////                         lvGDs.notifyAll();
////                        }
//                        else {
//                            showDialog1("You cannot delete this GD!", "Info", "ok", false);
//                        }
//                        Util.getInstance().deleteByKey(AcceptedGDActivity.this, gds.get(position).key);
//                        httpRequest2(new String[] {"key"}, new String[] {gds.get(position).key});
//                        dialog.cancel();
//                        initializeCustomGDList(userId);
//                        adapter.notifyDataSetChanged();
//
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        //Setting the title manually
////        alert.setTitle("Error Dialog");
//        alert.show();
//
//    }

    private void showDialog1(String message, String title, String buttonLabel, boolean closeDialog){
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
//                        else {
//
//                        }
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
                    JSONObject jObj = JSONParser.getInstance().makeHttpRequest("http://10.0.2.2:80/Online_GD/sync.php", "POST", params);
                    return jObj;
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
                        JSONArray eventsJson = jObj.getJSONArray("gds");
                        for(int i=0; i<eventsJson.length(); i++){
                            JSONObject row = eventsJson.getJSONObject(i);
                            String key = row.getString("key");
                            String value = row.getString("value");
                            String status = row.getString("status");

                            if(!key.isEmpty() && !value.isEmpty()) {
                                Util.getInstance().setKeyValue(AcceptedGDActivity.this, key, value, status);
                            }
                        }
                        initializeCustomGDList(sharedPreferences1.getString("user_id", ""));
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }

//    @SuppressLint("StaticFieldLeak")
//    private void httpRequest2(final String keys[], final String values[]) {
//        new AsyncTask<Void, Void, JSONObject>() {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected JSONObject doInBackground(Void... param) {
//                try {
//                    List<NameValuePair> params = new ArrayList<NameValuePair>();
//                    for(int i=0; i<keys.length; i++) {
//                        params.add(new BasicNameValuePair(keys[i], values[i]));
//                    }
//                    JSONObject jObj = JSONParser.getInstance().makeHttpRequest("http://10.0.2.2:80/Online_GD/delete.php", "POST", params);
//                    return jObj;
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//                return null;
//            }
//            @Override
//            protected void onPostExecute(JSONObject jObj) {
//                String msg = "Failed to send request";
//                if(jObj != null) {
//                    try {
//                        JSONArray eventsJson = jObj.getJSONArray("gds");
//                        for(int i=0; i<eventsJson.length(); i++){
//                            JSONObject row = eventsJson.getJSONObject(i);
//                            String key = row.getString("key");
//                            String value = row.getString("value");
//                            String status = row.getString("status");
//
//                            if(!key.isEmpty() && !value.isEmpty()) {
//                                Util.getInstance().setKeyValue(AcceptedGDActivity.this, key, value, status);
//                            }
//                        }
//                        initializeCustomGDList(sharedPreferences1.getString("user_id", ""));
//                        adapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }.execute();
//    }

}