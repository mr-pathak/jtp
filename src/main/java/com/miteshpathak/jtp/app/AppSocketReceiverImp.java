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


import java.io.IOException;

import rice.p2p.commonapi.appsocket.AppSocket;
import rice.p2p.commonapi.appsocket.AppSocketReceiver;

/**
 * <code>AppSOcketReceiverImp</code> class implements <code>AppSocketReceivers</code>.
 * 
 * TODO Implement methods
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public class AppSocketReceiverImp implements AppSocketReceiver {
	
	@Override
	public void receiveException(AppSocket arg0, Exception arg1) {
	}

	@Override
	public void receiveSelectResult(AppSocket arg0, boolean arg1, boolean arg2)
			throws IOException {
	}

	@Override
	public void receiveSocket(AppSocket socket) throws IOException {
	}
	
}