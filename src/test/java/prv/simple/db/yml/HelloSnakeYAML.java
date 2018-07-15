package prv.simple.db.yml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class HelloSnakeYAML {
    @Test
    public void test1() {
        try {
            Yaml yaml = new Yaml();
            URL url = HelloSnakeYAML.class.getClassLoader().getResource("conf.yml");
            if (url != null) {
                Object obj = yaml.load(new FileInputStream(url.getFile()));
                System.out.println(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPojo() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        URL url = HelloSnakeYAML.class.getClassLoader().getResource("test.yaml");
        Map result = (Map) yaml.load(new FileInputStream(url.getFile()));
        System.out.println(result);
        System.out.println(result.get("pojo"));
        System.out.println(result.get("pojo2"));
    }
}