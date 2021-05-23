package org.learn.gossip;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import org.learn.gossip.config.GlobalConfig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * 
	 * @throws Exception
	 */
	public void testApp() throws Exception {

		GossipMain main = null, main1 = null, main2 = null;
		List<MemberInfo> seedNodes = new LinkedList<MemberInfo>();
		{
			GlobalConfig globalConfig = new GlobalConfig(Executors.newScheduledThreadPool(3), 2000, 1000, 4000, 5);
			MemberInfo info = new MemberInfo("gossip-0", "localhost", 2420, 0);
			GossipMain lmain = new GossipMain(globalConfig, info, seedNodes);
			globalConfig.getExecutorService().submit(() -> {
				lmain.start();
			});
			seedNodes.add(info);
			main = lmain;
		}
		{
			GlobalConfig globalConfig = new GlobalConfig(Executors.newScheduledThreadPool(3), 2000, 1000, 4000, 5);
			MemberInfo info = new MemberInfo("gossip-1", "localhost", 2424, 0);
			GossipMain lmain = new GossipMain(globalConfig, info, seedNodes);
			globalConfig.getExecutorService().submit(() -> {
				lmain.start();
			});
			main1 = lmain;
		}
		{
			GlobalConfig globalConfig = new GlobalConfig(Executors.newScheduledThreadPool(3), 2000, 1000, 4000, 5);
			MemberInfo info = new MemberInfo("gossip-2", "localhost", 2422, 0);
			GossipMain lmain = new GossipMain(globalConfig, info, seedNodes);
			globalConfig.getExecutorService().submit(() -> {
				lmain.start();
			});
			main2 = lmain;
		}
		long start = System.currentTimeMillis();
		while (true) {
			System.out.println("First Loop");
			main.PrintMembers();
			main1.PrintMembers();
			main2.PrintMembers();
			if (main.getMembers() == main1.getMembers() && main2.getMembers() == main1.getMembers()) {
				break;
			}
			Thread.sleep(3000);
		}
		System.setProperty("test", "");
		Thread.sleep(5000);
		main.PrintMembers();
		main1.PrintMembers();
		main2.PrintMembers();
		System.clearProperty("test");
		Thread.sleep(5000);
		while (true) {
			main.PrintMembers();
			main1.PrintMembers();
			main2.PrintMembers();
			if (main.getLiveMembers() == 2 && main.getLiveMembers() == main1.getLiveMembers() && main2.getLiveMembers() == main1.getLiveMembers()) {
				break;
			}
			Thread.sleep(3000);
		}

	}
}
