# TraductorVoz

Aplicación de ejemplo que utiliza Azure Speech Service para traducir audio en vivo.

## Uso

1. Obtén una clave de Azure Speech y asigna la variable de entorno `AZURE_SPEECH_KEY`.
2. Opcionalmente define `AZURE_SPEECH_REGION` (por defecto `westeurope`).
3. Ejecuta la aplicación con Maven:

```bash
mvn compile exec:java -Dexec.mainClass=traductor.Main
```

La interfaz muestra subtítulos en inglés del audio reconocido en español.
