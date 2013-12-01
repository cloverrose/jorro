wget -nc -P lib http://antlr4.org/download/antlr-4.0-complete.jar
wget -nc -P lib http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.2.3/jackson-core-2.2.3.jar
wget -nc -P lib http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.2.3/jackson-databind-2.2.3.jar
wget -nc -P lib http://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.2.3/jackson-annotations-2.2.3.jar
java -classpath .:lib/antlr-4.0-complete.jar -jar lib/antlr-4.0-complete.jar parse/TS.g4
javac -classpath .:lib/antlr-4.0-complete.jar:lib/jackson-core-2.2.3.jar:lib/jackson-databind-2.2.3.jar:lib/jackson-annotations-2.2.3.jar parse/*.java main/*.java
