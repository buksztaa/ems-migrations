______________________________________________[ EMS-MIGRATIONS ]________________________________________________________
_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-

1___DEVELOPMENT_________________________________________________________________________________________________________

    1.1 TIBCO EMS client libraries are not available in public repositories. Thus you need to install them manually to
        your local repository. Assuming you have maven installed, navigate to your <TIBCO_HOME>/ems/8.2/lib folder and
        run the following:
        mvn install:install-file -Dfile=jms-2.0.jar -DgroupId=com.tibco -DartifactId=jms -Dversion=2.0 -Dpackaging=jar
        mvn install:install-file -Dfile=tibjms.jar -DgroupId=com.tibco -DartifactId=tibjms -Dversion=8.2 -Dpackaging=jar

    1.2 Ems-migrations relays on connection to TIBCO EMS. Default connection details are in test.properties. This
        property file also specifies the installation directory of TIBCO EMS.


2___INSTALLATION________________________________________________________________________________________________________



3___USAGE_______________________________________________________________________________________________________________


