import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alejandro Bajo Pérez
 */

public class Principal {

	public static void main(String[] args) {
		Scanner entrada = new Scanner(System.in);
		String url;
		System.out.print("Introduce una URL: ");
		url = entrada.nextLine();
		String texto = obtenerWeb(url);
		titulo(texto);
		descargarImagen(menuEnlaces(url, texto));
	}

	/**
	 * Obtiene el código fuente una página web
	 * 
	 * @param url
	 * @return
	 */
	public static String obtenerWeb(String url) {
		String linea, texto = "";
		try {
			URL direccion = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(direccion.openStream()));
			while ((linea = br.readLine()) != null) {
				texto += linea;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return texto;
	}

	/**
	 * Muestra por pantalla el título de la página web
	 * 
	 * @param url
	 */
	public static void titulo(String texto) {
		Pattern pattern = Pattern.compile("<title.*?>(.*)<\\/title>");
		Matcher matcher = pattern.matcher(texto);
		if (matcher.find()) {
			System.out.println("\nTítulo: " + matcher.group(1));
		}
		System.out.println("========================================\n");
	}

	/**
	 * Muestra los 10 primeros link de las imágenes de la página web pasada por
	 * parámetros
	 * 
	 * @param url
	 */
	public static String menuEnlaces(String url, String texto) {
		Scanner entrada = new Scanner(System.in);
		ArrayList<String> enlaces = new ArrayList<>();
		System.out.println("\n----> 10 Primeras imágenes de: \"" + url + "\".");
		// Con un patrón voy sacando los enlaces de las imágenes
		Pattern pattern = Pattern.compile("img.*? src=\"(.*?)\"");
		Matcher matcher = pattern.matcher(texto);
		// Los añado a una lista
		while (matcher.find() && enlaces.size() < 10) {
			// Compruebo que esté bien formado el enlace
			String rutaImagen = matcher.group(1);
			if (rutaImagen.startsWith("/")) {
				try {
					URL direccion = new URL(url);
					rutaImagen = direccion.getProtocol() + "://" + direccion.getHost() + rutaImagen;
				} catch (Exception e) {
				}
			} else if (rutaImagen.startsWith("//")) {
				rutaImagen = "http:" + rutaImagen;
			}
			enlaces.add(rutaImagen);
		}
		// Los muestro
		for (int i = 0; i < enlaces.size(); i++) {
			System.out.println(i + ") " + enlaces.get(i));
		}

		int img = -1;
		while (img < 0 || img > 10) {
			if (enlaces.isEmpty()) {
				System.out.print("Pulse una tecla del 0-0: ");
			} else {
				System.out.print("Pulse una tecla del 0-" + (enlaces.size() - 1) + ": ");
			}
			// Llamo al método con el enlace seleccionado
			img = entrada.nextInt();
		}
		return enlaces.get(img);
	}

	public static void descargarImagen(String url) {
		// Genero una URL
		URL imagen = null;
		try {
			imagen = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// Saco el nombre de la imagen
		String[] partesImagen = imagen.getPath().split("/");
		String nombreImagen = partesImagen[partesImagen.length - 1];
		// Abro flujos
		try {
			InputStream is = imagen.openStream();
			FileOutputStream fos = new FileOutputStream(new File("descargas" + File.separator + nombreImagen));
			byte[] buffer = new byte[1024];
			int bytesLeidos = 0;
			// Leo y guardo cuantos bytes he leido
			while ((bytesLeidos = is.read(buffer)) > 0) {
				// Genero el elemento descargado
				fos.write(buffer, 0, bytesLeidos);
			}
			// Cierro flujos
			is.close();
			fos.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("\nImagen guardada.");
	}
}