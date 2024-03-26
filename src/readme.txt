
//  logovanje na server -   pass:  5gZf1EnZUP9sr0Km
ssh -i /Users/ibc/Desktop/dynamic_url/serversshkey/id_rsa_dynamic_url root@165.22.28.99

(ako postoji problem sa konekcijom koristi parametre -vT da vidis output ali onda nemas commad prompt
      ssh -vT -i /Users/ibc/Desktop/dynamic_url/serversshkey/id_rsa_dynamic_url root@165.22.28.99
)




// copy from remote server to local
scp -i /Users/ibc/Desktop/TapTapp/serversshkey/mican-privatekey.pem root@mobile.taptap.rs:/root/.profile /Users/ibc/Desktop/TapTapp/serversshkey/


// copy local file to remote server
scp file.txt remote_username@10.10.0.2:/remote/directory



========================= BUILD JAVA APP ======================
 mvn clean install

salji na server:

scp -i /Users/ibc/Desktop/dynamic_url/serversshkey/id_rsa_dynamic_url /Users/ibc/Desktop/all/prog/java/UrlWebService/target/UrlWebService-1.0-SNAPSHOT.jar root@165.22.28.99:/root/dynamic_url/

na serveru startujes sa 
nohup java -jar UrlWebService-1.0-SNAPSHOT.jar &
 (pa jos jedan ENTER)

i onda logove gledas sa less nohup.out


Napravio sam i systemctl verziju za startovanje pa je moguce da se radi startovanje i ovako:  
	 systemmctl start dynamic_url     (logovi ce biti preusmereni u /root/dynamic_url/logs.log)
