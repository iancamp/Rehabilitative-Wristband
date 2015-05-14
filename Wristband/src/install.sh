#!/bin/bash
#make necessary changes to the file system so the host interfaces with an arduino correctly
#Derived from the "Mac OS X Locking" section at: http://playground.arduino.cc/interfacing/java
#This script MUST be run as root and given a username as an arguement (in terminal "sudo  <path to this file>  <your username>" where <path to this file> could be soemthing like /Users/iancamp/Desktop/install.sh 
USER1=$1

echo "$USER1"

#create /var/lock directory if it doesn't exist
if [[ ! -d /var/lock ]]; then
	echo "mkdir /var/lock"
fi

#Change group permissions on /var/lock to uucp
if [[ ! $(ls -l /var/ | grep lock | awk -v col=4 '{print $col}') = "_uucp" ]]; then
	echo "chgrp uucp /var/lock"
fi

#Change permissions on /var/lock to 775 if it is not 775
if [[ ! $(stat -f %Mp%Lp /var/lock) = "0775" ]]; then
	echo "chmod 775 /var/lock"
fi

#add the user to the uucp group if they are not in it
if [[ -z $(dscl . -read /Groups/_uucp GroupMembership | grep -o "$USER1") ]]; then
	echo "dscl . -append /groups/_uucp GroupMembership $USER1"
fi

exit 0;
	
	
