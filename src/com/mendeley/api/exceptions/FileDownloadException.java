package com.mendeley.api.exceptions;

/**
 * Exception class that will be sent back to the application
 * when the a file failed to download,
 *
 */
public class FileDownloadException extends MendeleyException {

	private static final long serialVersionUID = 1L;
	private String fileId;

	public FileDownloadException(String message, String fileId) {
		super(message);
		this.fileId = fileId;
	}

	public String getFileId() {
		return this.fileId;
	}
}
