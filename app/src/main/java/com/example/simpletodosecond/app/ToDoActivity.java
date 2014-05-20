package com.example.simpletodosecond.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class ToDoActivity extends Activity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    static final int REQUEST_LINK_TO_DBX = 0;
    private final int REQUEST_CODE = 20;
    private DbxAccountManager mDbxAcctMgr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        items = new ArrayList <String>();
        //find list view
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), "65vwxcxq5cnsl72", "ue111fdgv4056al");
        lvItems = (ListView) findViewById(R.id.lvitems);
        items.add("First item");
        items.add("second item");
        loadItems();

        //Instantiate and create adapter
        itemsAdapter = new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, items);

        //bind adapters to list view
        lvItems.setAdapter(itemsAdapter);


        //Setup listener for removing an item
        setupListViewListenter();
        setupEditItemListener();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.to_do, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTodoItem(View view) {
        EditText eNewItem = (EditText) findViewById(R.id.edtitem);
        String text = eNewItem.getText().toString();
        if (text.length() > 0) {
          itemsAdapter.add(eNewItem.getText().toString());
        }
        eNewItem.setText("");
        saveItems();
    }

    private void setupListViewListenter(){
        //Binding during runtime, event driven programming, programatically defining the click pattern
        this.lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView <?> parent, View view, int position, long rowId) {
                //remove the item
               items.remove(position);
               itemsAdapter.notifyDataSetChanged();
               return true;
            }

        });


    }

    public void setupEditItemListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                // Launch the edit text view and pass the text
                Intent i = new Intent(ToDoActivity.this, EditItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("text", itemsAdapter.getItem(position));
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    public void linkToDbx(View view) {
        if (!mDbxAcctMgr.hasLinkedAccount()){
            mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
        }
        Toast.makeText((Activity)this, "Successfully Linked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                try{
                    DbxPath appPath =  new DbxPath("app.txt");
                    DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
                    DbxFile appFile1 = dbxFs.create(appPath);
                    DbxFile appFile = dbxFs.open(appPath);
                    Gson gson = new Gson();
                    String json = gson.toJson(items);
                    appFile.writeString(json);
                } catch(DbxException.Unauthorized e){
                    Log.e(getLocalClassName(), "not authorized", e);
                } catch (IOException e){
                    Log.e(getLocalClassName(), "unable to write to file", e);
                }

                Toast.makeText((Activity)this, "Successfully Linked and created file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText((Activity)this, "Link Failed", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String name = data.getExtras().getString("name");
            int position = data.getExtras().getInt("position");
            items.set(position, name);
            saveItems();
            itemsAdapter.notifyDataSetChanged();


        }
    }

    private void loadItems() {

        DbxFile dbxFile = getFile();
        if (dbxFile == null){
            return;
        }
        try {
            Gson gson = new Gson();
            items = gson.fromJson(dbxFile.readString(), ArrayList.class);
        } catch (IOException e) {
            items = new ArrayList<String>();
            Log.w(getLocalClassName(), "unable to load items", e);
        } finally{
            dbxFile.close();
        }
    }

    private void saveItems() {
        DbxFile dbxFile = getFile();
        if (dbxFile == null){
            return;
        }
        try {
            Gson gson = new Gson();
            String json = gson.toJson(items);
            dbxFile.writeString(json);
        } catch (IOException e) {
            Log.e(getLocalClassName(), "unable to save items", e);
        }finally{
            dbxFile.close();
        }
    }

    private DbxFile getFile() {
        DbxFileSystem dbxFs = null;
        try{
           if (mDbxAcctMgr.hasLinkedAccount()){
              dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            }else{
                return null;
            }
        } catch(DbxException.Unauthorized e){
            Log.e(getLocalClassName(), "not authorized", e);
            return null;
        }
        DbxPath appPath =  new DbxPath("app.txt");
        DbxFile appFile = null;
        try {
         appFile = dbxFs.open(appPath);
        } catch (DbxException e2) {
              Log.e(getLocalClassName(), "unable to open file", e2);
        }

        return appFile;
    }


}
