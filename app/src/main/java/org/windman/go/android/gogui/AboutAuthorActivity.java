package org.windman.go.android.gogui;

import org.windman.go.MainActivity;
import org.windman.go.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class AboutAuthorActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_author_layout);
		
		setTitle(R.string.about_author);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        
        case android.R.id.home:
        	finish();
        	startActivity(new Intent(this, MainActivity.class));
            break;
            
        default:
        	break;
        }
		return true;
    }
}
