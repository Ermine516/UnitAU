The first commandline argument is the file. The remaining arguments must 
be put in the following order:

   linear debug complete clean

linear - runs the linear algorithm only
debug - displays rules applied
complete - displays rules applied and resulting configurations
clean -  removes unecessary non-terminals from final binding list. 

Note that complete can only come after debug.

Valid Examples:

Example1.txt linear debug clean
Example1.txt linear  clean
Example1.txt linear  debug complete
Example1.txt debug complete
Example1.txt debug complete clean
Example1.txt debug clean

 Invalid Examples:

test.txt debug clean linear
test.txt complete clean 

