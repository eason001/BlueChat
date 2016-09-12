package com.example.bluemessenger;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	protected final int REQUEST_ENABLE_BT = 1 ;
	public final static String EXTRA_MESSAGE = "com.example.bluemessenger.MESSAGE";
	protected BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	private Switch Tb;
	public static boolean p2p_mode = false;
	private SeekBar Sb;
	public static int progress = 5;

	public MainActivity(){
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Sb = (SeekBar) findViewById(R.id.interval);
		Tb = (Switch) findViewById(R.id.P2P);
		while(!CheckBT()){
			Toast.makeText(getApplicationContext(), "~Please active your BlueTooth~", Toast.LENGTH_LONG).show();
		};
		final Button button = (Button) findViewById(R.id.login);
		final EditText username = (EditText) findViewById(R.id.username);
	
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(username.length()==0 || username.equals(" ")){
            		Toast.makeText(getApplicationContext(), "Please enter an username", Toast.LENGTH_LONG).show();
            	}else{
	            	if(username.length()<=6) {
	            	Intent intent = new Intent(MainActivity.this, ChatRoom.class);
	            	intent.putExtra(EXTRA_MESSAGE, username.getText().toString());
	                startActivity(intent);
	            	}else {
	                    Toast.makeText(getApplicationContext(), "Max 6 characters", Toast.LENGTH_LONG).show();
	                    username.setText("");
	                }
            	}
            }
        });
        
        Tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	 
        	   @Override
        	   public void onCheckedChanged(CompoundButton buttonView,
        	     boolean isChecked) {
        	 
        	    if(isChecked){
        	     p2p_mode = true;
        	    }else{
        	     p2p_mode = false;
        	    }
        	 
        	   }
        });
        
        
        Sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			  
			  @Override
			  public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				  if(progresValue == 0){
					  progress = 5;
				  }else{
					  progress = progresValue + 5;
				  }
				//  Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			  }
			
			  @Override
			  public void onStartTrackingTouch(SeekBar seekBar) {
				//  Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
			  }
			
			  @Override
			  public void onStopTrackingTouch(SeekBar seekBar) {
				//  Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
			  }
		   });
	}

	public static boolean getP2P(){
		return p2p_mode;
	}
	
	public static int getInterval(){
		return progress;
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public boolean CheckBT(){
		boolean flag = false;
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(), "Sorry, your device does not support BlueTooth", Toast.LENGTH_LONG).show();
		}else if (!mBluetoothAdapter.isEnabled()) {
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
					startActivity(discoverableIntent);
		    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		    flag = true;
		}else {
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivity(discoverableIntent);
	        	Toast.makeText(getApplicationContext(), "Bluetooth activated~", Toast.LENGTH_SHORT).show();	
	        	flag = true;
		}
		
		return flag;
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REQUEST_ENABLE_BT) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	Toast.makeText(getApplicationContext(), "Bluetooth activated!", Toast.LENGTH_SHORT).show();
	        }
	        if (resultCode == RESULT_CANCELED) {
	        	Toast.makeText(getApplicationContext(), "Turn on Bluetooth to continue!", Toast.LENGTH_LONG).show();
	        }
	    }
	}

	

}
