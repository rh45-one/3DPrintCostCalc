APP_NAME = 3DPrintCostCalc
MAIN_CLASS = main.Application
SRC_DIR = src
BIN_DIR = bin
DOCS_DIR = docs
JAR_FILE = $(APP_NAME).jar

.PHONY: all compile doc jar clean

all: compile doc jar

compile:
	javac -d $(BIN_DIR) $(shell find $(SRC_DIR) -name '*.java')

doc:
	javadoc -d $(DOCS_DIR) $(shell find $(SRC_DIR) -name '*.java')

jar:
	jar cfe $(JAR_FILE) $(MAIN_CLASS) -C $(BIN_DIR) .

clean:
	rm -rf $(BIN_DIR)/* $(DOCS_DIR)/*