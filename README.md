#   ems-migrations

##  Introduction

Ems-migrations is an attempt to organize and structure scripts TIBCO EMS is updated with. The approach is based on various implementations of well-known various database migrations.
The concept is:

*   A migration is a script that does ammendments to the configuration of EMS. The script is in fact what you would run TIBEMSADMIN with.
*   Each migration consists of two parts: up and down - to migrate changes and roll them back. You need to write both of them.
*   The library migrates the server it connects to by running a series of scripts up (or down for rollback) to a version you specify. If not specified the version, the server will be migrated to the newest version.
*   The library knows what is the version the server was migrated with the last time by reading a message from a designated queue. After successful migration it stores a new message containing the updated migration version number.

##  Installation

**1.**  Download the latest stable package from here: `[link]`.
**2.**  Unpack the archive.
**3.**  Copy *tibjms.jar* and *jms-2.0.jar* from your `<EMS_HOME>/lib` to the lib directory of newly extracted directory.
  
##  Usage  

After the installation, the structure of the project should look like the example below:
  
    root
        |-bin
        |   |-ems-m
        |   |-ems-m.bat
        |-conf
        |   |-default.properties
        |-migrations
        |   |-up
        |   |-down
        |-lib
            |-ems-m.jar
            |-jms-2.0.jar
            |-tibjms.jar

### Running the application

The command syntax is:

    ./ems-m [command] [-option option_value]*

Command is required. In case of a wrong command, help is printed. Some options are required by some commands, some are optional, and some optional only under condition they are defined in the property file. 

### Available commands

| Command           | Description                                                                   | Options                                                                                                                                                   |
|-------------------|-------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **create**        | Creates a new migration                                                       | **desc** *(optional)* - description of the migration. Will become a part of the filename. If not provided, will be replaced by a timestamp                | 
| **migrate**       | Migrates the target server to the speccified version or to the newest found   | **ver** *(optional)* - version of the migration the target server should be migrated to. If not provided, the latest migration version will be used       |
| **rollback**      | Rolls back the target server to the specified version                         | **ver** *(required)* - version of the migration the target server should be rolled back to.                                                               |
| **check-version** | Displays a the migration version of the target server                         |                                                                                                                                                           |
| **help**          | Prints help                                                                   |                                                                                                                                                           |

All properties can be also provided as options. Options have a priority over properties.

### Configuration

Initially ems-migrations are configured by default.properties within in the conf folder. More configurations can be created by adding other properties files in the conf folder and providing `-conf` option, for example:

    ./ems-m check-version -conf development

In this example conf/development.properties will be used to establish connection.

All configuration properties are enlisted below:

| Property          | Description                                                                   | Required  | Default value                                         |
|-------------------|-------------------------------------------------------------------------------|-----------|-------------------------------------------------------|
| dir               | Migrations root directory. It's where up and down folder will be created      | X         |                                                       |
| emshome           | EMS local installation home directory                                         | X         |                                                       |
| url               | EMS connection url                                                            | X         | tcp://localhost:7222                                  |   
| user              | EMS connection username                                                       | X         | admin                                                 |
| pw                | EMS connection password                                                       |           |                                                       |
| type              | Migrations type                                                               |           | file                                                  |
| confactory        | EMS connection factory object                                                 |           | QueueConnectionFactory                                |
| queue             | EMS migration queue                                                           |           | q.ems-migrations.server.version                       |
| ctxfactory        | Initial context factory class                                                 |           | com.tibco.tibjms.naming.TibjmsInitialContextFactory   |



##  Development

TIBCO EMS client libraries are not available in public repositories. Thus you need to install them manually to your local repository. Assuming you have maven installed, navigate to your <TIBCO_HOME>/ems/8.2/lib folder and run the following:

        mvn install:install-file -Dfile=jms-2.0.jar -DgroupId=com.tibco -DartifactId=jms -Dversion=2.0 -Dpackaging=jar
        mvn install:install-file -Dfile=tibjms.jar -DgroupId=com.tibco -DartifactId=tibjms -Dversion=8.2 -Dpackaging=jar

Ems-migrations relays on connection to TIBCO EMS. Default connection details are in test.properties. This property file also specifies the installation directory of TIBCO EMS.

---
TIBCO, the TIBCO logo, TIBCO Software, EMS are trademarks or registered trademarks of TIBCO Software Inc. or its subsidiaries in the United States and/or other countries. All other product and company names and marks in this document are the property of their respective owners and mentioned for identifcation purposes only.