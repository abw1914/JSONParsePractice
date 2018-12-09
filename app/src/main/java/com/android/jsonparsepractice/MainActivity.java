package com.android.jsonparsepractice;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    private ListView listView;

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        listView = findViewById(R.id.list);

        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,
                    "JSON Data is downloading", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... args0) {
            NetworkUtils networkUtils = new NetworkUtils();
            String url = "http://api.androidhive.info/contacts/";
            String JsonString = networkUtils.makeServiceCall(url);

            Log.e(LOG_TAG, "Response from JSON:  " + JsonString);

            if (JsonString != null) {
                try {
                    JSONObject contactsRoot = new JSONObject(JsonString);

                    JSONArray contacts = contactsRoot.getJSONArray("contacts");

                    /**
                     * Loop through all contacts coming from the API
                     */

                    for (int i = 0; i < contacts.length(); i++) {

                        JSONObject jsonObject = contacts.getJSONObject(i);
                        
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        String address = jsonObject.getString("address");
                        String gender = jsonObject.getString("gender");

                        /**
                         * Phone node is a JSON Object
                         */
                        JSONObject phone = jsonObject.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        /**
                         * This is a temporary hold of data within a Hashmap
                         */

                        HashMap<String, String> contactHold = new HashMap<>();

                        contactHold.put("id", id);
                        contactHold.put("name", name);
                        contactHold.put("email", email);
                        contactHold.put("mobile", mobile);

                        contactList.add(contactHold);
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json Parsing Error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json Parsing Error ", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {
                Log.e(LOG_TAG, "Can not get JSON from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Can not get JSON from Server", Toast.LENGTH_LONG).show();
                    }
                });
            }
                return null;
            }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "email","mobile"},
                    new int[]{R.id.email, R.id.mobile});
            listView.setAdapter(adapter);
            }
        }
    }

