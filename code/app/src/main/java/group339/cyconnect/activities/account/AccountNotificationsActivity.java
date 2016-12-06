package group339.cyconnect.activities.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;
/**
 * Activity wrapped around NotificationsFragment
 */
public class AccountNotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_notifications);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of item in menu selected
        int id = item.getItemId();
        //Get intent for menu item
        Intent intent = MenuUtil.getMenuIntent(AccountNotificationsActivity.this, id);
        //If there is an intent, jump to activity
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}