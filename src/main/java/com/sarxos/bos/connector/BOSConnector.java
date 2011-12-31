package com.sarxos.bos.connector;

import java.net.Socket;

import com.sarxos.win32.regutil.HKey;
import com.sarxos.win32.regutil.RegException;
import com.sarxos.win32.regutil.Win32Reg;


/**
 * BossaAPI connector for NOL3 application.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class BOSConnector {

	private static final String NOL3_REG_KEY = "Software\\COMARCH S.A.\\NOL3\\7\\Settings";
 
	
	private int syncPort = 0;
	private int asyncPort = 0;
	private Socket syncSocket = null;
	private Socket asyncSocket = null;
	
	
	public BOSConnector() throws BOSConnectorException {
		this.syncPort = Integer.parseInt(getSyncPortSettings());
		System.out.println(syncPort);
		this.asyncPort = Integer.parseInt(getAsyncPortSettings());
		this.syncSocket = null;
		System.out.println(asyncPort);
	}
	
	private String getSyncPortSettings() throws BOSConnectorException {
		Win32Reg reg = Win32Reg.getInstance();
		String value = null;
		String name = "nca_psync";
		try {
			value = reg.readString(HKey.HKCU, NOL3_REG_KEY, name);
		} catch (RegException e) {
			throw new RuntimeException(e);
		}
		if (value == null) {
			throw new BOSConnectorException("Value " + name + " in " + HKey.HKCU + "\\" + NOL3_REG_KEY + " does not exist");
		}
		return value;
	}

	private String getAsyncPortSettings() throws BOSConnectorException {
		Win32Reg reg = Win32Reg.getInstance();
		String value = null;
		String name = "nca_pasync";
		try {
			value = reg.readString(HKey.HKCU, NOL3_REG_KEY, name);
		} catch (RegException e) {
			throw new RuntimeException(e);
		}
		if (value == null) {
			throw new BOSConnectorException("Value " + name + " in " + HKey.HKCU + "\\" + NOL3_REG_KEY + " does not exist");
		}
		return value;
	}

	public static void main(String[] args) throws BOSConnectorException {
	
		new BOSConnector();
	}
}
