package com.miteshpathak.jtp.core;

/*
 * Copyright 2013-14 Mitesh Pathak <miteshpathak05@gmail.com>
 *
 * This file is part of JTP (Java Trusted Peer).
 *
 * JTP is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version.
 *
 * JTP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with JTP; if not, see <http://www.gnu.org/licenses/>.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>Config</code> saves configuration of peer as serializable object.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public class Config implements Serializable {
	public static final String FILE_NAME_PREFIX = ".jtp-config-";
	public static String FILE_PATH;
	
	private static final long serialVersionUID = 272105714694625959L;
	private static final Logger logger = Logger.getLogger(Config.class.toString());
	
	private String username;
	private int bindport;
	private InetSocketAddress bootaddress;
	
	private double trust;
	
	/**
	 * Creates new configuration for peer with given <code>username</code> and binds to port <code>bindport</code>.
	 * To enter an existing ring it requires <code>bootaddress</code> of an online peer.
	 * 
	 * @param name
	 * @param bindport
	 * @param bootaddress
	 */
	public Config(String name, int bindport, InetSocketAddress bootaddress) {
		super();
		this.username = name;
		this.bindport = bindport;
		this.bootaddress = bootaddress;
		this.trust = 0.0;
	}
	
	/**
	 * Returns <code>username</code> of peer.
	 *
	 * @return String
	 */
	public String getName() {
		return username;
	}
	
	/**
	 * Returns port to which peer is binded.
	 * 
	 * @return int
	 */
	public int getBindport() {
		return bindport;
	}
	
	/**
	 * Returns <code>SocketAddress</code> of bootstrap peer. 
	 * @return {@link InetSocketAddress}
	 */
	public InetSocketAddress getBootaddress() {
		return bootaddress;
	}
	
	/**
	 * returns TRUST value of the peer.
	 */
	public double getTrust() {
		return trust;
	}
	
	/**
	 * Updates trust value to <code>trust</code>.
	 * 
	 * @param trust
	 */
	public void updateTrust(double trust) {
		this.trust = trust;
		saveFile();
	}
	
	/**
	 * Saves <code>this</code> object configuration infile.
	 */
	public void saveFile() {
		try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH + FILE_NAME_PREFIX + this.username);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unable to save config file directly", e);
		}
		
	}
	
	/**
	 * Saves object configuration in a file referred by <code>file</code>.
	 * @param file
	 * @throws Exception
	 */
	public void saveObject(File file) throws Exception {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream os = new ObjectOutputStream(fos);
		os.writeObject(this);
		os.close();
	}

	/**
	 * This method returns an <code>Config</code> object represented stored in <code>File</code>.
	 * 
	 * @param file
	 * @return {@link Config}
	 * @throws Exception
	 */
	public static Config valueOf(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream is = new ObjectInputStream(fis);
		Config conf =  (Config) is.readObject();
		is.close();
		return conf;
		
	}

}
