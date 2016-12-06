package group339.cyconnect.activities.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;
/**
 * Activity for holding ChatActivityFragment
 */
public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of item selected in menu and go to its associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(ChatActivity.this, id);
        if (intent!=null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}