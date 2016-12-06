package group339.cyconnect.activities.book;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import group339.cyconnect.R;
import group339.cyconnect.activities.MenuUtil;

/**
 * "Home Page" activity for Books: from here you can find existing books and create a new book
 */
public class BookActivity extends AppCompatActivity {
    //Button for creation of a new book
    Button createBook;
    //Button for jumping to list of books
    Button viewBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        //Get createBook button and set its OnClickListener
        createBook = (Button) findViewById(R.id.create_book);
        createBook.setOnClickListener(createScreen);
        //Get viewBooks Button and set its OnClickListener
        viewBooks = (Button) findViewById(R.id.show_books);
        viewBooks.setOnClickListener(viewScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get id of item selected in menu and jump to the associated activity if it exists
        int id = item.getItemId();
        Intent intent = MenuUtil.getMenuIntent(BookActivity.this, id);
        if(intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a on click listener to go to CreateBookActivity
     */
    private View.OnClickListener createScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Jump to CreateBookActivity
            Intent createBookIntent = new Intent(BookActivity.this,
                    CreateBookActivity.class);
            startActivity(createBookIntent);
        }
    };

    /**
     * Create a on click listener to go to the AllBooksActivity
     */
    private View.OnClickListener viewScreen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Jump to AllBooksActivity
            Intent listBooksActivityIntent = new Intent(BookActivity.this,
                    AllBooksActivity.class);
            startActivity(listBooksActivityIntent);
        }
    };

}