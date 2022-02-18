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


public class AllGDActivity extends Activity {

    private ListView lvGDs;
    private ArrayList<GD> gds;
    private CustomGDAdapter adapter;
    private SharedPreferences sharedPreferences1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences1 = getSharedPreferences("MySharedPref1", MODE_PRIVATE);
        setContentView(R.layout.activity_all_gd);

        lvGDs = findViewById(R.id.lvGDs);



        findViewById(R.id.btnMakeGD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(AllGDActivity.this, MakeGDActivity.class);
                startActivity(i);

            }
        });

        findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpRequest(new String[] {"is_future"}, new String[] {"false"});
            }
        });

        findViewById(R.id.btnAccectedGD).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(AllGDActivity.this, AcceptedGDActivity.class);
                startActivity(i);

            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText pass = findViewById(R.id.etPassword);
//                pass.setText("");
//                PendingGDActivity.this.finish();
//                System.exit(0);
                Intent i = new Intent(AllGDActivity.this, MainActivity.class);
                startActivity(i);
            }

        });

    }


    @Override
    public void onStart() {
        super.onStart();
        initializeCustomGDList();
    }


    private void initializeCustomGDList(){

        KeyValueDB db = new KeyValueDB(this);
        Cursor rows = db.execute("SELECT * FROM key_value_pairs");
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

            String userId = "";
            userId = sharedPreferences1.getString("user_id", "");

//            ArrayList<String> thanaList = (ArrayList<String>) getIntent().getSerializableExtra("key");
            MainActivity num;
            num = new MainActivity();
            ArrayList<String> thanaList = num.getArrayList();

            if (userId.equals(g.Nid)) {
                gds.add(g);
            }
//                for(int i=0; i<thanaList.size(); i++){
//                    if (userId.equals(thanaList.get(i))) {
//                        gds.add(g);
//                        break;
//                    }
//                }
            else if (userId.equals(g.police_station)) {
                    gds.add(g);
            }

//            else if (!userId.equals("thana")) {
//                showDialog1("Log in successful", "Info", "ok", false);
//            }
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

                Intent i = new Intent(AllGDActivity.this, GdDetail.class);
//                i.putExtra("GDKey", gds[position].key);
//                Intent i = new Intent(PendingGDActivity.this, GdDetail.class);
                i.putExtra("GDKey", gds.get(position).key);
                startActivity(i);
            }
        });

        lvGDs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String message = "Do you want to delete GD of "+gds.get(position).full_name +"?";
                showDialog(message, "Delete GD", position);
                return true;
            }
        });
    }

    private void showDialog(String message, String title, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(message);
        builder.setTitle(title);

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            Util.getInstance().deleteByKey(AllGDActivity.this, gds.get(position).key);
                            httpRequest2(new String[] {"key"}, new String[] {gds.get(position).key});
                            dialog.cancel();
                            initializeCustomGDList();
                            adapter.notifyDataSetChanged();
//                            lvGDs.notifyAll();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
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
                                Util.getInstance().setKeyValue(AllGDActivity.this, key, value, status);
                            }
                        }
                        initializeCustomGDList();
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void httpRequest2(final String keys[], final String values[]) {
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
                    JSONObject jObj = JSONParser.getInstance().makeHttpRequest("http://10.0.2.2:80/Online_GD/delete.php", "POST", params);
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
                                Util.getInstance().setKeyValue(AllGDActivity.this, key, value, status);
                            }
                        }
                        initializeCustomGDList();
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute();
    }

}