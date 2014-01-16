package com.miteshpathak.jtp.file;

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


import org.mpisws.p2p.filetransfer.BBReceipt;
import org.mpisws.p2p.filetransfer.FileReceipt;
import org.mpisws.p2p.filetransfer.FileTransferListener;
import org.mpisws.p2p.filetransfer.Receipt;

import com.miteshpathak.jtp.app.FileShareApp;

public class FileTransferListnerImp implements FileTransferListener {
    final FileShareApp app;
    
    public FileTransferListnerImp(FileShareApp app) {
    	this.app = app;
	}
    
	public void fileTransferred(FileReceipt receipt, long bytesTransferred, long total, boolean incoming) {
		String status = incoming == true ? "DOWNLOADED" : "UPLOADED";
		double percent = 100.0 * bytesTransferred / total;		
        System.out.println(app + status + percent + "% of " + receipt);    
	}

	public void msgTransferred(BBReceipt receipt, int bytesTransferred, int total, boolean incoming) {
		String status = incoming == true ? "DOWNLOADED" : "UPLOADED";
		double percent = 100.0 * bytesTransferred / total;		
		System.out.println(app + status + percent + "% of " + receipt);
	}

	public void transferCancelled(Receipt receipt, boolean incoming) {
		String status = incoming == true ? "DOWNLOADING" : "UPLOADING";
		System.out.println(app + ": CANCELLED " + status + " of " + receipt);	
	}
	
	public void transferFailed(Receipt receipt, boolean incoming) {
		String status = incoming == true ? "DOWNLOADED" : "UPLOADED";
		System.out.println(app + ": TRANSFER FAILED " + status + " of " + receipt);
	}

}