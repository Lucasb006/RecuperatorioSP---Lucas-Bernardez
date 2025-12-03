package modelo;

import excepciones.ProductoInvalidoException;
import excepciones.ProductoVencidoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;


public class SerializadorProductos {

    public String toJson(ArrayList<ProductoLimpieza> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < products.size(); i++) {
            ProductoLimpieza p = products.get(i);
            sb.append("  {");
            sb.append("\"tipo\":\"").append(escape(p.getType())).append("\",");
            sb.append("\"nombre\":\"").append(escape(p.getNombre())).append("\",");
            sb.append("\"concentracion\":\"").append(escape(p.getConcentracion())).append("\",");
            sb.append("\"vencimiento\":\"").append(p.getFechaVencimiento().toString()).append("\",");
            if (p instanceof ProductoQuimico) {
                sb.append("\"warning\":\"").append(escape(((ProductoQuimico) p).getWarning())).append("\"");
            } else if (p instanceof ProductoEcologico) {
                sb.append("\"etiqueta\":\"").append(escape(((ProductoEcologico) p).getEtiquetaEco())).append("\"");
            } else {
                sb.append("\"detalle\":\"").append(escape(p.getSpecificDetail())).append("\"");
            }
            sb.append("}");
            if (i < products.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public ArrayList<ProductoLimpieza> fromJson(String json)
            throws ProductoInvalidoException {
        ArrayList<ProductoLimpieza> list = new ArrayList<ProductoLimpieza>();
        if (json == null) return list;
        String trimmed = json.trim();
        if (trimmed.length() == 0 || trimmed.equals("[]")) {
            return list;
        }

        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new ProductoInvalidoException("Archivo JSON inválido.");
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        ArrayList<String> objects = splitObjects(body);

        for (int i = 0; i < objects.size(); i++) {
            String obj = objects.get(i).trim();
            if (obj.startsWith("{") && obj.endsWith("}")) {
                ProductoLimpieza p = parseProduct(obj);
                if (p != null) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    private ArrayList<String> splitObjects(String body) {
        ArrayList<String> parts = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int brace = 0;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '{') {
                brace++;
            } else if (c == '}') {
                brace--;
            }
            current.append(c);
            if (brace == 0 && c == '}') {
                parts.add(current.toString().trim());
                current = new StringBuilder();
                int j = i + 1;
                while (j < body.length() && (body.charAt(j) == ',' || body.charAt(j) == '\n' || body.charAt(j) == ' ')) {
                    j++;
                }
                i = j - 1;
            }
        }
        return parts;
    }

    private ProductoLimpieza parseProduct(String obj)
            throws ProductoInvalidoException {
        String tipo = extractString(obj, "tipo");
        String nombre = extractString(obj, "nombre");
        String concentracion = extractString(obj, "concentracion");
        String expiracionStr = extractString(obj, "vencimiento");
        LocalDate expiracion = null;
        try {
            expiracion = LocalDate.parse(expiracionStr);
        } catch (Exception e) {
            throw new ProductoInvalidoException("Fecha inválida en JSON.");
        }

        try {
            if ("QUIMICO".equalsIgnoreCase(tipo)) {
                String warning = extractString(obj, "warning");
                return new ProductoQuimico(nombre, concentracion, expiracion, warning);
            } else if ("ECOLOGICO".equalsIgnoreCase(tipo)) {
                String etiqueta = extractString(obj, "etiqueta");
                return new ProductoEcologico(nombre, concentracion, expiracion, etiqueta);
            } else {
                String detalle = extractString(obj, "detalle");
                return new ProductoEcologico(nombre, concentracion, expiracion, detalle);
            }
        } catch (ProductoVencidoException e) {
            return null;
        }
    }

    private String extractString(String obj, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = obj.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        int end = obj.indexOf("\"", start);
        if (end == -1) return "";
        String value = obj.substring(start, end);
        return unescape(value);
    }

    private String escape(String s) {
        String r = s;
        r = r.replace("\\", "\\\\");
        r = r.replace("\"", "\\\"");
        r = r.replace("\n", "\\n");
        r = r.replace("\r", "\\r");
        return r;
    }

    private String unescape(String s) {
        String r = s;
        r = r.replace("\\n", "\n");
        r = r.replace("\\r", "\r");
        r = r.replace("\\\"", "\"");
        r = r.replace("\\\\", "\\");
        return r;
    }

    public void saveToFile(String path, ArrayList<ProductoLimpieza> products) throws Exception {
        String json = toJson(products);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        try {
            bw.write(json);
        } finally {
            bw.close();
        }
    }

    public ArrayList<ProductoLimpieza> loadFromFile(String path) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            br.close();
        }
        return fromJson(sb.toString());
    }
}