# Algorithms Part 2 (By Princeton University) Assignments
A repository containing the source code for the assignments in Algorithm Part 1 on Coursera.org.  
Link of the course: https://www.coursera.org/learn/algorithms-part2  
This repository contains 5 assignments:
  - WordNet (Graph Processing)
  - SeamCarving (Graph Processing)
  - Baseball Elimination (Maxflow problem)
  - Boggle (Tries)
  - Burrows-Wheeler (Data compression)
 ## Detail of each assignment
 ### 1. WordNet (Directed Graph Processing)
 This is the definition of WordNet given from the specification:  
 WordNet is a semantic lexicon for the English language that computational linguists and cognitive scientists use extensively. For example, 
 WordNet was a key component in IBM’s Jeopardy-playing Watson computer system. WordNet groups words into sets of synonyms called synsets. 
 For example, { AND circuit, AND gate } is a synset that represent a logical gate that fires only when all of its inputs fire. WordNet also 
 describes semantic relationships between synsets. One such relationship is the is-a relationship, which connects a hyponym (more specific 
 synset) to a hypernym (more general synset). For example, the synset { gate, logic gate } is a hypernym of { AND circuit, AND gate } 
 because an AND gate is a kind of logic gate.  
 Your task is to build the WordNet class that supports relevent operations using directed graph (Digraph) processing algorithms.  
 Link to the assignment specification: https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php
 ### 2. SeamCaring (Directed Graph Processing)
 This is the definition of Seam Carving from the specification:  
 Seam-carving is a content-aware image resizing technique where the image is reduced in size by one pixel of height (or width) at a time. A 
 vertical seam in an image is a path of pixels connected from the top to the bottom with one pixel in each row; a horizontal seam is a path 
 of pixels connected from the left to the right with one pixel in each column. Unlike standard content-agnostic resizing 
 techniques (such as cropping and scaling), seam carving preserves the most interest features (aspect ratio, set of objects present, etc.) 
 of the image.  
 Demo video for seam carving: https://www.youtube.com/watch?v=6NcIJXTlugc  
 Your task is to build the SeamCarver class that can import an image (2D array pixels), process it using Digraph processing algorithms 
 and export another image that is smaller than the original image but still keep the important information.  
 Link to the assignment specification: https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php
 ### 3. Baseball Elimination (Maxflow problem)
 This is the definition of the baseball elimination problem grom Wikipedia: https://en.wikipedia.org/wiki/Maximum_flow_problem#Baseball_elimination  
 You task is to build a class that can import a dataset containing the information of many baseball teams (number of win, loss, 
 remaining games) and tell which team can be mathematically eliminated (teams that don't have any chance of winning the championship).  
 Link to the assignment specification: https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php
 ### 4. Boggle (Tries)
 Your task is to build the program that can find all the valid words from a given Boggle game (Boggle is a wordgame distributed by Hasbro).  
 Link to the assignment specification: https://coursera.cs.princeton.edu/algs4/assignments/boggle/specification.php
 ### 5. Burrows-Wheeler (Data compression)
 Your task is to implement the Burrows-Wheeler algorithm for data compression. The Burrows-Wheeler algorithm contains 3 steps:  
    - Burrows-Wheeler transforming  
    - Move-to-front encoding  
    - Huffman encoding  
 You have to implement the first 2 steps from the algorithm.  
 Link to the assignment specification: https://coursera.cs.princeton.edu/algs4/assignments/burrows/specification.php
 ## How to run the solution
 ### 1. Download the third-party Java library provided by Princeton University
 The solutions in the assignments all use the library provided in the course so you need to download it first in order to run and test 
 the solutions.  
 You can download each class needed for each solution or you can simply download the whole solution from the course's website: 
 https://algs4.cs.princeton.edu/code/  
 ### 2. Download the project folder from the assignment specification webpage
 The project folder containing all the test data and the supporting classes (that not provided in the library above) can be downloaded in the specification webpage for each assignment.
