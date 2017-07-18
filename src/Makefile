.PHONY: test

ANTLR_LIB=../lib/antlr-4.7-complete.jar

all:
	java -cp $(ANTLR_LIB) org.antlr.v4.Tool -package gram.tfg -visitor ./gram/tfg/tfg.g4
	java -cp $(ANTLR_LIB) org.antlr.v4.Tool -package gram.conf -visitor ./gram/conf/conf.g4
build:
	javac -cp $(ANTLR_LIB) gram/tfg/*.java gram/conf/*.java semantic/tfg/*.java semantic/conf/*.java drivers/*.java domain/*.java gui/*.java Main.java -d ../bin/ -Xlint:deprecation
	jar cvf ../bin/AlgoVisu.jar ../bin/* ./resources/*
test:
	java -cp $(ANTLR_LIB):../bin/ Main
merge:
	java -cp $(ANTLR_LIB):../bin/ Main ../test/merge
clean:
	find ../bin/ ./gram/ -type f \( -name \*.class -o -name \*.java -o -name \*.tokens \) -delete
