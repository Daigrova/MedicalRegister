GSON = -classpath src/Librerias/gson-2.8.5.jar

classes:
	javac $(GSON) -sourcepath src src/Trabajadores.java
	javac $(GSON) -sourcepath src src/Paramedico.java
	javac $(GSON) -sourcepath src src/Enfermero.java
	javac $(GSON) -sourcepath src src/Doctor.java
	javac $(GSON) -sourcepath src src/Paciente.java
	javac $(GSON) -sourcepath src src/Requerimiento.java
	javac $(GSON) -sourcepath src src/Pacientes.java
	javac $(GSON) -sourcepath src src/Requerimientos.java
	javac $(GSON) -sourcepath src src/IP.java
	javac $(GSON) -sourcepath src src/Servidor.java
	javac $(GSON) -sourcepath src src/Cliente.java
	javac $(GSON) -sourcepath src src/WaitingRequest.java
	javac $(GSON) -sourcepath src src/Main.java
	

run:
	java $(GSON):src Main

clean:
	rm -rf src/*.class Operaciones.log