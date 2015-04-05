package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.reldb.relui.dbui.preferences.PreferencePageCmd;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;
import org.reldb.relui.version.Version;

public class Preferences {
	private static PreferenceStore preferences;

	public static PreferenceStore getPreferences() {
		return preferences;
	}
	
	public static String getPreference(String name) {
		return preferences.getString(name);
	}

	private PreferenceDialog preferenceDialog;

	public Preferences(Shell parent) {
		PreferenceManager preferenceManager = new PreferenceManager();
		
		PreferenceNode general = new PreferenceNode("General", new PreferencePageGeneral());
		preferenceManager.addToRoot(general);

		PreferenceNode cmd = new PreferenceNode("Command line", new PreferencePageCmd());
		preferenceManager.addToRoot(cmd);
		
		preferenceDialog = new PreferenceDialog(parent, preferenceManager);
		preferences = new PreferenceStore(Version.getPreferencesRepositoryName());
		preferenceDialog.setPreferenceStore(preferences);
	}

	public void show() {
		try {
			preferences.load();
		} catch (IOException e) {
			System.out.println("Preferences: Creating new preferences.");
		}
		preferenceDialog.open();
		try {
			preferences.save();
		} catch (IOException e) {
			System.out.println("Preferences: Unable to load preferences: " + e);
		}
	}
}
