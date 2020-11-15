# Reisenavet Integration-tool backend  

## User guide

See [User guide](https://github.com/Kundestyrt-ReiseNavet/integration-tool-frontend/wiki/User-guide)

## Supported files

See [Supported files](https://github.com/Kundestyrt-ReiseNavet/integration-tool-frontend/wiki/Supported-files)

## Installing

### For windows:
* Install [Visual Studio Code](https://code.visualstudio.com/download)
* Install [Java Jdk 14.0.2](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)  
* Install [Maven (Binary zip)](https://maven.apache.org/download.cgi) and put the "apache-maven-3.6.3" folder in "C:\Program Files\" 
* Fix environment variables:
  * Add ```C:\Program Files\Java\jdk-14.0.2``` to environment variable -> "JAVA_HOME"
  * Add ```C:\Program Files\apache-maven-3.6.3\bin``` to environment variable -> "Path"
* Open the repository folder in Visual Studio Code and choose "Start Debugging" (not Run Code)

### For mac:
* Install [Visual Studio Code](https://code.visualstudio.com/download)
* Install [Java Jdk 14.0.2](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)
* Install [Maven (Binary zip)](https://maven.apache.org/download.cgi)
  * Make a folder named "Maven" in the systemlibrary. Put the "apache-maven-3.6.3" folder in 'Library/Maven'.
* Write in the terminal: 
  * ```~/.bash_profile```
* Copy the following to bash_profile
  * ```export JAVA_HOME='/Library/Java/JavaVirtualMachines/jdk-14.jdk/Contents/Home'```
  * ```export PATH=/Library/Maven/apache-maven-3.6.3/bin:$PATH```
* Pick exit, save and open a new terminal
* Check if Java and Maven are of the correct versions by writing this in the terminal:
  * ```source ~/.bash_profile```
  * ```java --version```
  * ```mvn -v```
 * Open the repository folder in Visual Studio Code and choose "Start Debugging" (not Run Code)
   
## Deployment

Our system is entirely (both front-end and back-end) hosted on SINTEF's server at ProISP. 
The server is available on [this link](http://dataintegrasjon.reisenavet.no/) (not HTTPS), or on IP `46.250.220.200`

Before deployment:

* Install an SFTP-client
  * Windows: [WinSCP](https://winscp.net/eng/download.php)
  * Mac/Linux: [FileZilla](https://filezilla-project.org/download.php?show_all=1)
* Install an SSH-client
  * Windows: [Putty](https://www.putty.org/)
  * Mac/Linux: [Already installed in terminal](https://www.ssh.com/ssh/command/)
* Install [Eclipse](https://www.eclipse.org/downloads/) for java-development

Deployment:

1. Open the project folder in Eclipse.
2. Navigate to `src/main/java/` > `(default package)` > `App.java`
3. Set up a run-configuration 
   * Set main class to `App.java`
   * Click `Apply`
   * Close `Run Configurations`-window
4. Right click `App.java` and click `Export...`.
5. Select "Runnable Jar file" and use the following settings:
   * Launch configuration: The one you created before.
   * Export destination: `SemanticMatcher\App.jar` (where `SemanticMatcher` is the name of the project.)
   * Library handling: `Extract required packages into generated JAR`.
6. Click finish to build the App.jar file. 
7. Connect to SSH:
   * Windows - Putty:
     * Hostname: `student@46.250.220.200`
     * Port: `22`
     * Connection type: `SSH`
     * Password prompt: Ask Audun
   * Mac/Linux - Terminal: 
     * `ssh student@46.250.220.200`
     * Password prompt: Ask Audun
8. SSH: Stop the running java process with `killall java`
9. Connect to SFTP:
   * Protocol: `SFTP`
   * Hostname/IP: `46.250.220.200`
   * Port: `22`
   * Username: `student`
   * Password: Ask Audun
10. SFTP: Copy the compiled `App.jar` into this the server-folder `/home/student`
11. SSH: Restart the java server with `nohup java -jar App.jar &`


(If this doesn't work due to some missing dependency, fix this in eclipse before step 4.)
