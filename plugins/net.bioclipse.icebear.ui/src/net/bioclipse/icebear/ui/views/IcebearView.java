/* Copyright (c) 2012  Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.icebear.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.core.domain.IBioObject;
import net.bioclipse.icebear.business.Entry;
import net.bioclipse.rdf.business.IRDFStore;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author ola
 *
 */
public class IcebearView extends ViewPart{

	private static final Logger logger = Logger.getLogger(IcebearView.class);

	public static final String VIEW_ID="net.bioclipse.icebear.ui.views.IcebearView";

	private IcebearView instance;

	protected ISelection storedSelection;

	private TableViewer viewer;

	private Action refreshAction;

	private List<IRDFStore> viewmodel;

	public IcebearView() {
	}

	public IcebearView getInstance() {
		return instance;
	}

	@Override
	public void createPartControl( Composite parent ) {

		this.instance=this;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);

		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getTable().setLayoutData(gridData);
		ColumnViewerToolTipSupport.enableFor(viewer,ToolTip.NO_RECREATE);

		viewer.setContentProvider(new IceBearContentProvider());

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Predicate");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((Entry) element).predicate;
			}
		});


		ColumnViewerSorter cSorter = new ColumnViewerSorter(viewer,column) {

			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Entry p1 = (Entry) e1;
				Entry p2 = (Entry) e2;
				return p1.predicateLabel.compareToIgnoreCase(p2.predicateLabel);
			}

		};

		CellLabelProvider labelProvider = new ColumnLabelProvider() {
			public Point getToolTipShift(Object object) { return new Point(5, 5); }
			public int getToolTipDisplayDelayTime(Object object) { return 200; }
			public int getToolTipTimeDisplayed(Object object) { return 5000; }
			
			public String getToolTipText(Object element) {
				return ((Entry) element).resource;
			}

			public String getText(Object element) {
				return ((Entry) element).object;
			}
		};

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Object");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(labelProvider);

		new ColumnViewerSorter(viewer,column) {

			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				Entry p1 = (Entry) e1;
				Entry p2 = (Entry) e2;
				return p1.object.compareToIgnoreCase(p2.object);
			}

		};


		viewmodel = new ArrayList<IRDFStore>();
		viewer.setInput(viewmodel);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		cSorter.setSorter(cSorter, ColumnViewerSorter.ASC);



		//We need to add change listener to something

		//        net.bioclipse.browser.Activator.getDefault().getScrapingModel()
		//        .addChangedListener(this);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
				VIEW_ID);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		addDoubleClickAction();

		getSite().setSelectionProvider(viewer);

	}


	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				viewer.refresh();
			}
		};
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refreshes viewer");
		//        refreshAction.setImageDescriptor(Activator.getImageDescriptor(  "icons/refresh2.png" ));
		//        refreshAction.setDisabledImageDescriptor( Activator.getImageDescriptor( "icons/smallRun_dis.gif" ));


	}

	private void addDoubleClickAction() {
		viewer.addDoubleClickListener( new IDoubleClickListener() {

			public void doubleClick( DoubleClickEvent event ) {
				IStructuredSelection ssel = (IStructuredSelection)event
						.getSelection();
				Object obj = ssel.getFirstElement();
				if ( obj instanceof IBioObject ) {
					//                    handleOpen((IBioObject)obj);
				}
			}
		});

	}


	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu", "net.bioclipse.icebear.context");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//                updateActionStates();
				fillContextMenu(manager);
			}

		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	public void refresh() {
		Display.getDefault().syncExec(new Runnable(){
			@Override
			public void run() {
				viewer.refresh();
			}
		});
	}

	public void clearModel() {
		viewmodel=new ArrayList<IRDFStore>();
		viewer.setInput(viewmodel);
	}

	public void addStore(IRDFStore chunk) {
		viewmodel.add(chunk);
	}



}
