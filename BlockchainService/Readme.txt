1. Run the ith server: 
	1) Create a BFTServer.jar by selecting only the src and config folder.
	1) Open command line.
	2) Go to the <project root> folder.
	3) Run 
		
		.\runscripts\smartrun.bat i
	   
	   where i is the ID of the server to run, configured in host.config and system.config

2. Run client test, such as MainTester: configure -Dlogback.configurationFile="./config/logback.xml" and -cp properties
in the VM argument list to correctly show logs.

3. Timeout configuration in system.config (system.totalordermulticast.timeout), 
	Other possible locations: 
	1) ServersCommunicationLayer (serverSocket.setSoTimeout), 
	2) ServerCommunicationSystem (MESSAGE_WAIT_TIME)
	3) ServiceProxy (invokeTimeout)
	4) NettyClientServerCommunicationSystemClientSide (this.enoughCompleted.await(10000, TimeUnit.MILLISECONDS))
	
4. All the properties in the application.properties need to be adapted to the runntime environment

5. Data: 
	1) blockchain is in data/blk<id>.dat files
	2) event database is in EventDB<id> folders