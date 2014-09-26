package vamix.GUI;

public interface DownloadGUI {
	/**
	 * Interface for allowing download GUI components to use the download worker class to perform
	 * downloads.
	 * @param exitStatus
	 */
	public void downloadDone(int exitStatus);
	
	public void setProgressBar(int progress);
}
