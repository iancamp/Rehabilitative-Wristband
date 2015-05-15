



Before you can use an arduino with your computer, you must first make some modifications so it is detected and functions properly. For your convenience, we have included two scripts which will automatically make the necessary modifications:

check_install.sh
	This script checks whether the necessary modifications have been made. It will either tell you to run the installations script, or that everything is already installed.

install.sh
	This script performs the necessary modifications to your computer so it detects the arduino properly. You must supply the username for the user who will run the software. REQUIRES ADMIN PRIVILEGES!!!!

How to use these scripts:

The check_install.sh script can be run without admin privileges. To run, simply open terminal, browse to the folder the script is in, and run it (see below):
	
	Open terminal
	cd Desktop/PlayGym/Mac_OS_X/scripts 	#change directory to the folder containing the script
	./check_install.sh

The install.sh script performs the actual install. The script ONLY works on Mac OS X and requires administrator privileges. It must be run for every user who will use the program.
	
	Open terminal
	cd Desktop/PlayGym/Mac_OS_X/scripts	#change directory tot he folder containing the script
	sudo ./install.sh <user>	#run install.sh as administrator and supply the username of the user who will be using the program / arduino
 	./check_install.sh	#confirm that everything was installed correctly

If you prefer to manually make these modifications (or if you use a different operating system other than OS X), you can follow the directions on the arduino website found below
	http://playground.arduino.cc/interfacing/java
		If using Mac OS X, make sure to follow the directions under the "Mac OS X Locking" header

