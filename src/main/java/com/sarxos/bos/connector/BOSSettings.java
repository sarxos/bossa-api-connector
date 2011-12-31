package com.sarxos.bos.connector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import quickfix.RuntimeError;

import com.sarxos.win32.regutil.HKey;
import com.sarxos.win32.regutil.RegException;
import com.sarxos.win32.regutil.Win32Reg;

public class BOSSettings {

	private static Properties props = null;
	private static final String NOL3_REG_KEY = "Software\\COMARCH S.A.\\NOL3\\7\\Settings";
	
	
	public static String getUser() {
		return get("user");
	}
	
	public static String getPassword() {
		return get("password"); 
	}
	
	public static int getSyncPort() {
		try {
			return Integer.parseInt(getHKCU(NOL3_REG_KEY, "nca_psync"));
		} catch (NumberFormatException | BOSConnectorException e) {
			throw new RuntimeError(e);
		}
	}
	
	public static int getAsyncPort() {
		try {
			return Integer.parseInt(getHKCU(NOL3_REG_KEY, "nca_pasync"));
		} catch (NumberFormatException | BOSConnectorException e) {
			throw new RuntimeError(e);
		}
	}
	
	private static String get(String key) {
		if (props == null) {
			init();
		}
		return props.getProperty(key);		
	}
	
	private static void init() {
		props = new Properties();
		File file = new File("data/credentials.ini");
		try {
			props.load(new FileReader(file));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getHKCU(String key, String name) throws BOSConnectorException {
		Win32Reg reg = Win32Reg.getInstance();
		String value = null;
		try {
			value = reg.readString(HKey.HKCU, key, name);
		} catch (RegException e) {
			throw new RuntimeException(e);
		}
		if (value == null) {
			throw new BOSConnectorException("Value " + name + " in " + HKey.HKCU + "\\" + key + " does not exist");
		}
		return value;
	}
}
