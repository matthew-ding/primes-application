# primes
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
1) Solutions for Problems 1-5,8,9 can be found in the pdf file called: ... Also included are general comments and descriptions for programs created for Problems 6,7,10,11, and 12.

2) Files relating to Problem 6 and 7 can be found under the directory called "Problem 6-7". Within this should be the "src/Problem6_7" directory, which should contain the following files:
   
   a. problem6.java (Problem 6)
   b. problem7.java (Problem 7)
   c. Grammar.java (Problem 6,7)
   
Additionally, following files should be found under "Problem 6-7"

   a. problem6.in
   b. problem7.in
   c. problem7.out
   d. Additional files???
   
3) Files relating to Problem 10, 11, and 12 can be found under the directory called "Problem 10-12". Within this should be the "src/Problem10_11_12" directory, which should contain the following files:
   
   a. problem10.java (Problem 10)
   b. problem11.java (Problem 11)
   c. problem12.java (Problem 12)
   d. PGrammar.java (Problem 10,11,12)
   e. Derivation.java (Problem 10,11,12)
   f. DerivationList.java (Problem 11,12)
   g. BackPointer.java (Problem 11,12)
   
Additionally, following files should be found under "Problem 10-12"

   a. problem10.in
   b. problem10.out
   c. problem11.in
   d. problem12.in
   e. problem12Grammar.in
   f. problem12.out
   g. Additional files???
   
   
HOW TO RUN PROGRAMS
---------------------------------
1) Problems 6 and 7

Take the project files found under "Problem 6-7", and build and compile a Java program.

   a. Problem 6
      -Run the main method within "problem6.java"
      -The CFG is inputted from "problem6.in"
      -A string is inputted from the console
      -The program returns "Yes" or "No" to the console depending on whether the string is included in the language
      
   b. Problem 7
      -Run the main method within "problem7.java"
      -The CFG is inputted from "problem7.in"
      -An integer "n" is inputted from the console
      -The program returns the first "n" strings in the language (shortlex order) to "problem7.out"
      
2) Problem 10,11,12

Take the project files found under "Problem 10-12", and build and compile a Java program.

   a. Problem 10
      -Run the main method within "problem10.java"
      -The PCFG is inputted from "problem10.in"
      -An integer "n" is inputted from the console
      -The program returns "n" randomly generated strings from the PCFG to "problem10.out"
      
   b. Problem 11
      -Run the main method within "problem11.java"
      -The PCFG is inputted from "problem11.in"
      -A string is inputted from the console
      -The program returns all parse trees of the string to the console
      
   c. Problem 12
      -Run the main method within "problem12.java"
      -The set of 1000 strings is inputted from problem12.in
      -The PCFG (without probabilities) is inputted from problem12Grammar.in
      -The program returns the expected probabilties for each rule to "problem12.out"


      
