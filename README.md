# FastAccount

CLI para la creación rápida de cuentas.
No crea la cuenta en sí, sino que guarda el nombre, email vinculado, usuario, contraseña y etiquetas.

## Atención

La contraseña no está cifrada y los datos de las cuentas se guardan en un .txt, por lo que (actualmente) no es seguro.
Úsalo bajo tu propio riesgo.

## Instalación

Dentro del repositorio se incluye un archivo *fast-account.jar*. Este archivo es una versión compilada y ya lista para usar.

Para usar el CLI directamente:

```console
    java -jar fast-account.jar <parámetros>
```

## Primera ejecución

Al ejecutar por primera vez se creará un archivo *accounts.data* donde se guardarán las cuentas.
El programa interpreta los comandos y el contenido del archivo.

## Contenido del archivo

Cada línea representa una cuenta.
Los datos están dividos por una barra vertical (|) en el siguiente orden:

- Nombre de la cuenta                             (1 o más palabras)

- Email asociado a la cuenta                      (1 dirección de correo electrónico) (opcional)
- Usuario de la cuenta                            (1 o más letras) (opcional)
- Contraseña de la cuenta                         (1 o más letras) (opcional)
- Etiquetas separadas por comas y espacios (, )   (0 o más etiquetas)(opcional)

## Comandos

1. add      : Nueva cuenta
2. remove   : Eliminar una cuenta
3. list     : Listado de cuentas
4. edit     : Editar una cuenta
5. search   : Buscar una cuenta
6. help     : Mostrar la ayuda
