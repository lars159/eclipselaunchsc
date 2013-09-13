package org.eclipse.ls;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;

import junit.framework.TestCase;
/**
 * simpel testcase for simulate a Launh Schedule
 * @author lc
 *
 */
public class TestLaunchScheduler extends TestCase {

	
	public void testLaunhScheduler(){
		ILaunchConfiguration[] iLaunchConfigurations;
		try {
//			LaunchSchedule launchSchedule = new LaunchSchedule
			iLaunchConfigurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			if(iLaunchConfigurations.length ==0){
				System.out.println("ILaunchConfiguration is null");
		
			}
			
			new Thread(new Runnable() {

				public void run() {
					while (true) {
						try { Thread.sleep(20000); 
						} 
						catch (Exception e) { 

						}
						 
					}
				}
			}
			).start();
			
//			iLaunchConfigurations[0].launch(ILaunchManager.RUN_MODE, new  TaskMonitor());
//			System.out.println("iLaunchConfigurations.length  " +iLaunchConfigurations.length);
//			LaunchSchedule launchScheduler = new LaunchSchedule();
//			Calendar c = Calendar.getInstance();
//			c.add(c.SECOND, 20);
//			launchScheduler.addTask(iLaunchConfigurations[1], c);
//			if(launchScheduler.taskList.size()==0){
//				System.out.println("taskList is 0");
//				fail();
//			}
//			Thread.sleep(30);
//			if(launchScheduler.taskList.size()!=0){
//				System.out.println("taskList is not 0");
//				fail();
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
      
  
	}
	class TaskMonitor extends NullProgressMonitor {
		@Override
		public void done() {
		}
	}
	
}
