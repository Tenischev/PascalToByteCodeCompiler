Pascal to Java bytecode converter
=

Project allows converting program file written on Pascal into Java bytecode. 
Using the self-written [ANTLR grammar](src/main/antlr/Pascal.g4) Pascal file is translating and converting to bytecode with ASM library.

---
Grammar files:
* [SimplePascal](src/main/antlr/SimplePascal.g4) (without support of procedures, only longint type supported)
* [Pascal](src/main/antlr/Pascal.g4)

---
Test examples:
* Simple test of loops and arithmetic operations [TestForPascal](src/test/resources/TestForPascal.fpc)
* Calculation of 10000th simple number - [SimpleNumber](src/test/resources/SimpleNumber.fpc)

