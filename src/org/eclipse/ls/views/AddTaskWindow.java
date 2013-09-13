package org.eclipse.ls.views;


import java.awt.Checkbox;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ls.core.LaunchScheduleStorage;
import org.eclipse.ls.core.RunTaskScheduler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.prefs.Preferences;
/**
 * The AddTaskWindow creats the runtaskScheduler object 
 * @author lc
 *
 */
public class AddTaskWindow extends org.eclipse.jface.dialogs.Dialog
{
	private ArrayList<RunTaskScheduler> sessionAddTaskList = new ArrayList<RunTaskScheduler>();
    private static TableViewer tableViewer;
    private Table table;
    private Calendar taskCalander = Calendar.getInstance();
    private ILaunchConfiguration taskConfiguration;
    private Combo selectLaunche ;
    private DateTime time ;
    private Button hour;
    private Button week;
    private Button day;
    private DateTime calendar;
	public static Log log = LogFactory.getLog("AddTaskWindow") ;
    
    public AddTaskWindow(Shell shell)
    {
        super(shell);
        
    }
    
    public ILaunchConfiguration getTask(){
     return taskConfiguration;
    } 
    public Calendar getTime(){
        return taskCalander;
    } 

    protected Control createContents(Composite parent)
    {
    	
       Shell shell = this.getShell();
       shell.setText("Launch Schedule");
       FormLayout layout= new FormLayout();
       shell.setLayout (layout);
       shell.setSize(500, 300);

       Label repeatLabel = new Label (shell, SWT.NULL);
       repeatLabel.setText("Repeat every: ");	
       hour = new Button(shell, SWT.CHECK);
       hour.setText("Hour");
       day =  new Button(shell, SWT.CHECK);
       day.setText("day");
       week = new Button(shell, SWT.CHECK);
       week.setText("Week");
       
       Label laucheLabel = new Label (shell, SWT.NULL);
       laucheLabel.setText("Runner to schedule: ");	
       
       selectLaunche = new Combo(shell, SWT.NULL );
        ILaunchConfiguration[] iLaunchConfigurations;
		try {
			iLaunchConfigurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
	        log.info("iLaunchConfigurations.length  " +iLaunchConfigurations.length);
	        for (ILaunchConfiguration launchConfiguration : iLaunchConfigurations) {
	        	   selectLaunche.add(launchConfiguration.getName());
			}
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		selectLaunche.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
	        	Device device = Display.getCurrent ();
	        	Color white = new Color (device, 255, 255, 255);
	        	selectLaunche.setBackground(white);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label dateLabel = new Label (shell, SWT.NULL);
		dateLabel.setText("Date for run:");	
        calendar = new DateTime (shell, SWT.CALENDAR);
    	calendar.addSelectionListener (new SelectionAdapter () {
    		public void widgetSelected (SelectionEvent e) {
    			System.out.println ("calendar date changed: " );
    		}
    	});

    	  time = new DateTime (shell, SWT.TIME);
    	time.addSelectionListener (new SelectionAdapter () {
    		public void widgetSelected (SelectionEvent e) {
    			System.out.println ("time changed");
    		}
    	});
    	
    	
    
    	
    	Button add = new Button (shell, SWT.PUSH);
    	add.setText (" Add task ");
    	add.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			log.info("add");
    			addTask();
    			Preferences prefs =
    				    //Platform.getPreferencesService().getRootNode().node(Plugin.PLUGIN_PREFEERENCES_SCOPE).node(MY_PLUGIN_ID);
    				    InstanceScope.INSTANCE.getNode(""); // does all the above behind the scenes

    				  prefs.put("test", "test"); 

    				  try {
    				    // prefs are automatically flushed during a plugin's "super.stop()".
    				    prefs.flush();
    				    log.info("check pref" +prefs.get("test","err"));
    				  } catch(Exception e1) {
    				    //TODO write a real exception handler.
    				    e1.printStackTrace();
    				  }
    		}
    	});
    	Button ok = new Button (shell, SWT.PUSH);
    	ok.setText ("Ok");
    	ok.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			log.info("Ok");
    			close();
    		}
    	});
    	
    	Button cancel = new Button (shell, SWT.PUSH);
    	cancel.setText ("Cancel");
    	cancel.addSelectionListener(new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			log.info("Cancel");
    			removeAddedTask();
    			close();
    		}
    	});
    	//row1
	  	  FormData laucheForm = new FormData();
	  	  laucheForm .left    = new FormAttachment(0,5);
	  	  laucheLabel.setLayoutData(laucheForm);

	  	  //row 1
	  	  FormData data0 = new FormData();
		  data0 .left    = new FormAttachment(laucheLabel,5); 
		  selectLaunche.setLayoutData(data0);
		  //row  1
    	  FormData data1 = new FormData();
    	  data1 .left    = new FormAttachment(0,5); 
    	  data1.top    = new FormAttachment(laucheLabel,10);
    	  dateLabel.setLayoutData(data1);
    	  //row 2
    	  FormData data2 = new FormData();
    	  data2.left     = new FormAttachment(laucheLabel,5);
    	  data2.top    = new FormAttachment(laucheLabel,10);
    	  calendar.setLayoutData(data2);

    	  FormData data3 = new FormData(); 
    	  data3.left     = new FormAttachment(laucheLabel,5);
    	  data3.top      = new FormAttachment(calendar,5);
    	  time.setLayoutData(data3);
    	  
    	  FormData re1 = new FormData(); 
    	  re1.left     = new FormAttachment(time,5);
    	  re1.top      = new FormAttachment(calendar,5);
    	  repeatLabel.setLayoutData(re1);
    	  
    	  FormData re2 = new FormData(); 
    	  re2.left     = new FormAttachment(repeatLabel,5);
    	  re2.top      = new FormAttachment(calendar,5);
    	  hour.setLayoutData(re2);
    	  
    	  FormData re3 = new FormData(); 
    	  re3.left     = new FormAttachment(hour,5);
    	  re3.top      = new FormAttachment(calendar,5);
    	  day.setLayoutData(re3);
    	  
    	  FormData re4 = new FormData(); 
    	  re4.left     = new FormAttachment(day,5);
    	  re4.top      = new FormAttachment(calendar,5);
    	  week.setLayoutData(re4);
    	  
    	  
    	  
    	  FormData dataAdd = new FormData(); 
    	  dataAdd.top      = new FormAttachment(time,5);
    	  dataAdd.left     = new FormAttachment(laucheLabel,5);
    	  add.setLayoutData(dataAdd);
    	  
    	  FormData dataOk = new FormData(); 
    	  dataOk.top   = new FormAttachment(add,10);
    	  dataOk.left     = new FormAttachment(week,5);
    	  ok.setLayoutData(dataOk);
    	  
    	  FormData dataCancel = new FormData(); 
    	  dataCancel.top   = new FormAttachment(add,10);
    	  dataCancel.left     = new FormAttachment(ok,5);
    	  cancel.setLayoutData(dataCancel);

 
        return null;
    }
    

    protected void removeAddedTask() {   	
    	for (RunTaskScheduler task : sessionAddTaskList){
    		LaunchScheduleStorage.getInstance().deleteTask(task.getTaskName());
    	}
    }

	public void addTask(){
			String launch = selectLaunche.getText();
			ILaunchConfiguration[] iLaunchConfigurations=null;;
			try {
				iLaunchConfigurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        log.info("iLaunchConfigurations.length  " +iLaunchConfigurations.length);
	        for (ILaunchConfiguration launchConfiguration : iLaunchConfigurations) {
	            log.info("launchConfiguration equels lanunch " +launchConfiguration.getName() +" lanch " +launch);
	        	  if(launchConfiguration.getName().equals(launch)){
	        		  taskConfiguration =launchConfiguration; 
	        	  }
	        }
	        if(taskConfiguration == null){
	        	Device device = Display.getCurrent ();
	        	Color red = new Color (device, 255, 139, 139);
	        	selectLaunche.setBackground(red);
	        	return;
	        }
	        taskCalander.set(calendar.getYear(), calendar.getMonth(), calendar.getDay(), time.getHours(),time.getMinutes(),time.getSeconds());
	        if(taskCalander.getTimeInMillis() < new Date().getTime()) {
	            final MessageBox box = new MessageBox(getShell(), SWT.OK);
	            box.setMessage("Date/time is in the past.");
	            box.setText("Date/Time");
	            box.open();
	            return;
	        	
	        }
	        log.info("time set " +taskCalander.toString());
	        try {
				sessionAddTaskList.add(LaunchScheduleStorage.getInstance().addTask(taskConfiguration, taskCalander, hour.getSelection(), day.getSelection(), week.getSelection()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    

 
    public static TableViewer getTbv()
    {
        return tableViewer;
    }

 

    public static void setTbv(TableViewer viewer)
    {
        tableViewer = viewer;
    }
}
