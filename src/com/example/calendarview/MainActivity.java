package com.example.calendarview;

import com.example.calendarview.CalendarController.EventHandler;
import com.example.calendarview.CalendarController.EventInfo;
import com.example.calendarview.CalendarController.EventType;
import com.example.calendarview.recurrencepicker.EventRecurrence;
import com.example.calendarview.recurrencepicker.RecurrencePickerDialog;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class MainActivity extends Activity implements EventHandler, RecurrencePickerDialog.OnRecurrenceSetListener{
	
	private static final String FRAG_TAG_RECUR_PICKER = "recurrencepicker";
	private EventRecurrence mEventRecurrence = new EventRecurrence();
	
	private CalendarController mController;
	Fragment monthFrag;
	Fragment dayFrag;
	private EventInfo event;
	private boolean dayView;
	private long time;
	private long eventID;
	boolean eventView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//new ImportEntries().execute(this);
		mController = CalendarController.getInstance(this);
		setContentView(R.layout.cal_layout);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
				
		monthFrag = new MonthByWeekFragment(System.currentTimeMillis(), false);
        ft.replace(R.id.cal_frame, monthFrag).commit();
        mController.registerEventHandler(R.id.cal_frame, (EventHandler) monthFrag);
        
        mController.registerFirstEventHandler(0, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public long getSupportedEventTypes() {
		return EventType.GO_TO | EventType.VIEW_EVENT | EventType.UPDATE_TITLE;
	}

	@Override
	public void handleEvent(EventInfo event) {
		if (event.eventType == EventType.GO_TO) {
			this.event = event;
			dayView = true;
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				dayFrag = new DayFragment(event.startTime.toMillis(true),1);
				ft.replace(R.id.cal_frame, dayFrag).addToBackStack(null).commit();
		}if(event.eventType == EventType.VIEW_EVENT){
			//TODO do something when an event is clicked
					dayView = false;
					eventView = true;
					this.event = event;
//					FragmentTransaction ft = getFragmentManager().beginTransaction();
//					edit = new EditEvent(event.id);
//					ft.replace(R.id.cal_frame, edit).addToBackStack(null).commit();

			
		}
		
	}

	@Override
	public void eventsChanged() {
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "click en settings", Toast.LENGTH_LONG).show();
		
		FragmentManager fm = getFragmentManager();
        RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm
                .findFragmentByTag(FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.dismiss();
        }
		rpd = new RecurrencePickerDialog();
		rpd.setOnRecurrenceSetListener(MainActivity.this);
        rpd.show(fm, FRAG_TAG_RECUR_PICKER);
		
		return true;
	}
	
	@Override
    public void onRecurrenceSet(String rrule) {
		mEventRecurrence.parse(rrule);
        populateRepeats();
    }

	private void populateRepeats() {
        // TODO: populate the calendar with the RRULE string
    }
}
