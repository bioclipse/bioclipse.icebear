package net.bioclipse.icebear.ui.handlers;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.jchempaint.editor.JChemPaintEditor;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.icebear.Activator;
import net.bioclipse.icebear.business.IIcebearManager;
import net.bioclipse.icebear.ui.views.IcebearView;
import net.bioclipse.jobs.BioclipseJobUpdateHook;
import net.bioclipse.rdf.business.IRDFStore;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class RunIcebearHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		//Get the mol
		//Show IceBearView
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().
								      getActivePage().showView(IcebearView.VIEW_ID);
		} catch (PartInitException e1) {
			e1.printStackTrace();
			throw new ExecutionException("Could not open Icebear view");
		}

		
        IEditorPart editor = HandlerUtil.getActiveEditor( event );
        if(editor==null) return null;
        final JChemPaintEditor jcp = 
                (JChemPaintEditor) editor.getAdapter( JChemPaintEditor.class );
        if(jcp==null) return null;
        
        ICDKMolecule mol = jcp.getCDKMolecule();
		
		IIcebearManager icebear = Activator.getDefault().getJavaIcebearManager();
		

		final IcebearView icebearView = (IcebearView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			      getActivePage().findView(IcebearView.VIEW_ID);
		icebearView.clearModel();
		
		try {
			icebear.findInfo(mol, 
			           new BioclipseJobUpdateHook<IRDFStore>("Icebear"){
			               public void partialReturn( IRDFStore chunk ) {
			            	   icebearView.addStore(chunk);
			            	   icebearView.refresh();
			               }
			           });
		} catch (BioclipseException e) {
			throw new ExecutionException(e.getMessage());
		}
		
		return null;
	}
	
}