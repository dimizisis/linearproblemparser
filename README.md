# linearproblemparser
Parser for linear problems

Parser written in Java.

Converts a linear problem of type:

min (or max) c1x1 + c2x2 + ... + cnxn

-----------------------------------------------------
a11x1 + a12x2 + ... + a1nxn <= (or = or =>) b1
.
.
.
am1x1 + am2x2 + ... + amnxn <= (or = or =>) bm
-----------------------------------------------------

to

-----------------------------------------------------
min (max) c^T x
s.t. Ax <= (or = or =>) b
-----------------------------------------------------

Coding in output: 

1) -1 instead of <, 0 instead of =, 1 instead of >
2) -1 instead of min, 1 instead of max

Guide:

1. Your linear problem has to be a .txt file.
2. Browse your input file.
3. Select your output folder.
4. Convert the problem.
5. You can open directly the converted problem (converted_lp.txt) through a message box (Y/N).


![alt text](https://i.imgur.com/cmkxqrD.png)
