package ca._4946.mreynolds.customSwing;

import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

public class SimpleFileSystemView extends FileSystemView {
	private File m_defaultDir;

	private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");

	public SimpleFileSystemView(File defaultDir) {
		super();
		m_defaultDir = defaultDir;
	}

	/**
	 * Creates a new folder with a default folder name.
	 */
	public File createNewFolder(File containingDir) throws IOException {
		if (containingDir == null) {
			throw new IOException("Containing directory is null:");
		}
		
		// Using NT's default folder name
		File newFolder = createFileObject(containingDir, newFolderString);

		if (newFolder.exists()) {
			throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
		} else {
			newFolder.mkdirs();
		}

		return newFolder;
	}

	@Override
	public File getDefaultDirectory() {
		return m_defaultDir;
	}
}