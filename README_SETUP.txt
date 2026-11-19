+----------------------------------------------------------------------------------------------------------------------------------------------+
██╗   ██╗██████╗  ██╗   ██╗ ███╗   ███╗
██║   ██║██╔══██╗ ██║   ██║ ████╗ ████║
██║   ██║██████╔╝ ██║   ██║ ██╔████╔██║    - Guia de instalación del proyecto 
██║   ██║██╔═══╝  ╚██╗ ██╔╝ ██║╚██╔╝██║
╚██████╔╝██║       ╚████╔╝  ██║ ╚═╝ ██║
 ╚═════╝ ╚═╝        ╚═══╝   ╚═╝     ╚═╝
                                     
Proyecto por:
Camargo Méndez José Joaquín
Valencia Hernández Ricardo
Meléndez Moreno Ximena Zoe
Santiago Gonzaléz Luis Manuel
Gálan Hernández Raúl

+----------------------------------------------------------------------------------------------------------------------------------------------+

Requisitos de Instalación

Para ejecutar correctamente el sistema en su equipo, es necesario contar con las siguientes herramientas y dependencias instaladas:

1. Entorno de Desarrollo (IDE):
   - IntelliJ IDEA (recomendado) o cualquier IDE compatible con proyectos Java / JavaFX.

2. Kit de Desarrollo de Java (JDK):
   - Java JDK 21 o superior (probado en JDK 24).

3. Framework de Interfaz Gráfica:
   - JavaFX SDK (versión compatible con el JDK instalado, p. ej., JavaFX 21 o 26).

4. Servidor de Base de Datos Local:
   - XAMPP (con el servicio de MySQL / MariaDB activo en el puerto 3306). Descarga: https://www.apachefriends.org/es/index.html
   - phpMyAdmin o cliente SQL para importar el respaldo de la base de datos (database/sistema_upvm.sql).

5. Conector JDBC:
   - MySQL Connector/J (mysql-connector-j-.jar) agregado a las librerías del proyecto. 
   +---Descarga: https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-26.10.0.zip

+----------------------------------------------------------------------------------------------------------------------------------------------+

Guía de Instalación y Ejecución — Control de Inventarios UPVM

Descargar o actualizar el proyecto desde GitHub

Si aún no tienen el repositorio en su PC:

git clone https://github.com/JoseJoaquin27/Control-de-Inventarios-UPVM-4280403.git

+----------------------------------------------------------------------------------------------------------------------------------------------+

Si ya tenían clonado el proyecto anteriormente:
git pull origin main

+----------------------------------------------------------------------------------------------------------------------------------------------+

Configurar la base de datos en XAMPP

1. Abran el panel de XAMPP e inicien los módulos de Apache y MySQL.

2. Entren a su navegador en http://localhost/phpmyadmin.


3. En el panel izquierdo, den clic en Nueva, escriban exactamente   sistema_upvm   como nombre de la base de datos y den clic en Crear.


4. Con la base "sistema_upvm" seleccionada, vayan a la pestaña Importar en el menú superior.


5. Den clic en Seleccionar archivo, busquen dentro de la carpeta del proyecto el archivo "database/sistema_upvm.sql" y hasta abajo den clic en Importar (o Continuar).


6. Verifiquen que aparezcan las 5 tablas: "historial", "material", "movimientoinventario", "préstamo" y "solicitud".


(Opcional)Si su MySQL en XAMPP tiene contraseña o usa un puerto distinto al "3306", ajústenlo en `src/database/DatabaseConnection.java`.


+----------------------------------------------------------------------------------------------------------------------------------------------+

Configurar el proyecto en IntelliJ IDEA

1. Abran IntelliJ IDEA y seleccionen Open (o File → Open) apuntando a la carpeta del proyecto descargado.


2. Vayan a File → Project Structure con Ctrl + Alt + Shift + S
|
|
|--> Project → SDK: Seleccionen su JDK instalado (Java 21 o 24).
|
|
|-->Libraries: Agreguen (con el botón + → Java) la carpeta lib de su JavaFX SDK local y el archivo ".jar" de "mysql-connector-j".


+----------------------------------------------------------------------------------------------------------------------------------------------+

Configurar VM Options para ejecutar JavaFX:
En la barra superior junto al botón de Play (▶), den clic en el menú de configuración → "Edit Configurations..."
Seleccionen la clase principal "view.Main" (o "Main").


Den clic en Modify options → Add VM options y peguen la siguiente línea (cambiando la ruta por la carpeta "lib" de su JavaFX SDK):


--module-path "C:\ruta\a\su\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml


Den clic en Apply y OK.

+----------------------------------------------------------------------------------------------------------------------------------------------+

Ejecutar el sistema

1. Asegúrense de que MySQL en XAMPP siga encendido.
2. Den clic en el botón Run (▶) en IntelliJ IDEA. El sistema abrirá directamente en la pantalla de Inicio y cargará el inventario, préstamos, trámites e historial desde "sistema_upvm".
+----------------------------------------------------------------------------------------------------------------------------------------------+
