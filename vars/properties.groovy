@Grab('org.yaml:snakeyaml:1.17')

import org.yaml.snakeyaml.Yaml
def properties() {
Yaml parser = new Yaml()
List example = parser.load(("${WORKSPACE}/test.yml" as File).text)

example.each{println it.project_Key}
}
