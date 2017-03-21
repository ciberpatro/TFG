.PHONY: test

all:
	java -cp ../lib/antlr-4.6-complete.jar org.antlr.v4.Tool -package gram ./gram/tfg.g4 -visitor 
build:
	javac -cp ../lib/antlr-4.6-complete.jar gram/*.java semantica/*.java Main.java -d ../bin/ -Xlint:deprecation
test:
	java -cp ../lib/antlr-4.6-complete.jar:../bin/ Main ../test/asdf
clean:
	rm ../bin/*.class ./gram/*.java ./gram/*.tokens
