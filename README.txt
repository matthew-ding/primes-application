OVERVIEW
----------------------------------
Development Framework:
Eclipse IDE for Java Developers
Version: 2019-09 R (4.13.0)
Build id: 20190917-1200

Platform:
macOS 10.14.5

Language:
Java Version 8 Update 221 (build 1.8.0_221-b11)


WHERE TO FIND SOLUTIONS
-----------------------------------
Math:
All solutions can be found in the pdf file called: Math-Solutions

CS:
1) Solutions for Problems 1-5,8,9 can be found in the pdf file called: CS-Solutions. Also included are additional comments and descriptions for programs created for Problems 6,7,10,11, and 12.

2) Files relating to Problem 6 and 7 are under their own Java project, and can be found under the directory called "Problem6-7". Within this should be the "src/Problem6_7" directory, which should contain the following files:
   
   a. problem6.java (Problem 6)
   b. problem7.java (Problem 7, uses Problem 6 CYK method)
   c. Grammar.java (Problem 6,7)
   
Additionally, following input/output files should be found under "Problem6-7" project directory.

   a. problem6.in - problem 6 main input file
   b. testCase6-2.txt - problem 6 2nd input file
   c. testCase6-3.txt - problem 6 3rd input file
   
   d. problem7.in - problem 7 main input file
   e. problem7.out - problem 7 main output file
   f. testCase7-2.txt - problem 7 2nd input file
   g. testCase7-3.txt - problem 7 3rd input file
   
3) Files relating to Problem 10, 11, and 12 are under another Java project, which can be found under the directory called "Problem10-12". Within this should be the "src/Problem10_11_12" directory, which should contain the following files:
   
   a. problem10.java (Problem 10)
   b. problem11.java (Problem 11)
   c. problem12.java (Problem 12)
   d. PGrammar.java (Problem 10,11,12)
   e. Derivation.java (Problem 10,11,12)
   f. DerivationList.java (Problem 11,12)
   g. BackPointer.java (Problem 11,12)
   
Additionally, following input/output files should be found under "Problem10-12"

   a. problem10.in - problem 10 main input file
   b. problem10.out - problem 10 main output file
   c. testCase10-2.txt - problem 10 2nd input file
   d. testCase10-3.txt - problem 10 3rd input file
   
   e. problem11.in - problem 11 main input file
   f. testCase11-2.txt - problem 11 2nd input file
   
   g. problem12.in - problem 12 main input file (1000 strings)
   h. problem12Grammar.in - problem 12 Grammar input file (CFG input)
   i. problem12.out - problem 12 main output file
   j. testCase12-2.txt - problem 12 2nd input file (output of problem10.java with testCase10-2)
   k. testCase12-3.txt - problem 12 3rd input file (output of problem10.java with testCase10-3)
   
   
HOW TO RUN PROGRAMS
---------------------------------
 NOTE: All programs automatically take input from appropriately labeled .in file (for example, problem6.in is the input file for problem6). If you would like to test the input files with a .txt extension, you need to copy/paste its contents into the respective .in file for the problem.
 
 Within the ding-CS-solution folder, there are two separate project files: Problem6-7 and Problem10-12. These are two separate projects, and should be imported separately.
 
1) Problems 6 and 7

Import the "Problem6-7" directory as a standalone project, and build and compile a Java program.

   a. Problem 6
      -Run the main method within "problem6.java"
      -The CFG is inputted from "problem6.in"
      -A string is inputted from the console
      -The program returns "Yes" or "No" to the console depending on whether the string is included in the language
      
   b. Problem 7
      -Run the main method within "problem7.java"
      -The CFG is inputted from "problem7.in"
      -An integer "n" is inputted from the console
      -The program returns the first "n" strings in the language (shortlex order) to "problem7.out", with a possible console output (see program comments)
      
2) Problem 10,11,12

Import the "Problem10-12" directory as a standalone project, and build and compile a Java program.

   a. Problem 10
      -Run the main method within "problem10.java"
      -The PCFG is inputted from "problem10.in"
      -An integer "n" is inputted from the console
      -The program returns "n" randomly generated strings from the PCFG to "problem10.out"
      
   b. Problem 11
      -Run the main method within "problem11.java"
      -The PCFG is inputted from "problem11.in"
      -A string is inputted from the console
      -The program returns all parse trees of the string and probabilities to the console
      
   c. Problem 12
      -Run the main method within "problem12.java"
      -The set of 1000 strings is inputted from problem12.in
      -The PCFG (without probabilities) is inputted from problem12Grammar.in
      -The program returns the expected probabilities for each rule to "problem12.out"