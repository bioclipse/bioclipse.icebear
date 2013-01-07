/* Copyright (c) 2012  Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *               2012  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.business;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Class to hold the molecular properties.
 */
public class Entry implements IPropertySource2 {

	// the next two fields are for provenance

	/** A URI for the resource for which this property applies. */
	public String resource;

	public String predicate;
	public String object;
	
	public Entry(String resource, String predicate, String object) {
		super();
		this.resource = resource;
		this.predicate = predicate;
		this.object = object;
	}

	public String toString() {
		return "[" + predicate + ": " + object + "]";
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	private static final String SOURCE_ID = "isbjorn.source.id";
	private static final TextPropertyDescriptor SOURCE_PROPERTY_DESCRIPTOR =
	     new TextPropertyDescriptor(SOURCE_ID, "Source URI");
	static {
		SOURCE_PROPERTY_DESCRIPTOR.setCategory("Linked Data");
		SOURCE_PROPERTY_DESCRIPTOR.setAlwaysIncompatible(true);
	}

	private static final IPropertyDescriptor[] DESCRIPTORS = {
		SOURCE_PROPERTY_DESCRIPTOR
	};

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return DESCRIPTORS;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SOURCE_ID.equals(id)) {
			return resource;
		}
		return null;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// nothing to do
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		// nothing to do
	}

	@Override
	public boolean isPropertyResettable(Object id) {
		return false;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return true; // always true, because there is no reasonable default
	}

}
