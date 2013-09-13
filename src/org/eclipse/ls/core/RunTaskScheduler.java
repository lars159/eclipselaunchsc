package org.eclipse.ls.core;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

/**
 * The RunTaskScheduler is the contains the information of with lauchare and when it will be runned.
 * 
 * @author lc
 * 
 */
public class RunTaskScheduler {
	/**
	 * 
	 * The diffrent state a lauch can have.
	 *
	 */
	public enum Rank {
		COMPLETE, STANDBYE, RUNNING
	}

	private static int taskValue;
	public ILaunchConfiguration iLaunchConfigurations;
	private String nameLconf;
	private Long waitInQueue;
	private Rank rankStatus;
	private int taskId;
	private String taskName;
	private Calendar launchTime;
	private boolean week= false;
	private boolean hour= false;
	private boolean day= false;
	public static Log log = LogFactory.getLog("RunTaskScheduler") ;
	
	public RunTaskScheduler() {
	}

	
	public RunTaskScheduler(ILaunchConfiguration iLaunchConfiguration, Calendar calendar, boolean hour, boolean day, boolean week) {
		nameLconf = iLaunchConfiguration.getName();
		taskId = taskValue++;
		rankStatus = Rank.STANDBYE;
		this.iLaunchConfigurations = iLaunchConfiguration;
		launchTime = (Calendar) calendar.clone();
		String rep = "";
		if ( hour || day || week){
			rep = "Every: ";
			if(hour)
				rep = "hour";
			if(day)
				rep += "day";
			if(week)
				rep += "week";
		}
		taskName =nameLconf + " <" + launchTime.getTime().toString() + ">" +" [" +getStaus()  +"]" +rep;
		waitInQueue = (calendar.getTimeInMillis() - System.currentTimeMillis());
		log.info("RunTaskScheduler in : " + waitInQueue / 1000 + " s");
		log.info("RunTaskScheduler.tostring : " + this.toString());
		log.info("iLaun :  " + iLaunchConfiguration);
		this.week = week;
		this.hour = hour;
		this.day= day;
		new Schedule("RunTaskScheduler in : " + waitInQueue + " s").schedule(waitInQueue);
	}

	public String getTaskName() {
	
		String rep = "";
		if ( hour || day || week){
			rep = "Every: ";
			if(hour)
				rep = "hour";
			if(day)
				rep += "day";
			if(week)
				rep += "week";
		}
		taskName =nameLconf + " <" + launchTime.getTime().toString() + ">" +" [" +getStaus() +"] " +rep;
		return taskName;
	}

	public int getTaskId() {
		return taskId;
	}

	public Rank getStaus() {
		return rankStatus;
	}

	public void setComplete() {
		rankStatus = Rank.COMPLETE;
	}

	public boolean isStatusComplete() {
		if (rankStatus == Rank.COMPLETE)
			return true;
		else
			return false;
	}

	class TaskMonitor extends NullProgressMonitor {
		@Override
		public void done() {
			log.info("TaskMonitor");
			setComplete();
		}
	}

	class Schedule extends Job {
		public Schedule(String name) {
			super(name);
		}

		public IStatus run(IProgressMonitor monitor) {
			try {
				rankStatus = Rank.RUNNING;
				log.info("system run " + getName() + " task id " + taskId);
				iLaunchConfigurations.launch(ILaunchManager.RUN_MODE, new TaskMonitor());
				if (hour) {
					log.info("run in on  houre");
					launchTime.add(Calendar.HOUR,1);
					LaunchScheduleStorage.getInstance().addTask(iLaunchConfigurations, launchTime, hour, day, week);
				}
				else if (day) {
					log.info("run in on  day");
					launchTime.add(Calendar.HOUR,24);
					LaunchScheduleStorage.getInstance().addTask(iLaunchConfigurations, launchTime, hour, day, week);
				}
				else if (week) {
					log.info("run in on  week");
					launchTime.add(Calendar.HOUR,24*7);
					LaunchScheduleStorage.getInstance().addTask(iLaunchConfigurations, launchTime, hour, day, week);
				}
			} catch (CoreException e) {
				System.err.println("Could not run the configuration ");
				e.printStackTrace();
			}
			return Status.OK_STATUS;
		}
	}
}
