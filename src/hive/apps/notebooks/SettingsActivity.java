package hive.apps.notebooks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	File notebooksdir = new File(Environment.getExternalStorageDirectory()
			+ "/HIVE/Notebooks");
	File notebooksbackupdir = new File(
			Environment.getExternalStorageDirectory()
					+ "/HIVE/Backups/Notebooks");
	File backupname = new File(notebooksbackupdir + "/backup.zip");

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		getActionBar().setIcon(R.drawable.ic_gear);

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		String key = preference.getKey();

		if (key.equals("delete_notebooks")) {
			askToDeleteNotebooks();
		} else if (key.equals("backup_notebooks")) {
			askToBackupNotebooks();
		} else if (key.equals("restore_notebooks")) {
			askToRestoreNotebooks();
		} else if (key.equals("delete_backups")) {
			askToDeleteBackups();
		} else if (key.equals("delete_notebook_content")) {
			askToDeleteNotebookContent();
		}

		return true;
	}

	private void askToDeleteNotebooks() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// deleteAllNotebooks();
					deleteDir(notebooksdir);
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_delete_notebooks_desc)
				.setPositiveButton(R.string.yes, dialogClickListener)
				.setNegativeButton(R.string.no, dialogClickListener).show();
	}

	public void askToBackupNotebooks() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					backupNotebooks();
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_backup_notebooks_desc)
				.setPositiveButton(R.string.yes, dialogClickListener)
				.setNegativeButton(R.string.no, dialogClickListener).show();

	}

	public void backupNotebooks() {

		if (!notebooksbackupdir.exists()) {
			notebooksbackupdir.mkdirs();
		}
		try {
			Zipper.zipDirectory(notebooksdir, backupname);
		} catch (IOException e) {
			Toast.makeText(this, R.string.error_failed_to_export_backup,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void askToRestoreNotebooks() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					restoreNotebooks();
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_restore_notebooks_desc)
				.setPositiveButton(R.string.yes, dialogClickListener)
				.setNegativeButton(R.string.no, dialogClickListener).show();

	}

	public void restoreNotebooks() {
		try {
			Zipper.unzip(backupname, notebooksdir);
		} catch (IOException e) {
			Toast.makeText(this, R.string.error_failed_to_restore_from_backup,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	private void askToDeleteBackups() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					deleteDir(notebooksbackupdir);
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_delete_backups_desc)
				.setPositiveButton(R.string.yes, dialogClickListener)
				.setNegativeButton(R.string.no, dialogClickListener).show();
	}

	private void askToDeleteNotebookContent() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					deleteNotebookContent(notebooksdir);
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}

		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_delete_notebook_content_desc)
				.setPositiveButton(R.string.yes, dialogClickListener)
				.setNegativeButton(R.string.no, dialogClickListener).show();
	}

	public void deleteNotebookContent(File dir) {
		if (dir.isDirectory()) {
			for (File c : dir.listFiles()) {
				deleteNotebookContent(c);
			}
		} else if (dir.getAbsolutePath().endsWith("png")) {
			if (!dir.delete()) {
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}
}