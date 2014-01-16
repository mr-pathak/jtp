package com.miteshpathak.jtp.main;

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
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.miteshpathak.jtp.app.FileShareApp;
import com.miteshpathak.jtp.core.Config;
import com.miteshpathak.jtp.core.TrustSystem;
import com.miteshpathak.jtp.msg.MessageImpl;
import com.miteshpathak.jtp.msg.MessageType;

import rice.environment.Environment;
import rice.p2p.commonapi.IdFactory;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * Main driver class.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public final class Main {
	private static final Logger logger = Logger.getLogger(Main.class.toString());
	
	public static Scanner src = new Scanner(System.in);
	
	public static void main(String args[]) {
		NodeIdFactory nidFactory;
	    PastryNodeFactory factory;
	    PastryNode node = null;
	    FileShareApp app = null;

		
		int bindport = -1;
		InetSocketAddress bootaddress = null;
		Environment env = null;
		
		String in[];

		String filePath = null;
		String username = null;
		File dir = null;
		
        System.out.println("\n    JTP  Copyright (C) 2013-14  Mitesh Pathak <miteshpathak05@gmail.com> \n" +
        
        	"    This program comes with ABSOLUTELY NO WARRANTY.\n" +
        	"    This is free software, and you are welcome to redistribute it\n" +
        	"    under certain conditions; type `show l' for details.\n");
        
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Starting JTP: Java Trusted Peer");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        
        
        
        try {
        	StringBuilder sb = new StringBuilder();
        	for (int i = 1; i < args.length; i++) {
        		sb.append(args[i]);
        	}
        	filePath = sb.toString();
			dir = new File(filePath);
        	

        	if (!dir.exists() || !dir.isDirectory() || args == null || args.length < 2) {
        		throw new Exception("[ERROR] Invalid arguments");
        	}
        	username = args[0];
        	
        } catch (Exception e) {
            System.out.println("\nUSAGE: java -jar jtp*.jar [nickname] [pathToShare]\n");

        	do {
    			System.out.println("[ENTER] existing source directory");
    			filePath = src.nextLine();
    			dir = new File(filePath);
    		} while (!dir.exists() || !dir.isDirectory());
        	
    		System.out.println("[ENTER] username");
    		username = src.nextLine();
        }
        

		Config.FILE_PATH = filePath;
		
		
		File file = new File(Config.FILE_PATH + Config.FILE_NAME_PREFIX + username);
		
		Config peerConfig = null;
		if (file.exists()) {
			try {
				peerConfig = Config.valueOf(file);
				bindport = peerConfig.getBindport();
				bootaddress = peerConfig.getBootaddress();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Invalid config file exist", e);
				System.err.println("[ERROR] Invalid config file. Delete it");
				System.exit(0);
			}
		} else {
			while (true) {
				try {
					System.out.println("[ENTER] PORT BINDHOST BINDPORT");
					in = src.nextLine().split(" ");
					
					if (in.length != 3) {
						continue;
					}
					bindport = Integer.parseInt(in[0]);
					bootaddress = new InetSocketAddress(in[1], Integer.parseInt(in[2]));
					
					peerConfig = new Config(username, bindport, bootaddress);
					file.createNewFile();
					
					peerConfig.saveObject(file);
				    break;
				} catch (Exception e) {
					logger.log(Level.WARNING, "Invalid Information - Cannot create Node", e);
					System.err.println("[ERROR] enter valid info");
				}

			}
	        System.out.println("[INFO] Creating user with nickname '" + username + "'\n");
		}
		
		try {
			env = new Environment();
			env.getParameters().setString("nat_search_policy","never");
			
			nidFactory = new RandomNodeIdFactory(env); // random nodeID
			factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
			node = factory.newNode(); // new Node
				    
			IdFactory idFactory = new PastryIdFactory(env);
			app = new FileShareApp(node, idFactory, peerConfig);
			
			node.boot(bootaddress);
			
			synchronized (node) {
				while (!node.isReady() && !node.joinFailed()) {
					node.wait(500); // busy-wait
				}
			}
			env.getTimeSource().sleep(1000);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unable to boot into network", e);
		}

		System.out.println("[INFO] Welcome \'" + username + "\'!\n");
	
		
		while (true) {
			try {
	            System.out.print("(jtp)# ");

				String line = src.nextLine();
				int x = line.indexOf(' ');
				
				String choice = x == -1 ? line : line.substring(0, x);
				String argument = x == -1 ? "" : line.substring(x + 1);
				
				List<MessageImpl> searchResults = null;
				
				MessageImpl message;
				switch (choice) {
					case "exit": 						
				        System.out.println("\n[INFO] >>>> exiting <<<<");
				        System.out.println("[INFO] ------------------------------------------------------------------------");

				        System.out.println();
				        System.exit(-1); 
						break;
						
					case "search":
						message = new MessageImpl(node.getLocalNodeHandle(), null, MessageType.SEA, argument);
						app.multicast(message);//(argument);
						
						env.getTimeSource().sleep(5000);

						if (app.isSearchResultNull()) {
							System.out.println("NO result for file " + argument);
						} else {
							System.out.println("[ENTER] get OPTION");
							searchResults = app.getSearchResult();
							for (int i = 0, j = searchResults.size(); i < j; i++) {
								System.out.println(String.format("%-3d -\t%s", i, searchResults.get(i).getMsg()));
							}
						}
				        System.out.println();
						break;
						
					case "get":
						if (app.isSearchResultNull()) {
							System.err.println("[ERROR] Do search operation first");
							break;
						}
						app.setCurrReq(Integer.valueOf(argument));
						message = app.getSearchResultMessage();
						
						String arg[] = message.getMsg().split(" ");
						String fname = "";
						
						for (int i = 0, j = arg.length - 2; i < j; i++) {
							fname += arg[i] + " ";
						}
						
						message = new MessageImpl(message.getTo(), message.getFrom(), MessageType.GET, fname.trim());
						app.sendMessageDirectTo(message);

						/*
						 * new trust
						 */
			            MessageImpl newmsg = app.getSearchResultMessage();
			            double rating = 0.1;
			            
			            do {
			            	try {
			            		node.getEnvironment().getTimeSource().sleep(10000);
				            	System.out.println("[ENTER] Entered rating for download out of 5 = " + (rating = src.nextDouble()));
				            	rating = Double.valueOf(rating) / 5.0;
			            	} catch (Exception e) {
			    				logger.log(Level.WARNING, "Unable to log user rating", e);
			            		continue;
			            	}
			            } while (rating < 0 && rating > 1);
			            double newtrust = TrustSystem.calNewTrust(Double.valueOf(newmsg.getMsg().split(" ")[1]), app.getTrust(), rating);//TrsuSys.
			            app.sendMessageDirectTo(new MessageImpl(newmsg.getTo(), newmsg.getFrom(), MessageType.RAT, "" + newtrust));

				        System.out.println();
						break;
						
					case "help":
						System.out.println(String.format("%-20s - %s" , "exit", "Exit shell | QUIT"));
						System.out.println(String.format("%-20s - %s" , "get OPTION", "Fetch file from OPTIONS provided by 'search' results"));
						System.out.println(String.format("%-20s - %s" , "ping USERNAME", "Check status of user USERNAME"));
						System.out.println(String.format("%-20s - %s" , "search FILENAME", "Search for files corressponding to FILENAME | Returns list of available files from peers"));
						System.out.println(String.format("%-20s - %s" , "help", "List of commands available"));

						System.out.println();
						break;
						
					case "ping":
						message = new MessageImpl(node.getLocalNodeHandle(), null, MessageType.WHO, argument);
						app.multicast(message);
						
						env.getTimeSource().sleep(3000);
						
						if (app.getPingResult()) {
							System.out.println("[INFO] user \'" + argument + "\' is alive - ping succesfull");
						} else {
							System.out.println("[INFO] user \'" + argument + "\' is offline - ping unsuccessfull");
						}
						break;
						
					case "show":
						if (argument.equalsIgnoreCase("l")) {
							System.out.println("\n *\n* JTP is free software: you can redistribute it and/or modify it under the terms\n" +
									"* of the GNU General Public License as published by the Free Software Foundation,\n" + 
									"* either version 3 of the License, or (at your option) any later version.\n*\n" +
									"* JTP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n" +
									"* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A \n" + 
									"* PARTICULAR PURPOSE. See the GNU General Public License for more details.\n*\n" + 
									"* You should have received a copy of the GNU General Public License along\n" + 
									"* with JTP; if not, see <http://www.gnu.org/licenses/>.\n*\n");
						}
						break;
						
					case "": 
						break;
					default: 
						System.err.println("[ERROR] Invalid option. Enter 'help' to get list of commands");
				        System.out.println();		

				}
			} catch (Exception e) {
				System.err.println("[ERROR] Enter 'help' to get list of commands");
			}
		}
	}
}
