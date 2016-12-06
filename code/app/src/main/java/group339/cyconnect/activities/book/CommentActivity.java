package group339.cyconnect.activities.book;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;
/**
 * Activity for holding CommentActivityFragment
 */
public class CommentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_book);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get id of item selected in menu and jump to its associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(CommentActivity.this, id);
        if (intent!=null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}