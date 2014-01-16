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


import com.miteshpathak.jtp.msg.MessageImpl;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;

/**
 * <code>ScribeContentImp</code> implements <code>ScribeContent</code>.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public class ScribeContentImp implements ScribeContent {
	private static final long serialVersionUID = 2792494598517033778L;
	
	private NodeHandle from;
	private int seq;
	private MessageImpl message;
	
	
	/**
	 * Creates new <code>ScribeContent</code> with message sequence number <code>seq</code> and
	 * Message <code>message</code>.
	 * 
	 * @param seq
	 * @param message
	 */
	public ScribeContentImp(int seq, MessageImpl message) {
	    this.from = message.getFrom();
	    this.seq = seq;
	    this.message = message;
	}
	
	
	/**
	 * Returns <code>NodeHandle</code> of the sender.
	 * @return <code>NodeHandle</code>
	 */
	public NodeHandle getFrom() {
		return from;
	}


	/**
	 * Returns sequence number of the message.
	 * @return int
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * Returns <code>Message</code> of the <code>ScribeContent<code>.
	 * @return {@link MessageImpl}
	 */
	public MessageImpl getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "[ScribeContent] #" + this.seq + " from " + from;
	}

}