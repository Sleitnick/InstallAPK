package com.sleitnick.installapk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class AppSettings extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	private static final File OUTPUT = new File("appsettings.ser");
	
	private static AppSettings settings = null;

	public static AppSettings load() {
		try {
			if (!OUTPUT.exists()) {
				OUTPUT.createNewFile();
				settings = new AppSettings();
			} else {
				FileInputStream fis = new FileInputStream(OUTPUT);
				ObjectInputStream ois = new ObjectInputStream(fis);
				settings = (AppSettings)ois.readObject();
				ois.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return settings;
	}
	
	public static void save() {
		try {
			if (settings == null) return;
			if (!OUTPUT.exists()) {
				OUTPUT.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(OUTPUT);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(settings);
			oos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
