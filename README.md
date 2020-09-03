# Tutorial Java Backend  
### For windows:
* Installer [Visual Studio Code](https://code.visualstudio.com/download)
* Installer [Java Jdk 14.0.2](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)  
* Installer [Maven (Binary zip)](https://maven.apache.org/download.cgi) og sett "apache-maven-3.6.3" mappen i "C:\Program Files\" 
* Ordne environment variables:
  * Legg til ```C:\Program Files\Java\jdk-14.0.2``` til environment variable -> "JAVA_HOME"
  * Legg til ```C:\Program Files\apache-maven-3.6.3\bin``` til environment variable -> "Path"
* Gå inn på repo mappen i Visual Studio Code og velg "Start Debugging" (ikke Run Code)

### For mac:
* Installer [Visual Studio Code](https://code.visualstudio.com/download)
* Installer [Java Jdk 14.0.2](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)
* Installer [Maven (Binary zip)](https://maven.apache.org/download.cgi)
  * Lag en mappe som heter "Maven" i systembiblioteket. Unzip mappen apache-maven-3.6.3 og legg den til i 'Bibliotek/Maven'.
* Skriv i terminal: 
  * ```~/.bash_profile```
* Lim inn følgende i bash_profile
  * ```export JAVA_HOME='/Library/Java/JavaVirtualMachines/jdk-14.jdk/Contents/Home'```
  * ```export PATH=/Library/Maven/apache-maven-3.6.3/bin:$PATH```
* Velg exit, lagre og åpne en ny terminal
* Sjekk om Java og Maven er riktige versjoner ved å skrive dette i terminalen:
  * ```source ~/.bash_profile```
  * ```java --version```
  * ```mvn -v```
   Gå inn på repo mappen i Visual Studio Code og velg "Start Debugging" (ikke Run Code)
