.PHONY: test

ANTLR_LIB=../lib/antlr-4.7-complete.jar

all:
	java -cp $(ANTLR_LIB) org.antlr.v4.Tool -package gram ./gram/tfg.g4 -visitor 
build:
	javac -cp $(ANTLR_LIB) gram/*.java semantica/*.java gui/*.java Main.java -d ../bin/ -Xlint:deprecation
test:
	java -cp $(ANTLR_LIB):../bin/ Main ../test/test
merge:
	java -cp $(ANTLR_LIB):../bin/ Main ../test/merge
clean:
	find ../bin/ ./gram/ -type f \( -name \*.class -o -name \*.java -o -name \*.tokens \) -delete
