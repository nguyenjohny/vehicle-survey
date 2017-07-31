Vehicle Survey Technical Challenge - Johny Nguyen.

This challenge was chosen for the following reasons:
 * my eagerness to show case my level of competencies as I felt this problem was more of 
a challenge.
 * the problem space was interesting as I had to model real world activites to make can be 
represented in datasets.
 * it required an understanding of a engineering solution to a real world problem.

My approach was to break down the responsiblities of the application into two core fundamental 
cores. The first was to DECODE the input data and store this data into a usable form 
(PneumaticImpact) that stays true to its orginal form and factor. I aimed to separate this 
using the Parser<T> interface to ensure the out format was not locked in.

The second fundamental component was to provide query interface to process these samples. This was
aggregated into the VehicleSurveyor Class which takes in interfaces for different strategies on
fetching datasets. This aids with mocking of test data using Mockito.


I did run out of time with this issue and I did call a end to the work some much needed improvements
to be made:
 * I really wanted to add a Query function to the surveyor which returns a flat list of samples within a specified range. This will make the system much more powerful.
 * the display of information

Note: I do appologise if i made it awkward and you do not have access to maven, but i used TestNg 
and Mockito for mockito for tests. You can still build and run the source with maven but you wont be
to run the unit tests.


Intsructions manual build:
===============================

> javac -d bin @sources.txt

You can then run the application by 
> cd bin
> java aconex.DisplayQuery ../data.txt


Intsructions for a Maven build:
=================================
This will build the project plus pull in testing framework dependencies & run unit tests. 
> mvn install

You can run using the main runner using
> mvn exec:java -Dexec.mainClass="aconex.DisplayQuery" -Dexec.args="data.txt"





