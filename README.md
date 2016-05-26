#   ems-migrations

##  Introduction

Ems-migrations is an attempt to organize and structure scripts TIBCO EMS is updated with. The approach is based on various implementations of well-known various database migrations.
The concept is:

*   A migration is a script that does ammendments to the configuration of EMS. The script is in fact what you would run TIBEMSADMIN with.
*   Each migration consists of two parts: up and down - to migrate changes and roll them back. You need to write both of them
*   The library migrates the server it connects to by running a series of scripts up (or down for rollback) to a version you specify. If not specified the version, the server will be migrated to the newest version.
*   The library knows what is the version the server was migrated with the last time by reading a message from a designated queue. After successful migration it stores a new message containing the updated migration version number.
  
##  Project setup

*   TIBCO EMS client libraries are not available in public repositories. Thus you need to install them manually to your local repository. Assuming you have maven installed, navigate to your <TIBCO_HOME>/ems/8.2/lib folder and run the following:

    ```
    mvn install:install-file -Dfile=jms-2.0.jar -DgroupId=com.tibco -DartifactId=jms -Dversion=2.0 -Dpackaging=jar
    mvn install:install-file -Dfile=tibjms.jar -DgroupId=com.tibco -DartifactId=tibjms -Dversion=8.2 -Dpackaging=jar
    ``` 

*   Ems-migrations relays on connection to TIBCO EMS. Default connection details are in test.properties. This property file also specifies the installation directory of TIBCO EMS.

##  Installation

**TBD**

##  Usage

**TBD**
