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
public class BOS {

	private int syncPort = 0;
	private int asyncPort = 0;
	private Socket syncSocket = null;
	private Socket asyncSocket = null;
	
	
	public BOS() {
		this.syncPort = Integer.parseInt(getSyncPortSettings());
		this.asyncPort = Integer.parseInt(getAsyncPortSettings());
		this.syncSocket = null;
	}
	
	private String getSyncPortSettings() {
		Win32Reg reg = Win32Reg.getInstance();
		try {
			return reg.readString(HKey.HKCU, "Software\\COMARCH S.A.\\NOL3\\7\\Settings", "nca_psync");
		} catch (RegException e) {
			throw new RuntimeException(e);
		}
	}

	private String getAsyncPortSettings() {
		Win32Reg reg = Win32Reg.getInstance();
		try {
			return reg.readString(HKey.HKCU, "Software\\COMARCH S.A.\\NOL3\\7\\Settings", "nca_pasync");
		} catch (RegException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
	
		new BOS();
	}
}
