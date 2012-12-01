/* Copyright (c) 2012  Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.business;
public class Entry {

	public String predicate;
	public String object;
	
	public Entry(String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
	}

	public String toString() {
		return "[" + predicate + ": " + object + "]";
	}

}
