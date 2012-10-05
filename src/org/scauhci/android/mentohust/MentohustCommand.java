package org.scauhci.android.mentohust;

import android.util.Log;

import com.stericson.RootTools.Command;

public class MentohustCommand extends Command {

	public MentohustCommand(int id, int timeout, String... command) {
		super(id, timeout, command);
	}
	
	public MentohustCommand(int id, String... command) {
		super(id, command);
	}

	@Override
	public void output(int id, String line) {
	    if(line.contains("the MentoHUST has been exited.")){
	    	Log.e("Test", "Failed!");
	    }else if(line.contains("Pass auth!")){
	    	Log.e("Test", "Success!");
	    }
	    Log.i("Command", line);
	}

}
