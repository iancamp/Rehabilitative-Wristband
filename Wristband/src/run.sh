#!/bin/sh
#

#check and see if the system has been configured to interface with an arduino properly
if ! [[ -d /var/lock -o \
$(ls -l /var/lock | awk -v col=4 '{print $col}') = "uucp" -o \
$(stat -c "%a %b" /var/lock) = "775" -o \
-z $(dscl . -read /Groups/mygroup GroupMembership | grep -o $USER) ]]; then
	/usr/bin/osascript -e 'do shell script "./install.sh $USER" with administrator privileges'
fi

#need to insert "open" command statement here once we know name of executable


exit 0;
	
