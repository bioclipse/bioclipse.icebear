package net.bioclipse.icebear.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.icebear.Activator;
import net.bioclipse.icebear.business.Entry;
import net.bioclipse.icebear.business.IIcebearManager;
import net.bioclipse.rdf.business.IRDFStore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author ola
 *
 */
public class IceBearContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			List<IRDFStore> stores = (List<IRDFStore>) inputElement;
			
			IIcebearManager icebear = Activator.getDefault().getJavaIcebearManager();
			List<Entry> entries = new ArrayList<Entry>();
			
			//Unwrap the IRDFStores into Elements and return them
			for (IRDFStore store : stores){

					List<Entry> props;
					try {
						props = icebear.getProperties(store);
						entries.addAll(props);
					} catch (BioclipseException e) {
						e.printStackTrace();
					} catch (CoreException e) {
						e.printStackTrace();
					}
			}
			
			return entries.toArray(new Entry[0]);
		}
		
		return new Object[0];
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	
}