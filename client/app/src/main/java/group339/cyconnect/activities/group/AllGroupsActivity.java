package group339.cyconnect.activities.group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;
/**
 * Activity holding AllGroupsFragment
 */
public class AllGroupsActivity extends AppCompatActivity {
    @Override
    // Method to create the layout of this activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_groups);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of menu item selected and go to its associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(AllGroupsActivity.this,id);
        //noinspection SimplifiableIfStatement
        if (intent!=null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}