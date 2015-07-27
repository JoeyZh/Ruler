package com.joey.ruler;

import com.joey.ruler.library.Ruler;
import com.joey.ruler.library.RulerHandler;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Ruler ruler;
	TextView result;
	EditText editText;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ruler = (Ruler)findViewById(R.id.ruler);
		result = (TextView)findViewById(R.id.result_text);
		ruler.setRulerHandler(rulerHandler);
		editText = (EditText)findViewById(R.id.edit_text);
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(clickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private RulerHandler rulerHandler = new RulerHandler() {
		
		@Override
		public void markScrollto(int max, int min, float val) {
			// TODO Auto-generated method stub
			int hour = max;
			int minute = min*6 + (int)(val *6);
			result.setText(String.format("%02d:%02d", hour,minute));
		}
	};
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("MainActivity","onClick");
			String msg = editText.getText().toString();
			if(msg == null ||msg.isEmpty())
				return;
			result.setText(msg);
			ruler.scrollToTime(msg);
		}
	};
}
