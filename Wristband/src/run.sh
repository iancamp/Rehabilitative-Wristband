#!/bin/bash
#

#check and see if the system has been configured to interface with an arduino properly
if ! [[ -d /var/lock || \
$(ls -l /var/lock | awk -v col=4 '{print $col}') = "uucp" || \
$(stat -f %Mp%Lp /var/lock) = "0775" || \
! -z $(dscl . -read /Groups/_uucp GroupMembership | grep -o $USER) ]]; then
	echo "Now calling installation script"
	#/usr/bin/osascript -e 'do shell script "./install.sh $USER" with administrator privileges'
fi

#need to insert "open" command statement here once we know name of executable


exit 0;
	
