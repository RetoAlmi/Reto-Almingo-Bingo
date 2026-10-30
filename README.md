# Estructura
RetoAlmingoBingo/
 └─ src/
     ├─ 📦 common/
     │   ├─ IBingoServicio.java             (Interfaz RMI - Interface)
     │   ├─ Carton.java                     (Objeto del cartón - Class)
     │   └─ PreguntaSostenibilidad.java     (Objeto de las preguntas - Class)
     │
     ├─ 📦 server/
     │   ├─ ServidorBingoImpl.java          (Lógica del servidor y bombo - Class)
     │   └─ ServidorMain.java               (Arranca el registro RMI - Class)
     │
     └─ 📦 client/
         └─ ClienteMain.java                (Programa para jugar por la terminal - Class)

# Hacer
### 📦 Paquete: `common`

Aquí ponemos lo que necesitan conocer tanto el servidor como los clientes.

#### 1. `IBingoServicio.java` (Interfaz Remota)

Esta interfaz define qué acciones puede hacer el cliente en el servidor a través de RMI. Debe extender `java.rmi.Remote` y cada método debe lanzar `java.rmi.RemoteException`.

* **Qué programar en ella:**
* Un método para registrarse: `int registrarJugador(String nombre) throws RemoteException;`
* Un método para pedir un cartón: `Carton obtenerCarton(int idJugador) throws RemoteException;`
* Un método para cantar línea: `boolean cantarLinea(int idJugador) throws RemoteException;`
* Un método para cantar bingo: `boolean cantarBingo(int idJugador) throws RemoteException;`
* Un método para enviar la respuesta a una pregunta de sostenibilidad: `boolean responderPregunta(int idJugador, int idPregunta, String respuesta) throws RemoteException;`



#### 2. `Carton.java` (Modelo de datos)

Representa el cartón del bingo de un jugador. Debe implementar `java.io.Serializable`.

* **Qué programar en ella:**
* Una estructura de datos (por ejemplo, una matriz bidimensional `int[][]` o una lista) con los números del cartón.
* Métodos para marcar un número si sale del bombo.
* Métodos para comprobar si se ha completado una línea o el cartón entero (bingo).



#### 3. `PreguntaSostenibilidad.java` (Modelo de datos)

Almacena la información de las preguntas de sostenibilidad e informática exigidas en el reto. También debe implementar `Serializable`.

* **Qué programar en ella:**
* Atributos: `int id`, `String enunciado`, `String[] opciones` (o la respuesta correcta) y un booleano o indicador de si está enfocada a sostenibilidad IT.
* Constructores, getters y setters.



---

### 📦 Paquete: `server`

Aquí va la inteligencia del juego y el lanzamiento del servidor.

#### 4. `ServidorBingoImpl.java` (La Lógica del Servidor)

Debe extender `UnicastRemoteObject` e implementar la interfaz `IBingoServicio`.

* **Qué programar en ella:**
* Una lista o mapa de jugadores conectados y sus cartones asignados.
* El "bombo": una lista con los números del 1 al 90 (o los que decidas) que van saliendo de forma aleatoria sin repetirse.
* Un banco de preguntas de sostenibilidad (puedes inicializar una lista fija en el código con al menos 20 preguntas sobre sostenibilidad y tecnología, tal como pide la rúbrica).


* La lógica para validar si el jugador que canta línea o bingo tiene su cartón realmente completo, seleccionarle una pregunta aleatoria no repetida en la partida, y comprobar su respuesta.





#### 5. `ServidorMain.java` (Lanzador del Servidor)

* **Qué programar en ella:**
* El método `main`.
* Crea el registro RMI en el puerto por defecto (`LocateRegistry.createRegistry(1099)`).
* Instancia `ServidorBingoImpl` y lo publica usando `Naming.rebind("rmi://localhost/BingoServicio", servidor)`.
* Muestra un mensaje por consola indicando que el servidor de Bingo está activo y esperando jugadores.



---

### 📦 Paquete: `client`

La interfaz de usuario por terminal en Eclipse.

#### 6. `ClienteMain.java` (El Jugador en Terminal)

* **Qué programar en ella:**
* El método `main`.
* Se conecta al servidor mediante `Naming.lookup("rmi://localhost/BingoServicio")`.
* Pide al usuario por consola (`Scanner`) su nombre para registrarse.
* Solicita su cartón y se lo muestra formateado por la terminal.
* Muestra un menú interactivo en bucle para que el usuario pueda:
1. Ver el estado de su cartón o el último número cantado.
2. Cantar Línea.
3. Cantar Bingo.


* Si el servidor le devuelve una pregunta de sostenibilidad al cantar línea/bingo, la imprime en la terminal, lee la respuesta del usuario y se la manda al servidor para validarla definitivamente.