package group339.cyconnect.activities.book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;

/**
 * Activity to hold AllBooksActivityFragment
 */
public class AllBooksActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of menu item selected, find intent to jump to, and then go to it if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(AllBooksActivity.this,id);
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}