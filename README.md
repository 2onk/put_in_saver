# put_in_saver
Energy saver for UR Eseries

testet with SDK 1.12+ 
used 3rdparty lib: easymodbus, can be found in the modbuslib folder. 

This URCap will help you to save energy automatically. 
It powers off the robotarm after 8 minutes when the robot hasnt been moved or running any programm.

You might also use the option to shutdown the robot automatically after 1.5h.

There is not much to configure:

- Go to the installtion tab
- turn on the saver and it will power off only the arm
- the robot shutdown is only an option

New: the actual cursor position is tracked and will prevent from powering off the robot arm while programing in polyscope

![image](https://user-images.githubusercontent.com/122785824/212656998-d98067ee-5481-4af9-9b7b-5d00352566ba.png)
