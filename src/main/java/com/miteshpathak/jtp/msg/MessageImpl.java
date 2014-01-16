package com.miteshpathak.jtp.msg;

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


import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.Message;

/**
 * MessageImpl implements <code>Message</code>.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public class MessageImpl implements Message {
	private static final long serialVersionUID = -2024892123798657846L;
	private NodeHandle from;
	private NodeHandle to;
	
	private MessageType msgType;
	private String msg;
	
	public MessageImpl(NodeHandle from, NodeHandle to, MessageType msgType, String msg) {
		this.from = from;
		this.to = to;
		this.msg = msg;
		this.msgType = msgType;
	}
	
	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}
	
	@Override
	public String toString() {
		return "[MESSAGE:START]\n\t[FROM]\t" + from + "\n\t[TO]\t " + to + "\n\t[TYPE]\t" + msgType + "\n\t[CONTENT]\t" + msg + "\n[MESSAGE:END]";
	}
	
	public NodeHandle getFrom() {
		return from;
	}
	
	public NodeHandle getTo() {
		return to;
	}
	
	public MessageType getMsgType() {
		return msgType;
	}

	public String getMsg() {
		return msg;
	}

}
