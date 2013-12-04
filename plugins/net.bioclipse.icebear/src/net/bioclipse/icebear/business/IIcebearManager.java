/* Copyright (c) 2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.business;

import java.util.List;

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IMolecule;
import net.bioclipse.jobs.BioclipseJob;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IRDFStore;

import org.eclipse.core.runtime.CoreException;

@PublishedClass(
    value="Finds information about molecules on the web."
)
public interface IIcebearManager extends IBioclipseManager {

    @Recorded
    @PublishedMethod(
        params = "IMolecule mol",
        methodSummary = "Find information about this molecule and return it as RDF stores." )
    public List<IRDFStore> findInfo(IMolecule mol) throws BioclipseException;
//    public void findInfo(IMolecule mol, BioclipseUIJob<List<IRDFStore>> uiJob ) throws BioclipseException;
    public BioclipseJob<IRDFStore> findInfo(IMolecule mol, BioclipseJobUpdateHook<IRDFStore> hook) 
    throws BioclipseException;
    
    @Recorded
    @PublishedMethod(
        params = "String uri",
        methodSummary = "Find information about a molecule starting with the seed URI and return it as RDF stores." )
    public List<IRDFStore> findInfo(String uri) throws BioclipseException;
    public BioclipseJob<IRDFStore> findInfo(String uri, BioclipseJobUpdateHook<IRDFStore> hook) 
    throws BioclipseException;

    @Recorded
    @PublishedMethod(
        params = "List<IRDFStore> stores, String filename",
        methodSummary = "Find information in the RDF stores and save it as a HTML file." )
    public String saveAsHTML(List<IRDFStore> stores, String filename) throws BioclipseException, CoreException;

    @Recorded
    @PublishedMethod(
        params = "IRDFStore store",
        methodSummary = "Returns a List of Entry's containing the extracted properties." )
    public List<Entry> getProperties(IRDFStore store) throws BioclipseException, CoreException;

}
