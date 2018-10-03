package grupo_qualquer.projeto_qualquer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class App {

    private static List<RegistroCruUfMunicipioBairro> listaRegistros(Path arquivo) throws IOException, SAXException, TikaException {
        PDFParser parser = new PDFParser();

        try (InputStream is = Files.newInputStream(arquivo)) {
            RegistroCruHandler handler = new RegistroCruHandler();
            parser.parse(is, handler, new Metadata(), new ParseContext());
            return handler.getRegistros();
        }
    }

    private static void descreveEstados(List<Estado> estados) {
        System.out.printf("Quantidade de estados: %d%n", estados.size());

        System.out.println();
        imprimeOrdenado("Por quantidade de municipios", estados, e -> e.municipios.size());

        System.out.println();
        imprimeOrdenado("Por quantidade de bairros", estados, e -> e.listaBairros().size());

    }

    private static void imprimeOrdenado(String descricao, List<Estado> estados, Function<Estado, Integer> campo) {
        System.out.println(descricao);
        System.out.println(geraCabecalho());
        estados.stream().
            sorted((a, b) -> campo.apply(b) - campo.apply(a)).
            map(e -> geraLinha(e)).
            forEachOrdered(System.out::println);
    }

    private static String geraCabecalho() {
        return String.format("%2s - %11s %8s", "UF", "Municipios", "Bairros");
    }

    private static String geraLinha(Estado e) {
        return String.format("%s - %11d %8d", e.sigla, e.municipios.size(), e.listaBairros().size());
    }
    
    private static List<Estado> montaEstados(List<RegistroCruUfMunicipioBairro> cru) {
        List<Estado> resultado = new ArrayList<>();
        List<String> siglas = cru.stream().
                map(b -> b.nomeUf).
                distinct().
                collect(Collectors.toList());
        for (String s : siglas) {
            List<String> nomesMunicipios = cru.stream().
                filter(b -> s.equals(b.nomeUf)).
                map(b -> b.nomeMunicipio).
                distinct().
                collect(Collectors.toList());

            Estado e = new Estado();
            e.sigla = s;
            e.municipios = montaMunicipios(nomesMunicipios, cru);
            resultado.add(e);
        }
        return resultado;
    }

    private static List<Municipio> montaMunicipios(List<String> nomesMunicipios, List<RegistroCruUfMunicipioBairro> cru) {
        List<Municipio> resultado = new ArrayList<>();
        for (String m : nomesMunicipios) {
            List<Bairro> bairros = cru.stream().
                filter(b -> m.equals(b.nomeMunicipio)).
                map(b -> new Bairro(b.nomeBairro)).
                collect(Collectors.toList());

            Municipio municipio = new Municipio();
            municipio.nomeMunicipio = m;
            municipio.bairros = bairros;
            resultado.add(municipio);
        }
        return resultado;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, TikaException, SAXException {
        List<RegistroCruUfMunicipioBairro> registros = listaRegistros(Paths.get(args[0]));
        List<Estado> estados = montaEstados(registros);
        descreveEstados(estados);
    }
}
