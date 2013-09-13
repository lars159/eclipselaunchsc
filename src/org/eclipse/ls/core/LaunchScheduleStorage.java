package org.eclipse.ls.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ls.core.RunTaskScheduler.Rank;
import org.eclipse.ls.views.LaunchSchedule;
import org.eclipse.swt.widgets.Display;

/**
 * The LaunchScheduleStorage class is the storage class for all launches that are schedule it also update the display when the RunTaskScheduler state are changed.
 * 
 * 
 * @author Lars Carlsson
 *
 */
public class LaunchScheduleStorage {

	public static ArrayList<RunTaskScheduler> taskList = new ArrayList<RunTaskScheduler>();
	public static Log log = LogFactory.getLog(LaunchScheduleStorage.class) ;
	public static LaunchScheduleStorage launchScheduleStorage = null;
	public LaunchSchedule launchSchedule;
	
	public LaunchScheduleStorage(LaunchSchedule lS){
		launchScheduleStorage=this;
		launchSchedule =lS;
		
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					try { Thread.sleep(2000); 
					} 
					catch (Exception e) { 

					}
			
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							 launchSchedule.updateView();
						}
					});
				}
			}
		}
		).start();
				            
			            
	}
	
	public static LaunchScheduleStorage getInstance(){
			return launchScheduleStorage;
	}
	
	
	public void update() {
		log.info("update");
	}
	
	public  RunTaskScheduler addTask(ILaunchConfiguration iLaunchConfiguration, Calendar calendar, boolean hour, boolean day, boolean week) {
		log.info("add Task" + iLaunchConfiguration.getName() + "  " + calendar.getTime().toString());
		RunTaskScheduler rTS = null;
		try {
		rTS = new RunTaskScheduler(iLaunchConfiguration, calendar, hour, day, week);
		} catch (Exception e){
			log.warn(e);
		}
		log.info("creat RunTaskScheduler :" +rTS.getTaskName() +" : " +rTS.getTaskId());
		taskList.add(rTS);
		return rTS;
	}

	public  void deleteTask(String launchName) {
		log.info("selectedTask: " + launchName);
		if (taskList.size() > 0) {
			Iterator<RunTaskScheduler> iterableRunTask = taskList.iterator();
			try {
				for (int x = 0; iterableRunTask.hasNext(); x++) {
					log.info("x: " + x);
					RunTaskScheduler tempRTS = iterableRunTask.next();
					log.info("tempRTS.taskName: " + tempRTS.getTaskName());
					if (launchName.equals(tempRTS.getTaskName())) {
						log.info("tempRTS.getTaskId(): " + tempRTS.getTaskId());
						taskList.remove(tempRTS);
					}
				}
			} catch (Exception e) {
				log.info("no more elment");
			}
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				launchSchedule.updateView();
			}
		});
		
	}
	
	public  ArrayList<RunTaskScheduler> getTaskList() {
		log.info("getTaskList");
		return taskList;
	}

	public  void cleanCompleteTask() {
		log.info("cleanCompleteTask " );
		if (taskList.size() > 0) {
			Iterator<RunTaskScheduler> iterableRunTask = taskList.iterator();
			try {
				for (int x = 0; iterableRunTask.hasNext(); x++) {
					log.info("x: " + x);
					RunTaskScheduler tempRTS = iterableRunTask.next();
					log.info("tempRTS.taskName: " + tempRTS.getTaskName());
					if (tempRTS.getStaus().equals(Rank.COMPLETE)) {
						log.info("task complete");
						taskList.remove(tempRTS);
					}
				}
			} catch (Exception e) {
				log.info("no more elment");
			}
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				launchSchedule.updateView();
			}
		});
		
	}


	
}
