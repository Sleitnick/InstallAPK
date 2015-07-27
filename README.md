# InstallAPK
APK installer to make testing a bit faster.

The original use for this application was to speed up development testing for the Corona SDK. Before, I had to run the command from the command line to install the APK. While not difficult, I figured having an application that remembers your last APK file would speed things up a bit. Thus, I created this application. Now it's just a matter of clicking "Install APK" rather than having to run code on the command line.

Allows you to select an APK file in your file directory and then install it using the command: <code>adb install -r FILE</code>
where <code>FILE</code> is the selected file.
