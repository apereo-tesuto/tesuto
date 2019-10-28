Drools Engine
The drools-engine project is a Maven project which generates a stand-alone Jar.  This project is intended to be a standard dependency in other Maven projects that would like to include rules engine functionality.

Building the Project
To build drools-engine for local development, execute a standard Maven install to deploy it in your local Maven repository:

	mvn clean install

To build drools-engine for production, execute a standard Maven deploy to deploy it to the defined Nexus private repository:

	mvn clean deploy

Including drools-engine in other projects
You include this jar as a Maven dependency in whatever code you'd like.
For example, add the following dependency to your project's pom.xml

	<dependency>
    	<groupId>org.jasig.portlet.rules</groupId>
        <artifactId>drools-engine</artifactId>
        <version>0.0.1</version>
    </dependency>

This project expects that the containing application is a Spring application.  Create
the beans using whatever method is appropriate for your application.  If you already do a 
component-scan in one of your applicationContext.xml files, you simply need to add a 
base-package to create the needed beans (see example below).

    <context:component-scan base-package="org.jasig.portlet.rules"/>

The main class is the DroolsRulesService service.  Autowire this service into whatever function needs
access to the rules engine.  Here's an example of using the DroolsEngineService:

	@Autowired
	private DroolsRulesService rulesService;
	...
	
	String userName = "username";
	Map<String, Object> messageInfo = new HashMap<String, Object>();
	List<String> users = new ArrayList<String>();
	users.add(userName);
	messageInfo.put("users", users);
	List<Object> facts = new ArrayList<Object>();
	facts.add(messageInfo);
	log.debug("messageInfo:[" + messageInfo + "]");
	rulesService.execute(facts);

DroolsRulesService executes against a list of facts.  A fact is simply an object that a rule
can understand.  It could be a map, a string a staticly typed object, etc.

Drools-Engine also contains a couple of controllers which allow you to query the rules engine status and to add facts externally via REST calls.  This is primarily for testing.

Rules are automatically loaded into the DroolsRulesService from a Maven repository.  See the Drools-Rules project for details about creating rules.  The specific Maven project that contains the rules to be loaded is configurable.  Use this configurations to choose which Drools rules project to load.  Make these settings visible in as a configuration property.

	drools.rules.groupId=org.jasig.portlet.rules
	drools.rules.artifactId=drools-rules
	drools.rules.versionId=RELEASE

A versionId of RELEASE will automatically load later versions if a new version is added to the Maven repository.  

The frequency of checks to the Maven repository is also configurable.

	drools.scanner.refreshinmillis=10000  # refresh every 10 seconds

Requirements

Any project that includes this code will need to have access to Maven commands.  Maven v1.8 works; 
I haven't tested against earlier versions.  The initial use case was for uPortal to run in a Docker container, and the Docker container that runs uPortal needs to have Maven available.  In addition, Maven needs to have access, through a settings.xml, to the Maven repository containing the Drools Rules project artifacts.
