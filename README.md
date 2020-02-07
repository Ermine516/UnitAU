To compile run:	javac -d bin src/*.java

To run on the first example:  java -cp bin/ Main Examples/Example1.txt

Can be run with addition arguments such as: 

	java -cp bin/ Main Examples/Example1.txt linear 
	
	java -cp bin/ Main Examples/Example1.txt debug
	
	java -cp bin/ Main Examples/Example1.txt clean

The arguments must be put in the following order:   linear debug complete clean

For example: java -cp bin/ Main Examples/Example1.txt linear debug complete clean

linear - runs the linear algorithm only
debug - displays rules applied
complete - displays rules applied and resulting configurations
clean -  removes unecessary non-terminals from final binding list. 

Note that complete can only come after debug.

Valid Examples:

Examples/Example1.txt linear debug clean
Examples/Example1.txt linear  clean
Examples/Example1.txt linear  debug complete
Examples/Example1.txt debug complete
Examples/Example1.txt debug complete clean
Examples/Example1.txt debug clean

 Invalid Examples:

Examples/Example1.txt debug clean linear
Examples/Example1.txt complete clean 

