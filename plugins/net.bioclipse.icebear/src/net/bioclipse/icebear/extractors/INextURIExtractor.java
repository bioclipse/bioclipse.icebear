/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.extractors;

import java.util.List;

import net.bioclipse.rdf.business.IRDFStore;

public interface INextURIExtractor {

	public List<String> extractURIs(IRDFStore store, String resource);

}
