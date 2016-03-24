The actions of the submitter are guided by one or more submission.defs files, placed in:

The lib folder of the BlueJ installation (<BLUEJ_HOME>/lib). Definitions found here will be used for all projects opened by BlueJ.
The user's BlueJ configuration directory (<USER_HOME>/.bluej on Solaris/Linux, <USER_HOME>\bluej on Windows, or ~/Library/Preferences/org.bluej on Mac OS X). Definitions found here will be used for all BlueJ projects opened by that user.
The BlueJ project folder. Definitions found here will be used only for that project, and will only be available while that project is open.

The contents of these files are combined (in the order given above) to form a list of "schemes", the submission actions available at any given time.