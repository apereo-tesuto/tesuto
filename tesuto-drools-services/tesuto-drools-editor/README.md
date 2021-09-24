8011CCC Drools Rules Editor
=================

Drools Rules Simple Editor for (CCC) Apply and Associated Projects

About
=====

 * Allows simple editing of Rules, RuleSetRows By Highly Technical Individuals

Requirements
============

 * JDK 11 or higher
 * Maven 3.3.x
 * DynamoDb (local/embedded/AWS)

Configuration and Running (Development)
=======================================

## Steps Overview
In order for drools-editor to be fully functioning the following steps need to be completed.
.
* Install DynamoDb local or setup to use AWS dynamodb)

## DynamoDb local database install and configuration ##
 * Install Local DynamoDB: http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html
 
 Go to drools-editor/src/main/resources/application.propertes and uncomment: #aws.endpoint=

## Initial Build of the Project (create jars) ##
* mvn clean install

### To Run unit tests###
 * mvn clean install -Dmaven.test.skip=false

###Debug Assess or Placements ###
Note that port 8000 is a typical debug port but it is reserved for Dynamo DB.  So start with 8001 (tesuto-ui), 8012 (tesuto-placement) and so on.
 * mvn spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8012"


Explanation of UI
=======================================

##   To view ui:  
https://localhost:8448/rules-editor/ui
## Explanation Of Buttons
# Rule List
Gives a Faceted View into the Available Rules;
     Typing a space into any field will bring back all Rules in database
     Valid Statuses:  versioned, active, deleted

# Edit Rule
    1. Rules must be valid before they can be Published
    2. Rules must have an application (cccasssess, sns-listener are current expected values)
    3. Events will designate the validation object used current only MULTIPLE_MEASURE_PLACEMENT is a valid event.
    4. Import expects import statements other than the standard:
    		java.util.Map,
    		java.util.List,
    		java.util.ArrayList,
    		net.ccctechcenter.drools.RulesAction
       e.g. 
          import org.cccnext.tesuto.domain.viewdto.user.StudentViewDto
    5.  Simple Assignments look like the following:
            map : Map()
             student : StudentViewDto() from map.get("STUDENT_VIEW")
            variableSet : VariableSet() from map.get("MULTIPLE_MEASURE_VARIABLE_SET")
            facts : Map() from variableSet.facts
     6. Conditional Statements look like the following
          multipleMeasureVariableSetId: String() from variableSet.id
          cum_gpa :  Double(this >= ${gpa_cum} )from parseFactToDouble((Fact)facts.get("gpa_cum"))
          grade : Integer(this ==${grade_level}) from parseFactToInteger((Fact)facts.get("grade_level"))
          math_ranking : Double(this >= ${math_ranking}) from parseFactToDouble((Fact)facts.get("math_ranking"))
    7.  5) and 6) combined are the when statement
    8.	     Then Statement looks like
          List<RulesAction> actions = (List<RulesAction>) map.get("actions");
          MultipleMeasurePlacement placement = new MultipleMeasurePlacement();
           placement.setCccid(student.getCccid());
          placement.setTransferLevel("${transfer_level}");
          placement.setTransferLevelIndex(${transfer_level_index});
          placement.setSubjectArea("${subject_area}");
          placement.setMiscode("${miscode}");
          placement.setRuleSetId("${rule_set_id}");
          placement.setRuleId("${rule_id}");
          placement.setRowNumber("${row_number}");
          placement.setRuleSetRowId("${rule_set_row_id}");
          placement.setCompetencyMapDiscipline("MATH");
          placement.addProgram("${program}");
          placement.setMultipleMeasureVariableSetId(multipleMeasureVariableSetId);
          filterByCollegeSubjectPlacement(actions, placement);
     9.  Test Token Values are used to fill in tokens: ${XXXX}  when validating the rules,
          rule_set_id, rule_id, row_number, rule_set_row_id are automatically filled in by application
          Others are filled in by following format:
          TOKEN_NAME1,TOKEN_NAME2
          value1,value2
          
# RuleSetRow List
      1. Faceted search like for Rule with same values but returns RuleSetRows.

# RuleSetRow Edit
     1. A rule set row consist of a Rule (RuleTemplate) with Tokens and csvValidation.
         Examples of Tokens can be found in the cccaseesee-rule-core  module in the tesuto project.
         src/test/resources/rules/org/cccnext/tesuto/rules/*.csv
      2. csvValidation examples are in src/test/resources/rules/org/cccnext/tesuto/facts/*.csv
     
