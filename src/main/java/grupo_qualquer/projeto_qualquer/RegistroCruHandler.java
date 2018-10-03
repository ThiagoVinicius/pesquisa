package grupo_qualquer.projeto_qualquer;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RegistroCruHandler extends DefaultHandler {

    private List<RegistroCruUfMunicipioBairro> bairros = new ArrayList<>();

    private List<String> memoria = new ArrayList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("p".equals(localName)) {
            memoria.clear();
        }
        //System.out.printf("startElement(%s, %s, %s, %s)%n", uri, localName, qName, attributesToString(attributes));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("p".equals(localName)) {
            boolean descartado = true;
            if (memoria.size() == 5) {
                RegistroCruUfMunicipioBairro b = extraiBairro(memoria);
                if (ehBairro(b)) {
                    getRegistros().add(b);
                    descartado = false;
                }
            }
            
            if (descartado) {
                System.out.printf("Descartando: %s%n", memoria);
            }
        }
        //System.out.printf("endElement(%s, %s, %s)%n", uri, localName, qName);
    }

    private boolean ehBairro(RegistroCruUfMunicipioBairro b) {
        return !"UF".equals(b.nomeUf) &&
               !"MUNICÍPIO".equals(b.nomeMunicipio) &&
               !"BAIRRO".equals(b.nomeBairro);
    }

    private RegistroCruUfMunicipioBairro extraiBairro(List<String> strings) {
        RegistroCruUfMunicipioBairro bairro = new RegistroCruUfMunicipioBairro();
        bairro.nomeUf = strings.get(0);
        assert " ".equals(strings.get(1));
        bairro.nomeMunicipio = strings.get(2);
        assert " ".equals(strings.get(3));
        bairro.nomeBairro = strings.get(4);
        return bairro;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //System.out.printf("characters(%s)%n", new String(ch, start, length));
        memoria.add(new String(ch, start, length));
    }

    public List<RegistroCruUfMunicipioBairro> getRegistros() {
        return bairros;
    }

}
