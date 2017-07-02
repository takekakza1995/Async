package com.example.take.async;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ListView listView;
    ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview_book);


     /*   listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(MainActivity.this,books.get(position).getTitle(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String title = books.get(position).getTitle();
                Toast.makeText(MainActivity.this,title,Toast.LENGTH_SHORT).show();


            }
        });



        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<Void,Void,JSONObject>
                                            //doIn//onPro//onPost
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String MAX_RESULTS = "maxResults";
            final String PRINT_TYPE = "printType";

            Uri builtURI = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, "pride+prejudice")
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();


            URL requestURL = null;
            try {
                requestURL = new URL(builtURI.toString());
                HttpURLConnection conn = null;
                conn =(HttpURLConnection) requestURL.openConnection();
                conn.setReadTimeout(10000 /*milli*/);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                InputStream is = conn.getInputStream();


                BufferedReader r = new BufferedReader(new InputStreamReader(is)); //Read is Value and save in buffer
                StringBuilder total = new StringBuilder();
                String line;                                   //read buffer
                while ((line = r.readLine()) != null)           //line to line
                {
                    total.append(line) .append('\n');
                }
                String stringResult = total.toString();  //convert total value to string

                Log.i("json",stringResult);
                JSONObject jsonResult = new JSONObject(stringResult);

                // JSONArray itemArray = jsonObject.getJSONArray("items");
                Log.i("json",jsonResult.getString("kind"));
                Log.i("json",jsonResult.getString("items"));
                return jsonResult;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonResult)
        {
            super.onPostExecute(jsonResult);

            try {
                JSONArray itemJson = jsonResult.getJSONArray("items");
                books = new ArrayList<>();
                //foreach not support JsonArray
                for (int i=0; i<itemJson.length();i++)
                {
                    JSONObject jsonBook = (itemJson.getJSONObject(i));
                    JSONObject jsonInfo = (jsonBook.getJSONObject("volumeInfo"));
                    JSONObject jsonSaleInfo = (jsonBook.getJSONObject("saleInfo"));
                   // String title = jsonBook.getJSONObject("volumeInfo").getString("title");
                    String title = (jsonInfo.getString("title"));
                    Book book = new Book(title);
                    book.setAuthors(jsonInfo.getJSONArray("authors").toString());

                   /* double price = 0;
                    if (jsonSaleInfo.getJSONObject("listPrice") != null)
                    {
                        price = jsonSaleInfo.getJSONObject("listPrice").getDouble("amount");
                    }
                    book.setPrice();*/
                    book.setPrice(jsonInfo.getDouble("pageCount"));
                            //set price ดึงค่าจาก listPrice ที่อยู่ใน jsonSaleInfo ที่ถูกดึงมาจาก jsonBook

                    books.add(book); //เอาค่า ที่ get มา ไปยัดใส่ Array ที่สร้างไว้ line 114

                }


                ListBookAdap adap = new ListBookAdap(MainActivity.this,R.layout.edit_listbook,books);
                listView.setAdapter(adap);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }

}
