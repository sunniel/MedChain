package medsession.client.jmeter;

public class JMeterClient {

	// public static void main(String[] args) {
	// // Set jmeter home for the jmeter utils to load
	// File jmeterHome = new File("D://apache-jmeter-4.0");
	// String slash = System.getProperty("file.separator");
	//
	// if (jmeterHome.exists()) {
	// File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash
	// + "jmeter.properties");
	// if (jmeterProperties.exists()) {
	// try {
	// // JMeter Engine
	// StandardJMeterEngine jmeter = new StandardJMeterEngine();
	//
	// // JMeter initialization (properties, log levels, locale, etc)
	// JMeterUtils.setJMeterHome(jmeterHome.getPath());
	// JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
	// JMeterUtils.initLogging();// you can comment this line out to see extra log
	// messages of i.e. DEBUG
	// // level
	// JMeterUtils.initLocale();
	//
	// // JMeter Test Plan, basically JOrphan HashTree
	// HashTree testPlanTree = new HashTree();
	//
	// // Customized sampler
	// JavaSampler client = new JavaSampler();
	// client.setClassname("medsession.client.executable.MedSessionMain");
	//
	//
	// // Loop Controller
	// LoopController loopController = new LoopController();
	// loopController.setLoops(2);
	// loopController.setFirst(true);
	// loopController.setProperty(TestElement.TEST_CLASS,
	// LoopController.class.getName());
	// loopController.setProperty(TestElement.GUI_CLASS,
	// LoopControlPanel.class.getName());
	// loopController.initialize();
	//
	// // Thread Group
	// ThreadGroup threadGroup = new ThreadGroup();
	// threadGroup.setName("Sample Thread Group");
	// threadGroup.setNumThreads(1);
	// threadGroup.setRampUp(1);
	// threadGroup.setSamplerController(loopController);
	// threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
	// threadGroup.setProperty(TestElement.GUI_CLASS,
	// ThreadGroupGui.class.getName());
	//
	// // Test Plan
	// TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
	//
	// testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
	// testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
	// testPlan.setUserDefinedVariables((Arguments) new
	// ArgumentsPanel().createTestElement());
	//
	// // Construct Test Plan from previously initialized elements
	// testPlanTree.add(testPlan);
	// HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
	// // threadGroupHashTree.add(examplecomSampler);
	// threadGroupHashTree.add(client);
	//
	// // save generated test plan to JMeter's .jmx file format
	// SaveService.saveTree(testPlanTree, new
	// FileOutputStream("report//jmeter_java_request.jmx"));
	//
	// // add Summarizer output to get test progress in stdout like:
	// // summary = 2 in 1.3s = 1.5/s Avg: 631 Min: 290 Max: 973 Err: 0 (0.00%)
	// Summariser summer = null;
	// String summariserName = JMeterUtils.getPropDefault("summariser.name",
	// "summary");
	// if (summariserName.length() > 0) {
	// summer = new Summariser(summariserName);
	// }
	//
	// // Store execution results into a .jtl file, we can save file as csv also
	// String reportFile = "report//java_request.jtl";
	// String csvFile = "report//java_request.csv";
	// ResultCollector logger = new ResultCollector(summer);
	// logger.setFilename(reportFile);
	// ResultCollector csvlogger = new ResultCollector(summer);
	// csvlogger.setFilename(csvFile);
	// testPlanTree.add(testPlanTree.getArray()[0], logger);
	// testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
	// // Run Test Plan
	// jmeter.configure(testPlanTree);
	// jmeter.run();
	//
	// System.out.println("Test completed. See " + jmeterHome + slash + "report.jtl
	// file for results");
	// System.out.println(
	// "JMeter .jmx script is available at " + jmeterHome + slash +
	// "jmeter_api_sample.jmx");
	// System.exit(0);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	//
	// System.err.println("jmeterHome property is not set or pointing to incorrect
	// location");
	// System.exit(1);
	//
	// }
	//
}
