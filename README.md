# MedChain

1. WANem is applied for simuatiing the latecy of a Wide Area Network. The latest version can be downloaded from: http://wanem.sourceforge.net/. To use WANem, a virtual machine, such as VirtualBox is needed to load the virtual appliance image file. For details, see the instruction on the website. When WANem is on, the <b>Packet Limit</b> size must be configured as large as possible. Otherwise, message may get lost and consensus may fail.

2. There are three JAVA-based sub-projects under the MedChain project, which are:
	1) BlockchainService,
	2) DirectoryService, and
	3) MedSessionClient (named for history reasons).

BlockchainService hosts the blockchain service, which can be replicated and run on different servers. If the replicas are not run on the local machine, each hosts.config under config/ folder needs to be updated with the new IP address. The executable files can be found in the runscripts/ folder, which contains two .bat files, medblockrun.bat and medsession.bat. Executing medsession.bat will start the proposed blockchain service, while medblockrun.bat will start the altertive service for performance comparison. To generate a jar file, packaging everyting from the src/ and config/ folder. Other useful information regarding the BFT server included in the project can be found at https://github.com/bft-smart. The source directory includes /src and /properties in the BlockchainService folder.

DirectoryService hosts the P2P directory service, which is designed to be a Web Application, runing on Tomcat 9 (or other Web Application servers). It simulate a peer-to-peer storage service based on the Chord protocol. Data are stored in key-value pair in memory. Currently, nodes are simulated by different thread. However, they can be easily extended to be run on different physical or virutal nodes with a database or file system for data persistency. The configuration of the Chord protocol is available through the configuration file chord.properties at WebContent/config. When starting the service, it will take time to stablize the Chord network. With WAN-level delay, roughly, 10 node can be setup within 1 minutes, while 100 nodes will take around 10 minutes. For identifying the initialization state, pay attention to the console of the web server until the message "P2P network initialization completes" show up.

MedSessionClient contains the client codes for testing the MedChain functions. It has several components deserved to be noticed. the source directory includes /src, /bftsmart, and /config in the MedSessionClient folder.
1) The cryptographic keys can be found in the keys/ folder which contains the related keystores. Specifically, keystore.jks contains the public-private key pairs for asymmetric encoding and digest generation, with alias 'key1', 'key2', ..., 'key100'. All public and private keys are genenerated with the ECC algorithm. The keystore.jceks stores the secret keys for symmetric encoding, with alias 'key1', 'key2', ..., 'key500'. The passwords for keystores, private keys, and secret keys can be found in the properties file (application. properties).
2) The config/ folder contains all the configuration files. Pay some attention to the hosts.config which contains the endpoints of the blochain servers for service invocation.
3) The data/ folder contains the sample medical records and data chunks for testing. Specifically, the mitdb/ folder contains 50 sample ECG data chunks from www.physionet.org (https://www.physionet.org/physiobank/database/mitdb/). The VA/ foler contains 50 EHR sample record fils from BlueButton (https://www.va.gov/BLUEBUTTON/Resources.asp). The SampleECG/ folder contains more than 120000 sample ECG records from Shimmer (https://www.shimmersensing.com), which is further partitioned into 10 data chunks.
4) The bftsmart/foler contains a code replication for invocating the blockchain service
5) src/ contains all test and utility codes. All tests and other executable classes are located under the medsession.client.executable package:
	* KeyGenerator: for generating the keystores,
	* MedSessionThroughput for scalability test,
	* MedBlockMultiDataAccess for evaluating the data access efficiency with the blockchain service only,
	* MedSessionMultiDataAccess for evaluating the data access efficiency with the blockchain service and the directory service,
	* MedSessionMixedDataAccess for evaluating the data access efficiency with the blockchain service and the directory service, with both healthcare records and data stream.
	* UpdateThroughBlockchain for evaluating the data description update efficieny from blockchain,
	* UpdateThroughDirectory for evaluating the data description update efficieny from P2P storage,
	* BigBlockSessionManagement for evaluating the storage overhead in data sharing with the blockchain service only,
	* DirectoryBasedSessionManagement for evaluating the storage overhead in data sharing with both the blockchain service and the directory service,
	* TransactionRateScalability for evaluating the scalability of transaction rate in terms of node number.

In the transaction rate test case, the following configurations need to be changed: 

1) host config in hosts.config and system.config on the blockchain server and client

(system.config)
system.servers.num = 5

system.servers.f = 1  

system.initial.view = 0,1,2,3,4

2) remove currentView

3) re-export the .jar file, including /src and /config

4) modify chord.properties in the DirectoryService: 

ds.web.DirectoryServlet.node = 20

If a jar file is needed to be generated (most of the time not needed), packaging everyting from the src/, bftsmart/, and config/ foler.
