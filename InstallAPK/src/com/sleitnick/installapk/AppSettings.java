package com.sleitnick.installapk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


/**
 * AppSettings<p>
 * Settings for the application.
 * <p>
 * Use the <code>load()</code> method to
 * load the settings. This will return the AppSettings object with all
 * saved settings. The <code>load</code> method will only explicitly
 * load from the disk if actually needed. If it has already been loaded,
 * it will just return the object that was loaded.
 * <p>
 * Use the <code>save()</code> method to save the settings. This should
 * be called before the application closes and after all necessary settings
 * have been set. Attempting to save before loading will do nothing.
 * @author Stephen Leitnick
 *
 */
public class AppSettings extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	private static final File OUTPUT = new File("appsettings.ser");
	
	private static AppSettings settings = null;

	public static AppSettings load() {
		if (settings == null) {
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
