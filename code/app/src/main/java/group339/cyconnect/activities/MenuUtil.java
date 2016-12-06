package group339.cyconnect.activities;

import android.content.Context;
import android.content.Intent;

import group339.cyconnect.R;
import group339.cyconnect.activities.account.AccountActivity;
import group339.cyconnect.activities.book.BookActivity;
import group339.cyconnect.activities.event.EventActivity;
import group339.cyconnect.activities.group.GroupActivity;

/**
 * Helper class with one method, getMenuIntent, which gets the intent needed when selecting an item in the dropdown menu
 * Created by Jacob on 10/30/2015.
 */
public class MenuUtil {
    /**
     * Gets the intent used to jump to a new activity based on the MenuItem selected
     * @param ctx Context of the application
     * @param id Id of the MenuItem selected
     * @return
     */
    public static Intent getMenuIntent(Context ctx, int id) {
        //If the Events option selected, go to EventActivity
        if (id == R.id.action_event) {
            return new Intent(ctx, EventActivity.class);
        }
        //If the Groups option is selected, go to GroupActivity
        else if (id == R.id.action_group) {
            return new Intent(ctx, GroupActivity.class);
        }
        //If the Books option selected, go to BookActivity
        else if (id == R.id.action_book) {
            return new Intent(ctx, BookActivity.class);
        }
        //If the Account option selected, go to AccountActivity
        else if (id == R.id.action_account) {
            return new Intent(ctx, AccountActivity.class);
        }
        //If one of the available options is not selected, return null and the calling class will call its super method for handling click
        else {
            return null;
        }
    }
}