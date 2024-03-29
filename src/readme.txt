
//  logovanje na server -   pass:  5gZf1EnZUP9sr0Km
ssh -i /Users/ibc/Desktop/dynamic_url/serversshkey/id_rsa_dynamic_url root@165.22.28.99

(ako postoji problem sa konekcijom koristi parametre -vT da vidis output ali onda nemas commad prompt
      ssh -vT -i /Users/ibc/Desktop/dynamic_url/serversshkey/id_rsa_dynamic_url root@165.22.28.99
)




// copy from remote server to local
scp -i /Users/ibc/Desktop/TapTapp/serversshkey/mican-privatekey.pem root@mobile.taptap.rs:/root/.profile /Users/ibc/Desktop/TapTapp/serversshkey/


// copy local file to remote server
scp file.txt remote_username@10.10.0.2:/remote/directory
