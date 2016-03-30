# maics2016

This code is the basis of my 2016 MAICS paper 
"Real-time Unsupervised Clustering".

All of the code is in Java 1.8.  I've included my complete Eclipse project.

It is part of a larger code base; hence all the weird package names.  
I isolated the elements that are pertinent to the paper in this version.  
I don't plan to update the code, except to fix small bugs; I want it to be 
an accurate archive of what was described in my paper.

Except for edu.hendrix.ev3.YUYVImage (GPL v3), all the code is in
the public domain.

The main BSOC algorithm described in the paper is 
edu.hendrix.ev3.ai.bsoc.BoundedSelfOrgCluster.java

My SOM implementation is 
edu.hendrix.ev3.ai.som.SOM.java

The image files are in ev3Files/ev3Video.
The sonar file is sonar.log.

For each figure in the paper, I give the command-line arguments to 
replicate the experiment.  All the executables are in 
edu.hendrix.ev3.ai.cluster.visualize

As it stands, the BoundedSelfOrgCluster algorithm is controlled by a
static variable.  By default, it does the weighted version.  You'll have 
to modify BoundedSelfOrgCluster.java (flip BASIC_VERSION_MAICS to true)
to get the basic version described in the paper.


Figure 6
SOMSonarAssessor 16 4 sonar.log
SOMSonarAssessor 32 4 sonar.log
SOMSonarAssessor 64 8 sonar.log
BSOCSonarAssessor 16 sonar.log
BSOCSonarAssessor 32 sonar.log
BSOCSonarAssessor 64 sonar.log

Figure 8
SOMVideoAssessor 16 4 1
SOMVideoAssessor 32 4 1
SOMVideoAssessor 64 8 1
BSOCVideoAssessor 16 1
BSOCVideoAssessor 32 1
BSOCVideoAssessor 64 1

Figure 10
SOMVideoAssessor 16 4 2
SOMVideoAssessor 32 4 2
SOMVideoAssessor 64 8 2
BSOCVideoAssessor 16 2
BSOCVideoAssessor 32 2
BSOCVideoAssessor 64 2

Figure 12
SOMSonarAssessor 64 8 sonar.log -shuffle
BSOCSonarAssessor 64 sonar.log -shuffle

Figure 13
SOMVideoAssessor 64 8 1 -shuffle
BSOCVideoAssessor 64 1 -shuffle

Figure 14
SOMVideoAssessor 64 8 2 -shuffle
BSOCVideoAssessor 64 2 -shuffle
