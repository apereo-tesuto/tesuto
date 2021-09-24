import org.cccnext.tesuto.placement.model.StringListConverter
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
class StringListConverterSpec extends Specification {
    @Shared StringListConverter stringListConverter;

    def setupSpec() {
        this.stringListConverter = new StringListConverter();
    }

    def "convertToDatabaseColumn properly creates joined string"() {
        when:
        def listOfStrings = Arrays.asList("This", "is", "a", "string", "array")

        then:
        def expectedString = "This,is,a,string,array"
        def actualString = stringListConverter.convertToDatabaseColumn(listOfStrings)
        expectedString.equals(actualString)
    }

    def "convertToEntityAttribute properly converts comma-delimited string to ArrayList"() {
        when:
        def commaDelimitedString = "This,is,a,comma,delimited,string"

        then:
        def expectedStringArray = Arrays.asList("This", "is", "a", "comma", "delimited", "string")
        def actualStringArray = stringListConverter.convertToEntityAttribute(commaDelimitedString)
        expectedStringArray == actualStringArray
    }

    def "convertToEntityAttribute returns null for a null string"() {
        when:
        def nullString = null;

        then:
        def expectedString = null;
        def actualString = stringListConverter.convertToEntityAttribute(nullString);
        expectedString == actualString
    }

    def "convertToEntityAttribute returns empty List<String> for a string with only commas"() {
        when:
        def commaString = ",,,";

        then:
        def expectedList = new ArrayList<String>()
        def actualList = stringListConverter.convertToEntityAttribute(commaString)
        expectedList.equals(actualList)
    }

    def "convertToEntityAttribute returns empty List<String> for an empty string"() {
        when:
        def emptyString = ""

        then:
        def expectedList = new ArrayList<String>()
        def actualList = stringListConverter.convertToEntityAttribute(emptyString)
        expectedList.equals(actualList)
    }
}
