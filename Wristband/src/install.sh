#!/bin/bash
#make necessary changes to the file system so the host interfaces with an arduino correctly

USER1=$1

echo "$USER1"

#create /var/lock directory if it doesn't exist
if ! [[ -d /var/lock ]]; then
	echo "mkdir /var/lock"
fi

#Change group permissions on /var/lock to uucp
if ! [[ $(ls -l /var/lock | awk -v col=4 '{print $col}') = "uucp" ]]; then
	echo "chgrp uucp /var/lock"
fi

#Change permissions on /var/lock to 775 if it is not 775
if ! [[ $(stat -c "%a %b" /var/lock) = "775" ]]; then
	echo "chmod 775 /var/lock"
fi

#add the user to the uucp group if they are not in it
if ! [[ -z $(dscl . -read /Groups/mygroup GroupMembership | grep -o $USER1) ]]; then
	echo "dscl . -append /groups/_uucp GroupMembership $USER1"
fi

exit 0;
	
	
