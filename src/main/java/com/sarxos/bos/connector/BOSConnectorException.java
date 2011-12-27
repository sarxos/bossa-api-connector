package com.sarxos.bos.connector;

/**
 * BossaAPI connector exception.
 * 
 * @author Bartosz Firyn (SarXos)
 */
@SuppressWarnings("serial")
public class BOSConnectorException extends Exception {

	public BOSConnectorException() {
		super();
	}

	public BOSConnectorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BOSConnectorException(String arg0) {
		super(arg0);
	}

	public BOSConnectorException(Throwable arg0) {
		super(arg0);
	}
}
