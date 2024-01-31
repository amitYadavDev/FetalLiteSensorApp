# Task
A sample looks like this. 
!""01487A0245FE004EB9022AC6

The sample structure is as follows:

!<Sample NumberMSB><Sample NumberLSB><Channel 1 Data><Channel 2 Data><Channel 3 Data><Channel 4 Data>

Each channel data is a 6-byte hex-encoded voltage value. So convert from hex to double. 

There are multiple samples in a single line. Every line has a timestamp at the start. 

The challenge is to read each sample from the input file in a background service. In a separate executor/ async service - run 4 threads to decode each channel and display every 100th value on the UI. The sampling frequency is 1000 samples per second. So every 100th value should be displayed every 100 m/s. 

Stream from the file accounting for read time, and processing time. 

At the same time run the video in loop from the UI.

So the final output when the app loads should be that a video is running with 4 text views below. The text views should get updated with the channel values for every 100th sample. (However, it is expected that all samples are processed). 

# Working App (Video)
https://github.com/amitYadavDev/FetalLiteSensorApp/assets/45551012/dd1c1822-937d-4722-9c63-11423c65408c

