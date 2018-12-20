# Intent based VNF descriptor compiler for ODL

This project started as a fork of https://gitgub.com/telefonicaid/vibnemo . It contains all the development files to implement a compiler for Virtual Network Functions (VNFs) based on the IETF draft on [High-level VND Descriptors using NEMO](https://datatracker.ietf.org/doc/draft-aranda-nfvrg-recursive-vnf/) .

### Prerequisites
  * [Ubuntu Server 18.04 LTS](https://www.ubuntu.com/download/server)
    * Memory: 4GB
    * Storage: 10GB
  * Java 1.8
     ```
     sudo add-apt-repository ppa:webupd8team/java
     sudo apt-get install oracle-java8-installer
     ```
  * Apache Maven 3.5.4
     ```
     sudo apt-get install maven
     ```
  * Python 3.6
     
### Installation

  * Set *M2_HOME*
    ```
    sudo vim /etc/profile.d/apache-maven.sh
    ```
    ```
    export M2_HOME=/home/mami
    export PATH=${M2_HOME}/bin:${PATH}
    ```
  * Create *.m2* folder
    ```
    cd /home/mami
    mkdir .m2
    ```
  * Create *settings.xml* file
    ```
    cd /home/mami/.m2
    touch settings.xml
    cp -n $M2_HOME/.m2/settings.xml{,.orig} ; \wget -q -O - https://raw.githubusercontent.com/opendaylight/odlparent/master/settings.xml > $M2_HOME/.m2/settings.xml
    ```
  * Clone the project
    ```
    cd /home/mami
    git clone https://github.com/mami-project/nemo.git
    ```
  * Compile the project
    ```
    cd /home/mami
    mvn clean install -DskipTests
    ```
  * Run karaf
    ```
    cd /home/mami/nemo/nemo-karaf/target/assembly/bin
    ./karaf
    ```
  * If *[ERROR] Failed to construct terminal*
    ```
    logout
    ```
    ```
    export TERM=xterm-color
    ./karaf
    ```
  * Install features
    ```
    feature:install odl-nemo-api odl-nemo-engine odl-nemo-engine-rest
    ```
    
    
### Demo
  * Move to demo folder
    ```
    cd /home/mami/nemo/demo
    ```
  * Modify *vlc_vnfd* and *vlc_vnfd_2* path in the intent.txt file
    ```
    file:///{PATH}/nemo/demo/vlc_vnf.yaml
    ```
  * Run demo
    ```
    python3 config.py
    python3 demo.py --intent intent.txt --instance video --style osm
    ```  
  * *video_vnfd.yaml* has been generated
    
      
    
