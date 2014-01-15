package com.appspot.jtp.core;

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


/**
 * <code>TrustSystem</code> contains different methods to calculate trust.
 * 
 * @author Mitesh Pathak <miteshpathak05@gmail.com>
 */
public class TrustSystem {
	private static double ALPHA = 0.2;

	/**
	 * Returns new trust value based on naive algorithm.
	 * @param oldtrust
	 * @param trustOfRec
	 * @param rating
	 * @return returns <code>double</code> as new score 
	 */
	public static double calNewTrust(double oldtrust, double trustOfRec, double rating) {
		return Math.min(oldtrust + Math.log10(oldtrust + (1 + trustOfRec * ALPHA) * rating), 1);	
	}

}
