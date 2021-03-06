package com.example.bluemessenger;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ChatRoom extends ActionBarActivity {
	
	protected BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	protected Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	private ArrayList<user> DeviceList = new ArrayList<user>();
	private static ReentrantLock lock = new ReentrantLock();
	//private ListView DL;
	ListView dialogList;
	ListView userList;
	//ChatArrayAdapter dialogAdapter;
	ArrayAdapter<String> dialogAdapter;
	ArrayAdapter<String> userAdapter;
	private Button send;
	private EditText message;	
	private String myname;
	private final ArrayList<String> userlist = new ArrayList<String>();
	private int pointer = 0;
	private boolean flag = false;
	private String mymess;
	//MainActivity ma = new MainActivity();
	Map <String,String> userMap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices);
		Intent intent = getIntent();
		userMap =new HashMap<String, String>();
		
		//Views
		
	    myname = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    message = (EditText) findViewById(R.id.message);
		send = (Button) findViewById(R.id.send);
		dialogList = (ListView) findViewById(R.id.dialogList);
        userList = (ListView) findViewById(R.id.userList);
	//	dialogAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        dialogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        dialogList.setAdapter(dialogAdapter);
        userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        userList.setAdapter(userAdapter);
        // Filters
	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	    filter.addAction(BluetoothDevice.ACTION_UUID);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
	    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	    registerReceiver(ActionFoundReceiver, filter);
	    //Discover
	    ChangeName();
	  /*  Timer t = new Timer();
	    t.scheduleAtFixedRate(new TimerTask() {

	        @Override
	        public void run() {
	        	  CheckBT();	 
	        }

	    }, 0, 1000);*/
	  //  Toast.makeText(getApplicationContext(), MainActivity.getInterval(), Toast.LENGTH_SHORT).show();
	    mBluetoothAdapter.startDiscovery();
	   
		 
	}
	
	
	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
	     
	    @Override
	    public void onReceive(Context context, Intent intent) {

	     String action = intent.getAction();
	     if(BluetoothDevice.ACTION_FOUND.equals(action)) {
	       BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	       updateDevices(device);         
	     } 
	     
	     if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	         
	         } else {
	           if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	            
	             /*if(!flag){
	        	   ChangeName();
	        	   Toast.makeText(getApplicationContext(), "welcome~ ", Toast.LENGTH_LONG).show();
	        	   flag=true;
	             }else{*/
	             CheckMac(); //Update userMap
	             UpdateUserList();
	             UpdateDialogList();
	             CheckBT();	 
	             if (MainActivity.getP2P()){
		             if (!flag){
			             new Thread(new Runnable()
		                 {
		                     @Override
		                     public void run()
		                     {
		                    	// dialogAdapter.add("1");
		                    	// dialogAdapter.notifyDataSetChanged();	
		                    	 updateName();
		                     }
		                 }).start();
		             }
	             }
	     		 btDeviceList.clear();
	           //  }
	             mBluetoothAdapter.startDiscovery();
	             
	           }
	         }
	     
	    }
	  };
	  
	  
	  
	public void sendDialog(View view) throws InterruptedException{
		if(message.length()>0) {
			flag = true;
			mymess = message.getText().toString();			
			//dialogAdapter.add(new ChatMessage(false,myname + " : " + mymess));
			dialogAdapter.add(myname + " : " + mymess);
			dialogAdapter.notifyDataSetChanged();	
			lock.lock();
			try{
				new Thread(new Runnable()
		        {
		            @Override
		            public void run()
		            {
		            	send.setClickable(false);
		            	startSend(mymess);
		            	send.setClickable(true);
		            }
		        }).start();
			}finally{lock.unlock();}
			message.setText("");
			flag = false;
		}
	}
	
	private void startSend(String mymess){
		
		int numberSeg = 1;
		Random rand = new Random();
		
		
		
			if (mymess.length()<=20 && !mymess.equals("")){				
				int randomNum = rand.nextInt(99) + 1;
				String msn = getString(R.string.app_identifier) + getString(R.string.delimiter) + 1 + getString(R.string.delimiter) + 1 + getString(R.string.delimiter) + myname + getString(R.string.delimiter) + mymess + getString(R.string.delimiter) + randomNum;
		//		dialogAdapter.add("set name" + " : " + msn);
				mBluetoothAdapter.setName(msn);
			
				try
	            {
	                Thread.sleep(MainActivity.getInterval()*1000);                
	            } catch (InterruptedException ex)
	            {
	                Thread.currentThread().interrupt();
	            }
			}
			
			if (mymess.length()>20 && !mymess.equals("")){				
				int randomNum = rand.nextInt(99) + 1;
				numberSeg = 1 + mymess.length()/20;
			
				String auxmsn = "";
				for (int i=0;i<numberSeg;i++){
					int currentSeg = i+1;
					if (i!=numberSeg-1){
						auxmsn = mymess.substring(i*20, (i+1)*20);
					}else{
						auxmsn = mymess.substring(i*20, mymess.length());
					}
					String msn = getString(R.string.app_identifier) + getString(R.string.delimiter) + currentSeg + getString(R.string.delimiter) + numberSeg + getString(R.string.delimiter) + myname + getString(R.string.delimiter) + auxmsn + getString(R.string.delimiter) + randomNum;
					mBluetoothAdapter.setName(msn);
			
					try
		           {
		               Thread.sleep(MainActivity.getInterval()*1000);                
		            } catch (InterruptedException ex)
		            {
		                Thread.currentThread().interrupt();
		            }
				}
			}
		
		
		
	}
	
	private void updateName(){
		Random rand = new Random();	
		int ID = 0;
		String brocastname = "";
		String brocastmsn = "";
		int currentSeg = 0;
		int finalSeg = 0;
		if(btDeviceList.size()!=0){
			
		    int randomNum = rand.nextInt(btDeviceList.size())-1;
		    if (randomNum<0){randomNum=0;}
		    BluetoothDevice device = btDeviceList.get(randomNum);
		    String content[] = device.getName().split(getString(R.string.delimiter));
		    currentSeg = Integer.parseInt(content[1]);
		    finalSeg = Integer.parseInt(content[2]);
		    if (content.length==6){
		    	ID = Integer.parseInt(content[5]);
		    	brocastname = content[3];
		    	brocastmsn = content[4];
		    }else{
		    	ID = Integer.parseInt(content[6]);
		    	brocastname = content[4];
		    	brocastmsn = content[5];
		    }
		 //   dialogAdapter.add("2 : " + ID);
		//	dialogAdapter.notifyDataSetChanged();	
		    if (ID!=0){
		    //	dialogAdapter.add("I am here: " + device.getName());
		    //	dialogAdapter.notifyDataSetChanged();	
		    	mBluetoothAdapter.setName(getString(R.string.app_identifier) + getString(R.string.delimiter) + currentSeg + getString(R.string.delimiter) + finalSeg + getString(R.string.delimiter) + myname + getString(R.string.delimiter) + brocastname + getString(R.string.delimiter) + brocastmsn + getString(R.string.delimiter) + ID);
		    }
		    try
	           {
	               Thread.sleep(MainActivity.getInterval()*1000);                
	            } catch (InterruptedException ex)
	            {
	                Thread.currentThread().interrupt();
	            }
		    mBluetoothAdapter.setName(getString(R.string.app_identifier) + getString(R.string.delimiter) + 0 + getString(R.string.delimiter) + 0 + getString(R.string.delimiter) + myname + getString(R.string.delimiter) + "" + getString(R.string.delimiter) + 0);
		}
		//btDeviceList.clear();
	}
	  

	
	private void CheckMac(){
	//boolean disconnect = true;
		Map <String,String> auxMap = new HashMap();
		for (Map.Entry<String, String> entry : userMap.entrySet()){	
			for (BluetoothDevice device:btDeviceList){
				if(device.getAddress().equals(entry.getKey())){
					auxMap.put(entry.getKey(),entry.getValue());
					//disconnect = false;
				}	
			}
			/*if(disconnect){
				auxMap.remove(entry.getKey());
			}*/
		}
		userMap = auxMap;
	}

	protected void UpdateUserList(){
		userAdapter.clear();
		for (BluetoothDevice device: btDeviceList){
			String content[] = device.getName().split(getString(R.string.delimiter));
				userAdapter.add(content[3]);			
		}		
	}
	
	protected void UpdateDialogList(){
		boolean exist = false;
		int counter = 0;
		int[] removeList = new int[DeviceList.size()];
		for (user x: DeviceList){
		//	dialogAdapter.add("complete: " + " : " + x.isComplete());
			if (x.isComplete()){
				long time = (System.currentTimeMillis() - x.getIniTimer())/1000;
				String auxtime = time + " secs";
				exist = false;
				String msn = x.getName() + " : " + x.getMessage();
				for(int i=0;i<dialogAdapter.getCount();i++){
					//dialogAdapter.add(new ChatMessage(true,"msn : " + msn));
					//dialogAdapter.add(new ChatMessage(true,"adapter : " + dialogAdapter.getItem(i).toString()));
					if (dialogAdapter.getItem(i).toString().equals(msn)){
						exist=true;
					}
				}
				if (!exist){
					//dialogAdapter.add(new ChatMessage(true,x.getName() + " : " + x.getMessage()));
					dialogAdapter.add(x.getName() + " : " + x.getMessage());
					dialogAdapter.notifyDataSetChanged();
					Toast.makeText(getApplicationContext(), auxtime, Toast.LENGTH_SHORT).show();
				}
				removeList[counter] = 1;
			}else{
				removeList[counter] = 0;
			}		
			counter++;
		}
		for (int i=DeviceList.size();i>0;i--){
			if(removeList[i-1]==1){
				DeviceList.remove(i-1);
			}
		}
		
	}
	
	private void updateDevices(BluetoothDevice device){
		 if((device.getName() != null) && device.getName().startsWith("‡")){ 
			 btDeviceList.add(device);
			 parse(device);
		 }
	}
	
	private void parse(BluetoothDevice device){

		String macID = device.getAddress();	
		String content[] = device.getName().split(getString(R.string.delimiter));	
		String username = "";
		String message = "";
		int	ID = 0;		
		int finalsegment = 0;
		int currentsegment = 0;
		boolean exist = false;
	//	dialogAdapter.add(new ChatMessage(true,"String lenght" + " : " + content.length));
	//	dialogAdapter.add(new ChatMessage(true,"String lenght" + " : " + device.getName()));
	//	dialogAdapter.add("Recieved" + " : " + device.getName());
		
		if (content.length==6){		
			username = content[3];
			message = content[4];
			ID = Integer.parseInt(content[5]);		
			finalsegment = Integer.parseInt(content[2]);
			currentsegment = Integer.parseInt(content[1]);

		}else{
			username = content[4];
			message = content[5];
			ID = Integer.parseInt(content[6]);		
			finalsegment = Integer.parseInt(content[2]);
			currentsegment = Integer.parseInt(content[1]);
	
		}
			if(!userMap.containsKey(macID)){
				userMap.put(macID,content[3]);
			}
			
			if (ID!=0){
				for (user x: DeviceList){
					if (x.getName().equals(username)&&x.getID()==ID){
						x.setSeg(currentsegment,message);
						exist = true;
					}	
				}		
				if (!exist){
					user u = new user(ID,currentsegment,finalsegment,message,username,macID);
					DeviceList.add(u);
				}
			}
		
	}
	
	
	protected void ChangeName(){
		String aux = "P2P mode: " + MainActivity.getP2P() + "\n" + "Interval Time: " + MainActivity.getInterval() + " secs";
		Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
		for (BluetoothDevice device: btDeviceList){
			String content[] = device.getName().split(getString(R.string.delimiter));
			String username = content[3];
	    	if (username.equals(myname)){
	    		int randomIndex = new Random().nextInt(9); 
	    		myname = myname + randomIndex;
	    	}
	    }
		// Display and register user name
	    mBluetoothAdapter.setName(getString(R.string.app_identifier) + getString(R.string.delimiter) + 0 + getString(R.string.delimiter) + 0 + getString(R.string.delimiter) + myname + getString(R.string.delimiter) + "" + getString(R.string.delimiter) + 0);
	}
	
	public void CheckBT(){	
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(), "Sorry, your device does not support BlueTooth", Toast.LENGTH_LONG).show();
		}else if (!mBluetoothAdapter.isEnabled()) {
					discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
					startActivity(discoverableIntent);	    
		}else if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivity(discoverableIntent);	        	
		}
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (mBluetoothAdapter != null) {
	    	mBluetoothAdapter.cancelDiscovery();
	    }
	    unregisterReceiver(ActionFoundReceiver);
	  }

}
//‡