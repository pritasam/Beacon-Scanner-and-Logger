Beacon Scanner & Logger
=======================

This Android app detects BLE Beacons and logs data to a file for some unspecified future use.

The app scans for Beacons and writes the details of any Beacons discovered to a file on the device. The app features functionality to select which properies should be logged and to email files from the device to an email address of the user's choice.

Scanning can be enabled and disabled via a button and the file produced will overwrite any previous versions of the same file.

Currently uses the free iBeacon SDK from Radius Networks and portions of code adapted from David G Young's IBeacon Reference Application. Will be updated to use the open source AltBeacons based library. 
