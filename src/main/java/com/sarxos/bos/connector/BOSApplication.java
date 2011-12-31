package com.sarxos.bos.connector;

import java.util.Iterator;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.MessageCracker;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.StringField;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.Password;
import quickfix.field.UserRequestType;
import quickfix.field.Username;
import quickfix.fix50.UserRequest;



public class BOSApplication extends MessageCracker implements Application {

	private SocketInitiator initiator = null;
	private MessageStoreFactory storage = null;
	private SessionSettings settings = null;
	private MessageFactory messageFactory = new DefaultMessageFactory();
	private boolean started = false;
	
	public BOSApplication() {

		try {
			settings = new SessionSettings("data/fixengine.cfg");
			settings.setString("SocketConnectPort", Integer.toString(BOSSettings.getSyncPort()));

			storage = new FileStoreFactory(settings);
			initiator = new SocketInitiator(this, storage, settings, messageFactory);

			start();
			login();

		} catch (ConfigError | BOSConnectorException e) {
			throw new RuntimeError(e);
		}
	}
	
	public static void main(String[] args) {
		new BOSApplication();
	}
	
	public boolean start() throws BOSConnectorException {

		if (started) {
			return false;
		}

		try {
			initiator.start();
			started = true;
		} catch (RuntimeError | ConfigError e) {
			throw new BOSConnectorException(e);
		}

		Iterator<SessionID> sids = initiator.getSessions().iterator();
		while (sids.hasNext()) {
			SessionID id = (SessionID) sids.next();
			System.out.println("Attempting to logon... ID = " + id);
			Session.lookupSession(id).logon();
		}

		return true;
	}
	
	public void stop() {
		Iterator<SessionID> sids = initiator.getSessions().iterator();
		while (sids.hasNext()) {
			SessionID sid = (SessionID) sids.next();
			Session.lookupSession(sid).logout("user requested");
		}
		initiator.stop();
	}

	public boolean login() {

		UserRequest request = new UserRequest();
		request.set(new UserRequestType(UserRequestType.LOGONUSER));
		request.set(new Username(BOSSettings.getUser()));
		request.set(new Password(BOSSettings.getPassword()));
		
		Iterator<SessionID> sids = initiator.getSessions().iterator();
		while (sids.hasNext()) {
			SessionID sid = (SessionID) sids.next();
			try {
				Session.sendToTarget(request, sid);
			} catch (SessionNotFound e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	@Override
	public void onCreate(SessionID sessionId) {
		System.out.println("on create session ID = " + sessionId);
	}

	@Override
	public void onLogon(SessionID sessionId) {
		System.out.println("on login session ID = " + sessionId);
	}

	@Override
	public void onLogout(SessionID sessionId) {
		System.out.println("on logout session ID = " + sessionId);
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		System.out.println("to admin message = " + message + "\nto admin session ID = " + sessionId);
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		System.out.println("from admin message = " + message + "\nfrom admin session ID = " + sessionId);
		Header hdr = message.getHeader();
		try {
			if (hdr.getField(new MsgType()).valueEquals(MsgType.LOGON)) {
				message.setField(new StringField(141, "Y"));
			}
			if (hdr.getField(new MsgType()).valueEquals(MsgType.REJECT)) {
				System.out.println("Incoming reject: " + message);
			}
			if (hdr.getField(new MsgType()).valueEquals(MsgType.HEARTBEAT)) {
				// heartbeat
			}
		} catch (FieldNotFound e) {
			System.err.println("Field not found!");
		}
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		System.out.println("to app message = " + message + "\nto app sessionID = " + sessionId);
	}

	@Override
	public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		System.out.println("from app message = " + message + "\nfrom app session ID = " + sessionId);
		crack(message, sessionId);
	}
}
