package group339.cyconnect.activities.event;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;
/**
 * Activity to hold AllEventsFragment
 */
public class AllEventsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of menu item selected and jump to its associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(AllEventsActivity.this, id);
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}