import org.cccnext.tesuto.placement.service.MMAPEquivalentServiceImpl
import org.cccnext.tesuto.placement.view.MMAPEquivalentDto
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
class MMAPEquivalentServiceSpec extends Specification {

    @Shared ApplicationContext context
    @Shared MMAPEquivalentServiceImpl service

    def setupSpec() {
        this.context = new ClassPathXmlApplicationContext("test-application-context.xml")
        this.service = context.getBean("mmapEquivalentService")
    }

    def "MMAPEquivalentServiceSpec returns ordered list of Equivalents for given discipline"() {
        when:
        def equivalents = service.getMMAPEquivalentsForCompetencyMapDiscipline("MATH");

        then:
        for (int index = 0; index < equivalents.size(); index++) {
            equivalents.get(index).getOrder() == index + 1
        }
    }

    def "MMAPEquivalentServiceSpec returns appropriate Equivalents for English & ESL"() {
        when:
        def eslEquivalents = service.getMMAPEquivalentsForCompetencyMapDiscipline("ESL")
        def englishEquivalents = service.getMMAPEquivalentsForCompetencyMapDiscipline("ENGLISH")
        def expectedEquivalents = ["English", "ESL", "Reading"]

        then:
        for (int index = 0; index < expectedEquivalents.size(); index++) {
            eslEquivalents.get(index).getMmapEquivalent().equals(expectedEquivalents.get(index))
            englishEquivalents.get(index).getMmapEquivalent().equals(expectedEquivalents.get(index))
        }
    }

    def "MMAPEquivalentServiceSpec returns appropriate Equivalents for Math"() {
        when:
        def mathEquivalents = service.getMMAPEquivalentsForCompetencyMapDiscipline("MATH")
        def expectedEquivalents = ["Int. Algebra", "Statistics", "General Education Math", "College Algebra", "Trigonometry", "Pre-Calculus", "Calculus"]
        def expectedEquivalentCodes = ["math_alg", "math_stat", "math_ge", "math_col_alg", "math_trig", "math_pre_calc", "math_calc_i"]

        then:
        for (int index = 0; index < expectedEquivalents.size(); index++) {
            mathEquivalents.get(index).getMmapEquivalent().equals(expectedEquivalents.get(index))
            mathEquivalents.get(index).getCode().equals(expectedEquivalentCodes.get(index))
        }
    }
}
