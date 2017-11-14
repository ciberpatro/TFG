.PHONY: test

ANTLR_LIB=../lib/antlr-4.7-complete.jar
ANTLR_SRC_DIR=./domain/antlr/gram/

all:
	java -cp $(ANTLR_LIB) org.antlr.v4.Tool -package domain.antlr.gram.tfg  -visitor $(ANTLR_SRC_DIR)tfg/tfg.g4
	java -cp $(ANTLR_LIB) org.antlr.v4.Tool -package domain.antlr.gram.conf -visitor $(ANTLR_SRC_DIR)conf/conf.g4