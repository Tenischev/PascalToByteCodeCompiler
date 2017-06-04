Contents:  
|-src - source code for Pascal to Byte Code compiler.  
|--\SimplePascal.g4 - simple grammar cut for execute SimpleNumber.fpc  
|--\Pascal.g4 - more extended pascal grammar for future use.  
|--\Prover - calculate 10000th simple number in java code, runs SimpleNumber.fpc and check time, runs SimpleNumber.fpc  recompiled into byte code and check time  
|--\PascalToByteCode - implement antlr's SimplePascalListener and use asm for create byte code.  
|--\Main - for execute Pascal to Byte Code compiler.  
|-lib - folder with asm and antlr.  
|-SimpleNumber.fpc - program for calculate 10000th simple number.  
|-Simple.class - recompiled into byte code the SimpleNumber.fpc  
