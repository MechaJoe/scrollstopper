Scrollstopper is a simple app where the user to sets a time to remind themselves to stop "doomscrolling."

Scrollstopper fires a notification and turns the screen to minimum brightness to get the user to stop 
scrolling Instagram, Facebook, etc. 

Upon clicking the notification, Scrollstopper either congratulates the user or berates them for lying 
if the orientation of the phone is face down (i.e. if the user is still laying in bed scrolling and 
just clicking the notification to get rid of it).

IMPORTANT NOTE: The app must be granted the permission to write system settings for dimming the screen,
                which can be done in the "Config" fragment.

Here is the technical breakdown of my project, which differs slightly from my initial proposal:

- Use of SharedPreferences
- Use of Three or More Activities
- Use of Android user-granted permissions (write system settings)
- Use of ConstraintLayout
- Use of HTTP API (Dictum, the quotes API)
- Use of hardware feature (phone orientation/sensors)
- Use of fragments

Joe Konno
