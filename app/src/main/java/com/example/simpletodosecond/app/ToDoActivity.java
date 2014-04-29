package com.example.simpletodosecond.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ToDoActivity extends Activity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        items = new ArrayList <String>();
        //find list view
        lvItems = (ListView) findViewById(R.id.lvitems);

        items.add("First item");
        items.add("second item");

        //Instantiate and create adapter
        itemsAdapter = new ArrayAdapter <String> (this, android.R.layout.simple_list_item_1, items);

        //bind adapters to list view
        lvItems.setAdapter(itemsAdapter);

        //Setup listener for removing an item
        setupListViewListenter();

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

}
