/*******************************************************************************
 * Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.icebear.business;

import org.eclipse.core.runtime.CoreException;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.managers.business.IBioclipseManager;

@PublishedClass(
    value="Finds information about molecules on the web."
)
public interface IIcebearManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "IMolecule mol, String filename",
        methodSummary = "Find information about this molecule and save it as a HTML file." )
    public String findInfo(IMolecule mol, String filename) throws BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "String uri, String filename",
        methodSummary = "Find information about this molecule and save it as a HTML file." )
    public String findInfo(String uri, String filename) throws BioclipseException, CoreException;

}
