package org.eclipse.ls.views;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ls.core.LaunchScheduleStorage;
import org.eclipse.ls.core.RunTaskScheduler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;
import org.eclipse.ui.part.ViewPart;

/**
 * The Launch Scheduler is the base view of launch Scheduler.
 * @author Lars Carlsson
 *
 */
public class LaunchSchedule extends ViewPart implements IWorkbenchWindowActionDelegate {

	private TableViewer viewer;
	private Action taskDialog;
	private Action deleteDialog;
	private Action cleanDialog;
	public static Log log = LogFactory.getLog(LaunchSchedule.class) ;
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {	
			ArrayList<RunTaskScheduler> taskList = LaunchScheduleStorage.getInstance().getTaskList();
			if (taskList.size() == 0) {
				String[] temp = new String[0];
				return temp;
			}
			String[] task = new String[taskList.size()];
			RunTaskScheduler[] runTaskSchedulers = new RunTaskScheduler[taskList.size()];
			runTaskSchedulers = taskList.toArray(runTaskSchedulers);

			for (int j = 0; j < runTaskSchedulers.length; j++) {
				task[j] = runTaskSchedulers[j].getTaskName();
			}
			
			return task;
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			String s = (String)obj;

			if(s.contains("[RUNNING]")){

				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_UP);

			} else if(s.contains("[STANDBYE]")){

				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJ_ELEMENT);

			} else if(s.contains("[COMPLETE]")){

				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJS_TASK_TSK);
			}
			return  PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
		
	class NameSorter extends ViewerSorter {
	}


	public void createPartControl(Composite parent) {
		log.info("Start LS"); 
		new LaunchScheduleStorage(this);
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				LaunchSchedule.this.fillContextMenu(manager);
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
		manager.add(new Separator());
		manager.add(taskDialog);
		manager.add(deleteDialog);
		manager.add(cleanDialog);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(taskDialog);
		manager.add(deleteDialog);
		manager.add(cleanDialog);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(taskDialog);
		manager.add(deleteDialog);
		manager.add(cleanDialog);
	}

	private void makeActions() {
		taskDialog = new Action() {
			public void run() {
				AddTaskWindow wwin = new AddTaskWindow(viewer.getControl().getShell());
				wwin.open();
			}
		};
		taskDialog.setText("Add Task Dialog");
		taskDialog.setToolTipText("Add the schedule task");
		taskDialog.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(SharedImages.IMG_OBJS_TASK_TSK));

		deleteDialog = new Action() {
			public void run() {
				deleteSelected();
			}
		};
		deleteDialog.setText("Delete Selected");
		deleteDialog.setToolTipText("Delete Selected");
		deleteDialog.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(SharedImages.IMG_TOOL_DELETE));
		
		cleanDialog = new Action() {
			public void run() {
				cleanComplete();
			}
		};
		cleanDialog.setText("Clean Complete Launches");
		cleanDialog.setToolTipText("Clean Complete Launches");
		cleanDialog.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(SharedImages.IMG_OPEN_MARKER));

	}

	public void setFocus() {
		viewer.getControl().setFocus();
		viewer.refresh();
	}

	public void updateView() {
		try{ 
			viewer.refresh();
		}catch(Exception e){
			log.info(e);
		}
	}

	public void deleteSelected() {

		log.info("deleteSelected");
		int i = viewer.getTable().getSelectionIndex();
		if (i == -1) {
			log.info("nothing selected");
		} else {
			String selectedTask = viewer.getTable().getItem(i).getText();
			LaunchScheduleStorage.getInstance().deleteTask(selectedTask);
		}
	}

	public void cleanComplete() {
		log.info("cleanComplete");
		LaunchScheduleStorage.getInstance().cleanCompleteTask();
	}

	@Override
	public void run(IAction arg0) {
		log.info("run");
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.launchscheduler.views.LaunchSchedule");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			log.info(e);
		}
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub
		
	}

	
}