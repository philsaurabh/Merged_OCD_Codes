# ocd_module_deployment
Repository for OCD module under IITP in collaboration with MHA and BPRD

# Video Obscenity Analysis
For Less than 2 minute video - 20 Frames per minute detection <br/>
For Less than 10 minute video - 15 Frames per minute detection<br/>
For Less than 30 minute video - 4 Frames per minute detection<br/>
For Less than 60 minute video - 2 Frames per minute detection<br/>
For More than 60 minute video - 1 Frames per minute detection<br/>

# Specification
Testing Mobile Android Version - 8.1.1 / 10.0<br/>
Mobile minimum required RAM - 2 GB<br/>
Tested Mobile RAM - 3 GB<br/>

# Required Permissions
Read and Write External Storage<br/>
Read Phone State to fetch IMEI<br/>

# Single Image Scanning
Testing Image Dimension - 972 * 3978 pixels<br/>
Testing Image Size - 1221 kb<br/>
Testing image time Taken - 321 milliseconds<br/>

# Single Video Scanning
Testing Video Duration - 1 min 18 seconds<br/>
Testing Video Size - 14.67 MB<br/>
Testing FPM - 20<br/>
Total Obscene Frames detected - 3<br/>
Highest Obscene score - 0.835<br/>

Time taken to fetch Video from gallery - 247 millisec<br/>
Time taken by model to analyse video - 59923 millisec<br/>

# Folder Image Scannning
Total number of images in testing device - 9311<br/>
Total number of folders - 31<br/>
Time taken to fetch Gallery Images Folder - 552 millisec<br/>

Total images in testing folder - 14<br/>
Obscene Images detected - 11<br/>
Time taken to fetch all images - 205 millisec<br/>
Time taken to analyse all images - 1403 millisec<br/>

# Folder Video Checking
Total number of videos in testing device - 202<br/>
Total number of videos folder - 20<br/>
Time taken to fetch gallery Video folders - 58500<br/>

Total videos in testing folder - 4<br/>
Total Obscene videos in testing folder - 3<br/>
Duration of videos - 8min 37sec, 1min 18sec, 6min 15sec, 5min 44sec<br/>
Time taken to check all videos - 102436 millisec <br/>

# Folder Video Deep Scanning<br/>
Total number of videos in testing device - 202<br/>
Total number of videos folder - 20<br/>
Time taken to fetch gallery Video folders - 58500<br/>

Total videos in testing folder - 3<br/>
Total Obscene videos in testing folder - 3<br/>
Duration of videos - 8min 37sec, 1min 18sec, 6min 15sec<br/>
Time taken to deep scan all videos - 185226 millisec<br/>
