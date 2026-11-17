import os

def consolidar_archivos(config_extensiones, archivo_salida="myfiles.txt"):
    patrones = [p.strip().replace("*", "") for p in config_extensiones.split(";")]

    with open(archivo_salida, 'w', encoding='utf-8') as f_destino:
        for raiz, carpetas, archivos in os.walk("../../../../../../../.."):

            # --- NUEVO: Evitamos las carpetas de código autogenerado y configuraciones ---
            carpetas_ignoradas = ["build", ".idea", ".gradle"]
            # En Windows os.sep es '\', en Linux/Mac es '/'
            partes_ruta = raiz.replace('\\', '/').split('/')
            if any(carpeta in partes_ruta for carpeta in carpetas_ignoradas):
                continue

            for nombre_archivo in archivos:
                if nombre_archivo == archivo_salida:
                    continue

                coincide = False
                for patron in patrones:
                    if patron.startswith("."):
                        if nombre_archivo.endswith(patron):
                            coincide = True
                            break
                    else:
                        if nombre_archivo == patron or nombre_archivo.endswith(f".{patron}"):
                            coincide = True
                            break

                if coincide:
                    ruta_completa = os.path.join(raiz, nombre_archivo)
                    try:
                        with open(ruta_completa, 'r', encoding='utf-8', errors='ignore') as f_origen:
                            f_destino.write(f"/* {'='*60} */\n")
                            f_destino.write(f"/* ARCHIVO: {ruta_completa} */\n")
                            f_destino.write(f"/* {'='*60} */\n\n")
                            f_destino.write(f_origen.read())
                            f_destino.write("\n\n")
                        print(f"Procesado: {ruta_completa}")
                    except Exception as e:
                        print(f"Error al leer {ruta_completa}: {e}")

# --- CONFIGURACIÓN ---
# Solo archivos Kotlin
extensiones_a_buscar = ".kt"

consolidar_archivos(extensiones_a_buscar)
print("\n¡Listo! Todo se ha guardado en 'myfiles.txt'")