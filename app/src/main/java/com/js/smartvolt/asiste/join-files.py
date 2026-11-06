import os

def consolidar_archivos(config_extensiones, archivo_salida="myfiles.txt"):
    # Limpiamos la entrada: quitamos espacios y asteriscos para comparar mejor
    # Ejemplo: "*.java; .py; config.xml" -> [".java", ".py", "config.xml"]
    patrones = [p.strip().replace("*", "") for p in config_extensiones.split(";")]
    
    # Abrimos el archivo de salida en modo escritura
    with open(archivo_salida, 'w', encoding='utf-8') as f_destino:
        
        # Recorrido recursivo desde el directorio actual (".")
        for raiz, carpetas, archivos in os.walk("../../../../../../../.."):
            for nombre_archivo in archivos:
                
                # Evitar que el programa se lea a sí mismo si ya existe el de salida
                if nombre_archivo == archivo_salida:
                    continue
                
                # Verificamos si el archivo coincide con algún patrón
                coincide = False
                for patron in patrones:
                    if patron.startswith("."): # Es una extensión
                        if nombre_archivo.endswith(patron):
                            coincide = True
                            break
                    else: # Es un nombre directo o extensión sin punto
                        if nombre_archivo == patron or nombre_archivo.endswith(f".{patron}"):
                            coincide = True
                            break
                
                if coincide:
                    ruta_completa = os.path.join(raiz, nombre_archivo)
                    
                    try:
                        with open(ruta_completa, 'r', encoding='utf-8', errors='ignore') as f_origen:
                            # Escribir el membrete con el nombre del archivo
                            f_destino.write(f"/* {'='*60} */\n")
                            f_destino.write(f"/* ARCHIVO: {ruta_completa} */\n")
                            f_destino.write(f"/* {'='*60} */\n\n")
                            
                            # Escribir el contenido y un par de saltos de línea al final
                            f_destino.write(f_origen.read())
                            f_destino.write("\n\n")
                            
                        print(f"Procesado: {ruta_completa}")
                    except Exception as e:
                        print(f"Error al leer {ruta_completa}: {e}")

# --- CONFIGURACIÓN ---
# Aquí pones tus extensiones o nombres separados por ";"
extensiones_a_buscar = ".java; .properties; application.yml; .xml; .json; .md; .bat;.sql; .js; .css; .html; .kt"

# Ejecutar el programa
consolidar_archivos(extensiones_a_buscar)
print("\n¡Listo! Todo se ha guardado en 'myfiles.txt'")
