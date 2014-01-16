package com.miteshpathak.jtp.app;

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
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mpisws.p2p.filetransfer.FileReceipt;
import org.mpisws.p2p.filetransfer.FileTransfer;
import org.mpisws.p2p.filetransfer.FileTransferCallback;
import org.mpisws.p2p.filetransfer.FileTransferImpl;

import com.miteshpathak.jtp.core.Config;
import com.miteshpathak.jtp.core.ScribeContentImp;
import com.miteshpathak.jtp.file.FileTransferListnerImp;
import com.miteshpathak.jtp.file.WildcardFileFilter;
import com.miteshpathak.jtp.msg.MessageImpl;
import com.miteshpathak.jtp.msg.MessageType;

import rice.Continuation;
import rice.p2p.commonapi.*;
import rice.p2p.commonapi.appsocket.*;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.p2p.util.rawserialization.SimpleInputBuffer;
import rice.p2p.util.rawserialization.SimpleOutputBuffer;
import rice.p2p.commonapi.NodeHandle;

/**
 * <code>FileShareApp</code> implements <code>Application</code> and <code>ScribeClient</code>.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
@SuppressWarnings("deprecation")
public class FileShareApp implements Application, ScribeClient {
	public static final String APP_INSTANCE_NAME = "APP INSTANCE";
	public static final String SCRIBE_INSTANCE_NAME = "SCRIBE INSTANCE";
	public static final String TOPIC_NAME = "FILE SHARING";
	
	private static final Logger logger = Logger.getLogger(FileShareApp.class.toString());
	
	private Endpoint endpoint;
	private Node node;
	private FileTransfer fileTransfer;
    
	private int seqNum = 0;
	private Scribe scribe;
	private Topic topic;
	
	private Config config;
	
	private List<MessageImpl> replies;
	private int currentReply;
	
	private boolean ping;
	public FileShareApp(Node node, final IdFactory factory, final Config config) {
		
		this.endpoint = node.buildEndpoint(this, FileShareApp.APP_INSTANCE_NAME);
		this.node = node;
		this.config = config;
		endpoint.accept(new AppSocketReceiver() {
			public void receiveSocket(AppSocket socket) {
				fileTransfer = new FileTransferImpl(socket,new FileTransferCallback() {
					public void messageReceived(ByteBuffer bb) {
						System.out.println("Message received: "+bb);	
					}
					
					public void fileReceived(File f, ByteBuffer metadata) {
						try {
							@SuppressWarnings("resource")
							String originalFileName = new SimpleInputBuffer(metadata).readUTF();
				            File dest = new File(Config.FILE_PATH + originalFileName);
				            System.out.println("Moving \'" + f + "\' to \'" + dest + "\' original:\'" + originalFileName + "\'");
				            System.out.println(f.renameTo(dest));
				            System.out.println("[ENTER] RATING for download out of 5");
						} catch (IOException ioe) {
							logger.log(Level.WARNING, "Deserialization failed", ioe);
							System.out.println("[ERROR] deserializing file name. " + ioe);
						}
					}
					
					public void receiveException(Exception ioe) {
						System.out.println("FTC.receiveException() "+ioe);
					}
				},FileShareApp.this.node.getEnvironment());        
				
				fileTransfer.addListener(new FileTransferListnerImp(FileShareApp.this));
		        endpoint.accept(this);
			}    
			
			public void receiveSelectResult(AppSocket socket, boolean canRead, boolean canWrite) {
				throw new RuntimeException("Shouldn't be called.");
			}
			
			public void receiveException(AppSocket socket, Exception e) {
				e.printStackTrace();
			}    
		});
		
		scribe = new ScribeImpl(node, FileShareApp.SCRIBE_INSTANCE_NAME);
		topic = new Topic(factory, FileShareApp.TOPIC_NAME);
		scribe.subscribe(topic, this);
		
		endpoint.register();
	}
		
	public void sendFileDirectTo(NodeHandle nh, final String fileName) {
	    System.out.println(this + " OPENING TO " + nh);    
	    
	    endpoint.connect(nh, new AppSocketReceiver() {
	    	public void receiveSocket(AppSocket socket) {
	    		FileTransfer sender = new FileTransferImpl(socket, null, node.getEnvironment());
	            sender.addListener(new FileTransferListnerImp(FileShareApp.this));
		        try {
		        	final File f = new File(Config.FILE_PATH + fileName);		        	
		        	@SuppressWarnings("resource")
					SimpleOutputBuffer sob = new SimpleOutputBuffer(); // serialize the filename
		        	sob.writeUTF(f.getName());
		        	System.out.println("-->" + f.getName());
		        	sender.sendFile(f,sob.getByteBuffer(),(byte)2,new Continuation<FileReceipt, Exception>() { // request transfer of the file with priority 2
		        		public void receiveException(Exception exception) {
		        			System.out.println("[ERROR] FAILED SENDING FILE: \'" + f + "\' " + exception);
		        		}
		        		
		        		public void receiveResult(FileReceipt result) {
		        			System.out.println("Send complete: " + result);
		        		}
		        	});
          
		        } catch (IOException ioe) {
					logger.log(Level.WARNING, "Failed Sending  File", ioe);
		        }
	    	}    
	    	
	    	public void receiveException(AppSocket socket, Exception e) {
	    		e.printStackTrace();
	    	}
      
	    	public void receiveSelectResult(AppSocket socket, boolean canRead, boolean canWrite) {   
	    		throw new RuntimeException("Shouldn't be called.");
	    	}
	    }, 30000); // endpoint.connect...complete
	    
	}
    
	public void deliver(Id id, Message msg) {
		MessageImpl message = (MessageImpl) msg;
		
		switch (message.getMsgType()) {
			case GET:
				sendFileDirectTo(message.getFrom(), message.getMsg());   
				try {
					node.getEnvironment().getTimeSource().sleep(1000);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Unable to handle GET request", e);
				}
			    break;
			    
			case REP:
				replies.add(message);
				break;
			    
			case RAT:
				config.updateTrust(Double.valueOf(message.getMsg()));
				break;

			case IAM:
				ping = true;
				break;
				
			default:
				System.err.println("[ERROR] INVALID MESSAGE");
				break;

		}

	}
	
	public void multicast(MessageImpl message) {
		if (message.getMsgType().equals(MessageType.SEA)) {
			replies = new ArrayList<MessageImpl>();
		}
		ScribeContentImp messages = new ScribeContentImp(seqNum++, message);
		scribe.publish(topic, messages);
	}

	public void sendMessageDirectTo(MessageImpl message) {
		endpoint.route(null, message, message.getTo());
	}
	
  
  /**
   * Called a message travels along your path.
   * Don't worry about this method for now.
   */
	public boolean forward(RouteMessage message) {
		return true;
	}
  
	public String toString() {
		return "[FileShareApp] " + endpoint.getId();
	}

	@Override
	public boolean anycast(Topic arg0, ScribeContent arg1) {
		return false;
	}

	@Override
	public void childRemoved(Topic arg0, NodeHandle arg1) {
	}

	@Override
	public void deliver(Topic t, ScribeContent sc) {
		ScribeContentImp sci = (ScribeContentImp) sc;
		MessageImpl message = sci.getMessage();
		
		switch(message.getMsgType()) {
			case SEA:
				
				File dir = new File(Config.FILE_PATH);
				FileFilter fileFilter = new WildcardFileFilter(message.getMsg());
				File[] files = dir.listFiles(fileFilter);
				for (File f : files) {
					sendMessageDirectTo(new MessageImpl(node.getLocalNodeHandle(), sci.getFrom(), MessageType.REP, f.getName() + " " + config.getTrust() + " " + config.getName())); // I have the file
				}				
				break;
				
			case WHO: // reply with my id
				if (sci.getMessage().getMsg().equalsIgnoreCase(config.getName())){
					sendMessageDirectTo(new MessageImpl(message.getTo(), message.getFrom(), MessageType.IAM, config.getName()));
				};
				break;
			default:
				break;
		}
	}

	public MessageImpl getSearchResultMessage() {
		return replies.get(currentReply);
	}

	public List<MessageImpl> getSearchResult() {
		return replies;
	}
	
	public boolean isSearchResultNull() {
		return replies == null;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public FileTransfer getFileTransfer() {
		return fileTransfer;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public Scribe getScribe() {
		return scribe;
	}

	public Topic getTopic() {
		return topic;
	}

	public Config getConfig() {
		return config;
	}

	public int getCurrReq() {
		return currentReply;
	}
	
	public Node getNode() {
		return node;	
	}
		
	public void setCurrReq(int currReq) {
		this.currentReply = currReq;
	}

	public double getTrust() {
		return config.getTrust();
	}
	
	public boolean getPingResult() {
		boolean res = ping;
		this.ping = false;
		return res;
	}
	/*
	 * TODO Implement following methods 
	 */
	
	@Override
	public void subscribeFailed(Topic arg0) {
	}

	@Override
	public void childAdded(Topic arg0, rice.p2p.commonapi.NodeHandle arg1) {
	}

	@Override
	public void update(rice.p2p.commonapi.NodeHandle arg0, boolean arg1) {
	}

}